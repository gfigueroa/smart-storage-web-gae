package util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.bind.DatatypeConverter;

public class TripleDES {

	private final static String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
	private final static String SECURE_RANDOM_PROVIDER = "SUN";
	private final static int SECRET_KEY_SIZE = 32;
	private final static String CRYPTOGRAPHIC_ALGORITHM = "DESede";
	private final static String CHARSET = "UTF8";

	public static byte[] generateSecretKey() throws NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchProviderException {
		SecureRandom secureRandom = SecureRandom.getInstance(
				SECURE_RANDOM_ALGORITHM, SECURE_RANDOM_PROVIDER);

		byte[] secretKey = new byte[SECRET_KEY_SIZE];

		secureRandom.nextBytes(secretKey);

		return secretKey;
	}

	public static String encrypt(String content, String secretKeyString)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidKeySpecException {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM);

		SecretKey secretKey = convertSecretKeyStringToSecretKey(secretKeyString);

		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] plainText = content.getBytes(CHARSET);

		byte[] encryptedText = cipher.doFinal(plainText);

		return encodeToBase64String(encryptedText);
	}

	public static String decrypt(String content, String secretKeyString)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			InvalidKeySpecException {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM);

		SecretKey secretKey = convertSecretKeyStringToSecretKey(secretKeyString);

		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		byte[] encryptedText = decodeFromBase64String(content);

		byte[] plainText = cipher.doFinal(encryptedText);

		return new String(plainText, CHARSET);
	}

	public static String encodeToBase64String(byte[] bytes) {
		String encodedBase64Text = DatatypeConverter.printBase64Binary(bytes);

		return encodedBase64Text;
	}

	public static byte[] decodeFromBase64String(String encodedBase64String) {
		byte[] decodedBytes = DatatypeConverter
				.parseBase64Binary(encodedBase64String);

		return decodedBytes;
	}

	private static SecretKey convertSecretKeyStringToSecretKey(
			String secretKeyString) throws UnsupportedEncodingException,
			InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		byte[] bytes = secretKeyString.getBytes(CHARSET);

		KeySpec keySpec = new DESedeKeySpec(bytes);

		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(CRYPTOGRAPHIC_ALGORITHM);

		return keyFactory.generateSecret(keySpec);
	}
}
