package com.example.inventory.repository.mongo;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.example.inventory.model.Author;
import com.example.inventory.model.Book;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class BookMongoRepoisotoryTestcontainersIT {
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5");
	
	private MongoClient client;
	private BookMongoRepository repo;
	private MongoCollection<Document> collection;
	
	public static final String LIBRARY_DB_NAME = "library";
	public static final String BOOK_COLLECTION_NAME = "book";

	@Before
	public void setUp(){
		client = new MongoClient(
				new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort())
			);
		repo = new BookMongoRepository(client, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
		database.drop();
		collection = database.getCollection(BOOK_COLLECTION_NAME);				
	}
	
	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAll() {
		addTestBookToDatabase("1", "Title_1", "a1", "Name", "Surname");
		addTestBookToDatabase("2", "Title_2", "a2", "Name_2", "Surname_2");
		assertThat(repo.findAll()).containsExactly(
					new Book("1", "Title_1", new Author("a1", "Name", "Surname")),
					new Book("2", "Title_2", new Author("a2", "Name_2", "Surname_2"))
				);
	}
	
	@Test
	public void findById() {
		addTestBookToDatabase("1", "Title_1", "a1", "Name", "Surname");
		addTestBookToDatabase("2", "Title_2", "a2", "Name_2", "Surname_2");
		assertThat(repo.findById("2")).isEqualTo(new Book("2", "Title_2", new Author("a2", "Name_2", "Surname_2")));
	}
	
	@Test
	public void testSave() {
		Book b = new Book("1", "Title", new Author("1", "Name", "Surname"));
		repo.save(b);
		assertThat(readAllBooksFromDatabase()).containsExactly(b);
	}
	
	@Test
	public void testDelete() {
		addTestBookToDatabase("1", "Title", "1", "Name", "Surname");
		repo.delete("1");
		assertThat(readAllBooksFromDatabase()).isEmpty();
	}
	
	@Test
	public void testBookWithNullAuthor() {
		Document book = new Document().append("id", "3").append("title", "book_with_null_author");
		collection.insertOne(book);
		
		Book bookToFind = repo.findById("3");
		assertThat(bookToFind.getId()).isEqualTo("3");
		assertThat(bookToFind.getTitle()).isEqualTo("book_with_null_author");
		assertThat(bookToFind.getAuthor()).isNull();
	}
	
	private void addTestBookToDatabase(String id, String title, String authorId, String authorName, String authorSurname) {
		Document authorDoc = new Document()
				.append("id", authorId)
				.append("firstName", authorName)
				.append("lastName", authorSurname);
		
		Document bookDoc = new Document()
				.append("id", id)
				.append("title", title)
				.append("author", authorDoc);
		
		collection.insertOne(bookDoc);
	}
	
	private List<Book> readAllBooksFromDatabase(){
		return StreamSupport.stream(collection.find().spliterator(),false)
			.map(d -> {
				Document authorDoc = (Document) d.get("author");
				Author author = new Author(
					"" + authorDoc.get("id"),
					"" + authorDoc.get("firstName"),
					"" + authorDoc.get("lastName")
				);
				
				return new Book(
					"" + d.get("id"),
					"" + d.get("title"),
					author
				);
			}).collect(Collectors.toList());
	}

}
