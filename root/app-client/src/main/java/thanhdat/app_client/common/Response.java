package thanhdat.app_client.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Response {
    private byte status; // 0x01: SUCCESS, 0x00: ERROR
    private String message;
    private byte[] data;

    public Response(byte status, String message, byte[] data) {
        this.status = status;
        this.message = message != null ? message : "";
        this.data = data != null ? data : new byte[0];
    }

    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeByte(this.status);
        
        out.writeShort(this.message.length());
        if (!this.message.isEmpty()) {
            out.writeUTF(this.message);
        }
        
        out.writeShort(this.data.length);
        if (this.data.length > 0) {
            out.write(this.data);
        }
        out.flush();
    }

    public static Response readFromStream(DataInputStream in) throws IOException {
        byte status = in.readByte();
        
        int msgLen = in.readShort();
        String message = "";
        if (msgLen > 0) {
            message = in.readUTF();
        }
        
        int dataLen = in.readShort();
        byte[] data = new byte[dataLen];
        if (dataLen > 0) {
            in.readFully(data);
        }
        
        return new Response(status, message, data);
    }

    // Getters
    public byte getStatus() { return status; }
    public String getMessage() { return message; }
    public byte[] getData() { return data; }
}
