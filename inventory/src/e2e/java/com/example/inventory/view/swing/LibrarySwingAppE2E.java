package com.example.inventory.view.swing;

import static org.assertj.swing.launcher.ApplicationLauncher.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class LibrarySwingAppE2E extends AssertJSwingJUnitTestCase{
	
	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:5");
	
	private static final String DB_NAME = "test-db";
	
	private static final String AUTHOR_COLLECTION_NAME = "author-test-collection";
	private static final String BOOK_COLLECTION_NAME = "book-test-collection";
	
	private MongoClient mongoClient;
	
	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		String containerIpAddress = mongo.getHost();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		
		mongoClient.getDatabase(DB_NAME).drop();
		
		addTestAuthorToDatabase("a1", "Leo", "Tolstoy");
		addTestAuthorToDatabase("a2", "Dante", "Alighieri");
		
		addTestBookToDatabase("1", "War and Peace", "a1", "Leo", "Tolstoy");
		addTestBookToDatabase("2", "Inferno", "a2", "Dante", "Alighieri");
		
		application("com.example.inventory.MainApp").withArgs(
			"--mongo-host=" + containerIpAddress,
			"--mongo-port=" + mappedPort.toString(),
			"--db-name=" + DB_NAME,
			"--db-author-collection=" + AUTHOR_COLLECTION_NAME,
			"--db-book-collection=" + BOOK_COLLECTION_NAME
		).start();
		
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Library Inventory Management".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
		
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	private void addTestAuthorToDatabase(String id, String name, String surname) {
		mongoClient.getDatabase(DB_NAME)
			.getCollection(AUTHOR_COLLECTION_NAME)
			.insertOne(
				new Document()
					.append("id", id)
					.append("firstName", name)
					.append("lastName", surname)
		);
	}
	
	private void addTestBookToDatabase(
			String id, 
			String title, 
			String authorId, 
			String authorFirstName, 
			String authorLastName) 
	{
		Document authorDoc = new Document()
			.append("id", authorId)
			.append("firstName", authorFirstName)
			.append("lastName", authorLastName);
		
		Document bookDoc = new Document()
			.append("id",  id)
			.append("title", title)
			.append("author", authorDoc);
		
		mongoClient.getDatabase(DB_NAME)
			.getCollection(BOOK_COLLECTION_NAME)
			.insertOne(bookDoc);
	}
	
	private void removeAuthorFromDatabase(String id) {
		mongoClient.getDatabase(DB_NAME).getCollection(AUTHOR_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}
	
	private void removeBookFromDatabase(String id) {
		mongoClient.getDatabase(DB_NAME).getCollection(BOOK_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("authorList").contents())
			.anySatisfy(e -> assertThat(e).contains("a1 Leo Tolstoy"))
			.anySatisfy(e -> assertThat(e).contains("Dante Alighieri"));
		
		assertThat(window.list("bookList").contents())
			.anySatisfy(e -> assertThat(e).contains("1", "War and Peace", "a1", "Leo", "Tolstoy"))
			.anySatisfy(e -> assertThat(e).contains("2", "Inferno", "a2", "Dante", "Alighieri"));
	}
	
	@Test @GUITest
	public void testAddAuthorButtonSuccess() {
		window.textBox("authorIdTextBox").enterText("a3");
		window.textBox("authorNameTextBox").enterText("Cesare");
		window.textBox("authorSurnameTextBox").enterText("Pavese");
		window.button(JButtonMatcher.withText("Add Author")).click();
		await().untilAsserted(() -> 
			assertThat(window.list("authorList").contents())
				.anySatisfy(e -> assertThat(e).contains("a3", "Cesare",  "Pavese"))
		);
	}
	
	@Test @GUITest
	public void testAddBookButtonSuccess() {
		window.textBox("bookIdTextBox").enterText("3");
		window.textBox("bookTitleTextBox").enterText("Book Title");
		window.comboBox("bookAuthorComboBox").selectItem("a1 Leo Tolstoy");
		window.button(JButtonMatcher.withText("Add Book")).click();
		
		await().untilAsserted(() ->
			assertThat(window.list("bookList").contents())
				.anySatisfy(e -> assertThat(e)
					.contains("Book Title")
					.contains("a1")
					.contains("Leo").contains("Tolstoy"))
		);
	}
	
	@Test @GUITest
	public void testAddAuthorButtonError() {
		window.textBox("authorIdTextBox").enterText("a1");
		window.textBox("authorNameTextBox").enterText("New Author Name");
		window.textBox("authorSurnameTextBox").enterText("New Author Surname");
		window.button(JButtonMatcher.withText("Add Author")).click();
		await().untilAsserted(() -> 
			assertThat(window.label("authorErrorMessageLabel").text())
				.contains("a1", "Leo", "Tolstoy")
		);
	}
	
	@Test @GUITest
	public void testAddBookButtonError() {
		window.textBox("bookIdTextBox").enterText("1");
		window.textBox("bookTitleTextBox").enterText("Book Title");
		window.comboBox("bookAuthorComboBox").selectItem("a1 Leo Tolstoy");
		window.button(JButtonMatcher.withText("Add Book")).click();
		await().untilAsserted(() -> 
			assertThat(window.label("bookErrorMessageLabel").text())
				.contains("1", "War and Peace", "a1", "Leo", "Tolstoy")
		);
	}
	
	@Test @GUITest
	public void testDeleteAuthorButtonSuccess() {
		window.list("authorList").selectItem(Pattern.compile(".*" + "Leo Tolstoy" + ".*"));
		window.button(JButtonMatcher.withText("Delete Selected Author")).click();
		await().untilAsserted(() -> 
			assertThat(window.list("authorList").contents()).noneMatch(e -> e.contains("Leo Tolstoy"))
		);
	}
	
	@Test @GUITest
	public void testDeleteBookButtonSuccess() {
		window.list("bookList").selectItem(Pattern.compile(".*" + "Inferno" + ".*"));
		window.button(JButtonMatcher.withText("Delete Selected Book")).click();
		await().untilAsserted(() -> 
			assertThat(window.list("bookList").contents()).noneMatch(e -> e.contains("Inferno"))
		);
	}
	
	@Test @GUITest
	public void testDeleteAuthorButtonError() {
		window.list("authorList").selectItem(Pattern.compile(".*" + "Leo Tolstoy" + ".*"));
		removeAuthorFromDatabase("a1");
		window.button(JButtonMatcher.withText("Delete Selected Author")).click();
		await().untilAsserted(() -> 
			assertThat(window.label("authorErrorMessageLabel").text()).contains("a1", "Leo", "Tolstoy")
		);
	}
	
	@Test @GUITest
	public void testDeleteBookButtonError() {
		window.list("bookList").selectItem(Pattern.compile(".*" + "War and Peace" + ".*"));
		removeBookFromDatabase("1");
		window.button(JButtonMatcher.withText("Delete Selected Book")).click();
		await().untilAsserted(() -> 
			assertThat(window.label("bookErrorMessageLabel").text())
				.contains("1", "War and Peace", "a1", "Leo", "Tolstoy")
		);
	}
	
}
