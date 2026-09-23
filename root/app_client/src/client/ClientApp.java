package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ClientSocket clientSocket;

    // Thành phần Màn hình Game
    private JLabel lblQuestionImage;
    private JLabel lblScore;
    private JButton[] btnAnswers = new JButton[4];
    private String currentCorrectAnswer = "";
    private int score = 0;

    public ClientApp() {
        clientSocket = new ClientSocket();

        // Cấu hình Cửa sổ ứng dụng
        setTitle("App Học Tiếng Anh Cho Bé 🎈");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Thêm 2 màn hình vào CardLayout
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createGamePanel(), "GAME");

        add(mainPanel);

        // Tự động đóng kết nối Socket khi ngắt ứng dụng
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientSocket.close();
            }
        });
    }

    // ==========================================
    // MÀN HÌNH 1: ĐĂNG NHẬP / KẾT NỐI SERVER
    // ==========================================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 239, 204)); // Vàng kem tươi sáng

        Box box = Box.createVerticalBox();

        JLabel lblTitle = new JLabel("BÉ HỌC TIẾNG ANH 🎈");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(new Color(230, 0, 92));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtName = new JTextField(15);
        txtName.setFont(new Font("Arial", Font.PLAIN, 20));
        txtName.setMaximumSize(new Dimension(300, 40));

        JLabel lblNameHint = new JLabel("Nhập tên của bé:");
        lblNameHint.setFont(new Font("Arial", Font.BOLD, 18));
        lblNameHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnStart = new JButton("VÀO HỌC NGAY ▶");
        btnStart.setFont(new Font("Arial", Font.BOLD, 22));
        btnStart.setBackground(new Color(51, 204, 51));
        btnStart.setForeground(Color.WHITE);
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sự kiện khi nhấn "VÀO HỌC NGAY"
        btnStart.addActionListener(e -> {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bé hãy nhập tên mình vào nhé!");
                return;
            }

            // Kết nối Server (Mặc định Port 8888)
            boolean isConnected = clientSocket.connect("127.0.0.1", 8888);
            
            if (isConnected) {
                clientSocket.send("LOGIN:" + name);
                SoundPlayer.play("correct.wav");
                cardLayout.show(mainPanel, "GAME");
                loadNextQuestionMockup();
            } else {
                // Nếu chưa bật Server thực tế, vẫn cho chạy chế độ Test Giao diện
                int choice = JOptionPane.showConfirmDialog(this, 
                    "Không thấy Server! Bạn có muốn test Giao diện không?", 
                    "Thông báo", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    cardLayout.show(mainPanel, "GAME");
                    loadNextQuestionMockup();
                }
            }
        });

        box.add(lblTitle);
        box.add(Box.createVerticalStrut(30));
        box.add(lblNameHint);
        box.add(Box.createVerticalStrut(10));
        box.add(txtName);
        box.add(Box.createVerticalStrut(25));
        box.add(btnStart);

        panel.add(box);
        return panel;
    }

    // ==========================================
    // MÀN HÌNH 2: HỌC & CHƠI GAME
    // ==========================================
    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(204, 230, 255)); // Xanh nhạt
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Thanh trên cùng: Điểm số
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        lblScore = new JLabel("⭐ Điểm: 0");
        lblScore.setFont(new Font("Arial", Font.BOLD, 24));
        lblScore.setForeground(new Color(204, 102, 0));
        topPanel.add(lblScore, BorderLayout.EAST);

        JLabel lblTitle = new JLabel("Con vật này tên là gì?", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        topPanel.add(lblTitle, BorderLayout.WEST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Khung ở giữa: Hình ảnh + Nút Phát âm thanh
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        centerPanel.setOpaque(false);

        lblQuestionImage = new JLabel();
        lblQuestionImage.setPreferredSize(new Dimension(220, 220));
        lblQuestionImage.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        lblQuestionImage.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnPlaySound = new JButton("🔊 Nghe");
        btnPlaySound.setFont(new Font("Arial", Font.BOLD, 20));
        btnPlaySound.setBackground(new Color(255, 153, 51));
        btnPlaySound.setForeground(Color.WHITE);
        btnPlaySound.addActionListener(e -> SoundPlayer.play("cat.wav"));

        centerPanel.add(lblQuestionImage);
        centerPanel.add(btnPlaySound);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Khung ở dưới: 4 Nút đáp án
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setOpaque(false);

        for (int i = 0; i < 4; i++) {
            btnAnswers[i] = new JButton();
            btnAnswers[i].setFont(new Font("Arial", Font.BOLD, 22));
            btnAnswers[i].setBackground(Color.WHITE);

            int index = i;
            btnAnswers[i].addActionListener(e -> handleAnswer(btnAnswers[index].getText()));
            optionsPanel.add(btnAnswers[i]);
        }

        panel.add(optionsPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Xử lý logic khi bé chọn đáp án
    private void handleAnswer(String selectedOption) {
        if (selectedOption.equalsIgnoreCase(currentCorrectAnswer)) {
            SoundPlayer.play("correct.wav");
            score += 10;
            lblScore.setText("⭐ Điểm: " + score);
            JOptionPane.showMessageDialog(this, "🎉 Chính xác! Bé nhận được 10 điểm!");
            
            clientSocket.send("ANSWER_CORRECT");
        } else {
            SoundPlayer.play("wrong.wav");
            JOptionPane.showMessageDialog(this, "❌ Chưa đúng rồi, bé thử lại nhé!");
            
            clientSocket.send("ANSWER_WRONG");
        }
    }

    // Nạp dữ liệu giả lập để hiển thị giao diện
    private void loadNextQuestionMockup() {
        currentCorrectAnswer = "Cat";

        ImageIcon icon = new ImageIcon("resources/images/cat.png");
        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            lblQuestionImage.setIcon(new ImageIcon(img));
            lblQuestionImage.setText("");
        } else {
            lblQuestionImage.setText("📷 [Thêm cat.png vào resources/images]");
            lblQuestionImage.setFont(new Font("Arial", Font.BOLD, 14));
        }

        btnAnswers[0].setText("Dog");
        btnAnswers[1].setText("Cat");
        btnAnswers[2].setText("Bird");
        btnAnswers[3].setText("Duck");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientApp app = new ClientApp();
            app.setVisible(true);
        });
    }
}