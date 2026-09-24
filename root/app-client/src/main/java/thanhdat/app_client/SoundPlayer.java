package thanhdat.app_client;

import java.io.ByteArrayInputStream;
import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {

    public static void play(String soundName) {
        // Chạy trên Thread riêng để không làm giật/đơ giao diện GUI
        new Thread(() -> {
            try {
                File soundFile = new File("resources/sounds/" + soundName);
                if (soundFile.exists()) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                } else {
                    System.out.println("⚠️ Chưa thấy file âm thanh: resources/sounds/" + soundName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public static void play(byte[] audioBytes) {
        if (audioBytes == null || audioBytes.length == 0) {
            System.out.println("⚠️ Dữ liệu âm thanh trống, không thể phát!");
            return;
        }

        // Chạy trên Thread riêng để không làm giật/đơ giao diện GUI giống hàm cũ của bạn
        new Thread(() -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
                 AudioInputStream audioIn = AudioSystem.getAudioInputStream(bais)) {
                
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                
                // Lắng nghe khi phát xong thì tự động giải phóng bộ nhớ của Clip
                clip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                
            } catch (Exception e) {
                System.out.println("❌ Lỗi không thể phát âm thanh từ Socket!");
                e.printStackTrace();
            }
        }).start();
    }
}