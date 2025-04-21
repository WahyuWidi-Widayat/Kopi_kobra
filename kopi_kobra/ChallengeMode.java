package kopi_kobra;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ChallengeMode extends JPanel implements ActionListener, KeyListener {
   

    int boardWidth, boardHeight, tileSize = 25;
    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    Tile food;
    ArrayList<Tile> obstacles;
    Random random;
    int velocityX, velocityY;
    Timer gameLoop;
    String snakeColor;
    String snakeShape;
    boolean gameOver = false;
    static int Timer = 100;
    static int obstacle = 20;
    
   
    private Image backgroundImage;
    private Image foodImage; 
    
    // Konstruktor yang menerima parameter color dan shape
    public ChallengeMode(int boardWidth, int boardHeight, String color, String shape) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.snakeColor = color;
        this.snakeShape = shape;

        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        random = new Random();

        obstacles = new ArrayList<>();
        generateObstacles();

        spawnFood();
        velocityX = 1;
        velocityY = 0;

        gameLoop = new Timer(Timer, this);
        gameLoop.start();
          try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\BackGround\\2.png"));
            foodImage = ImageIO.read(new File("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\Food\\1.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gambar latar belakang tidak ditemukan.");
        }

    }
   
    private void generateObstacles() {
        int obstacleCount = obstacle;
        for (int i = 0; i < obstacleCount; i++) {
            int x = random.nextInt(boardWidth / tileSize);
            int y = random.nextInt(boardHeight / tileSize);
            obstacles.add(new Tile(x, y));
        }
    }

private void spawnFood() {
    boolean validPosition = false;

    while (!validPosition) {
        // Tentukan posisi makanan secara acak
        int x = random.nextInt(boardWidth / tileSize);
        int y = random.nextInt(boardHeight / tileSize);

        food = new Tile(x, y);

        // Validasi lokasi makanan
        boolean isOutOfBounds = (x < 0 || x >= boardWidth / tileSize || 
                                 y < 0 || y >= boardHeight / tileSize);
        boolean isOnSnake = snakeBody.stream().anyMatch(part -> part.x == x && part.y == y);
        boolean isOnObstacle = obstacles.stream().anyMatch(obstacle -> obstacle.x == x && obstacle.y == y);

        // Jika tidak bertabrakan dengan rintangan, tubuh ular, atau keluar dari batas
        if (!isOutOfBounds && !isOnSnake && !isOnObstacle) {
            validPosition = true;
        }
    }
}

    
    private void move() {
        snakeBody.add(0, new Tile(snakeHead.x, snakeHead.y));
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        if (!(snakeHead.x == food.x && snakeHead.y == food.y)) {
            snakeBody.remove(snakeBody.size() - 1);
        } else {
            spawnFood();
        }
    }

private void checkCollision() {
    if (snakeHead.x == food.x && snakeHead.y == food.y) {
        System.out.println("Food eaten at: " + food.x + ", " + food.y);
        spawnFood(); // Panggil spawnFood untuk makanan baru
    }

    // Tabrakan dengan rintangan
    for (Tile obstacle : obstacles) {
        if (snakeHead.x == obstacle.x && snakeHead.y == obstacle.y) {
            System.out.println("Collision with obstacle at: " + obstacle.x + ", " + obstacle.y);
            gameOver = true;
        }
    }

    // Tabrakan dengan tubuh ular
    for (Tile bodyPart : snakeBody) {
        if (snakeHead.x == bodyPart.x && snakeHead.y == bodyPart.y) {
            System.out.println("Collision with body part at: " + bodyPart.x + ", " + bodyPart.y);
            gameOver = true;
        }
    }

    // Tabrakan dengan dinding
    if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || 
        snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
        System.out.println("Collision with wall at: " + snakeHead.x + ", " + snakeHead.y);
        gameOver = true;
    }
}


    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(new Color(128, 0, 128));
        }
        if (foodImage != null) {
            g.drawImage(foodImage, food.x * tileSize, food.y * tileSize, tileSize, tileSize, this);
        } else {
            // Jika gambar tidak tersedia, gunakan warna merah sebagai pengganti
           
            g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);
        }
        draw(g);
    }
    
    private Color getColorFromString(String color) {
        return switch (color) {
            case "Blue" -> Color.blue;
            case "Yellow" -> Color.yellow;
            case "White" -> Color.white;
            case "Red" -> Color.red;
            case "Purple" -> new Color(128, 0, 128); // Warna ungu
            default -> Color.green;  // Default color jika tidak ada yang cocok
        };
    }


    private void draw(Graphics g) {
        // Gambar grid
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Draw obstacles
        for (Tile obstacle : obstacles) {
            g.setColor(Color.gray);
            g.fill3DRect(obstacle.x * tileSize, obstacle.y * tileSize, tileSize, tileSize, true);
        }

      

        // Draw snake head
        g.setColor(getColorFromString(snakeColor));  // Gunakan warna yang dipilih untuk kepala ular
        int headX = snakeHead.x * tileSize;
        int headY = snakeHead.y * tileSize;

        if ("Oval".equals(snakeShape)) {
            g.fillOval(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
        } else if ("Star".equals(snakeShape)) {
            drawStar(g, snakeHead.x * tileSize, snakeHead.y * tileSize);
        } else {
            g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);
        }

        // Gambar muka ular pada kepala
        drawSnakeFace(g, headX, headY);
        
        // Draw snake body with the chosen color and shape
        for (Tile snakePart : snakeBody) {
            Color bodyColor = getRandomColor();  // Gunakan warna acak untuk setiap bagian tubuh
            g.setColor(bodyColor);

            int x = snakePart.x * tileSize;
            int y = snakePart.y * tileSize;

            switch (snakeShape) {
                case "Oval" -> g.fillOval(x, y, tileSize, tileSize);
                case "Star" -> drawStar(g, x, y);
                default -> g.fill3DRect(x, y, tileSize, tileSize, true); // Default kotak
            }
        }

        // Display score or game over message
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + snakeBody.size(), tileSize - 16, tileSize);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", boardWidth / 2 - 100, boardHeight / 2);
        } else {
            g.drawString("Score: " + snakeBody.size(), tileSize - 16, tileSize);
        }
    }

    
    private void drawStar(Graphics g, int x, int y) {
        int[] xPoints = { x + tileSize / 2, x + tileSize * 3 / 4, x + tileSize, x + tileSize * 5 / 6, 
                          x + tileSize * 3 / 4, x + tileSize / 2, x + tileSize / 4, x + tileSize / 6, x, x + tileSize / 4 };
        int[] yPoints = { y, y + tileSize / 4, y + tileSize / 4, y + tileSize / 2, 
                          y + tileSize * 3 / 4, y + tileSize * 2 / 3, y + tileSize * 3 / 4, y + tileSize / 2, y + tileSize / 4, y + tileSize / 4 };
        g.fillPolygon(xPoints, yPoints, 10);
    }

    
    private Color getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return new Color(r, g, b);
    }

    // Fungsi untuk menggambar muka ular (mata dan mulut)
    private void drawSnakeFace(Graphics g, int x, int y) {
        // Gambar mata
        g.setColor(Color.white);
        int eyeRadius = 5;
        g.fillOval(x + 7, y + 7, eyeRadius, eyeRadius);  // Mata kiri
        g.fillOval(x + 15, y + 7, eyeRadius, eyeRadius); // Mata kanan

        // Gambar pupil mata
        g.setColor(Color.black);
        g.fillOval(x + 9, y + 9, 3, 3);  // Pupil kiri
        g.fillOval(x + 17, y + 9, 3, 3); // Pupil kanan

        // Gambar mulut
        g.setColor(Color.black);
        g.drawArc(x + 5, y + 15, 15, 10, 0, -180); // Mulut ular
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            checkCollision();
            repaint();
        } else {
            gameLoop.stop();
            showGameOverDialog();
        }
    }

private void showGameOverDialog() {
    // Simpan skor ke database sebelum dialog ditampilkan
    App.saveScore(snakeBody.size(), "Challenge Mode"); // Mode Challenge

    int option = JOptionPane.showOptionDialog(this,
            "Game Over! Apakah Anda ingin kembali ke menu utama atau keluar?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Main Menu", "Keluar"},
            "Main Menu");

    if (option == JOptionPane.YES_OPTION) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.getContentPane().removeAll();
            parentFrame.repaint();
            App.showMainMenu();
        }
    } else if (option == JOptionPane.NO_OPTION) {
        System.exit(0); // Tutup aplikasi sepenuhnya
    }
}


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
