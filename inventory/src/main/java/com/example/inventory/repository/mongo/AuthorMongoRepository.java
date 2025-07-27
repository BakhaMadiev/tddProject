package com.example.inventory.repository.mongo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.inventory.model.Author;
import com.example.inventory.repository.AuthorRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class AuthorMongoRepository implements AuthorRepository{
	
	public static final String LIBRARY_DB_NAME = "library";
	public static final String AUTHOR_COLLECTION_NAME = "author";
	
	private MongoCollection<Document> authorCollection;

	public AuthorMongoRepository(MongoClient client, String databaseName, String collectionName) {
		authorCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}
	
	@Override
	public List<Author> findAll() {
		return StreamSupport.stream(authorCollection.find().spliterator(), false)
				.map(this::fromDocumentToAuthor)
				.collect(Collectors.toList());
	}
	
	private Author fromDocumentToAuthor(Document d) {
		return new Author("" + d.get("id"), "" + d.get("firstName"), "" + d.get("lastName"));
	}

	@Override
	public Author findById(String id) {
		Document d = authorCollection.find(Filters.eq("id", id)).first();
		if (Objects.isNull(d)) {
			return null;
		}
		return fromDocumentToAuthor(d);
	}

	@Override
	public void save(Author author) {
		authorCollection.insertOne(
			new Document("id", author.getId())
				.append("firstName", author.getFirstName())
				.append("lastName", author.getLastName())
		); 
	}

	@Override
	public void delete(String id) {
		authorCollection.deleteOne(Filters.eq("id", id));
	}

}
