package MaidsCC.Backend.exceptions;

import java.time.ZonedDateTime;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomExceptionHandler {
	
	 public static ResponseEntity<Object> handleException(Exception e, String message, String path) {
	        
		 CustomException exception = new CustomException();
	        
	        if (e instanceof EmptyResultDataAccessException) {
	            exception.ChangeException(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", message,
	                    path, ZonedDateTime.now());
	            return exception.toResponseEntity();
	        } else if(e instanceof DuplicateKeyException) {
	        	// One of the unique entity variables is already registered.
	        	exception.ChangeException(HttpStatus.CONFLICT.value(), "Duplicate Entry", message,
	                    path, ZonedDateTime.now());
	            return exception.toResponseEntity();
	        }
	        else {
	        	exception.ChangeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR",
		                   "An error occurred while accessing the data", path,  ZonedDateTime.now());
	            return exception.toResponseEntity();
	        }
	    }
}
