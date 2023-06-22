//package Sword.Group.FirstTask.security;
//
//import org.apache.commons.codec.digest.DigestUtils;
//
//import java.security.SecureRandom;
//import java.util.Base64;
//
//public class PasswordHasher {
//
//	public String[] hashPassword(String password, String salt) {
//
//		if (salt == null) {
//			salt = generateSalt();
//		}
//
//		// Concatenate the salt and password
//		String saltedPassword = salt + password;
//
//		// Use SHA-256 algorithm to hash the salted password
//		String hashedPassword = DigestUtils.sha256Hex(saltedPassword);
//
//		String result[] = { hashedPassword, salt };
//
//		return result;
//	}
//
//	private String generateSalt() {
//		SecureRandom random = new SecureRandom();
//		byte[] saltBytes = new byte[16];
//		random.nextBytes(saltBytes);
//		return Base64.getEncoder().encodeToString(saltBytes);
//	}
//}
