package Sword.Group.FirstTask.userDetails;

import Sword.Group.FirstTask.model.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	JdbcTemplate Jtemplate;

	//ASK IF I SHOULD CHANGE IT TO DAOIMPLEMENTATION
	
	// For JWT Filter
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		String sql = "SELECT Username FROM users WHERE Username= ? LIMIT 1";
		Optional<Users> user = Jtemplate.query(sql, new BeanPropertyRowMapper<>(Users.class), username).stream()
				.findFirst();
		return user.map(UserInfoUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("user not found " + username));

	}
//	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
//		String sql = "SELECT Username FROM users WHERE Email= ? LIMIT 1";
//
//		Optional<Users> user = Jtemplate.query(sql, new BeanPropertyRowMapper<>(Users.class), email).stream()
//				.findFirst();
//		return user.map(UserInfoUserDetails::new)
//				.orElseThrow(() -> new UsernameNotFoundException("user not found " + email));
//
//	}
//
}
