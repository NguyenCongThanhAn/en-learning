/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author PC
 */
public class PasswordHasher {
    
    private static final int ITERATIONS = 10000; // Số vòng lặp băm
    private static final int KEY_LENGTH = 256;   // Độ dài khóa đầu ra

    // 1. Hàm băm mật khẩu khi ĐĂNG KÝ (Tạo ra chuỗi bao gồm cả Salt để lưu vào DB)
    public static String hashPassword(String password) throws Exception {
        // Tạo chuỗi Salt ngẫu nhiên bảo mật
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        
        // Định dạng lưu trữ: "vòng_lặp:salt_base64:hash_base64"
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    // 2. Hàm kiểm tra mật khẩu khi ĐĂNG NHẬP
    public static boolean checkPassword(String inputPassword, String storedHash) throws Exception {
        String[] parts = storedHash.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);

        byte[] testHash = pbkdf2(inputPassword.toCharArray(), salt, iterations, hash.length * 8);
        
        // So sánh hai mảng byte một cách an toàn (tránh Timing Attack)
        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }
}
