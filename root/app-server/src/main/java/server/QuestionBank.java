/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

/**
 *
 * @author PC
 */

import common.QuestionPayload;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    private final List<QuestionPayload> questions = new ArrayList<>();

    public QuestionBank() {
        loadQuestions();
    }

    /**
     * Tự động quét file cấu hình và tải toàn bộ tài nguyên vào RAM
     */
    private void loadQuestions() {
    // 1. Đọc file questions.txt từ thư mục resources
    try (InputStream is = getClass().getResourceAsStream("/questions.txt")) {
        if (is == null) {
            System.err.println("❌ Không tìm thấy file questions.txt trong resources!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Bỏ qua dòng trống hoặc dòng chú thích
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Phân tách chuỗi bằng dấu "|"
                String[] parts = line.split("\\|");
                if (parts.length < 8) {
                    System.err.println("⚠️ Bỏ qua dòng lỗi (Thiếu thông tin): " + line);
                    continue; 
                }

                String questionText = parts[0].trim();
                String a = parts[1].trim();
                String b = parts[2].trim();
                String c = parts[3].trim();
                String d = parts[4].trim();
                String correct = parts[5].trim();
                String imageName = parts[6].trim();
                String audioName = parts[7].trim();

                // 2. Kiểm tra và đọc trực tiếp file ảnh từ thư mục /assets/images/
                byte[] imageBytes = new byte[0];
                if (!imageName.isEmpty()) {
                    imageBytes = loadResourceBytes("/assets/images/" + imageName);
                }

                // 3. Kiểm tra và đọc trực tiếp file âm thanh từ thư mục /assets/audios/ (Đã sửa lỗi đường dẫn)
                byte[] audioBytes = new byte[0];
                if (!audioName.isEmpty()) {
                    audioBytes = loadResourceBytes("/assets/audios/" + audioName);
                }

                // 4. Đóng gói vào Payload và thêm vào bể chứa
                QuestionPayload payload = new QuestionPayload(
                    questionText, a, b, c, d, correct, imageBytes, audioBytes
                );
                questions.add(payload);
            }
        }
        System.out.println("✅ Đã tải thành công " + questions.size() + " câu hỏi từ Resource vào bộ nhớ RAM!");
    } catch (IOException e) {
        System.err.println("❌ Có lỗi xảy ra trong quá trình nạp bể câu hỏi!");
        e.printStackTrace();
    }
}


    /**
     * Hàm tiện ích đọc file trong Resource chuyển thành byte[] không cần đường dẫn ổ đĩa
     */
    private byte[] loadResourceBytes(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Không tìm thấy file tài nguyên: " + resourcePath);
                return new byte[0];
            }
            // Đọc toàn bộ luồng dữ liệu của file thành mảng byte
            return is.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Lấy toàn bộ danh sách câu hỏi
     */
    public List<QuestionPayload> getAllQuestions() {
        return questions;
    }

    /**
     * Lấy ngẫu nhiên một câu hỏi từ bể câu hỏi
     */
    public QuestionPayload getRandomQuestion() {
        if (questions.isEmpty()) return null;
        int randomIndex = (int) (Math.random() * questions.size());
        return questions.get(randomIndex);
    }
}

