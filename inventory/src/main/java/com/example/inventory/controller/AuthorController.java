package com.example.inventory.controller;

import java.util.Objects;

import com.example.inventory.model.Author;
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.view.AuthorView;

public class AuthorController {
	private AuthorView authorView;
	private AuthorRepository authorRepository;
	
	public AuthorController(AuthorView authorView, AuthorRepository authorRepository) {
		this.authorView = authorView;
		this.authorRepository = authorRepository;
	}
	
	public void allAuthors() {
		authorView.showAllAuthors(authorRepository.findAll());
	}
	
	public void newAuthor(Author author) {
		//check ID of author
		if(Objects.isNull(author.getId()) || author.getId().trim().isEmpty()) {
			authorView.showError("Please, set a correct author id!", author);
			return;
		}
		
		//check First Name of author
		if(Objects.isNull(author.getFirstName()) || author.getFirstName().trim().isEmpty()) {
			authorView.showError("Please, set a correct name for author with ID: " + author.getId(), author);
			return;
		}
		
		// Check author's last name 
		if(Objects.isNull(author.getLastName()) || author.getLastName().trim().isEmpty()) {
			authorView.showError("Please, set a correct surname for author with ID: " + author.getId(), author);
			return;
		}
		
		
		if(Objects.nonNull(authorRepository.findById(author.getId()))) {
			authorView.showError("Already existing author with id: " + author.getId(), authorRepository.findById(author.getId()));
			return;
		}
		
		authorRepository.save(author);
		authorView.authorAdded(author);
	}
	
	public void deleteAuthor(Author author) {
		if(Objects.isNull(authorRepository.findById(author.getId()))) {
			authorView.showError("Cannot delete author - No existing author with id: " + author.getId(), author);
			return;
		}
		
		authorRepository.delete(author.getId());
		authorView.authorRemoved(author);
	}

}
