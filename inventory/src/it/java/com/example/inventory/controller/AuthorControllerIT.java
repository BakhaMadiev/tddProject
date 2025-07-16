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
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.repository.mongo.AuthorMongoRepository;
import com.example.inventory.view.AuthorView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * To start mongoDB container need to run this command
 * 
 * docker run -p 27017:27017 --rm mongo:5
 */

public class AuthorControllerIT {
	
	@Mock
	private AuthorView view;
	private AuthorRepository repo;
	private AuthorController controller;
	
	private AutoCloseable closeable;
	
	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		repo = new AuthorMongoRepository(new MongoClient(new ServerAddress("localhost", mongoPort)));
		for(Author author: repo.findAll()) {
			repo.delete(author.getId());
		}
		controller = new AuthorController(view, repo);
	}
	
	@After
	public void releaseMocks() throws Exception{
		closeable.close();
	}
	
	@Test
	public void testAllAuthors() {
		Author a = new Author("1", "Name", "Surname");
		repo.save(a);
		controller.allAuthors();
		verify(view).showAllAuthors(asList(a));
	}
	
	@Test
	public void testNewAuthor() {
		Author a = new Author("1", "Name", "Surname");
		controller.newAuthor(a);
		verify(view).authorAdded(a);
	}
	
	@Test
	public void testDeleteAuthor() {
		Author a = new Author("1", "Name", "Surname");
		repo.save(a);
		controller.deleteAuthor(a);
		verify(view).authorRemoved(a);
	}
	
	@Test
	public void testNewDuplicateAuthor() {
		Author a = new Author("1", "Name", "Surname");
		repo.save(a);
		controller.newAuthor(a);
		verify(view).showError(contains("Already existing author with id: " + a.getId()), eq(a));
	}
	
	@Test
	public void testDeleteNonExistingAuthor() {
		Author a = new Author("1", "Name", "Surname");
		controller.deleteAuthor(a);
		verify(view).showError(contains("Cannot delete author - No existing author with id: " + a.getId()), eq(a));
	}
	
	@Test
	public void testShowAllAuthorsWhenEmpty() {
		controller.allAuthors();
		verify(view).showAllAuthors(eq(Collections.emptyList()));
	}
}
