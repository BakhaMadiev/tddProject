package com.example.inventory.view.swing;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.example.inventory.controller.AuthorController;
import com.example.inventory.controller.BookController;
import com.example.inventory.model.Author;
import com.example.inventory.model.Book;
import com.example.inventory.view.AuthorView;
import com.example.inventory.view.BookView;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LibrarySwingView extends JFrame implements BookView, AuthorView{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField authorIdTextBox;
	private JTextField authorNameTextBox;
	private JTextField authorSurnameTextBox;
	private JTextField bookIdTextBox;
	private JTextField bookTitleTextBox;

	private JButton addBookButton;
	private JButton bookDeleteButton;
	private JButton addAuthorButton;
	private JButton authorDeleteButton;
	
	private JComboBox<Author> bookAuthorComboBox;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane;
	
	private JLabel authorHeadingLabel;
	private JLabel authorIdLabel;
	private JLabel authorNameLabel;
	private JLabel authorLastNameLabel;
	private JLabel authorErrorLabel;
	private JLabel bookHeadingLabel;
	private JLabel bookIdLabel;
	private JLabel bookTitleLabel;
	private JLabel bookAuthorLabel;
	private JLabel bookErrorLabel;

	private JList<Author> authorList;
	private DefaultListModel<Author> listAuthorModels;
	private JList<Book> bookList;
	private DefaultListModel<Book> listBookModels;
	
	private transient  AuthorController authorController;
	private transient BookController bookController;
	
	transient Thread lastAddAuthorThread;
	transient Thread lastAddBookThread;
	
	private int sleepMs = 1000;
	
	DefaultListModel<Author> getListOfAuthorModels(){
		return listAuthorModels;
	}
	
	DefaultListModel<Book> getListOfBookModels(){
		return listBookModels;
	}
	
	public void setAuthorController(AuthorController authorController) {
		this.authorController = authorController;
	}
	
	public void setBookController(BookController bookController) {
		this.bookController = bookController;
	}
	
	public void setAddSleepMs(int ms) {
		this.sleepMs = ms;
	}

	/**
	 * Create the frame.
	 */
	public LibrarySwingView() {
		setTitle("Library Inventory Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 707);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		authorHeadingLabel = new JLabel("Author");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(authorHeadingLabel, gbc_lblNewLabel);
		
		authorIdLabel = new JLabel("Author ID");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		contentPane.add(authorIdLabel, gbc_lblNewLabel_1);
		
		authorIdTextBox = new JTextField();
		authorIdTextBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addAuthorButton.setEnabled(
					!authorIdTextBox.getText().trim().isEmpty() && 
					!authorNameTextBox.getText().trim().isEmpty() && 
					!authorSurnameTextBox.getText().trim().isEmpty());
			}
		});
		authorIdTextBox.setName("authorIdTextBox");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		contentPane.add(authorIdTextBox, gbc_textField);
		authorIdTextBox.setColumns(10);
		
		authorNameLabel = new JLabel("Name");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		contentPane.add(authorNameLabel, gbc_lblNewLabel_2);
		
		authorNameTextBox = new JTextField();
		authorNameTextBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addAuthorButton.setEnabled(
					!authorNameTextBox.getText().trim().isEmpty() && 
					!authorSurnameTextBox.getText().trim().isEmpty() &&
					!authorIdTextBox.getText().trim().isEmpty());
			}
		});
		authorNameTextBox.setName("authorNameTextBox");
		authorNameTextBox.setColumns(10);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 2;
		contentPane.add(authorNameTextBox, gbc_textField_1);
		
		authorLastNameLabel = new JLabel("Last Name");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		contentPane.add(authorLastNameLabel, gbc_lblNewLabel_3);
		
		authorSurnameTextBox = new JTextField();
		authorSurnameTextBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addAuthorButton.setEnabled(
					!authorSurnameTextBox.getText().trim().isEmpty() &&
					!authorNameTextBox.getText().trim().isEmpty() &&
					!authorIdTextBox.getText().trim().isEmpty()
				);
			}
		});
		authorSurnameTextBox.setName("authorSurnameTextBox");
		authorSurnameTextBox.setColumns(10);
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 3;
		contentPane.add(authorSurnameTextBox, gbc_textField_2);
		
		addAuthorButton = new JButton("Add Author");
		addAuthorButton.addActionListener(
			e -> { 
				lastAddAuthorThread = new Thread(() -> {
					try {
						Thread.sleep(sleepMs);
					}catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					authorController.newAuthor(
						new Author(
							authorIdTextBox.getText(), 
							authorNameTextBox.getText(), 
							authorSurnameTextBox.getText())
					);
				});
			
				lastAddAuthorThread.start();
			}
		);
		addAuthorButton.setEnabled(false);
		addAuthorButton.setName("addAuthorButton");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 4;
		contentPane.add(addAuthorButton, gbc_btnNewButton);
		
		authorErrorLabel = new JLabel(" ");
		authorErrorLabel.setName("authorErrorMessageLabel");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_4.gridwidth = 2;
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 5;
		contentPane.add(authorErrorLabel, gbc_lblNewLabel_4);
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridwidth = 2;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 6;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		listAuthorModels = new DefaultListModel<>();
		authorList = new JList<>(listAuthorModels);
		authorList.addListSelectionListener(e ->
			authorDeleteButton.setEnabled(authorList.getSelectedIndex() != -1)
		);
		scrollPane_1.setViewportView(authorList);
		authorList.setName("authorList");
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		authorDeleteButton = new JButton("Delete Selected Author");
		authorDeleteButton.addActionListener(
			e -> authorController.deleteAuthor(authorList.getSelectedValue())
		);
		authorDeleteButton.setEnabled(false);
		authorDeleteButton.setName("authorDeleteButton");
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.gridwidth = 2;
		gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_2.gridx = 0;
		gbc_btnNewButton_2.gridy = 7;
		contentPane.add(authorDeleteButton, gbc_btnNewButton_2);
		
		bookHeadingLabel = new JLabel("Book");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_5.gridwidth = 2;
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 8;
		contentPane.add(bookHeadingLabel, gbc_lblNewLabel_5);
		
		bookIdLabel = new JLabel("Book ID");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 9;
		contentPane.add(bookIdLabel, gbc_lblNewLabel_6);
		
		bookIdTextBox = new JTextField();
		bookIdTextBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				Object selectedAuthor = bookAuthorComboBox.getSelectedItem();
				addBookButton.setEnabled(
						!bookIdTextBox.getText().trim().isEmpty() &&
						!bookTitleTextBox.getText().trim().isEmpty() &&
						selectedAuthor != null);
			}
		});
		bookIdTextBox.setName("bookIdTextBox");
		bookIdTextBox.setColumns(10);
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.insets = new Insets(0, 0, 5, 0);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 1;
		gbc_textField_3.gridy = 9;
		contentPane.add(bookIdTextBox, gbc_textField_3);
		
		bookTitleLabel = new JLabel("Book Title");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 10;
		contentPane.add(bookTitleLabel, gbc_lblNewLabel_7);
		
		bookTitleTextBox = new JTextField();
		bookTitleTextBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				Object selectedAuthor = bookAuthorComboBox.getSelectedItem();
				addBookButton.setEnabled(
						!bookIdTextBox.getText().trim().isEmpty() &&
						!bookTitleTextBox.getText().trim().isEmpty() &&
						selectedAuthor != null);
			}
		});
		bookTitleTextBox.setName("bookTitleTextBox");
		bookTitleTextBox.setColumns(10);
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.insets = new Insets(0, 0, 5, 0);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 1;
		gbc_textField_4.gridy = 10;
		contentPane.add(bookTitleTextBox, gbc_textField_4);
		
		bookAuthorLabel = new JLabel("Book Author");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 0;
		gbc_lblNewLabel_8.gridy = 11;
		contentPane.add(bookAuthorLabel, gbc_lblNewLabel_8);
		
		bookAuthorComboBox = new JComboBox<>();
		bookAuthorComboBox.addActionListener(e -> {
			Object selectedAuthor = bookAuthorComboBox.getSelectedItem();
			addBookButton.setEnabled(
				!bookIdTextBox.getText().trim().isEmpty() &&
				!bookTitleTextBox.getText().trim().isEmpty() &&
				selectedAuthor != null
			);
		});
		
		bookAuthorComboBox.setName("bookAuthorComboBox");
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 11;
		contentPane.add(bookAuthorComboBox, gbc_comboBox);
		
		addBookButton = new JButton("Add Book");
		addBookButton.addActionListener(
			e ->  { 
				lastAddBookThread = new Thread(() -> {
					try {
						Thread.sleep(sleepMs);
					}catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
						return;
					}
					bookController.newBook((
							new Book(
								bookIdTextBox.getText(), 
								bookTitleTextBox.getText(), 
								(Author) bookAuthorComboBox.getSelectedItem()
							)
					));
				});
				
				lastAddBookThread.start();
			}
		);
		addBookButton.setEnabled(false);
		addBookButton.setName("addBookButton");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 12;
		contentPane.add(addBookButton, gbc_btnNewButton_1);
		
		bookErrorLabel = new JLabel(" ");
		bookErrorLabel.setName("bookErrorMessageLabel");
		GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
		gbc_lblNewLabel_9.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_9.gridwidth = 2;
		gbc_lblNewLabel_9.gridx = 0;
		gbc_lblNewLabel_9.gridy = 13;
		contentPane.add(bookErrorLabel, gbc_lblNewLabel_9);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 14;
		contentPane.add(scrollPane, gbc_scrollPane);

		listBookModels = new DefaultListModel<>();
		bookList = new JList<>(listBookModels);
		bookList.addListSelectionListener(e ->
				bookDeleteButton.setEnabled(bookList.getSelectedIndex() != -1)
		);
		scrollPane.setViewportView(bookList);
		bookList.setName("bookList");
		bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		bookDeleteButton = new JButton("Delete Selected Book");
		bookDeleteButton.addActionListener(e -> bookController.deleteBook(bookList.getSelectedValue()));
		bookDeleteButton.setEnabled(false);
		bookDeleteButton.setName("bookDeleteButton");
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.gridwidth = 2;
		gbc_btnNewButton_3.gridx = 0;
		gbc_btnNewButton_3.gridy = 15;
		contentPane.add(bookDeleteButton, gbc_btnNewButton_3);
	}

	@Override
	public void showAllAuthors(List<Author> authors) {
		listAuthorModels.clear();
		authors.stream().forEach(listAuthorModels::addElement);
		updateBookAuthorComboBox();
	}

	@Override
	public void showError(String message, Author author) {
		SwingUtilities.invokeLater(() -> 
			authorErrorLabel.setText(message + ": " + author)
		);
	}

	@Override
	public void authorAdded(Author author) {
		SwingUtilities.invokeLater(() -> {
			listAuthorModels.addElement(author);
			updateBookAuthorComboBox();
			resetAuthorErrorLabel();			
		});
	}

	@Override
	public void authorRemoved(Author author) {
		listAuthorModels.removeElement(author);
		updateBookAuthorComboBox();
		resetAuthorErrorLabel();
	}

	@Override
	public void showAllBooks(List<Book> books) {
		books.stream().forEach(listBookModels::addElement);
	}

	@Override
	public void showError(String message, Book book) {
		SwingUtilities.invokeLater(() -> 
			bookErrorLabel.setText(message + ": " + book)		
		);
	}

	@Override
	public void bookAdded(Book book) {
		SwingUtilities.invokeLater(() -> {
			listBookModels.addElement(book);
			resetBookErrorLabel();
		});
	}

	@Override
	public void bookRemoved(Book book) {
		listBookModels.removeElement(book);
		resetBookErrorLabel();
	}
	
	public JComboBox<Author> getBookAuthorComboBox(){
		return bookAuthorComboBox;
	}
	
	private void updateBookAuthorComboBox() {
		bookAuthorComboBox.removeAllItems();
		for(int i = 0; i < listAuthorModels.size(); i++) {
			bookAuthorComboBox.addItem(listAuthorModels.get(i));
		}
	}
	
	private void resetAuthorErrorLabel() {
		authorErrorLabel.setText(" ");
	}
	
	private void resetBookErrorLabel() {
		bookErrorLabel.setText(" ");
	}

}
