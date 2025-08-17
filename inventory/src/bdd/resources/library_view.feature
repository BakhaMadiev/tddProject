Feature: Library Application Frame
	Specifications of the behavior of the Library Application Frame
	
	@initial
	Scenario: The initial state of the view 
		Given The database contains the authors with the following values
			| a1 | Leo   | Tolstoy   |
			| a2 | Dante | Alighieri |
		And The database contains the books with the following values
			| 1 | War and Peace | a1 | Leo 	 | Tolstoy 	 |
			| 2 | Inferno 			| a2 | Dante | Alighieri |
		When The Library View is shown 
		Then The Author List contains elements with the following values
			| a1 | Leo   | Tolstoy   |
			| a2 | Dante | Alighieri |
		And The Book List contains elements with the following values
			| 1 | War and Peace | a1 | Leo 	 | Tolstoy 	 |
			| 2 | Inferno 			| a2 | Dante | Alighieri |
			
	@author @add
	Scenario: Add a new Author
		Given The Library View is shown
		When The user enters the following values in the author text fields
			| Id | Name   | Surname    |
			| a3 | Fyodor | Dostoevsky |
		And The user clicks the "Add Author" button
		Then The Author List contains elements with the following values
			| a3 | Fyodor | Dostoevsky |
	
	@book @add
	Scenario: Add a new Book
		Given The database contains the authors with the following values
			| a4 | Italo | Calvino |
		And The Library View is shown
		When The user enters the following values in the book text fields
			| Id | Title            | Author           |
			| 3  | Invisible Cities | a4 Italo Calvino |
		And The user clicks the "Add Book" button
		Then The Book List contains elements with the following values
			| 3 | Invisible Cities | a4 | Italo | Calvino |
	
	@author @error @add
	Scenario: Add a new author with an existing id
		Given The database contains the authors with the following values
			| a4 | Italo | Calvino |
		And The Library View is shown
		When The user enters the following values in the author text fields
			| Id | Name                | Surname               |
			| a4 | Italo testing Error | Calvino Testing error |
		And The user clicks the "Add Author" button
		Then An "Author" error is shown containing the following error
			| a4 | Italo | Calvino |
	
	@book @error @add
	Scenario: Add a new Book with an existing id
		Given The database contains the authors with the following values
			| a2 | Dante | Alighieri |
		And The database contains the books with the following values
			| 2 | Inferno 			| a2 | Dante | Alighieri |
		And The Library View is shown
		When The user enters the following values in the book text fields
			| Id | Title            | Author           |
			| 2  | Inferno testing Error | a2 Dante Alighieri|
		And The user clicks the "Add Book" button
		Then An "Book" error is shown containing the following error				
			| 2 | Inferno 			| a2 | Dante | Alighieri |
			
	@author @delete
	Scenario: Delete an existing author from the List
		Given The database contains the authors with the following values
			| a2 | Dante     | Alighieri |
			| a3 | Alexander | Pushkin   |
		And The Library View is shown
		When The user selects the "Author" with id "a2"
		And The user clicks the "Delete Selected Author" button
		Then The "Author List" contains elements with the following values
			| a3 | Alexander | Pushkin   |
			
	@book @delete
	Scenario: Delete an existing book from the List
		Given The database contains the authors with the following values
			| a1 | Leo   | Tolstoy   |
			| a2 | Dante | Alighieri |
		And The database contains the books with the following values
			| 1 | War and Peace | a1 | Leo 	 | Tolstoy 	 |
			| 2 | Inferno 			| a2 | Dante | Alighieri |
		And The Library View is shown
		When The user selects the "Book" with id "1"
		And The user clicks the "Delete Selected Book" button
		Then The "Book List" contains elements with the following values
			| 2 | Inferno 			| a2 | Dante | Alighieri |
			
	@author @delete @error
	Scenario: Delete an non existing author
		Given The database contains the authors with the following values
			| a1 | Leo   | Tolstoy   |
			| a2 | Dante | Alighieri |
		And The Library View is shown
		When The "Author" with id "a1" is removed from the database
		And The user selects the "Author" with id "a1"
		And The user clicks the "Delete Selected Author" button
		Then An "Author" error is shown containing the following error
			| a1 | Leo   | Tolstoy   |
			
	@book @delete @error
	Scenario: Delete an non existing book
		Given The database contains the authors with the following values
			| a1 | Leo   | Tolstoy   |
			| a2 | Dante | Alighieri |
		And The database contains the books with the following values
			| 1 | War and Peace | a1 | Leo 	 | Tolstoy 	 |
			| 2 | Inferno 			| a2 | Dante | Alighieri |
		And The Library View is shown
		When The "Book" with id "1" is removed from the database
		And The user selects the "Book" with id "1"
		And The user clicks the "Delete Selected Book" button
		Then An "Book" error is shown containing the following error
			| 1 | War and Peace | a1 | Leo 	 | Tolstoy 	 |
		