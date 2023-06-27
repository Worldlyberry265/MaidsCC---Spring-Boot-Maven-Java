package Sword.Group.FirstTask.dao;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import Sword.Group.FirstTask.model.Role;
import Sword.Group.FirstTask.model.Users;

public interface UsersDAO { // Data Access Object

	ResponseEntity<Object> save(Users user);

	List<Users> getAll();

	Users getById(int id);
	
	ResponseEntity<Object>  authANDGetToken(Users user);

	ResponseEntity<Object>  AuthenticateUser(Users user);

//	int getIdByUsername(String Username);

//	List<Role> getUserRoles(String Username);

}
