package connection;

import entities.Book;
import entities.Patron;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LibraryDatabase {

	// global variables
	private String userName = "CS1434872";
	private String password = "nickleck";
	private String serverName = "waldo2.dawsoncollege.qc.ca";
	private int portNumber = 3306;
	private Connection conn;

	/**
	 * Default Constructor
	 */
	public LibraryDatabase() throws SQLException {
		conn = getConnection();
	}

	/**
	 * Constructor with initiated with values
	 * 
	 * @param username
	 * @param password
	 * @param serverName
	 * @param portNumber
	 */
	public LibraryDatabase(String userName, String password, String serverName,
			int portNumber) throws SQLException {
		this.userName = userName;
		this.password = password;
		this.serverName = serverName;
		this.portNumber = portNumber;

		conn = getConnection();
	}

	/**
	 * Helper method that acquires a connection to the server.
	 */
	public Connection getConnection() throws SQLException {

		Connection conn = null;
		String user = this.userName;
		String password = this.password;

		conn = DriverManager.getConnection("jdbc:mysql://" + this.serverName
				+ ":" + this.portNumber + "/" + user, user, password);

		return conn;
	}

	/**
	 * Takes as input an isbn, and creates a Book object (as you created
	 * earlier) using information from the database.
	 * 
	 * @param int isbn
	 * 
	 * @return Book corresponding with isbn. returns null if nonexistent
	 */
	public Book getBook(int isbn) throws SQLException {

		Statement stmt = null;
		Book book = null;
		String title = "";
		Date pubdate = null;
		int genre_id = -1;
		String genre_name = "";
		List<String> authors = new ArrayList<String>();

		// open connection
		stmt = conn.createStatement();

		String query = "SELECT book_title,publication_date,genre_name, genre_id ,firstname,lastname FROM book "
				+ "INNER JOIN book_authors ON isbn = book "
				+ "INNER JOIN author ON author = author_id "
				+ "INNER JOIN genre ON genre = genre_id "
				+ "WHERE isbn = "
				+ isbn;
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			title = rs.getString("book_title");
			pubdate = rs.getDate("publication_date");
			genre_id = rs.getInt("genre_id");
			genre_name = rs.getString("genre_name");
			authors.add(rs.getString("firstname"));
			authors.add(rs.getString("lastname"));
		}

		book = new Book(isbn, title, pubdate, genre_name, genre_id, authors);

		// close connection
		if (stmt != null) {
			stmt.close();
		}

		return book;

	}

	/**
	 * This method should print to the screen a list of all books, including all
	 * information about these books. Book title, isbn, author(s) first and last
	 * names, genre and publication date should all be formatted in a readable
	 * fashion and displayed to the screen.
	 */
	public void bookReport() throws SQLException {
		Statement stmt = null;
		int isbn = -1;
		String title = "";
		Date pubdate = null;
		int genre_id = -1;
		String genre_name = "";
		String firstname = "";
		String lastname = "";

		// open connection
		stmt = conn.createStatement();

		String query = "SELECT isbn,book_title,publication_date,genre_name, genre_id ,firstname,lastname FROM book "
				+ "INNER JOIN book_authors ON isbn = book "
				+ "INNER JOIN author ON author = author_id "
				+ "INNER JOIN genre ON genre = genre_id ";
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			isbn = rs.getInt("isbn");
			title = rs.getString("book_title");
			pubdate = rs.getDate("publication_date");
			genre_id = rs.getInt("genre_id");
			genre_name = rs.getString("genre_name");
			firstname = rs.getString("firstname");
			lastname = rs.getString("lastname");

			System.out
					.printf("isbn: %d | Title: %s | Publication Date: %s | genre_id: %d | genre_name %s | Author Name: %s %s\n",
							isbn, title, pubdate, genre_id, genre_name,
							firstname, lastname);
		}

		// close connection
		if (stmt != null) {
			stmt.close();
		}

	}

	/**
	 * This method should prompt the user for input from the keyboard to create
	 * a new patron record, and add that information to the database. It should
	 * prompt for a first name, a last name, and optionally an email address.
	 */
	public void newPatron() throws SQLException{
		Scanner scan = new Scanner(System.in);
		System.out
				.print("==Starting process for adding a new patron record==\nFirst Name:  ");
		String firstname = scan.nextLine();
		System.out.print("Last name:  ");
		String lastname = scan.nextLine();
		System.out.print("Email(optional):  ");
		String email = scan.nextLine();

		scan.close();
		
		Statement stmt = null;
		stmt = conn.createStatement();

		String query = "SELECT MAX(patron_id) FROM patron";
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			int patron_id = rs.getInt("patron_id") + 1;
		}
		
		if (stmt != null) {
			stmt.close();
		}
		
		
	}

	/**
	 * This method should prompt the user for necessary input from the keyboard,
	 * and add that information to the database. The librarian should be able to
	 * enter the book title, isbn, author(s) first and last names, genre,
	 * publication date.
	 */
	public void newBook() {

	}

	/**
	 * Another method for adding books to the library, but this time take the
	 * Book object you created earlier as a parameter.
	 * 
	 * @param b
	 *            Book to add to library
	 */
	public void newBook(Book b) {

	}

	/**
	 * The loan method should take a Book and a Patron, and create a new entry
	 * in the book_loan table.
	 * 
	 * The due date should be two weeks from the current date, and the returned
	 * field should be set to false (since the book is just now being loaned
	 * out).
	 * 
	 * This will require using java.sql.Date objects
	 * 
	 * @param b
	 *            New Book to enter in table
	 * @param p
	 *            New Patron to enter in table
	 */
	public void loan(Book b, Patron p) {

	}

	/**
	 * Updates the book_loan table such that the returned field is set to true.
	 * 
	 * If the book is late (it is past the due date), we calculate and assign a
	 * late fee to the Patron of 5 cents per day past the due date the book was
	 * returned.
	 * 
	 * @param b
	 *            Book being returned
	 */
	public void returnBook(Book b) {

	}

	/**
	 * A method that allows a librarian to easily renew all the books currently
	 * on loan by a particular patron. All books currently on loan with the
	 * patron should have their due date extended by two weeks. In addition, all
	 * late fees should be assessed, and added to the fees owed by the Patron
	 * prior to updating.
	 * 
	 * @param p
	 *            Patron whose books will be renewed
	 */
	public void renewBooks(Patron p) {

	}

	/**
	 * This recommender takes as input a Patron p, and then looks through the
	 * book_loan table to find the books the patron has taken out in the past.
	 * It assess which genres came up most often, and returns a list of the
	 * books available (not on loan by anyone) in that genre that the Patron has
	 * not yet read.
	 * 
	 * @param p
	 *            Patron whom we will be recommending books to
	 */
	public void recommendBook(Patron p) {

	}

}
