package entities;

import java.util.Optional;


public class Patron {
	// global variables
	private int id;
	private String firstname;
	private String lastname;
	private double fees;
	private String email;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param firstname
	 * @param lastname
	 * @param email
	 */
	public Patron(int id, String firstname, String lastname, String email) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}
}
