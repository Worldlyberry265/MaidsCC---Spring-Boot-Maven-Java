package Sword.Group.FirstTask.dto;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import Sword.Group.FirstTask.model.Role;
import Sword.Group.FirstTask.model.Users;

@Repository
public class UsersDTOImpl implements UsersDTO {

	@Autowired
	JdbcTemplate Jtemplate;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Override
	public String save(Users user) {
		try {
			// Validate input for special characters
			if (!isUsernametValid(user.getUsername())) {
				throw new IllegalArgumentException("INVALID USERNAME! ONLY LETTERS AND SPACES ARE ALLOWED.");
			}

			// Validate input for special characters
			if (!isPassValid(user.getPassword())) {
				throw new IllegalArgumentException(
						"INVALID Password! ONLY LETTERS, NUMBERS, AND !@#$%^&* ARE ALLOWED.");
			}

			// Validate input for special characters
			if (!isEmailValid(user.getEmail())) {
				throw new IllegalArgumentException("INVALID Email! ONLY LETTERS, NUMBERS, AND .-_ ARE ALLOWED.");
			}

			String hashedPassword = passwordEncoder.encode(user.getPassword());

			// Check if the email is unique
			boolean isEmailUnique = isEmailUnique(user.getEmail());
			if (!isEmailUnique) {
				return ("Email already exist.");
			}

			int result = Jtemplate.update("INSERT INTO users (Username, Password, Email) VALUES (?, ?, ?)",
					user.getUsername(), hashedPassword, user.getEmail());

			if (result > 0) {
				return "User saved successfully.";
			}
		} catch (DataAccessException e) {
			// Exception occurred while accessing the data
			return "An error occurred while accessing the data.";
		}
		return "Failed to save the user."; // not important

	}

//	@Override
//	public ResponseEntity<ResponseStatus> save(Users user) {
//
//		try {
//
//			// Validate input for special characters
//			if (!isUsernametValid(user.getUsername())) {
////				throw new IllegalArgumentException("INVALID USERNAME! ONLY LETTERS AND SPACES ARE ALLOWED.");
//
//				throw new RequestException("INVALID USERNAME");
////				throw new IllegalStateException("INVALID USERNAME 22222222");
//			}
//
//			// Validate input for special characters
//			if (!isPassValid(user.getPassword())) {
//				throw new IllegalArgumentException(
//						"INVALID Password! ONLY LETTERS, NUMBERS, AND !@#$%^&* ARE ALLOWED .");
//			}
//
//			// Validate input for special characters
//			if (!isEmailValid(user.getEmail())) {
//				throw new IllegalArgumentException("INVALID Email! ONLY LETTERS, NUMBERS, AND .-_ ARE ALLOWED .");
//			}
//
//			String hashedPassword = passwordEncoder.encode(user.getPassword());
//
//			// Check if the email is unique
//			boolean isEmailUnique = isEmailUnique(user.getEmail());
//			if (!isEmailUnique) {
//				throw new IllegalArgumentException("EMAIL IS ALREADY REGISTERED.");
//			}
//
//			int result = Jtemplate.update("INSERT INTO users (Username, Password, Email) VALUES (?, ?, ?)", // removed ,
//																											// Salt and
//																											// 1 ?
//					new Object[] { user.getUsername(), hashedPassword, user.getEmail() }); // removed the salt
//
//			if (result > 0) {
//				return new ResponseEntity<>(HttpStatus.OK); // Return HTTP 200 OK if the user was successfully saved
//			} else {
//				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Return HTTP 500 Internal Server Error
//																				// if the user was not saved
//			}
//		} catch (Exception e) { // error while saving the user in the database
//
//			System.out.println("An error occurred while saving the user: " + e.getMessage());
//
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Return HTTP 500 Internal Server Error in
//																			// case of any exception
//
//		}
//
//	}

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

//	public String AuthenticateUser(Users user) {
//		String sql = "SELECT COUNT(*) FROM users WHERE Email = ? AND Password = ? LIMIT 1";
//
//		// Validate input for special characters
//		if (!isEmailValid(user.getEmail())) {
//			throw new IllegalArgumentException("INVALID Email! ONLY LETTERS, NUMBERS, AND .-_ ARE ALLOWED .");
//		}
//
//		// Validate input for special characters
//		if (!isPassValid(user.getPassword())) {
//			throw new IllegalArgumentException("INVALID Password! ONLY LETTERS, NUMBERS, AND !@#$%^&* ARE ALLOWED .");
//		}
//
//		String sql2 = "SELECT Password FROM users WHERE Email = ? LIMIT 1";
//		String RetrievedPassword = Jtemplate.queryForObject(sql2, String.class, user.getEmail());
//
//		try {
//			int result = Jtemplate.queryForObject(sql, Integer.class, user.getEmail(), RetrievedPassword); // ASK FOR
//																											// THIS
//			if (result == 1) {
//				if ((passwordEncoder.matches(user.getPassword(), RetrievedPassword))) {
//					return "Correct credentials, you may sign in.";
//				} else {
//					return "Incorrect password.";
//				}
//			} else {
//				return "Incorrect email.";
//			}
//		} catch (DataAccessException e) {
//			// Exception occurred while accessing the data
//			return "An error occurred while accessing the data.";
//		}
//	}
	@Override
	public String AuthenticateUser(Users user) {
//	    String sql = "SELECT COUNT(*) FROM users WHERE Email = ? LIMIT 1";
		String sql2 = "SELECT Password FROM users WHERE Email = ? LIMIT 1";

		// Validate input for special characters
		if (!isEmailValid(user.getEmail())) {
			throw new IllegalArgumentException("INVALID Email! ONLY LETTERS, NUMBERS, AND .-_ ARE ALLOWED .");
		}

		// Validate input for special characters
		if (!isPassValid(user.getPassword())) {
			throw new IllegalArgumentException("INVALID Password! ONLY LETTERS, NUMBERS, AND !@#$%^&* ARE ALLOWED .");
		}

		try {

			String retrievedPassword = Jtemplate.queryForObject(sql2, String.class, user.getEmail());
			if (retrievedPassword != null && passwordEncoder.matches(user.getPassword(), retrievedPassword)) {
				return "Correct credentials, you may sign in.";
			} else if (retrievedPassword != null) {
				return "Incorrect password.";
			} else {
				return "Email not found.";
			}
		} catch (DataAccessException e) {
			// Exception occurred while accessing the data
			return "An error occurred while accessing the data.";
		}
	}

	@Override
	public int getIdByUsername(String Username) {

		String sql2 = "SELECT ID FROM users WHERE Username = ? LIMIT 1";
		int Id = Jtemplate.queryForObject(sql2, Integer.class, Username);

		return Id;
	}

	@Override
	public List<Role> getUserRoles(int userId) {
		List<Role> roles = new ArrayList<>();

		try {
			String sql = "SELECT r.ID, r.Name FROM Roles r INNER JOIN user_roles ur ON r.ID = ur.Role_ID WHERE ur.User_ID = ?";
			Jtemplate.query(sql, new Object[] { userId }, (resultSet) -> {

				int roleId = resultSet.getInt("ID");

				String roleName = resultSet.getString("Name");

				Role role = new Role(roleId, roleName); // Create Role object using the fetched data
				roles.add(role);
				return role;
			});
		} catch (DataAccessException e) {
			e.printStackTrace(); // Handle the exception appropriately
		}
		return roles;

//		return roles;
	}

}
