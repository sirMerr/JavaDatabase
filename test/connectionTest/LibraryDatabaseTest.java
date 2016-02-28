/**
 * 
 */
package connectionTest;

import java.sql.SQLException;

import connection.LibraryDatabase;

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
			database.newPatron();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
