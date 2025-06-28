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
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.view.AuthorView;

public class AuthorControllerTest {

	@Mock
	private AuthorRepository authorRepo;
	
	@Mock 
	private AuthorView authorView;
	
	@InjectMocks
	private AuthorController authorController;
	
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
	public void testAllAuthors() {
		List<Author> authors = asList(new Author("1", "Name", "Surname"));
		when(authorRepo.findAll()).thenReturn(authors);
		authorController.allAuthors();
		verify(authorRepo).findAll();
		verify(authorView).showAllAuthors(authors);
		verifyNoMoreInteractions(authorRepo, authorView);
	}
	
	@Test
	public void testNewAuthorIdInNull() {
		Author author = new Author(null, "Name", "Surname");
		authorController.newAuthor(author);
		verify(authorView).showError("Please, set a correct author id!", author);
		verifyNoMoreInteractions(authorRepo, authorView);
	}
	
	@Test
	public void testNewAuthorIdIsEmpty() {
		Author author = new Author(" ", "Name", "Surname");
		authorController.newAuthor(author);
		verify(authorView).showError("Please, set a correct author id!", author);
		verifyNoMoreInteractions(authorRepo, authorView);
	}
	
	@Test
	public void testNewAuthorWhenAuthorDoesNotExist() {
		Author author = new Author("1", "Name", "Surname");
		when(authorRepo.findById("1")).thenReturn(null);
		authorController.newAuthor(author);
		InOrder inOrder = inOrder(authorRepo, authorView);
		inOrder.verify(authorRepo).save(author);
		inOrder.verify(authorView).authorAdded(author);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testNewAuthorWhenAuthorAlreadyExists() {
		Author newAuthor = new Author("1", "Name", "Surname");
		Author existingAuthor = new Author("1", "ExistingName", "ExistingSurname");
		when(authorRepo.findById("1")).thenReturn(existingAuthor);
		authorController.newAuthor(newAuthor);
		verify(authorView).showError("Already existing author with id: " + existingAuthor.getId(), existingAuthor);
		verifyNoMoreInteractions(ignoreStubs(authorRepo));
	}
	
	@Test
	public void testDeleteAuthorWhenAuthorExists() {
		Author author = new Author("1", "Name", "Surname");
		when(authorRepo.findById("1")).thenReturn(author);
		authorController.deleteAuthor(author);
		InOrder inOrder = inOrder(authorRepo, authorView);
		inOrder.verify(authorRepo).delete("1");
		inOrder.verify(authorView).authorRemoved(author);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testDeleteAuthorWhenAuthorDoesNotExist() {
		Author author = new Author("1", "Name", "Surname");
		when(authorRepo.findById("1")).thenReturn(null);
		authorController.deleteAuthor(author);
		verify(authorView).showError("Cannot delete author - No existing author with id: " + author.getId(), author);
		verifyNoMoreInteractions(ignoreStubs(authorRepo));
	}

}
