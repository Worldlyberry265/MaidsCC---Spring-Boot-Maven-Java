package Sword.Group.FirstTask.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import Sword.Group.FirstTask.dao.UsersDAO;
import Sword.Group.FirstTask.userDetails.UserInfoUserDetails;

public class Users {

	private int ID;
	private String Username;
	private String Password;
	private String Email;
	private List<Role> roles;

	public List<Role> getRoles(UserInfoUserDetails UserDetail) {
		return (List<Role>) UserDetail.getAuthorities();
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public int getID() {
		return ID;
	}

	public Users() {

	}

	public Users(String username, String password, String email,List<Role> Roles) {
		super();
		Username = username;
		Password = password;
		Email = email;
		roles = Roles;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}
}
