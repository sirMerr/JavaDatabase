package entities;

import java.sql.Date;

public class Book {

	// global variables
	private int isbn;
	private String book_title;
	private Date publication_date;
	private String genre;
	private String firstname, lastname;
	private int author_id;

	/**
	 * Constructor
	 * 
	 * @param isbn
	 * @param book_title
	 * @param publication_date
	 * @param genre
	 */
	public Book(int isbn, String book_title, Date publication_date,
			String genre, String firstname, String lastname, int author_id) {
		this.isbn = isbn;
		this.book_title = book_title;
		this.publication_date = publication_date;
		this.genre = genre;
		this.firstname = firstname;
		this.lastname = lastname;
		this.author_id = author_id;
	}
}
