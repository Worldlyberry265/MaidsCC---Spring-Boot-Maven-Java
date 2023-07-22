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
import Sword.Group.FirstTask.model.MiniUser;
import Sword.Group.FirstTask.model.Users;
import Sword.Group.FirstTask.security.JwtService;

@RestController
public class UsersController {

	@Autowired // Instead ProductService service
	private UsersDAO eDAO; // Instead ProductService service

	@GetMapping("/homepage/getUsers/{field}")
	public  List<MiniUser> getUsers(@PathVariable String field) { //@RequestParam (defaultValue = "Username")
		return eDAO.getAll(field);
	}

	@GetMapping("/Users/{ID}")
	public Users getUserById(@PathVariable int ID) {
		return eDAO.getById(ID);
	}

	@PostMapping("/SaveUser")
	public ResponseEntity<Object> saveUser(@RequestBody Users user) {
		ResponseEntity<Object> result = eDAO.save(user);
		return result;
	}

	@PostMapping("/authenticate")
	public ResponseEntity<Object> authenticateAndGetToken(@RequestBody Users user) {
		ResponseEntity<Object> result = eDAO.authANDGetToken(user);
		return result;

	}

	@GetMapping("/AuthenticateUser")
	public ResponseEntity<Object> authenticateUser(@RequestBody Users user) {
		ResponseEntity<Object> result = eDAO.AuthenticateUser(user);
		return result;
	}

}
