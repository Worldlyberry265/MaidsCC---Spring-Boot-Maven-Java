//BUSINESS LOGIC, FOR VALIDATION PURPOSES
package Sword.Group.FirstTask.userDetails;

import Sword.Group.FirstTask.model.Role;
import Sword.Group.FirstTask.model.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	JdbcTemplate Jtemplate;

	// ASK IF I SHOULD CHANGE IT TO DAOIMPLEMENTATION

	// For JWT Filter
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		String sql = "SELECT Username, Password FROM users WHERE Username= ? LIMIT 1";
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

//	@SuppressWarnings("deprecation")
//	public List<Role> getUserRoles(String Username) {
//		int Id = (Integer) null;
//		List<Role> roles = new ArrayList<>();
//		try {
//			String sql = "SELECT ID FROM users WHERE Username = ? LIMIT 1";
//			 Id = Jtemplate.queryForObject(sql, Integer.class, Username);
//
//		} catch (DataAccessException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			String sql2 = "SELECT r.ID, r.Name FROM roles r INNER JOIN user_roles ur ON r.ID = ur.Role_ID WHERE ur.User_ID = ?";
//			Jtemplate.query(sql2, new Object[] { Id }, (resultSet) -> {
//
//				int roleId = resultSet.getInt("ID");
//
//				String roleName = resultSet.getString("Name");
//
//				Role role = new Role(roleId, roleName); // Create Role object using the fetched data
//				roles.add(role);
//				return role;
//			});
//		} catch (DataAccessException e) {
//			e.printStackTrace(); // Handle the exception appropriately
//		}
//		return roles;
//
////		return roles;
//	}

}
