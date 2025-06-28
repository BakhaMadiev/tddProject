package com.example.inventory.view;

import java.util.List;

import com.example.inventory.model.Book;

public interface BookView {
	void showAllBooks(List<Book> books);
	void showError(String message, Book book);
	void bookAdded(Book book);
	void bookRemoved(Book book);
}
