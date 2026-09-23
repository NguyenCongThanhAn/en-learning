/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package common;

import java.io.IOException;

/**
 *
 * @author PC
 */
public interface SerializablePayload {
    /**
     * Tự chuyển đổi cấu trúc thuộc tính của lớp thành mảng byte
     */
    byte[] toBytes() throws IOException;
}
