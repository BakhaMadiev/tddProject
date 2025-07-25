package com.example.inventory.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.inventory.model.Author;
import com.example.inventory.model.Book;
import com.example.inventory.repository.BookRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class BookMongoRepository implements BookRepository{
	
	public static final String LIBRARY_DB_NAME = "library";
	public static final String BOOK_COLLECTION_NAME = "book";
	
	private MongoCollection<Document> bookCollection;
	
	public BookMongoRepository(MongoClient client, String databaseName, String collectionName) {
		bookCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public List<Book> findAll() {
		return StreamSupport.stream(bookCollection.find().spliterator(), false)
				.map(this::fromDocumentToBook).collect(Collectors.toList());
	}
	
	private Book fromDocumentToBook(Document d) {
		Document authorDoc = (Document) d.get("author");
		Author author = null;
		if(authorDoc != null) {
			author = new Author("" + authorDoc.get("id"), "" + authorDoc.get("firstName"), "" + authorDoc.get("lastName"));
		}
		
		return new Book("" + d.get("id"), ""+d.get("title"), author);
	}

	@Override
	public Book findById(String id) {
		Document d = bookCollection.find(Filters.eq("id", id)).first();
		return null;
	}

	@Override
	public void save(Book book) {
		Document authorDoc = new Document("id", book.getAuthor().getId())
				.append("firstName", book.getAuthor().getFirstName())
				.append("lastName", book.getAuthor().getLastName());
		
		Document bookDoc = new Document("id", book.getId())
				.append("title", book.getTitle())
				.append("author", authorDoc);
		
		bookCollection.insertOne(bookDoc);
	}

	@Override
	public void delete(String id) {
		bookCollection.deleteOne(Filters.eq("id", id));
	}
	
}
