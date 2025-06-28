package com.example.inventory.repository;

import java.util.List;

import com.example.inventory.model.Author;

public interface AuthorRepository {
	public List<Author> findAll();
	public Author findById(String id);
	public void save(Author author);
	public void delete(String id);
}
