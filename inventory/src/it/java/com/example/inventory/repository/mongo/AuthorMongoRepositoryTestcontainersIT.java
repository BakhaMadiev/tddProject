package com.example.inventory.repository.mongo;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.inventory.repository.mongo.AuthorMongoRepository.LIBRARY_DB_NAME;
import static com.example.inventory.repository.mongo.AuthorMongoRepository.AUTHOR_COLLECTION_NAME;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.example.inventory.model.Author;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class AuthorMongoRepositoryTestcontainersIT {

	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");
	
	private MongoClient client;
	private AuthorMongoRepository repo;
	private MongoCollection<Document> collection;
	
	@Before
	public void setup() {
		client = new MongoClient(
				new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
		repo = new AuthorMongoRepository(client);
		MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
		database.drop();
		collection = database.getCollection(AUTHOR_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testFindAll() {
		addTestAuthorToDatabase("1", "Name", "Surname");
		addTestAuthorToDatabase("2", "Name_2", "Surname_2");
		assertThat(repo.findAll()).containsExactly(new Author("1", "Name", "Surname"), new Author("2", "Name_2", "Surname_2"));
	}
	
	@Test
	public void testFindById() {
		addTestAuthorToDatabase("1", "Name", "Surname");
		addTestAuthorToDatabase("2", "Name_2", "Surname_2");
		assertThat(repo.findById("2")).isEqualTo(new Author("2", "Name_2", "Surname_2"));
	}
	
	@Test
	public void testSave() {
		Author author = new Author("1", "Name", "Surname");
		repo.save(author);
		assertThat(readAllAuthorsFromDatabase()).containsExactly(author);
	}
	
	@Test
	public void testDelete() {
		addTestAuthorToDatabase("1", "name", "Surname");
		repo.delete("1");
		assertThat(readAllAuthorsFromDatabase()).isEmpty();
	}
	
	private void addTestAuthorToDatabase(String id, String name, String lastName) {
		collection.insertOne(new Document().append("id",  id).append("firstName", name).append("lastName", lastName));
	}
	
	private List<Author> readAllAuthorsFromDatabase() {
		return StreamSupport.stream(collection.find().spliterator(), false)
				.map(a -> new Author("" + a.get("id"), "" + a.get("firstName"), "" + a.get("lastName")))
						.collect(Collectors.toList());
	}
}
