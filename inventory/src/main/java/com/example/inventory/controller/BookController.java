package com.example.inventory.controller;

import java.util.Objects;

import com.example.inventory.model.Book;
import com.example.inventory.repository.BookRepository;
import com.example.inventory.view.BookView;

public class BookController {
	private BookView bookView;
	private BookRepository bookRepository;
	
	public BookController(BookView bookView, BookRepository bookRepository) {
		this.bookView = bookView;
		this.bookRepository = bookRepository;
	}
	
	public void allBooks() {
		bookView.showAllBooks(bookRepository.findAll());
	}
	
	public void newBook(Book book) {
		//Check if id is not null or empty
		if(Objects.isNull(book.getId()) || book.getId().trim().isEmpty()) {
			bookView.showError("Please, set a correct id for the book!", book);
			return;
		}
		
		//Check is the book how title not null or empty
		if(Objects.isNull(book.getTitle()) || book.getTitle().trim().isEmpty()) {
			bookView.showError("Please, set a title for the book with id: " + book.getId(), book);
			return;
		}
		
		//check if the book has an author
		if(Objects.isNull(book.getAuthor())) {
			bookView.showError("Please, select an author for the book with id: " + book.getId(), book);
			return;
		}
		
		if(Objects.nonNull(bookRepository.findById(book.getId()))) {
			bookView.showError("Already existing book with id: " + book.getId(), bookRepository.findById(book.getId()));
			return;
		}
		
		bookRepository.save(book);
		bookView.bookAdded(book);
	}
	
	public void deleteBook(Book book) {
		if(Objects.isNull(bookRepository.findById(book.getId()))) {
			bookView.showError("Cannot delete - no book with id: " + book.getId(), book);
			return;
		}
		
		bookRepository.delete(book.getId());
		bookView.bookRemoved(book);
	}
}
