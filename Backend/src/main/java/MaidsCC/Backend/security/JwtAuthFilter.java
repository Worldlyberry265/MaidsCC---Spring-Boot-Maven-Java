package MaidsCC.Backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import MaidsCC.Backend.userDetails.PatronDetails;
import MaidsCC.Backend.userDetails.PatronDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;
import java.util.Collection;

@Component
public class JwtAuthFilter extends OncePerRequestFilter { // So we can apply the filter just once to every request

	private HandlerExceptionResolver handlerExceptionResolver;

	@Autowired
	private JwtService jwtService;

	@Autowired
	PatronDetailsService patronDetailsService;

	// This filter will be applied to every Http request that arrives
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String id = null;
		//  the actual jwt token will be after "Bearer "
		if (authHeader != null && authHeader.startsWith("Bearer ")) { // If it's not a public API, and it actually does
																		// have a authorization header and a JWT token
			token = authHeader.substring(7);

			try {
				id = String.valueOf(jwtService.extractPatronID(token));

				// This if checks if the security context has already authenticated the patron from previous authentication
				// in the same http request, if yes it will avoid authenticating again
				// This is really not needed for this app because we're only authenticating the patron once in every authorization req
				if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) { 

					PatronDetails patronDetails = (PatronDetails) patronDetailsService.loadUserByUsername(id);

					Collection<? extends GrantedAuthority> auth = patronDetails.getAuthorities(); 
					// or simply add null instead of the auth since we don't have roles in our case
					if (jwtService.validateToken(token, patronDetails)) {
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								patronDetails, null, auth);
						// To update the authentication at SecurityContextHolder
						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authToken);
						// This marks the user as authenticated for the current request (since the SecurityContextHolder resets after 
						// every request
					} else {
						handlerExceptionResolver.resolveException(request, response, null, new SignatureException(
								"Jwt Payload is Modified,  JWT validity cannot be asserted and should not be trusted."));
						return;
					}
				}
			} catch (ExpiredJwtException e) {
				handlerExceptionResolver.resolveException(request, response, null, e);
				return;
			} catch (SignatureException e) {
				handlerExceptionResolver.resolveException(request, response, null, e);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}