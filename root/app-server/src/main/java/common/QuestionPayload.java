/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author PC
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class QuestionPayload implements SerializablePayload {
    // 1. Các thuộc tính dạng chuỗi (Text)
    private String questionText;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correctAnswer;

    // 2. Các thuộc tính dạng mảng byte (File)
    private byte[] imageBytes;
    private byte[] audioBytes;

    // Constructor mặc định cho bên gửi (Server)
    public QuestionPayload(String questionText, String answerA, String answerB, String answerC, String answerD, 
                           String correctAnswer, byte[] imageBytes, byte[] audioBytes) {
        this.questionText = questionText;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.imageBytes = (imageBytes != null) ? imageBytes : new byte[0];
        this.audioBytes = (audioBytes != null) ? audioBytes : new byte[0];
    }

    /**
     * CONSTRUCTOR ĐẶC BIỆT: Dùng cho bên nhận (Client) để giải nén mảng byte thành Object
     */
    public QuestionPayload(byte[] totalBytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(totalBytes);
             DataInputStream dis = new DataInputStream(bais)) {
            
            // Đọc các chuỗi văn bản theo đúng thứ tự đã ghi
            this.questionText = dis.readUTF();
            this.answerA = dis.readUTF();
            this.answerB = dis.readUTF();
            this.answerC = dis.readUTF();
            this.answerD = dis.readUTF();
            this.correctAnswer = dis.readUTF();

            // Đọc mảng byte của Ảnh (Đọc độ dài trước, sau đó đọc mảng dữ liệu)
            int imgLength = dis.readInt();
            this.imageBytes = new byte[imgLength];
            if (imgLength > 0) {
                dis.readFully(this.imageBytes);
            }

            // Đọc mảng byte của Âm thanh
            int audioLength = dis.readInt();
            this.audioBytes = new byte[audioLength];
            if (audioLength > 0) {
                dis.readFully(this.audioBytes);
            }
        }
    }

    /**
     * TỰ CHUYỂN ĐỔI CẤU TRÚC THÀNH MẢNG BYTE (Hiện thực hóa interface của bạn)
     */
    @Override
    public byte[] toBytes() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {
            
            // Ghi các chuỗi văn bản bằng chuẩn UTF-8
            dos.writeUTF(questionText);
            dos.writeUTF(answerA);
            dos.writeUTF(answerB);
            dos.writeUTF(answerC);
            dos.writeUTF(answerD);
            dos.writeUTF(correctAnswer);

            // Ghi mảng byte Ảnh: Lưu độ dài trước, sau đó lưu mảng bytes
            dos.writeInt(imageBytes.length);
            if (imageBytes.length > 0) {
                dos.write(imageBytes);
            }

            // Ghi mảng byte Âm thanh: Lưu độ dài trước, sau đó lưu mảng bytes
            dos.writeInt(audioBytes.length);
            if (audioBytes.length > 0) {
                dos.write(audioBytes);
            }

            dos.flush();
            return baos.toByteArray(); // Trả về mảng byte hợp nhất cuối cùng
        }
    }

    // --- CÁC HÀM GETTER ĐỂ CLIENT TRÍCH XUẤT DỮ LIỆU ---
    public String getQuestionText() { return questionText; }
    public String getAnswerA() { return answerA; }
    public String getAnswerB() { return answerB; }
    public String getAnswerC() { return answerC; }
    public String getAnswerD() { return answerD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public byte[] getImageBytes() { return imageBytes; }
    public byte[] getAudioBytes() { return audioBytes; }
}
