package connection;

import entities.Book;
import entities.Patron;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
			int portNumber) {
		this.userName = userName;
		this.password = password;
		this.serverName = serverName;
		this.portNumber = portNumber;

		conn = getConnection();
	}

	/**
	 * Helper method that acquires a connection to the server.
	 */
	public Connection getConnection() {

		Connection conn = null;
		String user = this.userName;
		String password = this.password;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://"
					+ this.serverName + ":" + this.portNumber + "/" + user,
					user, password);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error connecting to database");
		}

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
	public Book getBook(int isbn) {

		Statement stmt = null;
		Book book = null;
		String title = "";
		Date pubdate = null;
		String genre_name = "";
		List<String> authors = new ArrayList<String>();

		// open connection
		try {
			stmt = conn.createStatement();

			String query = "SELECT book_title,publication_date,genre_name,firstname,lastname FROM book "
					+ "INNER JOIN book_authors ON isbn = book "
					+ "INNER JOIN author ON author = author_id "
					+ "INNER JOIN genre ON genre = genre_id "
					+ "WHERE isbn = "
					+ isbn;
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				title = rs.getString("book_title");
				pubdate = rs.getDate("publication_date");
				genre_name = rs.getString("genre_name");
				authors.add(rs.getString("firstname"));
				authors.add(rs.getString("lastname"));
			}

			book = new Book(isbn, title, pubdate, genre_name, authors);

			// close connection
			if (stmt != null) {
				stmt.close();
			}
			return book;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error getting book");
		}

		return book;

	}

	/**
	 * This method should print to the screen a list of all books, including all
	 * information about these books. Book title, isbn, author(s) first and last
	 * names, genre and publication date should all be formatted in a readable
	 * fashion and displayed to the screen.
	 */
	public void bookReport() {
		Statement stmt = null;
		int isbn = -1;
		String title = "";
		Date pubdate = null;
		int genre_id = -1;
		String genre_name = "";
		String firstname = "";
		String lastname = "";

		// open connection
		try {
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
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error displaying book report");
		}

	}

	/**
	 * This method should prompt the user for input from the keyboard to create
	 * a new patron record, and add that information to the database. It should
	 * prompt for a first name, a last name, and optionally an email address.
	 */
	public void newPatron() {
		int patron_id = -1;
		Scanner scan = new Scanner(System.in);

		// Get patron info
		System.out
				.print("==Starting process for adding a new patron record==\nFirst Name:  ");
		String firstname = scan.nextLine();
		System.out.print("Last name:  ");
		String lastname = scan.nextLine();
		System.out.print("Email(optional):  ");
		String email = scan.nextLine();

		scan.close();

		// Open connection
		Statement stmt = null;
		try {
			stmt = conn.createStatement();

			// Get next patron_id
			String query = "SELECT MAX(patron_id) FROM patron";
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				patron_id = rs.getInt("MAX(patron_id)") + 1;
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error setting patron_id");
		}

		// Check if patron already exists
		String query = "SELECT firstname, lastname, email " + "FROM patron "
				+ "WHERE firstname=\"" + firstname + "\" AND lastname=\""
				+ lastname + "\";";
		try {
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				System.out.println("Patron already exists");

				// Close connection
				if (stmt != null) {
					stmt.close();
				}
				return;
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error validating patron name");
		}
		// Patron does not exist
		query = "INSERT INTO patron VALUES (?, ?, ?, 0, ?);";
		try {
			PreparedStatement addPatron = conn.prepareStatement(query);
			addPatron.setInt(1, patron_id);
			addPatron.setString(2, firstname);
			addPatron.setString(3, lastname);
			addPatron.setString(4, email);
			addPatron.execute();

			// Close connection
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());

			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error adding patron");
			return;
		}

	}

	/**
	 * This method should prompt the user for necessary input from the keyboard,
	 * and add that information to the database. The librarian should be able to
	 * enter the book title, isbn, author(s) first and last names, genre,
	 * publication date.
	 */
	@SuppressWarnings("deprecation")
	public void newBook() {
		boolean addAuthors = true;
		int counter = 0;
		Statement stmt = null;
		Scanner scan = new Scanner(System.in);
		String title = null, genre = null, firstname = null, lastname = null;
		int isbn;
		int pubYear = 0, pubMonth = 0, pubDay = 0;
		List<String> authors = new ArrayList<String>();

		// Start book creation
		System.out.println("==Starting Book Creation Process==");

		// Get title
		System.out.println("Enter a book title: ");
		title = scan.nextLine();
		if (title.length() <= 0) {
			throw new IllegalArgumentException("Error: Enter a title");
		}

		// Get isbn
		System.out.println("Enter an isbn (5 digits): ");
		isbn = scan.nextInt();
		if (isbn < 10000 || isbn > 99999) {
			throw new IllegalArgumentException(
					"Error: Out of range 10000 and 99999");
		}

		// Get genre
		System.out.println("Enter a genre: ");
		genre = scan.nextLine();
		if (genre.length() <= 0 || genre.matches(".*[0-9].*")) {
			throw new IllegalArgumentException("Error: Invalid genre name");
		}

		// Get pubdate
		System.out.println("Enter a publication year (yyyy): ");
		pubYear = scan.nextInt();
		if (pubYear < 1000 || pubYear > 9999) {
			throw new IllegalArgumentException("Error: Invalid year");
		}
		System.out.println("Enter a publication month (mm): ");
		if (pubMonth < 1 || pubMonth > 12) {
			throw new IllegalArgumentException("Error: Invalid month");
		}
		System.out.println("Enter a publication year (dd): ");
		if (pubDay < 1 || pubDay > 31) {
			throw new IllegalArgumentException("Error: Invalid day");
		}
		Date date = new Date(pubYear, pubMonth, pubDay);

		// Add authors or n to quit
		while (addAuthors || authors.size() == 0) {
			System.out.println("Enter the author name");
			authors.add(scan.nextLine());
			if (authors.get(counter).length() <= 0) {
				throw new IllegalArgumentException("Error: Invalid name");
			}

			System.out.println("Would you like to add more? (y/n)");
			if (!scan.nextLine().equals("y")) {
				addAuthors = false;
			}
		}

		// Make book object from info
		newBook(new Book(isbn, title, date, genre, authors));

	}

	/**
	 * Another method for adding books to the library, but this time take the
	 * Book object you created earlier as a parameter.
	 * 
	 * @param b
	 *            Book to add to library
	 */
	public void newBook(Book b) {
		Statement stmt = null;
		// Open connection
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			int id = 0;
			ResultSet rs;

			// Check if it exists
			rs = stmt
					.executeQuery("SELECT genre_id FROM genre WHERE genre_name = '"
							+ b.getGenre() + "';");
			if (!rs.next()) {
				stmt.execute("INSERT INTO genre (genre_name) VALUES ('" + b.getGenre()
						+ "');");
				rs = stmt
						.executeQuery("SELECT genre_id FROM genre WHERE genre_name = '"
								+ b.getGenre() + "';");
				rs.next();
			}
			id = rs.getInt("genre_id");

			// Check if the author exists
			for (int i = 0; i < b.getAuthors().size(); i++) {
				rs = stmt
						.executeQuery("SELECT CONCAT(firstname,' ',lastname) AS name FROM author "
								+ "WHERE name = '" + b.getAuthors().get(i) + "';");
				if (!rs.next()) {
					stmt.execute("INSERT INTO author (firstname, lastname) VALUES ('"
							+ b.getAuthors().get(i).substring(0,
									b.getAuthors().get(i).indexOf(" "))
							+ "','"
							+ b.getAuthors().get(i).substring(
									b.getAuthors().get(i).indexOf(" ") + 1) + "');");
				}
			}

			// Check for repeating Book
			rs = stmt
					.executeQuery("SELECT isbn FROM book WHERE isbn = " + b.getIsbn());
			if (!rs.next()) {
				stmt.execute("INSERT INTO book VALUES (" + b.getIsbn() + ",'" + b.getBookTitle()
						+ "','" + b.getPubDate() + "'," + id + ");");
			} else
				System.out.println("Book already exists");

			// Bridge
			for (int i = 0; i < b.getAuthors().size(); i++) {
				rs = stmt
						.executeQuery("SELECT author_id FROM author WHERE firstname = '"
								+ b.getAuthors().get(i).substring(0,
										b.getAuthors().get(i).indexOf(" "))
								+ "' AND lastname = '"
								+ b.getAuthors().get(i).substring(
										b.getAuthors().get(i).indexOf(" ") + 1) + "';");
				rs.next();
				id = rs.getInt("author_id");
				rs = stmt
						.executeQuery("SELECT * from book_authors WHERE book = "
								+ b.getIsbn() + " AND author = " + id + ";");
				if (!rs.next()) {
					stmt.execute("INSERT INTO book_authors VALUES (" + id + ","
							+ b.getIsbn() + ")");
				}
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error finding book");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					System.out.println("SQLException: " + e.getMessage());
					System.out.println("ErrorCode: " + e.getErrorCode());
					System.out.println("Error closing connection");
				}
			}
		}
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
	 * 
	 */
	public void loan(Book b, Patron p) {
		int loanId = -1;
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT COUNT(loan_id) FROM book_loan;";
			ResultSet rs = stmt.executeQuery(query);

			rs.next();
			loanId = rs.getInt(1) + 1;
			// close connection
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error getting loans");
		}

		Date date = Date.valueOf(LocalDate.now().plusWeeks(2));

		try {
			PreparedStatement stmt = conn
					.prepareStatement("INSERT INTO book_loan VALUES (?, ?, ?, ?, 0);");
			stmt.setInt(1, loanId);
			stmt.setInt(2, p.getId());
			stmt.setInt(3, b.getIsbn());
			stmt.setDate(4, date);
			stmt.execute();
			// close connection
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error executing stmt");
		}

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

		try {
			PreparedStatement stmt = conn
					.prepareStatement("UPDATE book_loan SET returned=1 "
							+ "WHERE book=? AND returned=0;");
			stmt.setInt(1, b.getIsbn());
			stmt.execute();
			System.out.println("Successfully returned book");
			// close connection
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("Error returning book");
			return;
		}

		int fees = checkFee(b);
		int id = 0;
		ResultSet rs = null;

		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT * FROM book_loan WHERE book=?;");
			stmt.setInt(1, b.getIsbn());
			rs = stmt.executeQuery();
			rs.next();
			id = rs.getInt(1);
			// close connection
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error fetching the Patron ID");
			return;
		}

		Patron p = new Patron(id, null, null, null);
		setFee(p, fees);
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
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("SELECT * " + "FROM book_loan "
					+ "WHERE returned=0 AND patron_id=?;");

			stmt.setInt(1, p.getId());
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error occurred executing stmt");
		}

		try {
			if (rs == null || !rs.next()) {
				return;
			}

			rs.beforeFirst();
			while (rs.next()) {
				Book book = null;
				book = new Book(rs.getInt(3), null, null, null, null);

				int fees = checkFee(book);
				setFee(p, fees);
				rs.updateDate(4, Date.valueOf(LocalDate.now().plusWeeks(2)));
				rs.updateRow();
			}
			// close connection
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error renewing the books");
			return;
		}
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
	 * @return list of recommended books
	 */
	public List<Book> recommendBook(Patron p) {
		List<Book> recommended = new ArrayList<Book>();
		ResultSet rs = null;
		Statement stmt = null;
		int isbn;

		try {
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("SELECT DISTINCT isbn, book_title, publication_date, genre_name, returned "
							+ "FROM book "
							+ "INNER JOIN genre ON genre=genre_id "
							+ "LEFT OUTER JOIN book_loan ON isbn=book "
							+ "WHERE genre = "
							+ "(SELECT genre FROM book "
							+ "INNER JOIN book_loan ON isbn=book "
							+ "INNER JOIN patron ON patron.patron_id=book_loan.patron_id "
							+ "WHERE patron.patron_id ="
							+ p.getId()
							+ "GROUP BY genre "
							+ "ORDER BY COUNT(genre) DESC) "
							+ "HAVING returned IN (1) OR returned IS NULL;");

			while (rs.next()) {
				isbn = rs.getInt("isbn");
				recommended.add(getBook(isbn));
			}
			// close connection
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error fetching the patrons books");
		}

		return recommended;

	}

	/**
	 * Returns a book's fee
	 * 
	 * @param book
	 * @return int fee
	 */
	private int checkFee(Book book) {
		int fees = 0;
		LocalDate dateDue = null;
		LocalDate now = LocalDate.now();
		int diff = 0;
		int yearDiff = now.getYear(), dayDiff = now.getDayOfYear();

		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT due_date FROM book_loan WHERE book=?;");
			ResultSet rs = null;
			stmt.setInt(1, book.getIsbn());
			rs = stmt.executeQuery();
			rs.next();
			dateDue = rs.getDate(1).toLocalDate();

			// close connection
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error fetching the due date");
		}

		if (dateDue.isBefore(now)) {
			yearDiff -= dateDue.getYear();
			dayDiff -= dateDue.getDayOfYear();

			if (yearDiff > 0)
				diff += yearDiff * 365;

			fees += (diff + dayDiff) * 5;
		}

		return fees;
	}

	/**
	 * Sets the fees in the Patron object and in the database
	 * 
	 * @param patron
	 * @param fees
	 * 
	 */
	private void setFee(Patron patron, int fees) {
		fees += patron.getFees();

		try {
			PreparedStatement stmt = conn
					.prepareStatement("UPDATE patron SET fees=? WHERE patron_id=?;");
			stmt.setInt(1, fees);
			stmt.setInt(2, patron.getId());

			stmt.execute();

			// close connection
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			System.out.println("Error setting fees");
		}
	}

}
