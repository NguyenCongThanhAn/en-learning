package thanhdat.app_client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import thanhdat.app_client.common.QuestionPayload;
import thanhdat.app_client.common.Request;
import thanhdat.app_client.common.RequestType;
import thanhdat.app_client.common.Response;
import thanhdat.app_client.common.StatusCode;

public class ClientApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ClientSocket clientSocket;
    private int score;

    // câu hỏi
    private QuestionPayload questionPayload;

    // Thành phần Màn hình Game
    private JLabel lblQuestionImage;
    private JLabel lblScore;
    private JButton[] btnAnswers = new JButton[4];
    private byte[] currentAudioBytes;

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

                // login
                // 2. Chuyển sang Màn hình Game
                cardLayout.show(mainPanel, "GAME");
                SoundPlayer.play("correct.wav");

                // 3. Xin Server câu hỏi đầu tiên
                Request request = new Request(RequestType.GET_QUIZ.code(), name, new byte[0]);
                clientSocket.sendRequest(request);
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
            if (currentAudioBytes != null && currentAudioBytes.length > 0) {
                // Gọi thẳng hàm mới của SoundPlayer, tự động chạy Thread ngầm bên trong luôn rồi
                SoundPlayer.play(currentAudioBytes);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "🔊 Không có dữ liệu âm thanh cho câu hỏi này!");
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
            btnAnswers[i].addActionListener(e -> {
                try {
                    handleAnswer(btnAnswers[index].getText());
                } catch (IOException ex) {
                    Logger.getLogger(ClientApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            optionsPanel.add(btnAnswers[i]);
        }

        panel.add(optionsPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Gửi đáp án bé chọn cho Server
    private void handleAnswer(String selectedOption) throws IOException {
        if (questionPayload.getCorrectAnswer().equals(selectedOption)) {
            updateScore(score += 10);
        }
        Request request = new Request(RequestType.LOGIN.code(), null, new byte[0]);
        clientSocket.sendRequest(request);
        Response response = clientSocket.receiveResponse();
        if (response.getStatus() == StatusCode.SUCCESS.code()) {
            questionPayload = new QuestionPayload(response.getData());
            String[] options = {
                questionPayload.getAnswerA(),
                questionPayload.getAnswerB(),
                questionPayload.getAnswerC(),
                questionPayload.getAnswerD()
            };
            displayQuestion(questionPayload);
        }
    }

    // =========================================================================
    // HÀM DÀNH CHO SERVER ĐIỀU KHIỂN GIAO DIỆN CLIENT (PUBLIC)
    // =========================================================================
    /**
     * Hàm này được gọi khi nhận tin nhắn chứa câu hỏi từ Server Ví dụ Server
     * gửi: imageName="cat.png", soundFile="cat.wav",
     * options=["Dog","Cat","Bird","Duck"]
     */
    public void displayQuestion(QuestionPayload payload) {
        if (questionPayload == null) {
            return;
        }

        // 1. Lưu lại mảng byte âm thanh của câu hỏi hiện tại để phát khi bấm nút nghe
        this.currentAudioBytes = questionPayload.getAudioBytes();

        // 2. Cập nhật nội dung câu hỏi chữ (Ví dụ bạn có lblQuestionText trên giao diện)
        // lblQuestionText.setText(questionPayload.getQuestionText());
        // 3. Cập nhật hình ảnh trực tiếp từ mảng byte (Không đọc file từ ổ cứng nữa)
        byte[] imageBytes = questionPayload.getImageBytes();

        if (imageBytes != null && imageBytes.length > 0) {
            // Khởi tạo ảnh từ mảng byte nhận từ mạng
            ImageIcon icon = new ImageIcon(imageBytes);

            // Co dãn ảnh về kích thước khung JLabel (Bạn dùng kích thước 220x220 hoặc 200x200 tùy cấu hình giao diện)
            Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
            lblQuestionImage.setIcon(new ImageIcon(img));
            lblQuestionImage.setText("");
        } else {
            // Trường hợp Server không gửi kèm ảnh minh họa
            lblQuestionImage.setIcon(null);
            lblQuestionImage.setText("📷 Không có ảnh minh họa");
            lblQuestionImage.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // 4. Trích xuất 4 đáp án từ payload đưa vào mảng
        String[] options = {
            questionPayload.getAnswerA(),
            questionPayload.getAnswerB(),
            questionPayload.getAnswerC(),
            questionPayload.getAnswerD()
        };

        // 5. Cập nhật chữ trên 4 nút bấm đáp án
        for (int i = 0; i < 4; i++) {
            if (btnAnswers[i] != null && options[i] != null) {
                btnAnswers[i].setText(options[i]);

                // Reset lại trạng thái các nút (nếu ở câu trước bạn có đổi màu nút khi chọn)
                btnAnswers[i].setEnabled(true);
                btnAnswers[i].setBackground(null);
            }
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
