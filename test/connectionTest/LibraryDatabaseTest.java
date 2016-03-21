/**
 * 
 */
package connectionTest;

import java.sql.SQLException;

import connection.LibraryDatabase;
import entities.Patron;

/**
 * @author Tiffany
 *
 */
public class LibraryDatabaseTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LibraryDatabase database = new LibraryDatabase();
			//System.out.println(database.getBook(12348).toString());
			//database.bookReport();
			//database.newPatron();
			//database.renewBooks(p);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
