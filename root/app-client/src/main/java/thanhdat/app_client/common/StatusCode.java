/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package thanhdat.app_client.common;

/**
 *
 * @author PC
 */
public enum StatusCode {
    SUCCESS(0),
    FAIL(1),
    ERROR(2);
    
    private byte code;
    
    private StatusCode(int id) {
        this.code = (byte) id;
    }
    
    public byte code() {
        return code;
    }
    
        public static StatusCode fromByte(byte code) {
        for (StatusCode type : StatusCode.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null; // Hoặc ném Exception nếu mã byte không hợp lệ
    }
}
