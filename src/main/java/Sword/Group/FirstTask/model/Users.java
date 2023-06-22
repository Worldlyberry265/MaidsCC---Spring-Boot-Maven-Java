package Sword.Group.FirstTask.model;

import java.util.List;

//@Entity
public class Users {

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID;

	private String Username;
	private String Password;
	private String Email;

//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "User_ID"), inverseJoinColumns = @JoinColumn(name = "Role_ID"))
	private List<Role> roles;

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public long getID() {
		return ID;
	}

	public Users(String username, String password, String email) {
		super();
		Username = username;
		Password = password;
		Email = email;
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
