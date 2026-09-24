/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thanhdat.app_client.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author PC
 */
public class LoginPayload implements SerializablePayload {
    private String username;
    private String password;

    public LoginPayload(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.username);
        dos.writeUTF(this.password);
        return baos.toByteArray();
    }

    public static LoginPayload fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        return new LoginPayload(dis.readUTF(), dis.readUTF());
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
