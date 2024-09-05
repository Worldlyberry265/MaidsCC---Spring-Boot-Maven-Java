package MaidsCC.Backend.dao.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import MaidsCC.Backend.dao.BorrowingDAO;
import MaidsCC.Backend.exceptions.CustomException;
import MaidsCC.Backend.exceptions.CustomExceptionHandler;

@Repository
public class BorrowingDAOImpl implements BorrowingDAO {

	@Autowired
	JdbcTemplate Jtemplate;

	CustomException exception = new CustomException();

	private void DoesBookPatronExist(int bookId, int patronId) {

		// Limit 1, so it doesn't keep searching the rest of the rows, since we are sure
		// there is only 1 instance with this id.
		String BookExistSQL = "Select * FROM Book where ID = ? LIMIT 1";
		Object book = Jtemplate.queryForObject(BookExistSQL, new BeanPropertyRowMapper<>(Object.class), bookId);

		String PatronExistSQL = "Select * FROM Patron where ID = ? LIMIT 1";
		Object patron = Jtemplate.queryForObject(PatronExistSQL, new BeanPropertyRowMapper<>(Object.class), patronId);

		// I'm not returning anything because the try catch will catch everytime these
		// queries doesn't return anything due
		// to the queryForObject throwing EmptyResultDataAccessException
	}

	@Transactional
	@Override
	public ResponseEntity<Object> borrowBook(int bookId, int patronId) {
		String error_msg = "";
		try {

			DoesBookPatronExist(bookId, patronId);

			// It's enough to find 1 borrow record of a book that isn't returned yet to
			// throw book is already booked error.
			String AvailableForBorrowingChecksql = "SELECT Book_Id from borrowing_record where Actual_return_date IS NULL AND Book_ID = ? LIMIT 1";
			List<Object> results = Jtemplate.query(AvailableForBorrowingChecksql,
					new BeanPropertyRowMapper<>(Object.class), bookId);
			if (!results.isEmpty()) {
				exception.ChangeException(HttpStatus.CONFLICT.value(), "Book Not Available",
						"Book with id " + bookId + " is already borrowed",
						"/api/borrow/" + bookId + "/patron/" + patronId, ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			String InsertSQL = "INSERT INTO borrowing_record (Book_ID, Patron_ID, Borrowing_date, Expected_return_date)"
					+ " VALUES (?,?, ?, ?)";

			// I'm assuming that the standard time to return a book is 15, that's why I'm
			// adding 15 to currentDate
			LocalDate currentDate = LocalDate.now();
			LocalDate expectedReturnDate = currentDate.plusDays(15);

			// Convert LocalDate to java.sql.Date
			Date sqlCurrentDate = Date.valueOf(currentDate);
			Date sqlExpectedReturnDate = Date.valueOf(expectedReturnDate);

			int insertResult = Jtemplate.update(InsertSQL, bookId, patronId, sqlCurrentDate, sqlExpectedReturnDate);
			if (insertResult > 0) {
				return ResponseEntity.ok("Congrats, you have borrowed the book successfully");
			}
		} catch (Exception e) {
			if (error_msg == "") {
				error_msg = "No book or patron with id " + bookId + " and " + patronId + " was found";
			}
			return CustomExceptionHandler.handleException(e, error_msg,
					"/api/borrow/" + bookId + "/patron/" + patronId);
		}
		return exception.toResponseEntity();
	}

	@Transactional
	@Override
	public ResponseEntity<Object> returnBook(int bookId, int patronId) {

		Date currentDate = new Date(System.currentTimeMillis());

		try {

			DoesBookPatronExist(bookId, patronId);

			// I return the book by only adding an actual return date.
			String updateSql = "UPDATE borrowing_record SET Actual_return_date = ? WHERE Book_id = ? AND Patron_Id = ? AND Actual_return_date IS NULL";

			int result = Jtemplate.update(updateSql, currentDate, bookId, patronId);

			if (result > 0) {
				return ResponseEntity.ok("Book Returned Successfully");

			} else {
				exception.ChangeException(HttpStatus.CONFLICT.value(),
						"Please check either the book ID or the patron ID",
						"The book is already returned, or one of the IDs is wrong",
						"/api/return/" + bookId + "/patron/" + patronId, ZonedDateTime.now());
				return exception.toResponseEntity();
			}

		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e,
					// If DoesBookPatronExist threw an error
					"No book or patron with id " + bookId + " and " + patronId + " was found",
					"/api/return/" + bookId + "/patron/" + patronId);
		}
	}
}
