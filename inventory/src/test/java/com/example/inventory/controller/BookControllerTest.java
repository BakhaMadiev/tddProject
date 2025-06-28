package com.example.inventory.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.inventory.model.Author;
import com.example.inventory.model.Book;
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.repository.BookRepository;
import com.example.inventory.view.BookView;

public class BookControllerTest {

	@Mock private BookRepository bookRepo;
	@Mock private AuthorRepository authorRepo;
	@Mock private BookView bookView;
	
	@InjectMocks
	private BookController bookController;
	
	private AutoCloseable closeable;
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@After
	public void releaseMocks() throws Exception{
		closeable.close();
	}
	
	@Test
	public void testAllBooks() {
		List<Book> books = asList(new Book("1", "Harry Potter", new Author("1", "Name", "Surname")));
		when(bookRepo.findAll()).thenReturn(books);
		bookController.allBooks();
		verify(bookRepo).findAll();
		verify(bookView).showAllBooks(books);
		verifyNoMoreInteractions(bookRepo, bookView);
	}
	
	@Test
	public void testNewBookWhenTitleIsNull() {
		Book book = new Book("1", null, new Author("1", "Name", "Surname"));
		bookController.newBook(book);
		verify(bookView).showError("Please, set a title for the book with id: " + book.getId(), book);
	    verifyNoMoreInteractions(bookRepo, bookView);
	}
	
	@Test
	public void testNewBookWhenTitleIsEmptyString() {
		Book book = new Book("1", " ", new Author("1", "Name", "Surname"));
		bookController.newBook(book);
		verify(bookView).showError("Please, set a title for the book with id: " + book.getId(), book);
		verifyNoMoreInteractions(bookRepo, bookView);
	}
	
	@Test
	public void testNewBookWhenIdIsNull() {
		Book book = new Book(null, "Title", new Author("1", "Name", "Surname"));
		bookController.newBook(book);
		verify(bookView).showError("Please, set a correct id for the book!", book);
		verifyNoMoreInteractions(bookRepo, bookView);
	}
	
	@Test
	public void testNewBookWhenIdIsEmpty() {
		Book book = new Book(" ", "Title", new Author("1", "Name", "Surname"));
		bookController.newBook(book);
		verify(bookView).showError("Please, set a correct id for the book!", book);
		verifyNoMoreInteractions(bookRepo, bookView);
	}
	
	@Test
	public void testNewBookWhenBookDoesNotExist() {
		Book book = new Book("1", "Title", new Author("1", "Name", "Surname"));
		when(bookRepo.findById("1")).thenReturn(null);
		bookController.newBook(book);
		InOrder inOrder = inOrder(bookRepo, bookView);
		inOrder.verify(bookRepo).save(book);
		inOrder.verify(bookView).bookAdded(book);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testNewBookWhenBookAlreadyExist() {
		Book newBook = new Book("1", "Title", new Author("1", "Name", "Surname"));
		Book existingBook = new Book("1", "Existing Title", new Author("1", "Name", "Surname"));
		when(bookRepo.findById("1")).thenReturn(existingBook);
		bookController.newBook(newBook);
		verify(bookView).showError("Already existing book with id: 1", existingBook);
		verifyNoMoreInteractions(ignoreStubs(bookRepo));
	}
	
	@Test
	public void testDeleteBookWhenBookExists() {
		Book book = new Book("1", "Title", new Author("1", "Name", "Surname"));
		when(bookRepo.findById("1")).thenReturn(book);
		bookController.deleteBook(book);
		InOrder inOrder = inOrder(bookRepo, bookView);
		inOrder.verify(bookRepo).delete("1");
		inOrder.verify(bookView).bookRemoved(book);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testDeleteBookWhenBookDoesNotExist() {
		Book book = new Book("1", "Title", new Author("1", "Name", "Surname"));
		when(bookRepo.findById("1")).thenReturn(null);
		bookController.deleteBook(book);
		verify(bookView).showError("Cannot delete - no book with id: 1", book);
		verifyNoMoreInteractions(ignoreStubs(bookRepo));
	}
	
	@Test
	public void testAddBookWithNullAuthor() {
		Book book = new Book("1", "Title", null);
		bookController.newBook(book);
		verify(bookView).showError("Please, select an author for the book with id: " + book.getId(), book);
		verifyNoMoreInteractions(bookRepo, bookView);
	}

}
