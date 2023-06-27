package Sword.Group.FirstTask.dao.impl;

import java.util.List;
import java.util.Map;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import Sword.Group.FirstTask.dao.UsersDAO;
import Sword.Group.FirstTask.exceptions.CustomException;
import Sword.Group.FirstTask.model.Role;
import Sword.Group.FirstTask.model.Users;
import Sword.Group.FirstTask.security.JwtService;

@Repository
public class UsersDAOImpl implements UsersDAO {

	@Autowired
	JdbcTemplate Jtemplate;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public ResponseEntity<Object> save(Users user) {
		try {
			// Validate input for special characters
			if (!isUsernametValid(user.getUsername())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Username, Only Letters and spaces are allowed.", "/SaveUser", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			// Validate input for special characters
			if (!isPassValid(user.getPassword())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Password, Only letters, numbers, and !@#$%^&* are allowed", "/SaveUser",
						ZonedDateTime.now());
				return exception.toResponseEntity();
//				throw new IllegalArgumentException(
//						"INVALID Password! ONLY LETTERS, NUMBERS, AND !@#$%^&* ARE ALLOWED.");
			}

			// Validate input for special characters
			if (!isEmailValid(user.getEmail())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Email, Only letters, numbers, and .-_ are allowed", "/SaveUser", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			String hashedPassword = passwordEncoder.encode(user.getPassword());

			// Check if the email is unique
			boolean isEmailUnique = isEmailUnique(user.getEmail());
			if (!isEmailUnique) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Email already exists, You should use a different email.", "/SaveUser", ZonedDateTime.now());
//				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception);
				return exception.toResponseEntity();
			}

			int result = Jtemplate.update("INSERT INTO users (Username, Password, Email) VALUES (?, ?, ?)",
					user.getUsername(), hashedPassword, user.getEmail());

			if (result > 0) {
				return ResponseEntity.ok("User Saved");
			}
		} catch (DataAccessException e) {
			// Exception occurred while accessing the data
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "An error occurred while accessing the data", "/SaveUser",
					ZonedDateTime.now());
			return exception.toResponseEntity();

		}
		CustomException exception = new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), // Another other value?
				"SERVICE_UNAVAILABLE", "An error occurred while accessing the data", "/SaveUser", ZonedDateTime.now());
		return exception.toResponseEntity();

	}

	// All the regexes below reduce significantly the percentage of successful sql
	// injections by not allowing semicolons and quotes

	private boolean isUsernametValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^[A-Za-z ]+$"; // Regular expression that only allows alphabets and spaces
		return input.matches(regex);
	}

	private boolean isPassValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^[A-Za-z0-9!@#$%^&*]+$"; // Regular expression that only allows alphabets, numbers, and special
													// // characters
		return input.matches(regex);
	}

	private boolean isEmailValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^[A-Za-z0-9._-]+@[A-Za-z.]+\\.[A-Za-z]{2,}$"; // Regular expression for an email
		return input.matches(regex);
	}

	private boolean isEmailUnique(String email) { // To search if the email is already used
		String sql = "SELECT COUNT(*) FROM users WHERE Email = ? LIMIT 1";
		int count = Jtemplate.queryForObject(sql, Integer.class, email);
		return count == 0; // if not found return true, else if count = 1 return false
	}

	@Override
	public List<Users> getAll() {
		return Jtemplate.query("SELECT * FROM users", new BeanPropertyRowMapper<Users>(Users.class));
	}

	@Override
	public Users getById(int ID) {
		return Jtemplate.queryForObject("SELECT * FROM users WHERE ID=?", new BeanPropertyRowMapper<Users>(Users.class),
				ID);
	}

	@Override
	public ResponseEntity<Object> AuthenticateUser(Users user) {
//	    String sql = "SELECT COUNT(*) FROM users WHERE Email = ? LIMIT 1";
		String sql2 = "SELECT Password FROM users WHERE Email = ? LIMIT 1";

		// Validate input for special characters
		if (!isEmailValid(user.getEmail())) {
			CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
					"Invalid Email, Only letters, numbers, and .-_ are allowed", "/AuthenticateUser",
					ZonedDateTime.now());
			return exception.toResponseEntity();
		}

		// Validate input for special characters
		if (!isPassValid(user.getPassword())) {
			CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
					"Invalid Paasword, Only letters, numbers, and !@#$%^&* are allowed", "/AuthenticateUser",
					ZonedDateTime.now());
			return exception.toResponseEntity();
		}

		try {

			String retrievedPassword = Jtemplate.queryForObject(sql2, String.class, user.getEmail());
			if (retrievedPassword != null && passwordEncoder.matches(user.getPassword(), retrievedPassword)) {
				return ResponseEntity.ok("You may sign in");
			} else if (retrievedPassword != null) {
				CustomException exception = new CustomException(HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED",
						"You have entered a wrong password", "/AuthenticateUser", ZonedDateTime.now());
				return exception.toResponseEntity();
			} else {
				CustomException exception = new CustomException(HttpStatus.NOT_FOUND.value(), "NOT_FOUND",
						"Please check your email and try again", "/AuthenticateUser", ZonedDateTime.now());
				return exception.toResponseEntity();
			}
		} catch (DataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "An error occurred while accessing the data", "/AuthenticateUser",
					ZonedDateTime.now());
			return exception.toResponseEntity();
		}
	}

	@Override
	public ResponseEntity<Object> authANDGetToken(@RequestBody Users user) {
		// The authenticate method will fetch the userDetails from the Db and compare it
		// with the authRequest
//		System.out.println("User: " + user.getUsername());
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
//		System.out.println("Pass: " + authentication.getCredentials());
//		System.out.println("Pass: " + user.getPassword());

		if (authentication.isAuthenticated()) {
//			return ResponseEntity.ok(jwtService.generateToken(user.getUsername(),user.getRoles()));
			return ResponseEntity.ok(jwtService.generateToken(user.getUsername()));

		} else {
			CustomException exception = new CustomException(HttpStatus.FORBIDDEN.value(), "FORBIDDEN",
					"Wrong credentials", "/authenticate", ZonedDateTime.now());
			return exception.toResponseEntity();
		}
	}

}
