package MaidsCC.Backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import MaidsCC.Backend.dao.PatronDAO;
import MaidsCC.Backend.exceptions.CustomException;
import MaidsCC.Backend.exceptions.CustomExceptionHandler;
import MaidsCC.Backend.model.Patron;
import MaidsCC.Backend.security.JwtService;

@Repository
public class PatronDAOImpl implements PatronDAO {

	@Autowired
	JdbcTemplate Jtemplate;

	// To hash the password
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	// To authenticate the patron and compare the hashed inputted password with the
	// hashed database password
	@Autowired
	private AuthenticationManager authenticationManager;

	CustomException exception = new CustomException();

	private boolean ValidateInputName(String name) {
		String regexLettersAndSpaces = "^[A-Za-z ]*$"; // Only letters and spaces are allowed
		return name.matches(regexLettersAndSpaces);
	}

	private boolean ValidateInputContactNumber(String phoneNumber) {
		String regex = "^00[1-9]\\d{0,17}$"; // Should start with 2 zeros and continue with the country code
		return phoneNumber.matches(regex);
	}

	// 8 chars min, 1 small, 1 capital letters, and include 1 number and 1 special
	// at least
	private boolean isPassValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$";
		// Regular expression that only allows alphabets, numbers, and special
		// characters
		return input.matches(regex);
	}

	private int isContactNumberUnique(String phoneNumber) { // To check if the contact number is unique
		String sql = "SELECT ID FROM patron WHERE contact_number = ? LIMIT 1";

		// I used query so it doesn't throw an EmptyResultDataAccessException.
		List<Integer> ids = Jtemplate.query(sql, (rs, rowNum) -> rs.getInt("ID"), phoneNumber);

		if (ids.isEmpty()) {
			return -1;
		} else {
			return ids.get(0);
		}
	}

	@Override
	public ResponseEntity<Object> getAllPatrons() {

		// Shouldn't retrieve the password for security reasons and its also hashed.
		String sql = "SELECT ID, patronName, contact_number from patron";

		try {

			List<Patron> patrons = Jtemplate.query(sql, (resultSet, rowNum) -> {
				int id = resultSet.getInt("id");
				String patronName = resultSet.getString("patronname");
				String contact_number = resultSet.getString("contact_number");
				Patron patron = new Patron(id, patronName, contact_number);
				return patron;
			});
			return ResponseEntity.ok(patrons);

		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "Empty Database", "/api/patrons");
		}
	}

	@Override
	public ResponseEntity<Object> getPatron(int id) {

		String sql = "SELECT ID, patronName, contact_number FROM patron WHERE id = ? LIMIT 1";

		try {
			Patron patron = Jtemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Patron.class), id);
			return ResponseEntity.ok(patron);

		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "No patron was found", "/api/patrons");
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> addPatron(Patron patron) {

		// If any of the inputs are invalid we cancel the transaction and return an
		// error.
		if (!(ValidateInputName(patron.getPatronName()) && ValidateInputContactNumber(patron.getContact_number()))) {
			exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "Check your input",
					" Only letters and spaces are allowed for the Name, and the contact"
							+ " number should start with 2 zeros and then continue with the country code and the number",
					"/api/patrons", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

		// If any of the password is empty or invalid we cancel the transaction and
		// return an error.
		if (patron.getPassword() == null || !isPassValid(patron.getPassword())) {
			exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "Invalid Password",
					"It should include at least 1 small letter , 1 capital letter, 1 number and 1 of the these "
							+ "special characters !@#$%^&*, and no other characters are allowed!",
					"/api/patrons", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

		String hashedPassword = passwordEncoder.encode(patron.getPassword());

		try {

			// Can't add a PATRON that has a duplicate contact_number
			if (isContactNumberUnique(patron.getContact_number()) != -1) {
				exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"A patron with Contact Number " + patron.getContact_number() + " is already registered",
						"/api/patrons", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			// Use PreparedStatement to get the generated key from the database. (Mainly for
			// testing purposes)
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int result = Jtemplate.update(con -> {
				PreparedStatement ps = con.prepareStatement(
						"INSERT INTO patron (patronName, password,  contact_number)  " + "VALUES (?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, patron.getPatronName());
				ps.setString(2, hashedPassword);
				ps.setString(3, patron.getContact_number());
				return ps;
			}, keyHolder);

			if (result > 0) {
				Number key = keyHolder.getKey();
				return ResponseEntity.ok("Patron Added with ID: " + key);
			}
		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "An error occurred while accessing the data",
					"/api/patrons");
		}
		return exception.toResponseEntity();
	}

	@Transactional
	@Override
	public ResponseEntity<Object> updatePatron(int id, Patron patron) {

		// If any of the inputs are invalid we cancel the transaction and return an
		// error.
		if (!(ValidateInputName(patron.getPatronName()) && ValidateInputContactNumber(patron.getContact_number()))) {
			exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "Check your input",
					" Only letters and spaces are allowed for the Name, and the contact"
							+ " number should start with 2 zeros and then continue with the country code and the number",
					"/api/patrons", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

		// If any of the password is empty or invalid we cancel the transaction and
		// return an error.
		if (patron.getPassword() != null && !isPassValid(patron.getPassword())) {
			exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "Invalid Password",
					"It should include at least 1 small letter , 1 capital letter, 1 number and 1 of the these "
							+ "special characters !@#$%^&*, and no other characters are allowed!",
					"/api/patrons", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

		try {
			int FetchedId = isContactNumberUnique(patron.getContact_number());
			// if it's empty, it will return -1, if the contact_number isn't updated, it
			// will return the same id.
			if (FetchedId != -1 && FetchedId != id) {
				exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "Wrong ISBN",
						"A patron with Contact Number " + patron.getContact_number() + " is already registered",
						"/api/patrons", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			int result = -1;
			// If the password is updated
			if (patron.getPassword() != null) {
				String hashedPassword = passwordEncoder.encode(patron.getPassword());
				String updateSql = "UPDATE patron SET patronname = ?, password = ? ,contact_number = ? WHERE id = ?";
				result = Jtemplate.update(updateSql, patron.getPatronName(), hashedPassword, patron.getContact_number(),
						id);
			} else {
				// If the password isn't updated
				String updateSql = "UPDATE patron SET patronname = ?, contact_number = ? WHERE id = ?";
				result = Jtemplate.update(updateSql, patron.getPatronName(), patron.getContact_number(), id);
			}

			if (result > 0) {
				return ResponseEntity.ok("Patron Updated");

			} else {
				exception.ChangeException(HttpStatus.NOT_FOUND.value(), "Patron Not Found",
						"A patron with ID " + id + " couldn't be found", "/api/patrons", ZonedDateTime.now());
				return exception.toResponseEntity();
			}
		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "An error occurred while accessing the data",
					"/api/patrons");
		}
	}

	@Transactional
	@Override
	public ResponseEntity<Object> deletePatron(int id) {

		String deleteSql = "DELETE FROM patron WHERE id = ? LIMIT 1";

		try {
			int result = Jtemplate.update(deleteSql, id);

			if (result > 0) {
				return ResponseEntity.ok("Patron deleted successfully");
			} else {
				exception.ChangeException(HttpStatus.NOT_FOUND.value(), "Patron Not Found",
						"A patron with ID " + id + " couldn't be found", "/api/patrons", ZonedDateTime.now());
				return exception.toResponseEntity();
			}
		} catch (Exception e) {
			return CustomExceptionHandler.handleException(e, "An error occurred while accessing the data",
					"/api/patrons");
		}
	}

	@Override
	public ResponseEntity<Object> LoginAndGetToken(Patron patron) {

		// We are authenticating the patron according to his ID and password only.

		// Validate input for special characters
		if (!isPassValid(patron.getPassword())) {
			exception.ChangeException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
					"It should include at least 1 small letter , 1 capital letter, 1 number and 1 of the these "
							+ "special characters !@#$%^&*, and no other characters are allowed!",
					"/api/patron", ZonedDateTime.now());
			return exception.toResponseEntity();

		}
		Patron returnedPatron = null;
		try {

			String PatronChecksql = "SELECT PatronName FROM patron WHERE id = ? LIMIT 1";
			returnedPatron = Jtemplate.queryForObject(PatronChecksql, new BeanPropertyRowMapper<>(Patron.class),
					patron.getID());

			Authentication authentication = null;
			try {
				// It will compare the hash the patron's password and compare it with the database hashed password
				// SecurityContextHolder.getContext().getAuthentication() won't be null anymore after this.
				authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(patron.getID(), patron.getPassword()));

				//if the patron is authenticated, a jwt will be created from the patron's name and id and will be returned.
				if (authentication.isAuthenticated()) {
					return ResponseEntity.ok(jwtService.generateToken(returnedPatron.getPatronName(), patron.getID()));
				}

			} catch (AuthenticationException ex) {
				// if the patron enters a wrong password, it will throw this error.
				exception = new CustomException(HttpStatus.FORBIDDEN.value(), "FORBIDDEN", "Wrong Password",
						"/api/patron", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

		} catch (Exception e) {
			// This will trigger if PatronChecksql returned null, as the patron wasn't found.
			return CustomExceptionHandler.handleException(e, "Please check your ID and try again", "/api/patron");
		}
		return exception.toResponseEntity();

	}
}
