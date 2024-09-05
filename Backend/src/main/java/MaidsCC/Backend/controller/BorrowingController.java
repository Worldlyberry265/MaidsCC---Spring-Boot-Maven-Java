package MaidsCC.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import MaidsCC.Backend.dao.BorrowingDAO;

@RestController
public class BorrowingController {

	@Autowired 
	private BorrowingDAO borrowingDAO;
	
	
	@PostMapping("/api/borrow/{bookId}/patron/{patronId}")
	public ResponseEntity<Object> borrowBook(@PathVariable int bookId, @PathVariable int patronId) {
		return borrowingDAO.borrowBook(bookId,patronId);
	}
	
	@PutExchange("/api/return/{bookId}/patron/{patronId}")
	public ResponseEntity<Object> returnBook(@PathVariable int bookId, @PathVariable int patronId) {
		return borrowingDAO.returnBook(bookId, patronId);
	}
	
}
