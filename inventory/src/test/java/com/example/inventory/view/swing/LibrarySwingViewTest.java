package com.example.inventory.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.inventory.controller.AuthorController;
import com.example.inventory.controller.BookController;
import com.example.inventory.model.Author;
import com.example.inventory.model.Book;

public class LibrarySwingViewTest extends AssertJSwingJUnitTestCase{
	
	private static final int TIMEOUT = 5000;
	
	private FrameFixture window;
	
	private LibrarySwingView swingView;
	
	@Mock
	private BookController bookController;
	
	@Mock
	private AuthorController authorController;
	
	private AutoCloseable closeable;
	
	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			swingView = new LibrarySwingView();
			swingView.setAuthorController(authorController);
			swingView.setBookController(bookController);
			return swingView;
		});
		window = new FrameFixture(robot(), swingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test @GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Author"));
		
		window.label(JLabelMatcher.withText("Author ID"));
		window.textBox("authorIdTextBox").requireEnabled();
		
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("authorNameTextBox").requireEnabled();
		
		window.label(JLabelMatcher.withText("Last Name"));
		window.textBox("authorSurnameTextBox").requireEnabled();
		
		window.button(JButtonMatcher.withName("addAuthorButton")).requireDisabled();
		
		window.list("authorList");
		window.label("authorErrorMessageLabel").requireText(" ");
		
		window.button(JButtonMatcher.withName("authorDeleteButton"));
		
		window.label(JLabelMatcher.withText("Book"));
		
		window.label(JLabelMatcher.withText("Book ID"));
		window.textBox("bookIdTextBox").requireEnabled();
		
		window.label(JLabelMatcher.withText("Book Title"));
		window.textBox("bookTitleTextBox").requireEnabled();
		
		window.label(JLabelMatcher.withText("Book Author"));
		window.comboBox("bookAuthorComboBox").requireEnabled();
		
		window.button(JButtonMatcher.withName("addBookButton")).requireDisabled();
		
		window.label("bookErrorMessageLabel").requireText(" ");
		window.list("bookList");
		
		window.button(JButtonMatcher.withName("bookDeleteButton")).requireDisabled();
	}
	
	@Test
	public void testWhenAuthorFieldsAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("authorIdTextBox").enterText("1");
		window.textBox("authorNameTextBox").enterText("Name");
		window.textBox("authorSurnameTextBox").enterText("Surname");
		window.button(JButtonMatcher.withText("Add Author")).requireEnabled();
	}
	
	@Test
	public void testWhenBokkFieldAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("bookIdTextBox").enterText("1");
		window.textBox("bookTitleTextBox").enterText("Book Title");
		
		Author author = new Author("a1", "Name", "Last Name");
		GuiActionRunner.execute(() -> {
			swingView.getBookAuthorComboBox().addItem(author);
			swingView.getBookAuthorComboBox().setSelectedItem(author);
			return null;
			
		});
		
		window.comboBox("bookAuthorComboBox").requireSelection("a1 Name Last Name");
		window.button(JButtonMatcher.withText("Add Book")).requireEnabled();
	}
	
	@Test
	public void testWhenEitherAuthorIdOrAuthorNameOrAuthorSurnameFieldsAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture authorIdTextBox = window.textBox("authorIdTextBox");
		JTextComponentFixture authorNameTextBox = window.textBox("authorNameTextBox");
		JTextComponentFixture authorSurnameTextBox = window.textBox("authorSurnameTextBox");
		
		authorIdTextBox.enterText(" ");
		authorNameTextBox.enterText(" ");
		authorSurnameTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add Author")).requireDisabled();
		
		authorIdTextBox.setText("");
		authorNameTextBox.setText("");
		authorSurnameTextBox.setText("");
		
		authorIdTextBox.enterText(" ");
		authorNameTextBox.enterText("Name");
		authorSurnameTextBox.enterText("Last Name");
		window.button(JButtonMatcher.withText("Add Author")).requireDisabled();
		
		authorIdTextBox.setText("");
		authorNameTextBox.setText("");
		authorSurnameTextBox.setText("");

		authorIdTextBox.enterText("1");
		authorNameTextBox.enterText(" ");
		authorSurnameTextBox.enterText("Last Name");
		window.button(JButtonMatcher.withText("Add Author")).requireDisabled();

		authorIdTextBox.setText("");
		authorNameTextBox.setText("");
		authorSurnameTextBox.setText("");

		authorIdTextBox.enterText("1");
		authorNameTextBox.enterText("Name");
		authorSurnameTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add Author")).requireDisabled();

		authorIdTextBox.setText("");
		authorNameTextBox.setText("");
		authorSurnameTextBox.setText("");

		authorIdTextBox.enterText(" ");
		authorNameTextBox.enterText(" ");
		authorSurnameTextBox.enterText("Last Name");
		window.button(JButtonMatcher.withText("Add Author")).requireDisabled();

		authorIdTextBox.setText("");
		authorNameTextBox.setText("");
		authorSurnameTextBox.setText("");

		authorIdTextBox.enterText("1");
		authorNameTextBox.enterText(" ");
		authorSurnameTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add Author")).requireDisabled();

		authorIdTextBox.setText("");
		authorNameTextBox.setText("");
		authorSurnameTextBox.setText("");

		authorIdTextBox.enterText(" ");
		authorNameTextBox.enterText("Name");
		authorSurnameTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add Author")).requireDisabled();

		authorIdTextBox.setText("");
		authorNameTextBox.setText("");
		authorSurnameTextBox.setText("");
		
		authorIdTextBox.enterText("1");
		authorNameTextBox.enterText("Name");
		authorSurnameTextBox.enterText("Last Name");
		window.button(JButtonMatcher.withText("Add Author")).requireEnabled();
	}
	
	@Test
	public void testWhenEitherBookIdOrBookTitleOrBookAuthorFieldAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture bookIdTextBox = window.textBox("bookIdTextBox");
		JTextComponentFixture bookTitleTextBox = window.textBox("bookTitleTextBox");
		JButtonFixture addBookButton = window.button(JButtonMatcher.withText("Add Book"));
		
		Author author = new Author("a1", "Name", "Last Name");
		GuiActionRunner.execute(() -> {
			swingView.getBookAuthorComboBox().addItem(author);
			swingView.getBookAuthorComboBox().setSelectedItem(author);
			return null;			
		});
		
		//All Feilds are blank
		bookIdTextBox.enterText(" ");
		bookTitleTextBox.enterText(" ");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		addBookButton.requireDisabled();
		
		bookIdTextBox.setText("");
		bookTitleTextBox.setText("");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));		
		
		//Only book id is written
		bookIdTextBox.enterText("1");
		bookTitleTextBox.enterText(" ");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		addBookButton.requireDisabled();
		
		bookIdTextBox.setText("");
		bookTitleTextBox.setText("");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		
		//only title is written
		bookIdTextBox.enterText(" ");
		bookTitleTextBox.enterText("Book Title");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		addBookButton.requireDisabled();
		
		bookIdTextBox.setText("");
		bookTitleTextBox.setText("");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		
		//only author is selected
		bookIdTextBox.enterText(" ");
		bookTitleTextBox.enterText(" ");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedItem(author));
		addBookButton.requireDisabled();
		
		bookIdTextBox.setText("");
		bookTitleTextBox.setText("");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		
		//ID + title
		bookIdTextBox.enterText("1");
		bookTitleTextBox.enterText("Book Title");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		addBookButton.requireDisabled();
		
		bookIdTextBox.setText("");
		bookTitleTextBox.setText("");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		
		//ID + author
		bookIdTextBox.enterText("1");
		bookTitleTextBox.enterText(" ");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedItem(author));
		addBookButton.requireDisabled();
		
		bookIdTextBox.setText("");
		bookTitleTextBox.setText("");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		
		//title + author
		bookIdTextBox.enterText(" ");
		bookTitleTextBox.enterText("Book Title");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedItem(author));
		addBookButton.requireDisabled();
		
		bookIdTextBox.setText("");
		bookTitleTextBox.setText("");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedIndex(-1));
		
		//all fields are correct
		bookIdTextBox.enterText("1");
		bookTitleTextBox.enterText("Book Title");
		GuiActionRunner.execute(() -> swingView.getBookAuthorComboBox().setSelectedItem(author));
		addBookButton.requireEnabled();
	}
	
	@Test
	public void testDeleteAuthorButtonShouldBeEnabledWhenAuthorFromListIsSelected() {
		GuiActionRunner.execute(() -> {
			swingView.getListOfAuthorModels().addElement(new Author("1", "Name", "Surname"));
		});
		window.list("authorList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected Author"));
		deleteButton.requireEnabled();
		window.list("authorList").clearSelection();
		deleteButton.requireDisabled();
	}
	
	@Test
	public void testDeleteBookButtonShouldBeEnablesWhenBookFromListIsSelected() {
		GuiActionRunner.execute(() -> {
			swingView.getListOfBookModels().addElement(new Book("1", "War and Peace", new Author("a1", "Leo", "Tolstoy")));
		});
		window.list("bookList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected Book"));
		deleteButton.requireEnabled();
		window.list("bookList").clearSelection();
		deleteButton.requireDisabled();
	}
	
	@Test
	public void testShowAllAuthorsShouldAddAuthorDescriptionToTheList() {
		Author a1 = new Author("1", "Fyodor", "Dostoevsky");
		Author a2 = new Author("2", "Dante", "Aliighieri");
		GuiActionRunner.execute(() -> {
			swingView.showAllAuthors(Arrays.asList(a1, a2));
		});
		String [] listContents = window.list("authorList").contents();
		assertThat(listContents).containsExactly(a1.toString(), a2.toString());
	}
	
	@Test
	public void testShowAllBooksShouldAddBookDescriptionToTheList() {
		Book b1 = new Book("1", "Book Title 1", new Author("1", "Fyodor", "Dostoevsky"));
		Book b2 = new Book("2", "Book Title 2", new Author("1", "Dante", "Allighieri"));
		GuiActionRunner.execute(() -> {
			swingView.showAllBooks(Arrays.asList(b1, b2));
		});
		String [] listContents = window.list("bookList").contents();
		assertThat(listContents).containsExactly(b1.toString(), b2.toString());
	}
	
	@Test
	public void testBookAuthorComboBoxWhenAuthorIsAdded() {
		Author a1 = new Author("1", "Fyodor", "Dostoevsky");
		Author a2 = new Author("2", "Dante", "Alighieri");
		GuiActionRunner.execute(() -> swingView.authorAdded(a1));
		String[] authorComboBoxContent = window.comboBox("bookAuthorComboBox").contents();
		assertThat(authorComboBoxContent).containsExactly(a1.toString());
		
		GuiActionRunner.execute(() -> swingView.authorAdded(a2));
		authorComboBoxContent = window.comboBox("bookAuthorComboBox").contents();
		assertThat(authorComboBoxContent).containsExactly(a1.toString(), a2.toString());
	}
	
	@Test
	public void testBookAuthorComboBoxWhenAuthorIsRemoved() {
		Author a1 = new Author("1", "Fyodor", "Dostoevsky");
		Author a2 = new Author("2", "Dante", "Alighieri");
		GuiActionRunner.execute(() -> {
			swingView.authorAdded(a1);
			swingView.authorAdded(a2);
		});
		
		GuiActionRunner.execute(() -> swingView.authorRemoved(a1));
		String[] authorComboBoxContent = window.comboBox("bookAuthorComboBox").contents();
		assertThat(authorComboBoxContent).containsExactly(a2.toString());
		
		GuiActionRunner.execute(() -> swingView.authorRemoved(a2));
		authorComboBoxContent = window.comboBox("bookAuthorComboBox").contents();
		assertThat(authorComboBoxContent).isEmpty();
	}
	
	//Error Labels
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheAuthorErrorLabel() {
		Author a1 = new Author("1", "Fyodor", "Dostoevsky");
		swingView.showError("error message", a1);
		window.label("authorErrorMessageLabel").requireText("error message: " + a1);
	}
	
	@Test
	public void testAuthorAddedShouldAddTheAuthorToTheListAndResetTheErrorLabel() {
		Author a1 = new Author("1", "Fyodor", "Dostoevsky");
		swingView.authorAdded(new Author ("1", "Fyodor", "Dostoevsky"));
		String[] authorListContents = window.list("authorList").contents();
		assertThat(authorListContents).containsExactly(a1.toString());
		window.label("authorErrorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testAuthorRemovedShouldRemoveTheAuthorFromTheListAndResetTheErrorLabel() {
		Author a1 = new Author("1", "Fyodor", "Dostoevsky");
		Author a2 = new Author("2", "Dante", "Alighieri");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Author> listAuthorModels = swingView.getListOfAuthorModels();
			listAuthorModels.addElement(a1);
			listAuthorModels.addElement(a2);
		});
		
		GuiActionRunner.execute(() -> swingView.authorRemoved(new Author("1", "Fyodor", "Dostoevsky")));
		String[] authorListContent = window.list("authorList").contents();
		assertThat(authorListContent).containsExactly(a2.toString());
		window.label("authorErrorMessageLabel").requireText(" ");
		
	}
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheBookErrorLabel() {
		Book b = new Book("1", "Book Title", new Author("1", "Fyoudor", "Dostoevsky"));
		swingView.showError("error message", b);
		window.label("bookErrorMessageLabel").requireText("error message: " + b);
	}
	
	@Test
	public void testBookAddedShouldAddTheBookToTheListAndResetTheErrorLabel() {
		Book b = new Book("1", "Book Title", new Author("1", "Fyoudor", "Dostoevsky"));
		swingView.bookAdded(
			new Book("1", "Book Title", new Author("1", "Fyoudor", "Dostoevsky"))
		);
		String[] bookListContents = window.list("bookList").contents();
		assertThat(bookListContents).containsExactly(b.toString());
		window.label("bookErrorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testBookRemovedShouldRemoveTheBookFromTheListAndResetTheErrorLabel() {
		Book b1 = new Book("1", "Book Title", new Author("1", "Fyoudor", "Dostoevsky"));
		Book b2 = new Book("2", "Book Title 2", new Author("2", "Dante", "Alighieri"));
		
		GuiActionRunner.execute(() -> {
			DefaultListModel<Book> listBookModels = swingView.getListOfBookModels();
			listBookModels.addElement(b1);
			listBookModels.addElement(b2);
		});
		
		GuiActionRunner.execute(() -> swingView.bookRemoved(new Book("1", "Book Title", new Author("1", "Fyoudor", "Dostoevsky"))));
		String[] bookListContents = window.list("bookList").contents();
		assertThat(bookListContents).containsExactly(b2.toString());
		window.label("bookErrorMessageLabel").requireText(" ");
	}
	
	//Controller 
	@Test
	public void testAddAuthorButtonShouldDelegateToAuthorControllerNewAuthor() {
		window.textBox("authorIdTextBox").enterText("1");
		window.textBox("authorNameTextBox").enterText("Leo");
		window.textBox("authorSurnameTextBox").enterText("Tolstoy");
		window.button(JButtonMatcher.withText("Add Author")).click();
		verify(authorController, timeout(TIMEOUT)).newAuthor(new Author("1", "Leo", "Tolstoy"));
	}
	
	@Test
	public void testDeleteAuthorButtonShouldDelegateToAuthorControllerDeleteAuthor() {
		Author a1 = new Author("1", "Fyodor", "Dostoevsky");
		Author a2 = new Author("2", "Dante", "Alighieri");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Author> listAuthorModels = swingView.getListOfAuthorModels();
			listAuthorModels.addElement(a1);
			listAuthorModels.addElement(a2);
		});
		window.list("authorList").selectItem(1);
		window.button(JButtonMatcher.withText("Delete Selected Author")).click();
		verify(authorController).deleteAuthor(a2);;
	}
	
	@Test
	public void testAddBookButtonShouldDelegateToBookControllerNewBook() {
		window.textBox("bookIdTextBox").enterText("1");
		window.textBox("bookTitleTextBox").enterText("Book Title");
		Author author = new Author("a1", "Leo", "Tolstoy");
		GuiActionRunner.execute(() -> {
			swingView.getBookAuthorComboBox().addItem(author);
			swingView.getBookAuthorComboBox().setSelectedItem(author);
		});
		
		window.button(JButtonMatcher.withText("Add Book")).click();
		verify(bookController, timeout(TIMEOUT)).newBook(new Book("1", "Book Title", new Author("a1", "Leo", "Tolstoy")));
	}
	
	@Test
	public void testDeleteBookButtonShouldDelegateToBookControllerDeleteBook() {
		Author author = new Author("a1", "Leo", "Tolstoy");
		Book b1 = new Book("1", "Book Title", author);
		Book b2 = new Book("2", "Book Title 2", author);
		
		GuiActionRunner.execute(() -> {
			swingView.getListOfBookModels().addElement(b1);
			swingView.getListOfBookModels().addElement(b2);
		});
		
		window.list("bookList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected Book")).click();
		verify(bookController).deleteBook(b1);
	}

}
