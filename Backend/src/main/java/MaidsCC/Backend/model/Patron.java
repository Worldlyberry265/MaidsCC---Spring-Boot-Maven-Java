package MaidsCC.Backend.model;


public class Patron {
	
	private int ID;
	private String PatronName;
	private String Password;
	private String contact_number;
	
	public Patron () {
		
	}
	
	public Patron(int iD, String password) {
		ID = iD;
		Password = password;
	}
	
	public Patron(int iD, String patronName, String contact_number) {
		ID = iD;
		PatronName = patronName;
		this.contact_number = contact_number;
	}

	public int getID() {
		return ID;
	}
	
	public void setID(int id) {
		ID = id;
	}

	public String getPatronName() {
		return PatronName;
	}

	public void setPatronName(String patronName) {
		PatronName = patronName;
	}

	public String getContact_number() {
		return contact_number;
	}

	public void setContact_number(String contact_number) {
		this.contact_number = contact_number;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}
	
	
}
