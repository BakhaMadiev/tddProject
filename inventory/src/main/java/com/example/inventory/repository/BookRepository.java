package com.example.inventory.repository;

import java.util.List;

import com.example.inventory.model.Book;

public interface BookRepository {
	public List<Book> findAll();
	public Book findById(String id);
	public void save(Book book);
	public void delete(String id);
}
