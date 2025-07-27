package com.example.inventory.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.*;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.bson.Document;

import com.example.inventory.bdd.LibrarySwingAppBDD;
import com.mongodb.MongoClient;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LibrarySwingAppSteps {
	
	private static final String DB_NAME = "test-db";
	private static final String AUTHOR_COLLECTION_NAME = "author-test-collection";
	private static final String BOOK_COLLECTION_NAME = "book-test-collection";
	
	private MongoClient mongoClient;
	
	private FrameFixture window;
	
	@Before
	public void setUp() {
		mongoClient = new MongoClient("localhost", LibrarySwingAppBDD.mongoPort);
		mongoClient.getDatabase(DB_NAME).drop();
	}
	
	@After
	public void tearDown() {
		mongoClient.close();
		if(window != null) window.cleanUp();
	}
	
	@Given("The database contains the authors with the following values")
	public void the_database_contains_the_authors_with_the_following_values(List<List<String>> authors) {
		authors.forEach(
			author -> mongoClient.getDatabase(DB_NAME).getCollection(AUTHOR_COLLECTION_NAME)
				.insertOne(
					new Document()
						.append("id", author.get(0))
						.append("firstName", author.get(1))
						.append("lastName", author.get(2))
				)
		);
	}
	
	@Given("The database contains the books with the following values")
	public void the_database_contains_the_books_with_the_following_values(List<List<String>> books) {
		books.forEach(book -> {
			Document authorDoc = new Document()
				.append("id", book.get(2))
				.append("firstName", book.get(3))
				.append("lastName", book.get(4));
			mongoClient.getDatabase(DB_NAME).getCollection(BOOK_COLLECTION_NAME)
				.insertOne(
						new Document()
							.append("id", book.get(0))
							.append("title", book.get(1))
							.append("author", authorDoc)
				);
		});
	}

	@When("The Library View is shown")
	public void the_Library_View_is_shown() {
		application("com.example.inventory.MainApp").withArgs(
			"--mongo-port=" + LibrarySwingAppBDD.mongoPort,
			"--db-name=" + DB_NAME,
			"--db-author-collection=" + AUTHOR_COLLECTION_NAME,
			"--db-book-collection=" + BOOK_COLLECTION_NAME
		).start();
		
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Library Inventory Management".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@Then("The Author List contains elements with the following values")
	public void the_Author_List_contains_elements_with_the_following_values(List<List<String>> authors) {
		await().untilAsserted(() -> 
			authors.forEach(author -> assertThat(window.list("authorList")
				.contents()).anySatisfy(
					e -> assertThat(e)
						.contains(
							author.get(0), 
							author.get(1), 
							author.get(2)
						)
				)
			)
		);
	}
	
	@Then("The Book List contains elements with the following values")
	public void the_Book_List_contains_elements_with_the_following_values(List<List<String>> books) {
		await().untilAsserted(() -> 
			books.forEach(book -> {
				assertThat(window.list("bookList").contents()).anySatisfy(
					e -> assertThat(e).contains(
						book.get(0),
						book.get(1),
						book.get(2),
						book.get(3),
						book.get(4)
					)
				);
			})
		);
	}
	
	@When("The user enters the following values in the author text fields")
	public void the_user_enters_the_following_values_in_the_author_text_fields(List<List<String>> inputs) {
		List<String> headers = inputs.get(0);
		List<String> values = inputs.get(1);
		
		for(int i = 0; i < headers.size(); i++) {
			String header = headers.get(i);
			String value = values.get(i);
			window.textBox("author" + header + "TextBox").enterText(value);
		}
	}
	
	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonText) {
		window.button(JButtonMatcher.withText(buttonText)).click();
	}
	
	@When("The user enters the following values in the book text fields")
	public void the_user_enters_the_following_values_in_the_book_text_fields(List<List<String>> inputs) {
		List<String> headers  = inputs.get(0);
		List<String> values = inputs.get(1);
		
		for(int i = 0; i < headers.size(); i++) {
			String header = headers.get(i);
			String value = values.get(i);
			
			if(header.equalsIgnoreCase("Author")) {
				await().untilAsserted(() -> {
					assertThat(window.comboBox("bookAuthorComboBox").contents()).anyMatch(e -> e.equals(value));
					window.comboBox("bookAuthorComboBox").selectItem(value);
				});
			} else {
				window.textBox("book" + header + "TextBox").setText("");
				window.textBox("book" + header + "TextBox").enterText(value);
			}
		}
	}
	
	@Then("An {string} error is shown containing the following error")
	public void an_error_is_shown_containing_the_following_error(String type, List<List<String>> errors) {
		String labelName = type.toLowerCase() + "ErrorMessageLabel";
		errors.forEach(error -> {
			error.forEach(e -> 
				await().untilAsserted(() -> {
					String labelText = window.label(labelName).text();
					assertThat(labelText).contains(e);
				})
			);
		});
	}
	
	@When("The user selects the {string} with id {string}")
	public void the_user_selects_the_with_id(String listType, String id) {
		String listName = listType.toLowerCase() + "List";
		window.list(listName).selectItem(Pattern.compile(".*" + id + ".*"));
	}
	
	@Then("The {string} contains elements with the following values")
	public void the_contains_elements_with_the_following_values(String listName, List<List<String>> expected) {
		String name;
		
		if(listName.equalsIgnoreCase("Author List")) {
			name = "authorList";
		} else if(listName.equalsIgnoreCase("Book List")) {
			name = "bookList";
		} else {
			throw new IllegalArgumentException("Unknown List Name: " + listName);
		}
		
		String[] contents = window.list(name).contents();
		expected.forEach(row -> {
			assertThat(contents).anySatisfy(e -> assertThat(e).contains(row.get(0), row.get(1), row.get(2)));
		});
	}
	
	@When("The {string} with id {string} is removed from the database")
	public void the_with_id_is_removed_from_the_database(String entityType, String id) {
		String collection = entityType.equalsIgnoreCase("Author") ? AUTHOR_COLLECTION_NAME : BOOK_COLLECTION_NAME;
		mongoClient.getDatabase(DB_NAME)
			.getCollection(collection)
			.deleteOne(new Document("id", id));
	}

}
