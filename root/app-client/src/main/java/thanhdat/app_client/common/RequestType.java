/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package thanhdat.app_client.common;
/**
 *
 * @author PC
 */
public enum RequestType {
    LOGIN(1),
    REGISTER(2),
    GET_QUIZ(3),
    SUBMIT_ANSWER(4),
    GET_LEADERBOARD(5);
    
    private byte code;
    
    private RequestType(int id) {
        this.code = (byte) id;
    }
    
    public byte code() {
        return code;
    }
    
        public static RequestType fromByte(byte code) {
        for (RequestType type : RequestType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null; // Hoặc ném Exception nếu mã byte không hợp lệ
    }
}
