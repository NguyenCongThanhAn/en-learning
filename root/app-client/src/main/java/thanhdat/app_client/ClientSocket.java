package thanhdat.app_client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import thanhdat.app_client.common.Request;
import thanhdat.app_client.common.Response;

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
public boolean sendRequest(Request request) {
        try {
            if (socket == null || socket.isClosed()) {
                System.out.println("❌ Lỗi: Socket chưa kết nối hoặc đã bị đóng!");
                return false;
            }
            request.writeToStream(out); // Gọi hàm tự ghi của Request
            return true;
        } catch (IOException e) {
            System.out.println("❌ Lỗi khi gửi request: " + e.getMessage());
            return false;
        }
    }

    /**
     * METHOD 3: Nhận phản hồi Response từ Server (Đọc luồng byte và trả về đối tượng Response)
     */
    public Response receiveResponse() {
        try {
            if (socket == null || socket.isClosed()) return null;
            // Gọi hàm đọc từ stream của class Response
            return Response.readFromStream(in); 
        } catch (IOException e) {
            System.out.println("❌ Lỗi khi nhận dữ liệu Response từ Server: " + e.getMessage());
            return null;
        }
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