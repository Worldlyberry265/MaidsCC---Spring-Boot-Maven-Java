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

import MaidsCC.Backend.dao.PatronDAO;
import MaidsCC.Backend.model.Patron;

@RestController
public class PatronController {

	@Autowired
	private PatronDAO patronDAO;

	@GetMapping("/api/patrons")
	public ResponseEntity<Object> getAllPatrons() {
		return patronDAO.getAllPatrons();
	}

	@GetMapping("/api/patrons/{id}")
	public ResponseEntity<Object> getPatron(@PathVariable int id) {
		return patronDAO.getPatron(id);
	}

	@PostMapping("/api/patrons")
	public ResponseEntity<Object> addPatron(@RequestBody Patron patron) {
		return patronDAO.addPatron(patron);
	}

	@PutExchange("/api/patrons/{id}")
	public ResponseEntity<Object> updatePatron(@PathVariable int id, @RequestBody Patron patron) {
		return patronDAO.updatePatron(id, patron);
	}

	@DeleteMapping("/api/patrons/{id}")
	public ResponseEntity<Object> deletePatron(@PathVariable int id) {
		return patronDAO.deletePatron(id);
	}

	@PostMapping("/api/patron/login")
	public ResponseEntity<Object> authenticateAndGetToken(@RequestBody Patron patron) {
		return patronDAO.LoginAndGetToken(patron);
	}
}
