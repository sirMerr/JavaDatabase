package entities;

import java.util.Optional;

public class Patron {
	// global variables
	private int id;
	private String firstname;
	private String lastname;
	private int fees;
	private Optional<String> email;

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
		this.email = Optional.ofNullable(email);
	}

	public int getId() {
		return id;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public int getFees() {
		return fees;
	}

	public String getEmail() {
		return email.orElse(null);
	}

	public void setPatronId(int id) {
		this.id = id;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setFees(int fees) {
		this.fees = fees;
	}

	public void setEmail(String email) {
		this.email = Optional.ofNullable(email);
				}
}
