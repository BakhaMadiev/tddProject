package com.example.inventory.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.example.inventory.controller.AuthorController;
import com.example.inventory.controller.BookController;
import com.example.inventory.model.Author;
import com.example.inventory.model.Book;
import com.example.inventory.repository.mongo.AuthorMongoRepository;
import com.example.inventory.repository.mongo.BookMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase{
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");
	
	private MongoClient mongoClient;
	
	private FrameFixture window;
	private AuthorController authorController;
	private BookController bookController;
	private AuthorMongoRepository authorRepo;
	private BookMongoRepository bookRepo;

	public static final String LIBRARY_DB_NAME = "library";
	public static final String AUTHOR_COLLECTION_NAME = "author";
	public static final String BOOK_COLLECTION_NAME = "book";
	
	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(
			new ServerAddress(
				mongo.getHost(),
				mongo.getFirstMappedPort()
			)
		);
		authorRepo = new AuthorMongoRepository(mongoClient, LIBRARY_DB_NAME, AUTHOR_COLLECTION_NAME);
		bookRepo = new BookMongoRepository(mongoClient, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME);
		for(Author author: authorRepo.findAll()) {
			authorRepo.delete(author.getId());
		}
		for(Book book: bookRepo.findAll()) {
			bookRepo.delete(book.getId());
		}
		
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			LibrarySwingView swingView = new LibrarySwingView();
			authorController = new AuthorController(swingView, authorRepo);
			bookController = new BookController(swingView, bookRepo);
			swingView.setAuthorController(authorController);
			swingView.setBookController(bookController);
			return swingView;
		}));
		
		window.show();
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	public void testAddAuthor() {
		window.textBox("authorIdTextBox").enterText("1");
		window.textBox("authorNameTextBox").enterText("Leo");
		window.textBox("authorSurnameTextBox").enterText("Tolstoy");
		window.button(JButtonMatcher.withText("Add Author")).click();
		
		await().untilAsserted(() -> 
			assertThat(authorRepo.findById("1")).isEqualTo(new Author("1", "Leo", "Tolstoy"))
		);
	}
	
	@Test
	public void testDeleteAuthor() {
		authorRepo.save(new Author("1", "Leo", "Tolstoy"));
		GuiActionRunner.execute(() -> authorController.allAuthors());
		window.list("authorList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected Author")).click();
		assertThat(authorRepo.findById("1")).isNull();
	}
	
	@Test
	public void testAddBook() {
		Author author = new Author("a1", "Leo", "Tolstoy");
		authorRepo.save(author);
		
		GuiActionRunner.execute(() -> authorController.allAuthors());
		
		window.textBox("bookIdTextBox").enterText("1");
		window.textBox("bookTitleTextBox").enterText("Book Title");
		window.comboBox("bookAuthorComboBox").selectItem(author.toString());
		window.button(JButtonMatcher.withText("Add Book")).click();
		
		await().untilAsserted(() -> 
			assertThat(bookRepo.findById("1")).isEqualTo(new Book("1", "Book Title", author))
		);
	}
	
	@Test
	public void testDeleteBook() {
		Author author = new Author("a1", "Leo", "Tolstoy");
		Book book = new Book("1", "Book Title", author);
		authorRepo.save(author);
		bookRepo.save(book);
		
		GuiActionRunner.execute(() -> {
			authorController.allAuthors();
			bookController.allBooks();
		});
		
		window.list("bookList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected Book")).click();
		assertThat(bookRepo.findById("1")).isNull();
	}

}
