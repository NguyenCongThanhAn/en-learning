/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.util;

/**
 *
 * @author PC
 */

import common.SerializablePayload;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketPayloadUtil {

    /**
     * GỬI PAYLOAD TỪ SERVER
     * @param socket
     */
    public static void sendPayload(Socket socket, SerializablePayload payload) throws IOException {
        byte[] data = payload.toBytes(); // Gọi hàm tự chuyển đổi của bạn
        
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // 1. Gửi độ dài tổng của mảng byte trước
        dos.writeInt(data.length);
        // 2. Gửi mảng byte dữ liệu thực tế
        dos.write(data);
        dos.flush();
    }

    /**
     * NHẬN PAYLOAD TẠI CLIENT
     */
    public static byte[] receivePayloadBytes(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        // 1. Đọc kích thước gói tin sắp tới
        int length = dis.readInt();
        
        byte[] data = new byte[length];
        if (length > 0) {
            // 2. Đọc chính xác và đầy đủ số byte đó
            dis.readFully(data);
        }
        return data;
    }
}
