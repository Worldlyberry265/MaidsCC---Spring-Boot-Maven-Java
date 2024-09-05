

package MaidsCC.Backend.exceptions;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("serial")
public class CustomException extends RuntimeException {

	private ZonedDateTime timestamp;;
	private int status;
	private String error;
	private String message;
	private String path;
	
	public CustomException() {

	}

	public CustomException(int status, String error, String message) {
		this.status = status;
		this.error = error;
		this.message = message;
	}
	
	public CustomException(int status, String error, String message, String path, ZonedDateTime timestamp) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}
	
	public void ChangeException(int status, String error, String message, String path, ZonedDateTime timestamp) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	// To print the exception in an error form.
	public ResponseEntity<Object> toResponseEntity() {
		Map<String, Object> response = new HashMap<>();
		response.put("timestamp", timestamp);
		response.put("status", status);
		response.put("error", error);
		response.put("message", message);
		response.put("path", path);

		return ResponseEntity.status(status).body(response);
	}


}
