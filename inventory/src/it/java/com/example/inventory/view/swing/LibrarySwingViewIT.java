package com.example.inventory.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.timing.Timeout.timeout;
import static org.assertj.swing.timing.Pause.pause;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.inventory.controller.AuthorController;
import com.example.inventory.controller.BookController;
import com.example.inventory.model.Author;
import com.example.inventory.model.Book;
import com.example.inventory.repository.mongo.AuthorMongoRepository;
import com.example.inventory.repository.mongo.BookMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class LibrarySwingViewIT extends AssertJSwingJUnitTestCase{
	
	private static final long TIMEOUT = 5000;
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient mongoClient;
	
	private FrameFixture window;
	private LibrarySwingView swingView;
	private AuthorController authorController;
	private BookController bookController;
	private AuthorMongoRepository authorRepo;
	private BookMongoRepository bookRepo;

	public static final String LIBRARY_DB_NAME = "library";
	public static final String AUTHOR_COLLECTION_NAME = "author";
	public static final String BOOK_COLLECTION_NAME = "book";
	
	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutDownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		authorRepo = new AuthorMongoRepository(mongoClient, LIBRARY_DB_NAME, AUTHOR_COLLECTION_NAME);
		bookRepo = new BookMongoRepository(mongoClient, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME);
		for(Author author: authorRepo.findAll()) {
			authorRepo.delete(author.getId());
		}
		for(Book book: bookRepo.findAll()) {
			bookRepo.delete(book.getId());
		}
		
		GuiActionRunner.execute(() -> {
			swingView = new LibrarySwingView();
			authorController = new AuthorController(swingView, authorRepo);
			bookController = new BookController(swingView, bookRepo);
			swingView.setAuthorController(authorController);
			swingView.setBookController(bookController);
			return swingView;
		});
		
		window = new FrameFixture(robot(), swingView);
		window.show();
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test @GUITest
	public void testAllAuthors() {
		Author a1 = new Author("1", "Leo", "Tolstoy");
		Author a2 = new Author("2", "Dante", "Alighieri");
		authorRepo.save(a1);
		authorRepo.save(a2);
		GuiActionRunner.execute(() -> authorController.allAuthors());
		assertThat(window.list("authorList")
			.contents())
			.containsExactly(a1.toString(), a2.toString());
	}
	
	@Test @GUITest
	public void testAllBooks() {
		Author author = new Author("a1", "Leo", "Tolstoy");
		Book b1 = new Book("1", "Book Title", author);
		Book b2 = new Book("2", "Book Title 2", author);
		bookRepo.save(b1);
		bookRepo.save(b2);		
		GuiActionRunner.execute(() -> bookController.allBooks());
		assertThat(window.list("bookList")
			.contents())
			.containsExactly(b1.toString(), b2.toString()
		);
	}
	
	@Test @GUITest
	public void testAddAuthorButtonSuccess() {
		window.textBox("authorIdTextBox").enterText("1");
		window.textBox("authorNameTextBox").enterText("Leo");
		window.textBox("authorSurnameTextBox").enterText("Tolstoy");
		window.button(JButtonMatcher.withText("Add Author")).click();
		
		pause(new Condition("Author appears in the list") {
			@Override
			public boolean test() {
				String[] contents = window.list("authorList").contents();
				return contents.length == 1 && contents[0].equals(new Author("1", "Leo", "Tolstoy").toString());
			}
		}, timeout(TIMEOUT));
		
		assertThat(window.list("authorList").contents())
			.containsExactly(new Author("1", "Leo", "Tolstoy").toString());
	}
	
	@Test @GUITest
	public void testAddAuthorButtonFail() {
		authorRepo.save(new Author("1", "Leo", "Tolstoy"));
		window.textBox("authorIdTextBox").enterText("1");
		window.textBox("authorNameTextBox").enterText("Leo Second");
		window.textBox("authorSurnameTextBox").enterText("Tolstoy Second");
		window.button(JButtonMatcher.withText("Add Author")).click();
		pause(
			new Condition("Error label to contain text") {
				@Override
				public boolean test() {
					return !window.label("authorErrorMessageLabel").text().trim().isEmpty();
				}
			}, timeout(TIMEOUT)
		);
		assertThat(window.list("authorList").contents()).isEmpty();
		window.label("authorErrorMessageLabel")
			.requireText("Already existing author with id: 1: 1 Leo Tolstoy");
	}
	
	@Test @GUITest
	public void testAddBookButtonSuccess() {
		window.textBox("bookIdTextBox").enterText("1");
		window.textBox("bookTitleTextBox").enterText("Book Title");
		Author author = new Author("a1", "Leo", "Tolstoy");
		GuiActionRunner.execute(() -> {
			swingView.getBookAuthorComboBox().addItem(author);
			swingView.getBookAuthorComboBox().setSelectedItem(author);
		});
		window.button(JButtonMatcher.withText("Add Book")).click();
		
		pause(new Condition("Book appears in list") {
			@Override
			public boolean test() {
				String[] contents = window.list("bookList").contents();
				return contents.length == 1 && contents[0].equals(new Book("1", "Book Title", author).toString());
			}
		}, timeout(TIMEOUT));
		
		assertThat(window.list("bookList").contents())
			.containsExactly(new Book("1", "Book Title", new Author("a1", "Leo", "Tolstoy")).toString());
	}
	
	@Test @GUITest
	public void testAddBookButtonFail() {
		bookRepo.save(new Book("1", "Book Title", new Author("a1", "Leo", "Tolstoy")));
		window.textBox("bookIdTextBox").enterText("1");
		window.textBox("bookTitleTextBox").enterText("Book Title");
		Author author = new Author("a1", "Leo", "Tolstoy");
		GuiActionRunner.execute(() -> {
			swingView.getBookAuthorComboBox().addItem(author);
			swingView.getBookAuthorComboBox().setSelectedItem(author);
		});
		window.button(JButtonMatcher.withText("Add Book")).click();
		pause(
			new Condition("Error label to contain text") {
				@Override
				public boolean test() {
					return !window.label("bookErrorMessageLabel").text().trim().isEmpty();
				}
			}, timeout(TIMEOUT)
		);
		assertThat(window.list("bookList").contents()).isEmpty();
		window.label("bookErrorMessageLabel")
			.requireText("Already existing book with id: 1: 1 Book Title (a1 Leo Tolstoy)");
	}
	
	@Test @GUITest
	public void testDeleteAuthorButtonSuccess() {
		GuiActionRunner.execute(() -> authorController.newAuthor(new Author("1", "Leo", "Tolstoy")));
		window.list("authorList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected Author")).click();
		assertThat(window.list("authorList").contents()).isEmpty();
	}
	
	@Test @GUITest
	public void testDeleteAuthorButtonFail() {
		Author author = new Author ("1", "Leo", "Tolstoy");
		GuiActionRunner.execute(() -> swingView.getListOfAuthorModels().addElement(author));
		window.list("authorList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected Author")).click();
		assertThat(window.list("authorList").contents()).containsExactly(author.toString());
		window.label("authorErrorMessageLabel")
			.requireText("Cannot delete author - No existing author with id: 1: 1 Leo Tolstoy");
	}
	
	@Test @GUITest
	public void testDeleteBookButtonSuccess() {
		GuiActionRunner.execute(() -> bookController.newBook(
			new Book("1", "Book Title", new Author("a1", "Leo", "Tolstoy"))
		));
		window.list("bookList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected Book")).click();
		assertThat(window.list("bookList").contents()).isEmpty();
	}
	
	@Test @GUITest
	public void testDeleteBookButtonFail() {
		Book b = new Book("1", "Book Title", new Author("a1", "Leo", "Tolstoy"));
		GuiActionRunner.execute(() -> swingView.getListOfBookModels().addElement(b));
		window.list("bookList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected Book")).click();
		assertThat(window.list("bookList").contents()).containsExactly(b.toString());
		window.label("bookErrorMessageLabel")
			.requireText("Cannot delete - no book with id: 1: 1 Book Title (a1 Leo Tolstoy)");
	}

}
