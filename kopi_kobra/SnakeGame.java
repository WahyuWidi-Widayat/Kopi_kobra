package kopi_kobra;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
   

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    //snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    Color snakeColor;
    String snakeShape;

    //food
    Tile food;
    Random random;

    //game logic
    int velocityX;
    int velocityY;
    Timer gameLoop;
    static int Timer = 100;

    boolean gameOver = false;

    private Image backgroundImage;
    private Image foodImage; 
    
SnakeGame(int boardWidth, int boardHeight, String color, String shape) {
    // Gunakan parameter untuk menentukan ukuran
    this.boardWidth = boardWidth > 0 ? boardWidth : Toolkit.getDefaultToolkit().getScreenSize().width; // Jika 0, gunakan ukuran layar
    this.boardHeight = boardHeight > 0 ? boardHeight : Toolkit.getDefaultToolkit().getScreenSize().height;

    // Atur warna ular berdasarkan parameter
    this.snakeColor = switch (color) {
        case "Blue" -> Color.blue;
        case "Yellow" -> Color.yellow;
        case "White" -> Color.white;
        case "Red" -> Color.red;
        default -> Color.green;  // Default warna hijau jika tidak cocok
    };

    // Bentuk ular
    this.snakeShape = shape;

    // Coba memuat gambar latar belakang dan makanan
    try {
        backgroundImage = ImageIO.read(new File("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\BackGround\\2.png"));
        foodImage = ImageIO.read(new File("C:\\Users\\Wahyu\\Documents\\Java\\UTS\\kopikobra\\src\\KOPIKOBRA\\Image\\Food\\1.png"));
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Image Missing.");
    }

    // Atur ukuran dan properti panel
    setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
    setBackground(Color.black);
    addKeyListener(this);
    setFocusable(true);

    // Inisialisasi game
    initializeGame();
}

   
    public void initializeGame() {
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;
        
        gameLoop = new Timer(Timer, this);
        gameLoop.start();
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

    public void draw(Graphics g) {
        // Gambar grid
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Gambar makanan
       

        // Gambar kepala ular
        g.setColor(snakeColor);
        if ("Oval".equals(snakeShape)) {
            g.fillOval(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
        } else if ("Star".equals(snakeShape)) {
            drawStar(g, snakeHead.x * tileSize, snakeHead.y * tileSize);
        } else {
            g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);
        }

        // Gambar wajah pada kepala ular
        drawSnakeFace(g, snakeHead.x * tileSize, snakeHead.y * tileSize);

        // Gambar tubuh ular
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);

            // Menggunakan warna acak untuk setiap bagian tubuh
            Color bodyColor = getRandomColor();
            g.setColor(bodyColor);

            int x = snakePart.x * tileSize;
            int y = snakePart.y * tileSize;

            switch (snakeShape) {
                case "Oval" -> g.fillOval(x, y, tileSize, tileSize);
                case "Star" -> drawStar(g, x, y);
                default -> g.fill3DRect(x, y, tileSize, tileSize, true); // Default kotak
            }
        }

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + snakeBody.size(), tileSize - 16, tileSize);
        } else {
            g.drawString("Score: " + snakeBody.size(), tileSize - 16, tileSize);
        }
    }

    // Fungsi untuk menggambar wajah pada kepala ular
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
    
    private void drawStar(Graphics g, int x, int y) {
        int[] xPoints = { x + tileSize / 2, x + tileSize * 3 / 4, x + tileSize, x + tileSize * 5 / 6, 
                          x + tileSize * 3 / 4, x + tileSize / 2, x + tileSize / 4, x + tileSize / 6, x, x + tileSize / 4 };
        int[] yPoints = { y, y + tileSize / 4, y + tileSize / 4, y + tileSize / 2, 
                          y + tileSize * 3 / 4, y + tileSize * 2 / 3, y + tileSize * 3 / 4, y + tileSize / 2, y + tileSize / 4, y + tileSize / 4 };
        g.fillPolygon(xPoints, yPoints, 10);
    }

    // Fungsi untuk mengembalikan warna acak
    private Color getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return new Color(r, g, b);
    }

    public void placeFood() {
        int maxX = boardWidth / tileSize - 1;
        int maxY = boardHeight / tileSize - 1;
        boolean validPosition = false;

        while (!validPosition) {
            int x = random.nextInt(maxX + 1);
            int y = random.nextInt(maxY + 1);
            food = new Tile(x, y);

            // Validasi agar makanan tidak muncul di tubuh ular
            boolean foodOnSnake = snakeBody.stream().anyMatch(snakePart -> snakePart.x == x && snakePart.y == y);
            if (!foodOnSnake) {
                validPosition = true; // Lokasi makanan valid
            }
        }
    }


    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        checkCollision();
    }

    public boolean collision(Tile head, Tile food) {
        return head.x == food.x && head.y == food.y;
    }

    public void checkCollision() {
        // Collision with the walls
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || 
            snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
        }

        // Collision with the body
        for (Tile bodyPart : snakeBody) {
            if (snakeHead.x == bodyPart.x && snakeHead.y == bodyPart.y) {
                gameOver = true;
            }
        }
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
    App.saveScore(snakeBody.size(), "Normal Mode"); // Mode Challenge

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
