package MaidsCC.Backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import MaidsCC.Backend.dao.BookDAO;
import MaidsCC.Backend.model.Book;

@RestController
public class BookController {
	
	@Autowired 
	private BookDAO bookDAO; 
		
	@GetMapping("/api/books")
	public ResponseEntity<Object> getAllBooks() {
		return bookDAO.getAllBooks();
	}
	
	@GetMapping("/api/books/{id}")
	public ResponseEntity<Object> getBook(@PathVariable int id) {
		return bookDAO.getBook(id);
	}
	
	@PostMapping("/api/books")
	public ResponseEntity<Object> addBook(@RequestBody Book book) {
		return bookDAO.addBook(book);
	}
	
	@PutExchange("/api/books/{id}")
	public ResponseEntity<Object> updateBook(@PathVariable int id, @RequestBody Book book) {
		return bookDAO.updateBook(id, book);
	}
	
	@DeleteMapping("/api/books/{id}")
	public ResponseEntity<Object> deleteBook(@PathVariable int id) {
		return bookDAO.deleteBook(id);
	}
	
}
