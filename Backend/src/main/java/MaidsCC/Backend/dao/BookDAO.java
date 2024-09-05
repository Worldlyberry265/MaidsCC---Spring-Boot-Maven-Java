package MaidsCC.Backend.dao;

import org.springframework.http.ResponseEntity;

import MaidsCC.Backend.model.Book;

public interface BookDAO {

	ResponseEntity<Object> getAllBooks();

	ResponseEntity<Object> getBook(int id);

	ResponseEntity<Object> addBook(Book book);

	ResponseEntity<Object> updateBook(int id, Book book);

	ResponseEntity<Object> deleteBook(int id);
	
}
