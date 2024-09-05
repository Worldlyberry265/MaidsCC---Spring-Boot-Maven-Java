package MaidsCC.Backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {

	// This will be triggered Globally, when any controller receive an http request or when we transfer the flow to here (from
	// DAOImplementation or anywhere.
	
	//This will catch when an input variable should be int and a String is given instead for example.
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	private ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {

		CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
				"Invalid Input format", request.getRequestURI() + ex.getValue(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	// When you try to access a URL that isn't found at any of the controllers methods.
	@ExceptionHandler(NoHandlerFoundException.class)
	private ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.NOT_FOUND.value(),
				"Please Check The Url you're sending to", "The requested URL was not found", request.getRequestURI(),
				ZonedDateTime.now());
		return exception.toResponseEntity();
	}
	
	//When you try to send a get request for a post URL for example.
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	private ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.METHOD_NOT_ALLOWED.value(), "METHOD_NOT_ALLOWED",
				"The HTTP method is not allowed for this URL", request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	// This will catch an expired jwt.
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.UNAUTHORIZED.value(), "JWT token Expired",
				"The jwt is expired or expired", request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}
	
	//This will catch a jwt with invalid signature.
	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<Object> handleExpiredJwtException(SignatureException ex, HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.UNAUTHORIZED.value(), "JWT signature is invalid",
				ex.getLocalizedMessage(), request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}

}
