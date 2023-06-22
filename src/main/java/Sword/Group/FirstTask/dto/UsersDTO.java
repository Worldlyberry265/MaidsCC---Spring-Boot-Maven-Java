package Sword.Group.FirstTask.dto;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ResponseStatus;

import Sword.Group.FirstTask.model.Role;
import Sword.Group.FirstTask.model.Users;

public interface UsersDTO { // Data Access Object

	String save(Users user);

	List<Users> getAll();

	Users getById(int id);

	String AuthenticateUser(Users user);

	int getIdByUsername(String Username);

	List<Role> getUserRoles(int userId);

}
