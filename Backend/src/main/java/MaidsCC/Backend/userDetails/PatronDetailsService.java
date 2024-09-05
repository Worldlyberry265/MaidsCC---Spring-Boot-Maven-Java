package MaidsCC.Backend.userDetails;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import MaidsCC.Backend.exceptions.CustomException;
import MaidsCC.Backend.model.Patron;

@Service
@Primary
public class PatronDetailsService implements UserDetailsService {

	@Autowired
	JdbcTemplate Jtemplate;

	@Override
	public UserDetails loadUserByUsername(String IDasString) {
		// Im passing the id as string since the the interface method expects a String
		CustomException exception = new CustomException();
		int id = Integer.parseInt(IDasString); // Parse the string back to an integer
		try {
			String sql = "SELECT PatronName, Password FROM Patron WHERE id = ? LIMIT 1";
			Patron patron = Jtemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Patron.class), id);
			    return new PatronDetails(patron);

		// No need to check if a patron was found or not, because this is only for authentication, and if this get executed, then
		// of course the patron is found due to the checkers i placed in the DAO implementation.
		}  catch (DataAccessException e) {
			exception.ChangeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR",
					"An error occurred while accessing the data", "api/patrons/login", ZonedDateTime.now());
			throw exception;
		}
	}

}
