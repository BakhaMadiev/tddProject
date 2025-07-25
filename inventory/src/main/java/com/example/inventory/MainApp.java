package com.example.inventory;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.inventory.controller.AuthorController;
import com.example.inventory.controller.BookController;
import com.example.inventory.repository.mongo.AuthorMongoRepository;
import com.example.inventory.repository.mongo.BookMongoRepository;
import com.example.inventory.view.swing.LibrarySwingView;
import com.mongodb.MongoClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import com.mongodb.ServerAddress;

@Command(mixinStandardHelpOptions = true)
public class MainApp implements Callable<Void>{
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";
	
	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "library";
	
	@Option(names = { "--db-author-collection" }, description = "Author collection name")
	private String authorCollectionName = "author";
	
	@Option(names = { "--db-book-collection" }, description = "Book collection name")
	private String bookCollectionName = "book";
	
	public static void main(String[] args) {
		new CommandLine(new MainApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				AuthorMongoRepository authorRepo = 
					new AuthorMongoRepository(new MongoClient(
							new ServerAddress(mongoHost, mongoPort)
					), databaseName, authorCollectionName
				);
				
				BookMongoRepository bookRepo = 
					new BookMongoRepository(new MongoClient(
						new ServerAddress(mongoHost, mongoPort)
					), databaseName, bookCollectionName
				);
				
				LibrarySwingView swingView = new LibrarySwingView();
				
				AuthorController authorController = 
					new AuthorController(swingView, authorRepo);
				
				BookController bookController = 
					new BookController(swingView, bookRepo);
				
				swingView.setAuthorController(authorController);
				swingView.setBookController(bookController);
				
				swingView.setVisible(true);
				
				authorController.allAuthors();
				bookController.allBooks();
						
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}
}
