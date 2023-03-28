package it.federico.BankChecker.Exception;

import java.io.IOException;

import java.util.Objects;

import org.slf4j.*;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import org.springframework.web.servlet.NoHandlerFoundException;

import com.auth0.jwt.exceptions.TokenExpiredException;

import it.federico.BankChecker.Http.HttpResponse;


@RestControllerAdvice
public class ExceptionHandling {
	private final Logger logger= LoggerFactory.getLogger(getClass());
	private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration.";
	private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
	private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request.";
	private static final String INCORRECCT_CREDENTIALS = "Username / password incorrect. Please try again.";
	private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration.";
	private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
	private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission.";
	private static final String ERROR_PATH = "/error";
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException(){
		return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
	}
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<HttpResponse> lockedException(){
		return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
	}
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception){
		HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
		return createHttpResponse( HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
	}
	
	@ExceptionHandler(NotAnImageFileException.class)
	public ResponseEntity<HttpResponse> notAnImageFIleException(NotAnImageFileException exception){
		return createHttpResponse( HttpStatus.BAD_REQUEST, exception.getMessage());
	}
	@ExceptionHandler(InternalServerError.class)
	public ResponseEntity<HttpResponse> internalServerErrorException(){
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
	}
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> incorrectCredentialsException(){
		return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECCT_CREDENTIALS);
	}
	
	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception){
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toLowerCase());
	}
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> accessDeniedException(){
		return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
	}
	@ExceptionHandler(EmailExistsException.class)
	public ResponseEntity<HttpResponse> emailExistsException(EmailExistsException exception){
		return createHttpResponse(HttpStatus.FOUND, "An account associated with this email already exists. Please try again.");
	}
	@ExceptionHandler(UsernameExistsException.class)
	public ResponseEntity<HttpResponse> usernameExistsException(UsernameExistsException exception){
		return createHttpResponse(HttpStatus.FOUND, "An account associated with this username already exists. Please try again.");
	}
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception){
		return createHttpResponse(HttpStatus.NOT_FOUND, "We couldn't find an account associated to this email.");
	}
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception){
		return createHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage().toLowerCase());
	}
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<HttpResponse> usernameNotFoundException(it.federico.BankChecker.Exception.UsernameNotFoundException exception){
			return createHttpResponse(HttpStatus.NOT_FOUND, "");
		
	}
	@ExceptionHandler(IOException.class)
	public ResponseEntity<HttpResponse> iOException(IOException exception){
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
	}
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<HttpResponse> notFoundException(NotFoundException exception){
		return createHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage().toLowerCase());
	}
	
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException exception){
		return createHttpResponse(HttpStatus.BAD_REQUEST, "This page was not found.");
	}		
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message){ 		
		return new ResponseEntity<HttpResponse>(new HttpResponse(httpStatus.value(), httpStatus, 
				httpStatus.getReasonPhrase().toUpperCase(), message),httpStatus);
	}		
}
