package entities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Book {

	// global variables
	private int isbn;
	private String book_title;
	private Date publication_date;
	private String genre_name;
	private List<String> authors = new ArrayList<String>();
	private int genre_id;

	/**
	 * Constructor
	 * 
	 * @param isbn
	 * @param book_title
	 * @param publication_date
	 * @param genre
	 */
	public Book(int isbn, String book_title, Date publication_date,
			String genre_name, int genre_id,List<String> authors) {
		this.isbn = isbn;
		this.book_title = book_title;
		this.publication_date = publication_date;
		this.genre_name = genre_name;
		this.authors = authors; //TODO Make deep copy
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Book [isbn=" + isbn + ", book_title=" + book_title
				+ ", publication_date=" + publication_date + ", genre_name="
				+ genre_name + ", authors=" + authors + ", genre_id="
				+ genre_id + "]";
	}
	
}
