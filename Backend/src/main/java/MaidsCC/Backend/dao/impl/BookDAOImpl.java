package MaidsCC.Backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import MaidsCC.Backend.dao.BookDAO;
import MaidsCC.Backend.exceptions.CustomException;
import MaidsCC.Backend.exceptions.CustomExceptionHandler;
import MaidsCC.Backend.model.Book;

@Repository
public class BookDAOImpl implements BookDAO {

	@Autowired
	JdbcTemplate Jtemplate;

	CustomException exception = new CustomException();

	private boolean ValidateInputString(String str) {
		String regexLettersAndSpaces = "^[A-Za-z ]*$"; // Only letters and spaces are allowed
		return str.matches(regexLettersAndSpaces);
	}

	private boolean ValidateInputISBN(String ISBN) {
		String regexLettersAndNumbers = "^[A-Za-z0-9]{13}$"; // Only letters and numbers are allowed
		return ISBN.matches(regexLettersAndNumbers);
	}

	private boolean ValidateInputPublicationYear(int year) {
		String regexYear = "^[12]\\d{3}$";// Can start only with either 1 or 2 and can have 3 more digits only
		return String.valueOf(year).matches(regexYear);
	}

	private int isISBNUnique(String ISBN) { // To check if the inputted ISBN is unique or already registered to another book.
		String sql = "SELECT ID FROM book WHERE ISBN = ? LIMIT 1";

		// I used query so it doesn't throw an EmptyResultDataAccessException.
		List<Integer> ids = Jtemplate.query(sql, (rs, rowNum) -> rs.getInt("ID"), ISBN);

		if (ids.isEmpty()) {
			return -1;
		} else {
			return ids.get(0);
		}
	}

	@Override
	public ResponseEntity<Object> getAllBooks() {

		String sql = "SELECT * from Book";

		// fetch and add every instance to the books List
		try {

			List<Book> books = Jtemplate.query(sql, (resultSet, rowNum) -> {
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				int publication_year = resultSet.getInt("publication_year");
				String author = resultSet.getString("author");
				String ISBN = resultSet.getString("isbn");

				Book book = new Book(id, title, publication_year, author, ISBN);
				return book;
			});
			return ResponseEntity.ok(books);

		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "Empty Database", "/api/books");
		}
	}

	@Override
	public ResponseEntity<Object> getBook(int id) {

		String sql = "SELECT * FROM book WHERE id = ? LIMIT 1";

		try {
			
			// it returns a single book instance and map it to book.
			Book book = Jtemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Book.class), id);
			return ResponseEntity.ok(book);

		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "No book with ID " + id + " was found",
					"/api/books/" + id);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> addBook(Book book) {

		// If any of the inputs are invalid we cancel the transaction and return an error.
		if (!(ValidateInputString(book.getAuthor()) && ValidateInputString(book.getTitle())
				&& ValidateInputISBN(book.getISBN()) && ValidateInputPublicationYear(book.getPublication_year()))) {
			exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "Check your input",
					" Only letters and spaces are allowed for the "
							+ "author's name, only letters and numbers are allowed for the ISBN and should be 13 digits exactly, and the publication year "
							+ "can start only with either 1 or 2 and can have 3 more digits only ",
					"/api/books", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

		try {

			// Can't add a book that has a duplicate ISBN
			if (isISBNUnique(book.getISBN()) != -1) {
				exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"A book with ISBN " + book.getISBN() + " is already added", "/api/books", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			// Use PreparedStatement to get the generated key from the database. (Mainly for testing purposes)
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int result = Jtemplate.update(con -> {
				PreparedStatement ps = con.prepareStatement(
						"INSERT INTO book (Title, Author, Publication_year, ISBN) VALUES (?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, book.getTitle());
				ps.setString(2, book.getAuthor());
				ps.setInt(3, book.getPublication_year());
				ps.setString(4, book.getISBN());
				return ps;
			}, keyHolder);

			if (result > 0) {
				Number key = keyHolder.getKey();
				return ResponseEntity.ok("Book Added with ID: " + key);
			}
		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "An error occurred while accessing the data",
					"/api/books");
		}
		return exception.toResponseEntity();

	}

	@Transactional
	@Override
	public ResponseEntity<Object> updateBook(int id, Book book) {

		// If any of the inputs are invalid we cancel the transaction and return an error.
		if (!(ValidateInputString(book.getAuthor()) && ValidateInputString(book.getTitle())
				&& ValidateInputISBN(book.getISBN()) && ValidateInputPublicationYear(book.getPublication_year()))) {

			exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST", "Check your input", "/api/books",
					ZonedDateTime.now());

			return exception.toResponseEntity();
		}

		try {
			int FetchedId = isISBNUnique(book.getISBN());
			// if it's empty, it will return -1, if the ISBN isn't updated, it will return the same id. 
			if (FetchedId != -1 && FetchedId != id) {
				exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "Wrong ISBN",
						"A book with ISBN " + book.getISBN() + " is already registered", "/api/books",
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			String updateSql = "UPDATE book SET author = ?, title = ?, publication_year = ? , ISBN = ? WHERE id = ?";

			int result = Jtemplate.update(updateSql, book.getAuthor(), book.getTitle(), book.getPublication_year(),
					book.getISBN(), id);

			if (result > 0) {
				return ResponseEntity.ok("Book Updated");

			} else {
				exception.ChangeException(HttpStatus.NOT_FOUND.value(), "Book Not Found",
						"A book with ID " + id + " couldn't be found", "/api/books", ZonedDateTime.now());
				return exception.toResponseEntity();
			}
		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "An error occurred while accessing the data",
					"/api/books");
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> deleteBook(int id) {

		String deleteSql = "DELETE FROM book WHERE id = ?";

		try {
			int result = Jtemplate.update(deleteSql, id);

			if (result > 0) {
				return ResponseEntity.ok("Book deleted successfully");
			} else {
				exception.ChangeException(HttpStatus.NOT_FOUND.value(), "Book Not Found",
						"A book with ID " + id + " couldn't be found", "/api/books", ZonedDateTime.now());
				return exception.toResponseEntity();
			}
		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "An error occurred while accessing the data",
					"/api/books");
		}
	}

}
