package com.example.inventory.model;

import java.util.Objects;

public class Book {
	private String id;
	private String title;
	private Author author;
	
	public Book(String id, String title, Author author) {
		this.id = id;
		this.title= title; 
		this.author = author;
	}
	
	//SETTERS
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setAuthor(Author author) {
		this.author = author;
	}
	//GETTERS
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Author getAuthor() {
		return author;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(author, id, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(author, other.author) && Objects.equals(id, other.id)
				&& Objects.equals(title, other.title);
	}
	
	@Override
	public String toString() {
		return title + " (" + (author != null ? author.toString() : "Unknown Author");
	}

}
