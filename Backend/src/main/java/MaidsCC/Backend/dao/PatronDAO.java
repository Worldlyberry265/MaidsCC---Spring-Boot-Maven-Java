package MaidsCC.Backend.dao;

import org.springframework.http.ResponseEntity;

import MaidsCC.Backend.model.Patron;

public interface PatronDAO {

	ResponseEntity<Object> getAllPatrons();

	ResponseEntity<Object> getPatron(int id);

	ResponseEntity<Object> addPatron(Patron patron);

	ResponseEntity<Object> updatePatron(int id, Patron patron);

	ResponseEntity<Object> deletePatron(int id);

	ResponseEntity<Object> LoginAndGetToken(Patron patron);

}
