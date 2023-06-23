package Sword.Group.FirstTask.userDetails;

import Sword.Group.FirstTask.dao.UsersDAO;
import Sword.Group.FirstTask.model.Role;
import Sword.Group.FirstTask.model.Users;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoUserDetails implements UserDetails {

	private int id;
	private String username;
	private String password;
	private List<GrantedAuthority> authorities;
	
//	private UserInfoUserDetailsService service;

//	@Autowired
//	private UsersDAO eDAO;

	public UserInfoUserDetails(Users user) { // Extract the roles of the user and map to a list
		id = user.getID();
		username = user.getUsername();
		password = user.getPassword();
		
		authorities = null;

//		authorities = eDAO.getUserRoles(username).stream().map(role -> new SimpleGrantedAuthority(role.getName()))
//				.collect(Collectors.toList());
//		authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
//				.collect(Collectors.toList());
		// Permissions??????
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Role> roles) {
		authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());

	}
	
	public int getId() {
		return id;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
