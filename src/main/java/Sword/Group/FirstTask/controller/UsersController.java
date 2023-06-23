package Sword.Group.FirstTask.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import Sword.Group.FirstTask.dao.UsersDAO;
import Sword.Group.FirstTask.exceptions.CustomException;
import Sword.Group.FirstTask.model.Users;
import Sword.Group.FirstTask.security.JwtService;

@RestController
public class UsersController {

	@Autowired // Instead ProductService service
	private UsersDAO eDAO; // Instead ProductService service

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/Users")
	public List<Users> getUsers() {
		return eDAO.getAll();
	}

	@GetMapping("/Users/{ID}")
	public Users getUserById(@PathVariable int ID) {
		return eDAO.getById(ID);
	}

	@PostMapping("/SaveUser")
	public ResponseEntity<Object> saveUser(@RequestBody Users user) {
		String result = eDAO.save(user);
		if (result.equals("User saved successfully.")) {
			return ResponseEntity.ok(result);
		} else if (result.equals("Email already exist.")) {
			CustomException exception = new CustomException(HttpStatus.FORBIDDEN.value(), "Email already exist",
					"You should use a different email.", "/SaveUser", ZonedDateTime.now());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception);
		} else {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Internal Server Error", "An error occurred while accessing the data, failed to save the user.",
					"/SaveUser", ZonedDateTime.now());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception);
		}
	}

	@PostMapping("/authenticate")
	public String authenticateAndGetToken(@RequestBody Users user) {
		// The authenticate method will fetch the userDetails from the Db and compare it
		// with the authRequest
//		System.out.println("User: " + user.getUsername());
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
//		System.out.println("Pass: " + authentication.getCredentials());
//		System.out.println("Pass: " + user.getPassword());

		if (authentication.isAuthenticated()) {
			return jwtService.generateToken(user.getUsername());
		} else {
			throw new UsernameNotFoundException("invalid user request !"); // Or wrong pasword
		}

	}

	@GetMapping("/AuthenticateUser")
	public ResponseEntity<Object> authenticateUser(@RequestBody Users user) {
		String result = eDAO.AuthenticateUser(user);

		if (result.equals("Correct credentials, you may sign in.")) {

			return ResponseEntity.ok(result);

		} else if (result.equals("Incorrect password.")) {

			CustomException exception = new CustomException(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
					"Incorrect password.", "/AuthenticateUser", ZonedDateTime.now());

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);

		} else if (result.equals("Email not found.")) {

			CustomException exception = new CustomException(HttpStatus.NOT_FOUND.value(), "Not Found",
					"Email not found.", "/AuthenticateUser", ZonedDateTime.now());

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception);

		} else {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),

					"Internal Server Error", "An error occurred while accessing the data.", "/AuthenticateUser",
					ZonedDateTime.now());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception);
		}
	}

}
