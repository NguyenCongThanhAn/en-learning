package client;

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
}