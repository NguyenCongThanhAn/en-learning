/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.api;

import common.LoginPayload;
import common.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import server.util.PasswordHasher;

/**
 *
 * @author PC
 */
public class UserService {

    private static UserService instance = new UserService();
    // Giả lập Database bằng HashMap cho nhẹ (hoặc kết nối JDBC ở đây)
    private Map<String, String> userDatabase = new HashMap<>();
    
    private UserService() {
    }

    public static UserService getInstance() {
        return instance;
    }

    public Response login(LoginPayload payload) throws Exception {
        // Mocking
        var name = "Ansida";
        String pass = "índf";
        // End of mocking
        var username = payload.getUsername();
        var hashedPassword = payload.getPassword();
        if (PasswordHasher.checkPassword(hashedPassword, pass)) {
            Response success = new Response((byte) 0, username, null);
            return success;
        } else {
            Response fail = new Response((byte) 1, username, null);
            return fail;
        }
    }
    
    public boolean register(String username, String password) throws Exception {
        if (userDatabase.containsKey(username)) return false; // Tài khoản đã tồn tại
        
        // Gọi class băm mật khẩu đã làm ở câu trước
        String hashedPassword = PasswordHasher.hashPassword(password);
        userDatabase.put(username, hashedPassword);
        return true;
    }

}
