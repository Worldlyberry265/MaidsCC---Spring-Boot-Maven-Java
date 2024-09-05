package MaidsCC.Backend.Testing;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import MaidsCC.Backend.model.Book;
import MaidsCC.Backend.model.Patron;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	// To store temporary the id of the created test book
	private int testBookId;

	// To store temporary the id of the created test patron
	private int testPatronId;

	// I created createTestBook, createTestPatron, deleteTestBook, deleteTestPatron,
	// createTestRecord, and returnTestRecord to avoid BeforeEach and AfterEach
	// being
	// run unnecessarily before and after certain methods. And to make every test
	// independent of another.

//	@BeforeEach
	private void createTestBook() throws Exception {
		Book book = new Book();
		book.setTitle("Test Book");
		book.setAuthor("Test Author");
		book.setPublication_year(2024);
		book.setISBN("0000000000000");// unusual ISBN that isn't problematic to other instances,
		// can't be a duplicate.

		// We need to pass the book in JSON format
		ObjectMapper objectMapper = new ObjectMapper();
		String bookJson = objectMapper.writeValueAsString(book);

		String response = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/books").contentType(MediaType.APPLICATION_JSON)
						.content(bookJson))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		// To retrieve the id of the book
		String idString = response.replace("Book Added with ID: ", "");

		testBookId = Integer.parseInt(idString);
	}

//	@AfterEach
	private void deleteTestBook() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", testBookId))
				.andExpect(MockMvcResultMatchers.status().isOk()) // status 200
				.andExpect(MockMvcResultMatchers.content().string("Book deleted successfully")).andReturn();
		testBookId = 0; // Reset testBookId after deletion
	}

//	@BeforeEach
	private void createTestPatron() throws Exception {
		Patron patron = new Patron();
		patron.setPatronName("Patron");
		patron.setPassword("Patron123!");
		patron.setContact_number("009000000000"); // unusual contact number that isn't problematic to other instances,
													// can't be a duplicate.

		// We need to pass the patron in JSON format
		ObjectMapper objectMapper = new ObjectMapper();
		String patronJson = objectMapper.writeValueAsString(patron);

		String response = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/patrons").contentType(MediaType.APPLICATION_JSON)
						.content(patronJson))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		// To retrieve the id of the patron
		String idString = response.replace("Patron Added with ID: ", "");
		testPatronId = Integer.parseInt(idString);
	}

//	@AfterEach
	private void deleteTestPatron() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/patrons/{id}", testPatronId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Patron deleted successfully")).andReturn();
		testPatronId = 0; // Reset testPatronkId after deletion
	}

//	@BeforeEach
	private void createTestRecord() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/{bookId}/patron/{patronId}", 1, 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Congrats, you have borrowed the book successfully"))
				.andReturn();
	}

//	@BeforeEach
	private void returnTestRecord() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/return/{bookId}/patron/{patronId}", 1, 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Book Returned Successfully")).andReturn();
	}

	@Test
	public void testGetAllBooks() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/books")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].title").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].author").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].publication_year").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].isbn").exists())
				// Can also add this if we have pagination for example
//				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10))
				.andReturn();

	}

	@Test
	public void testGetBook() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.title").isString())
				.andExpect(MockMvcResultMatchers.jsonPath("$.title").value("To Kill a Mockingbird"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.author").isString())
				.andExpect(MockMvcResultMatchers.jsonPath("$.author").value("Chill Lee"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.publication_year").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.publication_year").value("2019"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.isbn").isString())
				.andExpect(MockMvcResultMatchers.jsonPath("$.isbn").value("9780060935467")).andReturn();

		// These are dummy data i inserted at my database
	}

	@Test
	public void testAddBook() throws Exception {
		createTestBook();
		deleteTestBook();
		// we need these 2 methods because we need to delete the test book after we
		// created it.
	}

	@Test
	public void testUpdateBook() throws Exception {
		createTestBook();
		Book book = new Book();
		book.setTitle("Test Book");
		book.setAuthor("Test Author");
		book.setPublication_year(2024);
		book.setISBN("0000000000000"); // unusual contact number that isn't problematic to other instances,
		// can't be a duplicate.

		// Step 2: Convert the Book object to JSON
		ObjectMapper objectMapper = new ObjectMapper();
		String bookJson = objectMapper.writeValueAsString(book);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{id}", testBookId)
				.contentType(MediaType.APPLICATION_JSON).content(bookJson))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Book Updated")).andReturn();

		deleteTestBook();
	}

	@Test
	public void testDeleteBook() throws Exception {
		createTestBook();
		deleteTestBook();
		// we need these 2 methods because we need to create a test book before we can
		// actually delete one.
	}

	@Test
	public void testGetAllPatrons() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patrons")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].patronName").exists())
				// I wont check if password exists because I didnt even do a constructor for it,
				// since I wont be retrieving it since its hashed
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].contact_number").exists())
				// Can also add this if we have pagination for example
//				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10))

				.andReturn();
	}

	@Test
	public void testGetPatron() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patrons/{id}", 1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.patronName").value("Baraa GH"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.contact_number").value("0096112345678")).andReturn();
	}

	@Test
	public void testAddPatron() throws Exception {
		createTestPatron();
		deleteTestPatron();
		// we need these 2 methods because we need to delete the test patron after we
		// created it.
	}

	private void updatePatronHelper(String patronName, String contactNumber, String password) throws Exception {
		Patron patron = new Patron();
		patron.setPatronName(patronName);
		patron.setContact_number(contactNumber);

		String jwt = "";

		// The password can be null because the patron can either update all his
		// variables except the id, or only update
		// some and leave the password unchanged.
		if (password != null) {
			patron.setPassword(password);
			jwt = testLoginAndGetTokenHelper();
		}

		ObjectMapper objectMapper = new ObjectMapper();
		String patronJson = objectMapper.writeValueAsString(patron);

		if (password != null) {
			mockMvc.perform(MockMvcRequestBuilders.put("/api/patrons/{id}", testPatronId)
					.contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt)
					.content(patronJson)).andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().string("Patron Updated")).andReturn();
		} else {
			mockMvc.perform(MockMvcRequestBuilders.put("/api/patrons/{id}", testPatronId)
					.contentType(MediaType.APPLICATION_JSON).content(patronJson))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().string("Patron Updated")).andReturn();
		}

	}

	@Test
	public void testUpdatePatronWithPassword() throws Exception {
		createTestPatron();
		updatePatronHelper("Test Patron", "009999999", "Patron12345!");
		deleteTestPatron();
		// We create and then delete a test patron.
	}

	@Test
	public void testUpdatePatronWithoutPassword() throws Exception {
		createTestPatron();
		updatePatronHelper("Test Patron", "009999999", null);
		deleteTestPatron();
		// We create and then delete a test patron.
	}

	@Test
	public void testDeletePatron() throws Exception {
		createTestPatron();
		deleteTestPatron();
		// we need these 2 methods because we need to create a test book before we can
		// actually delete one.
	}

	private String testLoginAndGetTokenHelper() throws Exception {
		Patron patron = new Patron();
		patron.setID(testPatronId);
		patron.setPassword("Patron123!");

		// Pass the patron as JSON object
		ObjectMapper objectMapper = new ObjectMapper();
		String patronJson = objectMapper.writeValueAsString(patron);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/patron/login")
				.contentType(MediaType.APPLICATION_JSON).content(patronJson))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String jwtToken = result.getResponse().getContentAsString();
		assertNotNull(jwtToken); // Check that the token is not null
		return jwtToken;
	}

	@Test
	public void testLoginAndGetToken() throws Exception {
		createTestPatron();
		testLoginAndGetTokenHelper();
		deleteTestPatron();
	}

	@Test
	public void testBorrowAndReturnBook() throws Exception {
		createTestRecord();
		returnTestRecord();
		// Assuming the 1st book and patron are testing instances, the 1st patron borrows the 1st book and then return it.
		// We can't create and use here a new test patron and a test book and then delete them unless we have a method of 
		// deleting borrowing_records too, since the record has the patron and the book IDs as foreign keys.
	}
}
