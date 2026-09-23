/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import common.Request;
import common.RequestType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                byte b = request.getType();
                if (b == RequestType.LOGIN.toByte()) {
                    // Authentication
                    // Require JDBC
                } else if (b == RequestType.GET_LEADERBOARD.toByte()) {
                    
                } else if (b == RequestType.GET_QUIZ.toByte()) {
                    
                } else if (b == RequestType.REGISTER.toByte()) {
                    // Register to server db
                } else if (b == RequestType.SUBMIT_ANSWER.toByte()) {
                    
                }
            }
        } catch (Exception e) {

        }
    }

}
