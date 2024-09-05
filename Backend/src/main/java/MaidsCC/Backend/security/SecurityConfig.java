package MaidsCC.Backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import MaidsCC.Backend.userDetails.PatronDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private JwtAuthFilter authFilter;

	// Lazy to remove the dependency cycle between beans
	public SecurityConfig(@Lazy JwtAuthFilter authFilter) {
		this.authFilter = authFilter;
	}

	// For hashing the password
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// To fetch patron details
	@Bean
	public PatronDetailsService patronDetailsService() {
		return new PatronDetailsService();
	}

	// The security chain which configures the security for the whole server.
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// disabled csrf since im using jwt instead of cookies for session mangement 
		// cors will permit all since the server will be receiving http requests from many domains, and i secured the app well.
		// My server is stateless since it will be handling stateless REST APIs using JWT for authentication.
		return http.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();

	}

	// Provides the required setup for the authentication process.
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(patronDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	// It manages the authentication and sends it to the AuthenticationProvider
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
