/**
 * 
 */
package connection;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.math.BigInteger;
import java.sql.*;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Now we will methods for users to create new accounts and log in to the
 * database.
 * 
 * @author Tiffany
 *
 */
public class LibrarySecurity {

	private static SecureRandom random = new SecureRandom();
	private String dbuser = "CS1434872";
	private String dbname = "waldo2.dawsoncollege.qc.ca";
	private String dbpassword = "nickleck";

	/**
	 * This method generates a random string which can be used as salt for a
	 * password
	 * 
	 * @return String randomPassword
	 */
	public String getSalt() {
		return new BigInteger(130, random).toString(32);
	}

	/**
	 * This method generates a hash from the password and salt strings provided.
	 * 
	 * @param password
	 * @param salt
	 * @return byte[] hash
	 */
	public byte[] hash(String password, String salt) {
		try {
			SecretKeyFactory skf = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
					salt.getBytes(), 1024, 256);
			SecretKey key = skf.generateSecret(spec);
			byte[] hash = key.getEncoded();
			return hash;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Obtains a connection to the database using the dbusername, dbname and
	 * dbpassword.
	 * 
	 * @return Connection
	 */
	public Connection getConnection() throws SQLException {
		Connection c = null;
		c = DriverManager.getConnection(
				"jdbc:mysql://waldo2.dawsoncollege.qc.ca/" + dbname, dbuser,
				dbpassword);
		System.out.println("Connected to database");
		return c;
	}

	/**
	 * The first step is to write a method that takes as input a username and
	 * password, and adds an entry for that user to the database. Also, the
	 * password should be properly salted and hashed before it is stored! Note,
	 * we want to be protected against SQL Injection here, so make sure that you
	 * take proper precautions.
	 * 
	 * @param username
	 * @param password
	 */
	public void newUser(String username, String password) throws SQLException {

		if(checkUserInfo(username, password)) {
			System.out.println("Error: Username or Password is empty");
			return;
		} if (login(username, password)) {
			System.out.println("Error: User exists");
			return;
		}
		Connection conn = getConnection();

		String query = "INSERT INTO users VALUES (?, ?, ?);";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, username);
		stmt.setBytes(2, hash(password, getSalt()));
		stmt.setString(3, getSalt());
		stmt.executeUpdate();

		// Close connection
		if (stmt != null) {
			stmt.close();
		}

	}

	/**
	 * Creates an interactive form that asks for a username and password, then
	 * passes those as input the the
	 * {@code newUser(String username, String password) } method.
	 */
	public void newUser() throws SQLException {
		Scanner scan = new Scanner(System.in);

		// Get user info
		System.out
				.print("==Starting process for adding a new user==\nUserName:  ");
		String userName = scan.nextLine();
		System.out.print("Password:  ");
		String password = scan.nextLine();
		scan.close();
		
		newUser(userName,password);

	}

	/**
	 * Method that takes as input a username and password and determines if that
	 * user is valid or not. Recall, to determine if a user is valid, you will
	 * need to find that user’s salt, and compute a hash with their provided
	 * password, which you can then compare with the hash from the database. If
	 * it is a valid user, return true, if not, return false.
	 * 
	 * @param username
	 * @param password
	 * @return True if user is valid False if user is invalid
	 */
	public boolean login(String username, String password) throws SQLException {
		if (checkUserInfo(username,password)) {
			System.out.println("Error: Username or Password is empty");
			return false;
		}
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE userid=?;");
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();

	}

	/**
	 * interactive form that asks for a username and password, then passes those
	 * as input the {@code login(String username, String password) method},
	 * returning true if a user is valid or false otherwise.
	 * 
	 * @return
	 */
	public boolean login() throws SQLException {
		return false;

	}
	
	public boolean checkUserInfo(String username, String password) {
		if (username == null || username.trim().equals("") || password == null
				|| password.trim().equals("")) 
			return false;
		return true;
	}
}
