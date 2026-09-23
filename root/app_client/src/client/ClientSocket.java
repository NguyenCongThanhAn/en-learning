package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientSocket {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    // Kết nối tới Server
    public boolean connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (Exception e) {
            System.out.println("❌ Lỗi kết nối Server: " + e.getMessage());
            return false;
        }
    }

    // Gửi dữ liệu tới Server
    public void send(String message) {
        try {
            if (out != null) {
                out.writeUTF(message);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nhận dữ liệu từ Server
    public String receive() {
        try {
            if (in != null) {
                return in.readUTF();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Đóng kết nối
    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}