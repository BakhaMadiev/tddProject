package com.example.inventory.controller;

import static org.mockito.Mockito.*;

import java.util.Collections;

import static java.util.Arrays.asList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.inventory.model.Author;
import com.example.inventory.model.Book;
import com.example.inventory.repository.BookRepository;
import com.example.inventory.repository.mongo.BookMongoRepository;
import com.example.inventory.view.BookView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * To start mongoDB container need to run this command
 * 
 * docker run -p 27017:27017 --rm mongo:5
 */

public class BookControllerIT {
	
	@Mock
	private BookView view;
	private BookRepository repo;
	private BookController controller;
	
	private AutoCloseable closeable;
	
	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		repo = new BookMongoRepository(new MongoClient(new ServerAddress("localhost", mongoPort)));
		for(Book book: repo.findAll()) {
			repo.delete(book.getId());
		}
		controller = new BookController(view, repo);
	}
	
	@After
	public void releaseMocks() throws Exception{
		closeable.close();
	}
	
	@Test
	public void testAllBooks() {
		Book b = new Book("1", "Title", new Author("a1", "Name", "Surname"));
		repo.save(b);
		controller.allBooks();
		verify(view).showAllBooks(asList(b));
	}
	
	@Test
	public void testNewBook() {
		Book b = new Book("1", "Title", new Author("a1", "Name", "Surname"));
		controller.newBook(b);
		verify(view).bookAdded(b);
	}
	
	@Test
	public void testDeleteBook() {
		Book b = new Book("1", "Title", new Author("a1", "Name", "Surname"));
		repo.save(b);
		controller.deleteBook(b);
		verify(view).bookRemoved(b);
	}
	
	@Test
	public void testNewDuplicateBook() {
		Book b = new Book("1", "Title", new Author("a1", "Name", "Surname"));
		repo.save(b);
		controller.newBook(b);
		verify(view).showError(contains("Already existing book with id: " + b.getId()), eq(b));
	}
	
	@Test
	public void testShowAllBookWhenEmpty() {
		controller.allBooks();
		verify(view).showAllBooks(eq(Collections.emptyList()));
	}
}
