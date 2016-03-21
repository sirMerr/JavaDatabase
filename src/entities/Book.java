package entities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Book {

	// global variables
	private int isbn;
	private String book_title;
	private Optional<Date> publication_date;
	private String genre;
	private List<String> authors = new ArrayList<String>();

	/**
	 * Constructor
	 * 
	 * @param isbn
	 * @param book_title
	 * @param publication_date
	 * @param genre
	 */
	public Book(int isbn, String bookTitle, Date pubDate, String genre,
			List<String> authors) {
		this.isbn = isbn;
		this.book_title = bookTitle;
		this.publication_date = Optional.ofNullable(pubDate);
		this.genre = genre;
	}

	public int getIsbn() {
		return isbn;
	}

	public String getBookTitle() {
		return book_title;
	}

	public Date getPubDate() {
		return publication_date.orElse(null);
	}

	public String getGenre() {
		return genre;
	}
	public List<String> getAuthors() {
		return authors;
		
	}

	public void setIsbn(int isbn) {
		this.isbn = isbn;
	}

	public void setBookTitle(String bookTitle) {
		this.book_title = bookTitle;
	}

	public void setPubDate(Date pubDate) {
		this.publication_date = Optional.ofNullable(pubDate);
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	/*
	public void setAuthors() {
		authors = null;
		TODO
	}
	*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Book [isbn=" + isbn + ", book_title=" + book_title
				+ ", publication_date=" + publication_date + ", genre=" + genre
				+ ", authors=" + authors + "]";
	}

}
