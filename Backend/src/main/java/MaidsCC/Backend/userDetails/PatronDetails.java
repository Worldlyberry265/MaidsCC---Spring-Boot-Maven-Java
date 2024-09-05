package MaidsCC.Backend.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import MaidsCC.Backend.model.Patron;

import java.util.Collection;

@SuppressWarnings("serial")
public class PatronDetails implements UserDetails {

	private int id;
	private String patronName;
	private String password;

	public PatronDetails(Patron patron) {
																				
		id = patron.getID();
		patronName = patron.getPatronName();
		password = patron.getPassword();
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String getUsername() {
		return patronName;
	}
	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// since we dont have any roles in our case
		return null;
	}

	
	
}
