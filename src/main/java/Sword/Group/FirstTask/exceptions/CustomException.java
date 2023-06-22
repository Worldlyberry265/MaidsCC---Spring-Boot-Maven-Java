//package Sword.Group.FirstTask.exceptions;
//
//import java.time.ZonedDateTime;
//
//import org.springframework.http.HttpStatus;
//
//public class CustomException extends RuntimeException {
//	
//	private String message;
//	private HttpStatus httpStat;
//	private ZonedDateTime timestamp;
//	
//		
//	public CustomException(String message, HttpStatus httpStat, ZonedDateTime timestamp) {
//		super();
//		this.message = message;
//		this.httpStat = httpStat;
//		this.timestamp = timestamp;
//	}
//	public String getMessage() {
//		return message;
//	}
//	public void setMessage(String message) {
//		this.message = message;
//	}
//	public HttpStatus getHttpStat() {
//		return httpStat;
//	}
//	public void setHttpStat(HttpStatus httpStat) {
//		this.httpStat = httpStat;
//	}
//	public ZonedDateTime getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(ZonedDateTime timestamp) {
//		this.timestamp = timestamp;
//	}
//
//
//}

package Sword.Group.FirstTask.exceptions;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

	private final ZonedDateTime timestamp;;
	private final int status;
	private final String error;
	private final String message;
	private final String path;

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
}
