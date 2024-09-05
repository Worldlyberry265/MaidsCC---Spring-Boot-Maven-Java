package MaidsCC.Backend.model;

public class Book {

	private int ID;
	private String title;
	private int publication_year;
	private String author;
	private String ISBN;

	public Book() {

	}

	public Book(int ID, String title, int publication_year, String author, String ISBN) {
		this.ID = ID;
		this.title = title;
		this.publication_year = publication_year;
		this.author = author;
		this.ISBN = ISBN;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		this.ID = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPublication_year() {
		return publication_year;
	}

	public void setPublication_year(int publication_year) {
		this.publication_year = publication_year;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

}
