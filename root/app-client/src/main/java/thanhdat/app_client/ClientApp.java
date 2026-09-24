package thanhdat.app_client;

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
    private String currentSoundFile = "";

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
        panel.setBackground(new Color(255, 239, 204));

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

            // Gửi yêu cầu kết nối tới Server
            boolean isConnected = clientSocket.connect("0.tcp.ap.ngrok.io", 28501);
            if (isConnected) {
                // 1. Gửi tên đăng nhập cho Server
                clientSocket.send("LOGIN:" + name);
                
                // 2. Chuyển sang Màn hình Game
                cardLayout.show(mainPanel, "GAME");
                SoundPlayer.play("correct.wav");
                
                // 3. Xin Server câu hỏi đầu tiên
                clientSocket.send("GET_QUESTION");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể kết nối tới Server! Vui lòng kiểm tra lại.");
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
        panel.setBackground(new Color(204, 230, 255));
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
        
        btnPlaySound.addActionListener(e -> {
            if (!currentSoundFile.isEmpty()) {
                SoundPlayer.play(currentSoundFile);
            }
        });

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
            // Khi bé bấm nút đáp án -> Gửi lựa chọn về cho Server kiểm tra
            btnAnswers[i].addActionListener(e -> handleAnswer(btnAnswers[index].getText()));
            optionsPanel.add(btnAnswers[i]);
        }

        panel.add(optionsPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Gửi đáp án bé chọn cho Server
    private void handleAnswer(String selectedOption) {
        clientSocket.send("SUBMIT_ANSWER:" + selectedOption);
    }

    // =========================================================================
    // HÀM DÀNH CHO SERVER ĐIỀU KHIỂN GIAO DIỆN CLIENT (PUBLIC)
    // =========================================================================

    /**
     * Hàm này được gọi khi nhận tin nhắn chứa câu hỏi từ Server
     * Ví dụ Server gửi: imageName="cat.png", soundFile="cat.wav", options=["Dog","Cat","Bird","Duck"]
     */
    public void displayQuestion(String imageName, String soundFile, String[] options) {
        this.currentSoundFile = soundFile;

        // 1. Cập nhật hình ảnh từ Server
        ImageIcon icon = new ImageIcon("resources/images/" + imageName);
        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            lblQuestionImage.setIcon(new ImageIcon(img));
            lblQuestionImage.setText("");
        } else {
            lblQuestionImage.setIcon(null);
            lblQuestionImage.setText("📷 [" + imageName + "]");
            lblQuestionImage.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // 2. Cập nhật chữ trên 4 nút bấm đáp án
        for (int i = 0; i < 4; i++) {
            btnAnswers[i].setText(options[i]);
        }
    }

    /**
     * Cập nhật điểm số khi Server trả kết quả
     */
    public void updateScore(int newScore) {
        lblScore.setText("⭐ Điểm: " + newScore);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientApp app = new ClientApp();
            app.setVisible(true);
        });
    }
}