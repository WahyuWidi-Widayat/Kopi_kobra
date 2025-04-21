package kopi_kobra;
 
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javazoom.jl.player.Player;
import java.io.FileInputStream;


public class App {
    private static JFrame frame;
    private static String normalModeColor = "Green";
    private static String normalModeShape = "Rectangle";
    private static String challengeModeColor = "Blue";
    private static String challengeModeShape = "Oval";

    private static int playerId;
    private static MusicPlayer musicPlayer;

public static void main(String[] args) {
  
    musicPlayer = new MusicPlayer();
        musicPlayer.playMusic("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Music\\1.mp3");

    frame = new JFrame("Kopi Kobra");
    adjustScreenSize(); // Atur ukuran layar sesuai resolusi laptop
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    showLoginDialog(); // Menampilkan dialog login
}

static class MusicPlayer {
    private Player player;
    private Thread musicThread;
    private boolean playing = true;

    public void playMusic(String filePath) {
        musicThread = new Thread(() -> {
            try {
                while (playing) {
                    try (FileInputStream fis = new FileInputStream(filePath)) {
                        player = new Player(fis);
                        player.play();
                    } catch (Exception e) {
                        System.out.println("Error playing music: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in music loop: " + e.getMessage());
            }
        });
        musicThread.start();
    }

    public void stopMusic() {
        playing = false;  // Hentikan perulangan musik
        if (player != null) {
            player.close();
        }
        if (musicThread != null) {
            musicThread.interrupt();
        }
    }
}


    private static void showLoginDialog() {
        JTextField usernameField = new JTextField(20);
       
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(2, 2));
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);

        int result = JOptionPane.showConfirmDialog(frame, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();

            if (saveUserToDatabase(username)) {  // Save username only
                JOptionPane.showMessageDialog(frame, "Username Anda: " + username + "\nID: " + String.format("%05d", playerId) + "\nSelamat bermain!");
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Registrasi gagal. Coba lagi.");
                showLoginDialog();
            }
        }
    }
    
    private static void adjustScreenSize() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Ambil resolusi layar
    frame.setSize(screenSize.width, screenSize.height); // Set ukuran JFrame sesuai layar
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set JFrame menjadi fullscreen
}



    private static boolean saveUserToDatabase(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username) VALUES (?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted == 0) {
                System.out.println("No rows inserted. Registration failed.");
                return false;
            }

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                playerId = rs.getInt(1); 
                System.out.println("Registration successful. Player ID: " + playerId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return false;
    }


    // Connect to database
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/snakegame";
        String user = "root";
        String password = ""; // Ganti dengan password MySQL Anda jika ada
        System.out.println("Connecting to database...");  // Debugging
        return DriverManager.getConnection(url, user, password);
    }

   
            
public static void showMainMenu() {
    
    JPanel mainMenu = new JPanel() {
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            ImageIcon backgroundImage = new ImageIcon("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\BackGround\\1.png");
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    };
    mainMenu.setLayout(new GridBagLayout()); // Layout untuk tombol
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.CENTER;

    JLabel titleLabel = new JLabel("Kopi Kobra") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setFont(new Font("Serif", Font.BOLD, 72));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

            g2d.setColor(Color.GRAY);
            g2d.drawString(getText(), x + 4, y + 4);

            g2d.setColor(Color.WHITE);
            g2d.drawString(getText(), x, y);
            g2d.dispose();
        }
    };
    titleLabel.setPreferredSize(new Dimension(800, 100));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    titleLabel.setOpaque(false);

    gbc.gridx = 0;
    gbc.gridy = 0;
    mainMenu.add(titleLabel, gbc);


    // Membuat tombol-tombol untuk berbagai mode permainan
    JButton startButton = createImageButton("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\Button\\1.png");
        JButton challengeButton = createImageButton("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\Button\\2.png");
        JButton leaderboardButton = createImageButton("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\Button\\3.png");
        JButton tutorialButton =createImageButton("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\Button\\4.png");
        JButton customizeButton = createImageButton("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\Button\\5.png");
    
        gbc.gridy = 1;
        mainMenu.add(startButton, gbc);
        gbc.gridy = 2;
        mainMenu.add(challengeButton, gbc);
        gbc.gridy = 3;
        mainMenu.add(leaderboardButton, gbc);
        gbc.gridy = 4;
        mainMenu.add(tutorialButton, gbc);
        gbc.gridy = 5;
        mainMenu.add(customizeButton, gbc);
    
        frame.add(mainMenu);
        frame.revalidate();
        frame.repaint();
    
        // ActionListener untuk tombol Normal Mode
        startButton.addActionListener(e -> {
            frame.remove(mainMenu);
         
            SnakeGame snakeGame = new SnakeGame(1625, 825, normalModeColor, normalModeShape); 
            
            frame.add(snakeGame);
            frame.pack();
            snakeGame.requestFocus();
        });
    
        // ActionListener untuk tombol Challenge Mode
        challengeButton.addActionListener(e -> {
            frame.remove(mainMenu);
            ChallengeMode challengeMode = new ChallengeMode(1625, 825, challengeModeColor, challengeModeShape); // Menggunakan konfigurasi Challenge Mode
            frame.add(challengeMode);
            frame.pack();
            challengeMode.requestFocus();
    
        });
    
        // ActionListeners lainnya untuk tombol leaderboard, tutorial, dan customize
        leaderboardButton.addActionListener(e -> showLeaderboard());
        tutorialButton.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Gunakan tombol arah untuk menggerakkan ular:\n" +
                "- Panah atas untuk bergerak ke atas\n" +
                "- Panah bawah untuk bergerak ke bawah\n" +
                "- Panah kiri untuk bergerak ke kiri\n" +
                "- Panah kanan untuk bergerak ke kanan\n\n" +
                "Tujuan: Kumpulkan makanan sebanyak mungkin tanpa menabrak tubuh ular sendiri atau dinding.",
                "Tutorial", JOptionPane.INFORMATION_MESSAGE));
        customizeButton.addActionListener(e -> showCustomizeDialog());
    
        frame.setVisible(true);
    }
    
        
     
    private static JButton createImageButton(String imagePath) {
        JButton button = new JButton();
        button.setIcon(new ImageIcon(imagePath));
        button.setContentAreaFilled(false); // Hapus latar belakang default tombol
        button.setBorderPainted(false); // Hapus border default
        button.setFocusPainted(false); // Hapus efek fokus tombol
        button.setPreferredSize(new Dimension(250, 70)); // Ukuran tombol
        return button;
    }

    
public static void saveScore(int score, String mode) {
    if (playerId == 0) {
        System.out.println("Player ID is not set. Cannot save score.");
        return;
    }

    String query = "INSERT INTO leaderboard (user_id, score, date_played, mode) VALUES (?, ?, NOW(), ?)";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, playerId);
        stmt.setInt(2, score);
        stmt.setString(3, mode);

        int rowsInserted = stmt.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Score saved successfully for player ID: " + playerId);
        }
    } catch (SQLException e) {
        System.out.println("Error saving score: " + e.getMessage());
    }
}



private static void showLeaderboard() {
    JFrame leaderboardFrame = new JFrame("Leaderboard");
    leaderboardFrame.setSize(500, 400);
    leaderboardFrame.setLocationRelativeTo(null);

    String[] columnNames = {"ID", "Player", "Score", "Date", "Mode"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(model);

    JButton clearButton = new JButton("Clear Leaderboard");
    clearButton.addActionListener(e -> clearLeaderboard(model));

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {
        String query = "SELECT u.id, u.username, l.score, l.date_played, l.mode " +
                       "FROM leaderboard l " +
                       "JOIN users u ON l.user_id = u.id " +
                       "ORDER BY l.score DESC LIMIT 10";

        ResultSet rs = stmt.executeQuery(query);
        boolean dataFound = false;

        while (rs.next()) {
            String idUser = String.format("%05d", rs.getInt("id"));
            String player = rs.getString("username");
            int score = rs.getInt("score");
            Timestamp date = rs.getTimestamp("date_played");
            String mode = rs.getString("mode");

            model.addRow(new Object[]{idUser, player, score, date, mode});
            dataFound = true;
        }

        if (!dataFound) {
            JOptionPane.showMessageDialog(leaderboardFrame, "No leaderboard data available.", "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving leaderboard data: " + e.getMessage());
        e.printStackTrace();
    }

    leaderboardFrame.add(new JScrollPane(table), BorderLayout.CENTER);
    leaderboardFrame.add(clearButton, BorderLayout.SOUTH);
    leaderboardFrame.setVisible(true);
}


private static void clearLeaderboard(DefaultTableModel model) {
    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the leaderboard?", "Clear Leaderboard", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM leaderboard");
            model.setRowCount(0);
            JOptionPane.showMessageDialog(null, "Leaderboard cleared successfully!");
        } catch (SQLException e) {
            System.out.println("Error clearing leaderboard: " + e.getMessage());
        }
    }
}

    // Method to create custom buttons for the main menu
    private static JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(50, 205, 50));
        button.setForeground(Color.white);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
        button.setPreferredSize(new Dimension(250, 70));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(34, 139, 34));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 205, 50));
            }
        });

        return button;
    }

private static void showCustomizeDialog() {
    JDialog customizeDialog = new JDialog(frame, "Customize Snake", true);
    customizeDialog.setSize(1500, 300);
    customizeDialog.setLayout(new FlowLayout());

    JLabel modeLabel = new JLabel("Select Mode:");
    JComboBox<String> modeOptions = new JComboBox<>(new String[]{"Normal Mode", "Challenge Mode"});

    JLabel colorLabel = new JLabel("Select Snake Color:");
    JComboBox<String> colorOptions = new JComboBox<>(new String[]{"Green", "Blue", "Yellow", "White", "Red"});

    JLabel shapeLabel = new JLabel("Select Snake Shape:");
    JComboBox<String> shapeOptions = new JComboBox<>(new String[]{"Rectangle", "Oval", "Star"});
    JFrame frame = new JFrame("Pengatur Tampilan");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1400, 800);
    frame.setLayout(new BorderLayout()); // Ubah ke BorderLayout untuk fleksibilitas

    // Panel untuk Pengatur Kecepatan

   
    modeOptions.addActionListener(e -> {
        String selectedMode = (String) modeOptions.getSelectedItem();
        if ("Normal Mode".equals(selectedMode)) {
            colorOptions.setSelectedItem(normalModeColor);
            shapeOptions.setSelectedItem(normalModeShape);
        } else {
            colorOptions.setSelectedItem(challengeModeColor);
            shapeOptions.setSelectedItem(challengeModeShape);
        }
    });


/* Tombol Tingkat kesulitan */
JLabel SpeedLevel = new JLabel("Select Speed Level:");
    JButton easyButt = new JButton("Easy"); 
    easyButt.setPreferredSize(new Dimension(100, 40));
    easyButt.setBounds(200, 600, 500, 40);
    easyButt.addActionListener(e ->{
        SnakeGame.Timer = 100;
        ChallengeMode.Timer = 100;

    });

    JButton MedButt = new JButton("Medium"); 
    MedButt.setPreferredSize(new Dimension(100, 40));
    MedButt.setBounds(200, 600, 500, 40);
    MedButt.addActionListener(e ->{
        SnakeGame.Timer = 70;
        ChallengeMode.Timer = 70;
    });

    JButton HardButt= new JButton("Hard"); 
    HardButt.setPreferredSize(new Dimension(100, 40));
    HardButt.setBounds(200, 600, 500, 40);
    HardButt.addActionListener(e ->{
        SnakeGame.Timer = 40;
        ChallengeMode.Timer = 40;
    });

    /*Tombol Banyak Rintangan */
    JLabel obstacle = new JLabel("Select obstacle:");
      JButton easyButt1= new JButton("Easy"); 
    easyButt1.setPreferredSize(new Dimension(100, 40));
    easyButt1.setBounds(200, 600, 500, 40);
    easyButt1.addActionListener(e ->{
       ChallengeMode.obstacle=20;

    });

    JButton MedButt1 = new JButton("Medium"); 
    MedButt1.setPreferredSize(new Dimension(100, 40));
    MedButt1.setBounds(200, 600, 500, 40);
    MedButt1.addActionListener(e ->{
        ChallengeMode.obstacle=30;
    });

    JButton HardButt1= new JButton("Hard"); 
    HardButt1.setPreferredSize(new Dimension(100, 40));
    HardButt1.setBounds(200, 600, 500, 40);
    HardButt1.addActionListener(e ->{
        ChallengeMode.obstacle=40;
    });

    JButton okButton = new JButton("OK");
    okButton.setPreferredSize(new Dimension(100, 40));
    okButton.setBounds(200, 600, 500, 40);
    okButton.addActionListener(e -> {
        String selectedMode = (String) modeOptions.getSelectedItem();
        String selectedColor = (String) colorOptions.getSelectedItem();
        String selectedShape = (String) shapeOptions.getSelectedItem();

        if ("Normal Mode".equals(selectedMode)) {
            normalModeColor = selectedColor;
            normalModeShape = selectedShape;
        } else {
            challengeModeColor = selectedColor;
            challengeModeShape = selectedShape;
        }

        System.out.println("Customized snake for " + selectedMode + ": Color - " + selectedColor + ", Shape - " + selectedShape);
        customizeDialog.dispose();
    });

    customizeDialog.add(modeLabel);
    customizeDialog.add(modeOptions);
    customizeDialog.add(colorLabel);
    customizeDialog.add(colorOptions);
    customizeDialog.add(shapeLabel);
    customizeDialog.add(shapeOptions);
    
    customizeDialog.add(SpeedLevel);
    customizeDialog.add(easyButt);
    customizeDialog.add(MedButt);
    customizeDialog.add(HardButt);
    customizeDialog.add(obstacle);
    customizeDialog.add(easyButt1);
    customizeDialog.add(MedButt1);
    customizeDialog.add(HardButt1);
    
    customizeDialog.add(okButton);
    customizeDialog.setVisible(true);
}
}
