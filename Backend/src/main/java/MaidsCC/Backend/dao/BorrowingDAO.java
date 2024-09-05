package MaidsCC.Backend.dao;

import org.springframework.http.ResponseEntity;

public interface BorrowingDAO {

	ResponseEntity<Object> borrowBook(int bookId, int patronId);

	ResponseEntity<Object> returnBook(int bookId, int patronId);

}
