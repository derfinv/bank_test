package it.federico.BankChecker.Filter;

import java.io.IOException;
import static it.federico.BankChecker.Constants.SecurityConstants.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import it.federico.BankChecker.Jwt.JwtTokenProvider;
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	
	private JwtTokenProvider jwtTokenProvider;
	public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
		super();
		this.jwtTokenProvider = jwtTokenProvider;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(request.getMethod().equalsIgnoreCase("OPTIONS"))
			response.setStatus(HttpStatus.OK.value());
		else {
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
				filterChain.doFilter(request, response);
				return;
			}
			String token=authorizationHeader.substring(TOKEN_PREFIX.length());
			String username =  jwtTokenProvider.getSubject(token);
			if(jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
				Authentication authentication= jwtTokenProvider.getAuthentication(username, null, request);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				SecurityContextHolder.clearContext();
			}
				
		}
		filterChain.doFilter(request, response);		
	}		
}
