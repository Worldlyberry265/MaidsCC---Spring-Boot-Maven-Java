package MaidsCC.Backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import MaidsCC.Backend.userDetails.PatronDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

	// The secret to sign the jwt
	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	public String extractPatronname(String token) {
	    return extractClaim(token, Claims::getSubject);
	}
	
	public int extractPatronID(String token) {
		int id = extractClaim(token, claims -> claims.get("ID", Integer.class));
	    return id;
	}

	public Date extractExpiration(String token) {
	    return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
		// parsing involves breaking down the token into its three parts: header,
		// payload (claims), and signature.
		// It gets the key used to generate the jwt
		// It then apply the cryptographic algorithm with the SignKey and check if it
		// matches the signature
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, PatronDetails patronDetails) {
		final String patronName = extractPatronname(token);
		return (patronName.equals(patronDetails.getUsername()) && !isTokenExpired(token));
	}


	public String generateToken(String PatronName, int PatronId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("ID", PatronId);
		return createToken(claims, PatronName);
	}

	private String createToken(Map<String, Object> claims, String PatronName) {
		return Jwts.builder().setClaims(claims).setSubject(PatronName).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
