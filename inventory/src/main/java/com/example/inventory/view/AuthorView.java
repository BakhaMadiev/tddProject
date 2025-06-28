package com.example.inventory.view;

import java.util.List;

import com.example.inventory.model.Author;

public interface AuthorView {
	void showAllAuthors(List<Author> authors);
	void showError(String message, Author author);
	void authorAdded(Author author);
	void authorRemoved(Author author);
}
