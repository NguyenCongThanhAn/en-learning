/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import common.LoginPayload;
import common.Request;
import common.RequestType;
import common.Response;
import common.StatusCode;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.api.UserService;

/**
 *
 * @author PC
 */
public class ClientHandler implements Runnable {

    private String id;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientHandler(Socket s) {
        try {
            this.socket = s;
            this.dis = new DataInputStream(s.getInputStream());
            this.dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Request request = Request.readFromStream(dis);
                RequestType type = RequestType.fromByte(request.getType());
                if (type == null) {
                    System.out.println("Gói tin không hợp lệ!");
                    continue;
                }
                switch (type) {
                    case LOGIN -> {
                        // Authentication
                        LoginPayload login = LoginPayload.fromBytes(request.getData());
                        Response res = UserService.getInstance().login(login);
                        if (res.getStatus() == StatusCode.SUCCESS.code()) {

                        } else {

                        }
                        res.writeToStream(dos);
                        // Require JDBC (mocking rn)
                    }
                    case REGISTER -> {
                        LoginPayload login = LoginPayload.fromBytes(request.getData());
                        boolean b = UserService.getInstance().register(login.getUsername(), login.getPassword());
                        if (b) {
                            
                        }
                    }
                    case
                }
            }
        } catch (Exception e) {
            ca
        }
    }

}
