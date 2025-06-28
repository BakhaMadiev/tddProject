package com.example.inventory.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.inventory.model.Author;
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.view.AuthorView;

@RunWith(Parameterized.class)
public class AuthorControllerInputValidationTest {
	@Mock
    private AuthorRepository authorRepo;

    @Mock
    private AuthorView authorView;

    @InjectMocks
    private AuthorController authorController;

    private AutoCloseable closeable;

    private Author author;
    private String expectedMessage;
    
    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { new Author("1", null, "Surname"), "Please, set a correct name for author with ID: 1" },
            { new Author("1", " ", "Surname"), "Please, set a correct name for author with ID: 1" },
            { new Author("1", "Name", null), "Please, set a correct surname for author with ID: 1" },
            { new Author("1", "Name", " "), "Please, set a correct surname for author with ID: 1" }
        });
    }

    public AuthorControllerInputValidationTest(Author author, String expectedMessage) {
        this.author = author;
        this.expectedMessage = expectedMessage;
    }

    @Test
    public void testInputValidation() {
        authorController.newAuthor(author);
        verify(authorView).showError(expectedMessage, author);
        verifyNoMoreInteractions(authorRepo, authorView);
    }

}
