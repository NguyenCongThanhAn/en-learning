/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package common;
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
    
    private byte id;
    
    private RequestType(int id) {
        this.id = (byte) id;
    }
    
    public byte toByte() {
        return id;
    }
    
}
