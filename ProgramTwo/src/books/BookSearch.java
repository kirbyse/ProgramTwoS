package books;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class BookSearch {

	Connection conn;
	Statement stat;
	ResultSet rs;

	//Used by JDBC to connect to the assigned database.
	public void connect() throws Exception{
		Class.forName("org.sqlite.JDBC");
		conn =
				DriverManager.getConnection("jdbc:sqlite:Project2.db");
		stat = conn.createStatement();
		stat.execute("PRAGMA foreign_keys = ON;");
	}
	
	public boolean checkDate(int day, int month, int year) {
		if(year >= 2013 ) { //Can't have a future date
			return false;
		}
		if(month > 12 || month < 1) { //Months must be real
			return false;
		}
		if(day < 1) { //Days must be positive
			return false;
		}
		if(month == 1 || month == 3 || month ==5 || month == 7 || month == 8 || month == 10 || month == 12) { //Months with 31 days
			if(day > 31) {
				return false;
			}
		}
		if(month == 4 || month == 6 || month ==9 || month == 11) { //Months with 30 days
			if(day > 30) {
				return false;
			}
		}
		if(year % 4 != 0 || year % 100 == 0) { //Check if it is not a leap year
			if(month == 2 && day > 28) {
				return false;
			}
		}
		if(year % 4 == 0) { //Check if it is a leap year.
			if(month == 2 && day >29) {
				return false;
			}
		}
		return true;
		
	}


	//Adds a Book to the database with the following parameters as values for its attributes
	public void addBook(String title, int pDay, int pMonth, int pYear) {
		try {
			stat.execute("INSERT INTO BOOK VALUES ('" + title +
					"', " + pDay + ", " + pMonth + ", " +
					pYear + ", NULL);");
		} catch (SQLException e) {
			System.out.println("Database has too many Books. Autoincrement has reached the highest possible integer.");
		}
	}

	//Adds an Author to the database with the following parameters as values for its attributes
	public void addAuthor(String fName, String mName, String lName, int bDay, int bMonth, int bYear) {
		try {
			stat.execute("INSERT INTO AUTHOR VALUES ('" + fName +
					"', '" + mName + "', '" + lName + "', " + bDay + ", " + bMonth + ", " +
					bYear + ", NULL);");
		} catch (SQLException e) {
			System.out.println("Database has too many Authors. Autoincrement has reached the highest possible integer.");
		}
	}

	//Searches for all authors with a given first, middle, and last name and returns them all as an ArrayList of Authors
	//for easy outputting of messages
	public ArrayList<Author> searchAuthor(String fName, String mName, String lName){
		ArrayList<Author> authors = new ArrayList<Author>();
		try {
		rs = stat.executeQuery("SELECT FROM AUTHOR WHERE (fname = '" + fName + "')" +
				" AND (mName = '" + mName + "') AND (lName = '" + lName + "');");
		while (rs.next()) {
			authors.add(new Author(rs));
		}
		} catch(Exception err){
			
		}
		return authors;
	}

	//Searches for all books with a given title and returns them all as an ArrayList of Books
	//for easy outputting of messages
	public ArrayList<Book> searchBook(String title){
		ArrayList<Book> books = new ArrayList<Book>();
		try {
			rs = stat.executeQuery("SELECT FROM BOOK WHERE (title = '" + title + "');");
			while (rs.next()) {
				books.add(new Book(rs));
			}
		} catch (Exception err) {

		}
		return books;
	}

	//Returns all of the authors for a book given the book's ID
	public ArrayList<Author> getBookAuthors(int bookID){
		ArrayList<Author> authors = new ArrayList<Author>();
		try {
		rs = stat.executeQuery("SELECT * FROM AUTHOR AS A" +
				"WHERE A.authorID IN (SELECT authorID FROM AUTHORED WHERE (bookID = '" + bookID + "');");
		while (rs.next()) {
			authors.add(new Author(rs));
		}
		} catch(Exception err) {
			
		}
		return authors;
	}

	//Returns all of the books an author wrote given the author's ID
	public ArrayList<Book> getAuthorsBooks(int authorID){
		ArrayList<Book> books = new ArrayList<Book>();
		try {
		rs = stat.executeQuery("SELECT * FROM BOOK AS B" +
				"WHERE B.bookID IN (SELECT bookID FROM AUTHORED WHERE (authorID = '" + authorID + "');");
		while (rs.next()) {
			books.add(new Book(rs));
		}
		} catch (Exception err) {
			
		}
		return books;
	}

	//Removes a book from the database, also removes all AUTHORED tuples containing the bookID
	public void removeBook(int bookID) {
		try {
		rs = stat.executeQuery("DELETE FROM BOOK WHERE (bookID = '" + bookID + "');");
		} catch (Exception err){
			
		}
	}


}
