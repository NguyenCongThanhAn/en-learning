/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thanhdat.app_client.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Request {
    private byte type;
    private String token;
    private byte[] data; // Mảng byte chứa dữ liệu tùy biến của riêng từng chức năng

    // Constructor 1: Tiện lợi cho Client truyền trực tiếp Object Payload vào
    public Request(byte type, String token, SerializablePayload payload) throws IOException {
        this.type = type;
        this.token = token != null ? token : "";
        this.data = payload != null ? payload.toBytes() : new byte[0];
    }

    // Constructor 2: Dùng cho Server khi đọc luồng byte từ Socket lên
    public Request(byte type, String token, byte[] data) {
        this.type = type;
        this.token = token != null ? token : "";
        this.data = data != null ? data : new byte[0];
    }

    // Hàm tự động GHI gói tin ra Socket dưới dạng Byte Stream
    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeByte(this.type);              // 1. Ghi 1 byte mã hành động
        
        out.writeShort(this.token.length());   // 2. Ghi chiều dài chuỗi Token (2 bytes)
        if (!this.token.isEmpty()) {
            out.writeUTF(this.token);          // 3. Ghi chuỗi Token
        }
        
        out.writeShort(this.data.length);      // 4. Ghi chiều dài mảng data (2 bytes)
        if (this.data.length > 0) {
            out.write(this.data);              // 5. Ghi toàn bộ mảng byte data
        }
        out.flush();
    }

    // Hàm tự động ĐỌC và phân tích Byte Stream từ Socket thành Object Request
        public static Request readFromStream(DataInputStream in) throws IOException {
        byte type = in.readByte();
        int tokenLen = in.readShort();
        String token = tokenLen > 0 ? in.readUTF() : "";
        int dataLen = in.readShort();
        byte[] data = new byte[dataLen];
        if (dataLen > 0) in.readFully(data);
        return new Request(type, token, data);
    }

    // Getters
    public byte getType() { return type; }
    public String getToken() { return token; }
    public byte[] getData() { return data; }
}
