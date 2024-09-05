package MaidsCC.Backend.model;

import java.sql.Timestamp;

public class Borrowing_Record {

	private int Book_ID;
	private int Patron_ID;
	private Timestamp Borrowing_date;
	private Timestamp Expected_return_date;
	private Timestamp Actual_return_date;

	public Borrowing_Record(int book_ID, int patron_ID, Timestamp borrowing_date, Timestamp expected_return_date,
			Timestamp actual_return_date) {
		Book_ID = book_ID;
		Patron_ID = patron_ID;
		Borrowing_date = borrowing_date;
		Expected_return_date = expected_return_date;
		Actual_return_date = actual_return_date;
	}

	public int getBook_ID() {
		return Book_ID;
	}

	public int getPatrong_ID() {
		return Patron_ID;
	}

	public Timestamp getBorrowing_date() {
		return Borrowing_date;
	}

	public void setBorrowing_date(Timestamp borrowing_date) {
		Borrowing_date = borrowing_date;
	}

	public Timestamp getExpected_return_date() {
		return Expected_return_date;
	}

	public void setExpected_return_date(Timestamp expected_return_date) {
		Expected_return_date = expected_return_date;
	}

	public Timestamp getActual_return_date() {
		return Actual_return_date;
	}

	public void setActual_return_date(Timestamp actual_return_date) {
		Actual_return_date = actual_return_date;
	}

}
