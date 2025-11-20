import javax.swing.*; // GUI components
import java.awt.*; // Font and Color
import java.awt.event.*; // Acction Listeners and even handling
import java.awt.image.BufferedImage; // to render player, enemy, power-up images
import java.io.File; // file handling
import java.io.IOException; //to handle exceptions if files failed to load
import javax.imageio.ImageIO; // to handle exceptions if images failed to load
import java.util.Random; // for random numbers for enemies and power-ups
import java.sql.ResultSet; // to proccess queries 
import javax.sound.sampled.AudioInputStream; // to take bullet fired sound from PC
import javax.sound.sampled.AudioSystem; // helps convert audio file into playable stream
import javax.sound.sampled.Clip; // for storing the sound for it to be played repeatedly

public class Main extends JFrame {

                                                                 // Main menu components
    JButton strButton, leadButton, exiButton;
    JLabel label;

    static String username; // Placeholder for username

    // Main constructor to setup starting screen
    Main() {

        setTitle("Space Shooters");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setResizable(false);
        setLocationRelativeTo(null); // Center the frame on the screen
        setLayout(null);
        getContentPane().setBackground(Color.BLACK);

        // Initialize components
        strButton = new JButton();
        leadButton = new JButton();
        exiButton = new JButton();
        label = new JLabel();

        // Setting font for the label and buttons
        try {
            Font retroFont = Font.createFont(Font.TRUETYPE_FONT, new File("ARCADECLASSIC.TTF")).deriveFont(36f);
            label.setFont(retroFont);
            strButton.setFont(retroFont.deriveFont(20f));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            label.setFont(new Font("Monospaced", Font.BOLD, 24));
        }


        // Set label properties for main menu
        label.setForeground(Color.GREEN);
        label.setText("MAIN MENU");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBounds(0, 0, 600, 250);

        // Set button properties --> START GAME
        strButton.setText("START GAME");
        strButton.setForeground(Color.WHITE);
        strButton.setBackground(Color.BLACK);
        strButton.setOpaque(true);
        strButton.setBounds(200, 150, 200, 30);
        strButton.setBorderPainted(false);
        strButton.setFocusable(false);

        // --> LEADER BOARD
        leadButton.setText("LEADER BOARD");
        leadButton.setFont(strButton.getFont());
        leadButton.setForeground(Color.WHITE);
        leadButton.setBackground(Color.BLACK);
        leadButton.setOpaque(true);
        leadButton.setBounds(200, 200, 200, 30);
        leadButton.setBorderPainted(false);
        leadButton.setFocusable(false);

        // --> EXIT GAME
        exiButton.setText("EXIT GAME");
        exiButton.setFont(strButton.getFont());
        exiButton.setForeground(Color.WHITE);
        exiButton.setBackground(Color.BLACK);
        exiButton.setOpaque(true);
        exiButton.setBounds(200, 250, 200, 30);
        exiButton.setBorderPainted(false);
        exiButton.setFocusable(false);

        // Adding components to the frame
        add(label);
        add(strButton);
        add(leadButton);
        add(exiButton);

        // Action listeners
        // --> START GAME
        strButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                boolean isNew = DBHelper.isNewPlayer(username);

                if (isNew) {
                    Main.showInstructionWindow(username);
                } else {
                    int option = JOptionPane.showConfirmDialog(null,
                            "Would you like to read the instructions?",
                            "Instructions", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        showInstructionWindow(username);
                    } else {
                        new Game();
                        dispose();
                    }
                }

            }
        });

        // --> LEADER BOARD
        leadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new LeaderBoardFrame();
                dispose();
            }
        });

        // --> EXIT GAME
        exiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // Method to initialize Instruction Window
    public static void showInstructionWindow(String username) {
        JFrame instructionFrame = new JFrame("Instructions");
        instructionFrame.setSize(500, 400);
        instructionFrame.setLocationRelativeTo(null);
        instructionFrame.setLayout(null);
        instructionFrame.getContentPane().setBackground(Color.BLACK);

        // setup label components
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 16));
        String instructions = "<html>Instructions for " + username + "<br/><br/>" +
                "1. Use arrow keys to move your spaceship.<br/>" +
                "2. Press spacebar to shoot.<br/>" +
                "3. Avoid enemy and collect power-ups.<br/>" +
                "4. Reach the highest score possible!<br/><br/>" +
                "Good luck and have fun!</html>";
        label.setText(instructions);
        label.setBounds(50, 20, 400, 200);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        instructionFrame.add(label);

        // setup button components
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.PLAIN, 16));
        okButton.setBounds(200, 300, 100, 30);
        okButton.setBackground(Color.BLACK);
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(null);
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> {
            instructionFrame.dispose();
            new Game(); // Start the game after instructions
        });
        instructionFrame.add(okButton);

        instructionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        instructionFrame.setVisible(true);
    }

    // Launch the main menu
    public static void main(String[] args) {
        new Main();
    }

    // LeaderBoardFrame inner class containing leaderboard logic 
    static class LeaderBoardFrame extends JFrame {
        public LeaderBoardFrame() {
            setTitle("LEADER BOARD");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 600);
            setResizable(false);
            setLocationRelativeTo(null); // Center on screen
            setLayout(null);
            getContentPane().setBackground(Color.BLACK);

            // Create labels
            JLabel headerLabel = new JLabel();
            JLabel rankingLabel = new JLabel();
            JLabel playerLabel = new JLabel();
            JLabel scoreLabel = new JLabel();

            // Set fonts for each label
            try {
                Font retroFont = Font.createFont(Font.TRUETYPE_FONT, new File("ARCADECLASSIC.TTF")).deriveFont(36f);
                headerLabel.setFont(retroFont);
            } catch (IOException | FontFormatException e) {
                e.printStackTrace();
                headerLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
            }

            // Set header label properties
            headerLabel.setForeground(Color.GREEN);
            headerLabel.setText("LEADER BOARDS");
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            headerLabel.setVerticalAlignment(SwingConstants.TOP);
            headerLabel.setBounds(0, 0, 600, 80);

            // Ranking column (left)
            rankingLabel.setFont(headerLabel.getFont().deriveFont(16f));
            rankingLabel.setForeground(Color.YELLOW);
            rankingLabel.setText("RANKING");
            rankingLabel.setHorizontalAlignment(SwingConstants.LEFT);
            rankingLabel.setVerticalAlignment(SwingConstants.CENTER);
            rankingLabel.setBounds(0, 80, 200, 40);

            // Player column (center)
            playerLabel.setFont(rankingLabel.getFont());
            playerLabel.setForeground(Color.YELLOW);
            playerLabel.setText("PLAYER");
            playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            playerLabel.setVerticalAlignment(SwingConstants.CENTER);
            playerLabel.setBounds(110, 80, 200, 40);

            // Score column (right) – adjusted so score does not exceed the screen
            scoreLabel.setFont(rankingLabel.getFont());
            scoreLabel.setForeground(Color.YELLOW);
            scoreLabel.setText("SCORE");
            scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            scoreLabel.setVerticalAlignment(SwingConstants.CENTER);
            scoreLabel.setBounds(350, 80, 200, 40);

            // Back button
            JButton backButton = new JButton("BACK TO MENU");
            backButton.setFont(headerLabel.getFont().deriveFont(20f));
            backButton.setForeground(Color.WHITE);
            backButton.setBackground(Color.BLACK);
            backButton.setOpaque(true);
            backButton.setBounds(200, 500, 200, 30);
            backButton.setBorderPainted(false);
            backButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    new Main();
                    dispose();
                }
            });

            // Fetch leaderboard data
            ResultSet rs = DBHelper.getLeaderboard();
            String leaderboardText = "";
            int rank = 1;
            try {
                while (rs.next()) {
                    // Use %-10d for left-aligned rank, %-20s for left-aligned username,
                    // and %10d for right-aligned score.
                    leaderboardText += String.format("%-10d  %-30.30s  %7d\n",
                            rank, rs.getString("username"), rs.getInt("highscore"));
                    rank++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // leaderboard area for rankings to be printed on
            JTextArea leaderboardArea = new JTextArea(leaderboardText);
            leaderboardArea.setEditable(false);
            leaderboardArea.setBounds(50, 120, 500, 350);
            leaderboardArea.setBackground(Color.BLACK);
            leaderboardArea.setForeground(Color.WHITE);
            leaderboardArea.setFont(new Font("Monospaced", Font.PLAIN, 16));

            // Add all labels and leaderboard area to the frame
            add(backButton);
            add(headerLabel);
            add(rankingLabel);
            add(playerLabel);
            add(scoreLabel);
            add(leaderboardArea);

            setVisible(true);
        }
    }
}

                                                    // Game class containing the game logic and rendering
class Game extends JFrame {
    Random random = new Random();

    // --> player's properties
    int Playerx = 250; // Player's X position
    int Playery = 520; // Player's Y position
    int Playerwidth = 50;
    int Playerheight = 30;

    // --> enemy's properties
    int[] Enemywidth = {50, 50, 50, 50, 50}; // Enemy width
    int[] Enemyheight = {30, 30, 30, 30, 30}; // Enemy height
    boolean[] EnemyStatus = new boolean[5]; // Enemy status for movement
    int[] Enemyx = new int[5];
    int[] Enemyy = new int[5];

    // --> bullet's properties
    int[] bulletx = new int[5];
    int[] bullety = new int[5];
    boolean[] movingBullet = new boolean[5]; // Bullet status for movement

    // --> multi-bullet's properties
    int multiBulletx = random.nextInt(600); // Random X position for multi-bullet
    int multiBullety = 0; // Y position for multi-bullet
    int multiBulletwidth = 20; // Width of multi-bullet
    int multiBulletheight = 20; // Height of multi-bullet
    boolean multiBullet = false;
    boolean multiBulletEffect = false;
    BufferedImage multiBullets;
    int MBRx[] = new int[5];
    int MBRy[] = new int[5];
    boolean[] movingMBR = new boolean[5]; // Bullet status for movement
    int MBLx[] = new int[5];
    int MBLy[] = new int[5];
    boolean[] movingMBL = new boolean[5]; // Bullet status for movement
    int nextMultiBulletSpawnScore = 100; // initial threshold for spawning the multiBullet
    long multiBulletEffectStartTime = 0;

    static int score = 0;
    int lives = 5;

    Rect r; // Drawing panel

    // image variable
    private BufferedImage playerImage;
    private BufferedImage EnemyImage;
    private BufferedImage healthBar;

    // sound variable
    private Clip bulletSound;

    // Game End Window components
    JFrame gameEndWindow;
    JLabel gameOverLabel;
    JButton backButton, restartButton;

    // Pause window components
    JFrame pauseWindow;
    JButton resumeButton, exitButton;

    // loop for game 
    Timer Btimer;

    // Additional Life Power-Up fields
    int additionalLifeX = random.nextInt(600);
    int additionalLifeY = 0;
    int additionalLifeWidth = 20;
    int additionalLifeHeight = 20;
    boolean additionalLifePowerUp = false;
    int nextAdditionalLifeSpawnScore = 500; // Spawn after every 500 points, if lives < 5

    // constructor for main game logic
    Game() {
        setupGameEndWindow();
        setupPauseWindow();

        // --> player image handling
        try {
            playerImage = ImageIO.read(new File("r.png"));
        } catch (IOException e) {
            System.out.println("Error loading spaceship image: " + e.getMessage());
        }

        // --> Enemy image handling
        try {
            EnemyImage = ImageIO.read(new File("new2.png"));
        } catch (IOException e) {
            System.out.println("Error loading Enemy image: " + e.getMessage());
        }

        // --> Font handling
        try {
            Font retroFont = Font.createFont(Font.TRUETYPE_FONT, new File("ARCADECLASSIC.TTF")).deriveFont(20f);
            gameOverLabel.setFont(retroFont);
            backButton.setFont(retroFont);
            restartButton.setFont(retroFont);
            resumeButton.setFont(retroFont);
            exitButton.setFont(retroFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            gameOverLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        }

        // --> Bullet Sound handling
        try {
            File soundFile = new File("C:\\Users\\LAPTOP NATION\\Desktop\\project\\bullet.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            bulletSound = AudioSystem.getClip();
            bulletSound.open(audioIn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // --> Multi-bullet power-up image handling
        try {
            multiBullets = ImageIO.read(new File("bullet.png"));
        } catch (IOException e) {
            System.out.println("Error loading bullet image: " + e.getMessage());
        }

        // main components
        setTitle("SPACE SHOOTER !!");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setFocusable(true);

        // Setup Gmae (in start game) background panel
        r = new Rect();
        r.setBackground(new Color(0x0B0B1A));
        add(r);

        // Initialize bullet and enemy arrays
        for (int i = 0; i < 5; i++) {
            bulletx[i] = -1;
            bullety[i] = -1;
            movingBullet[i] = false;
        }
        for (int i = 0; i < 5; i++) {
            Enemyx[i] = -1;
            Enemyy[i] = -1;
            EnemyStatus[i] = false;
        }

        // Timer for game loop
        Btimer = new Timer(16, e -> {
            initializeEnemies();
            moveEnemies();
            moveBullet();

            if (multiBullet) {
                powerupMultiBullet();
            }

            if (multiBulletEffect) {
                moveMultiBulletLeft();
                moveMultiBulletRight();
                if (System.currentTimeMillis() - multiBulletEffectStartTime >= 10000) {
                    for (int i = 0; i < 5; i++) {
                        movingMBL[i] = false;
                        movingMBR[i] = false;
                    }
                    multiBulletEffect = false;
                    multiBullety = 0;
                }
            }

            moveAdditionalLifePowerUp();

            if (lives == 0) {
                DBHelper.updatePlayerScore(Main.username, score);
                showGameEndWindow();
                score = 0;
            }

            Collision();
            r.repaint();
        });
        Btimer.start();

        // Key listener for movement and shooting
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if ((keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) && Playerx > 0) {
                    Playerx -= 15;
                } else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) && Playerx + Playerwidth < 600) {
                    Playerx += 15;
                }
                if (keyCode == KeyEvent.VK_SPACE) {
                    shoot();
                    playBulletSound();
                } else if (keyCode == KeyEvent.VK_ESCAPE) {
                    showPauseWindow();
                }
                r.repaint();
            }
        });

        setVisible(true);
    }// constructor ends

    // Method for game ending window
    void setupGameEndWindow() {
        gameEndWindow = new JFrame("Game Over");
        gameEndWindow.setSize(400, 400);
        gameEndWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameEndWindow.setLocationRelativeTo(null);
        gameEndWindow.setLayout(null);
        gameEndWindow.setResizable(false);
        gameEndWindow.getContentPane().setBackground(Color.BLACK);

        gameOverLabel = new JLabel();
        gameOverLabel.setBounds(0, 0, 400, 50);
        gameOverLabel.setBackground(Color.BLACK);
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setVerticalAlignment(SwingConstants.TOP);
        gameOverLabel.setOpaque(true);

        backButton = new JButton("BACK TO MENU");
        backButton.setBounds(100, 100, 200, 30);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.setFocusable(false);
        backButton.addActionListener(ae -> {
            new Main();
            dispose();
        });

        restartButton = new JButton("RESTART GAME");
        restartButton.setBounds(100, 200, 200, 30);
        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(Color.WHITE);
        restartButton.setOpaque(true);
        restartButton.setBorderPainted(false);
        restartButton.setFocusable(false);
        restartButton.addActionListener(ae -> {
            new Game();
            dispose();
        });

        gameEndWindow.add(gameOverLabel);
        gameEndWindow.add(backButton);
        gameEndWindow.add(restartButton);
    }

    // Method for game Pausing window
    void setupPauseWindow() {
        pauseWindow = new JFrame("PAUSE");
        pauseWindow.setSize(300, 200);
        pauseWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pauseWindow.setLocationRelativeTo(null);
        pauseWindow.setLayout(null);
        pauseWindow.setResizable(false);
        pauseWindow.getContentPane().setBackground(Color.BLACK);

        resumeButton = new JButton("RESUME");
        exitButton = new JButton("MAIN MENU");

        resumeButton.setBounds(50, 50, 200, 30);
        resumeButton.setBackground(Color.BLACK);
        resumeButton.setForeground(Color.WHITE);
        resumeButton.setOpaque(true);
        resumeButton.setBorderPainted(false);
        resumeButton.setFocusable(false);
        resumeButton.addActionListener(ae -> {
            pauseWindow.setVisible(false);
            Btimer.start();
        });

        exitButton.setBounds(50, 100, 200, 30);
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);
        exitButton.setOpaque(true);
        exitButton.setBorderPainted(false);
        exitButton.setFocusable(false);
        exitButton.addActionListener(ae -> {
            new Main();
            dispose();
        });

        pauseWindow.add(resumeButton);
        pauseWindow.add(exitButton);
    }

    // Method for showing game ending window
    void showPauseWindow() {
        pauseWindow.setVisible(true);
        Btimer.stop();
    }

    // Method for showing game pausing window
    void showGameEndWindow() {
        gameOverLabel.setText("Game Over! \nYou Died  Your score  " + score);
        Btimer.stop();
        gameEndWindow.setVisible(true);
    }

    // Method for bullet sounds
    void playBulletSound() {
        if (bulletSound != null) {
            bulletSound.setFramePosition(0);
            bulletSound.start();
        }
    }

    // Method for shooting bullets
    void shoot() {
        if (multiBulletEffect) {
            for (int i = 0; i < 5; i++) {
                if (!movingMBL[i] && !movingMBR[i]) {
                    movingMBL[i] = true;
                    MBLx[i] = Playerx + 10;
                    MBLy[i] = Playery;

                    movingMBR[i] = true;
                    MBRx[i] = Playerx + Playerwidth - 10;
                    MBRy[i] = Playery;

                    return;
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                if (!movingBullet[i]) {
                    movingBullet[i] = true;
                    bulletx[i] = Playerx + Playerwidth / 2;
                    bullety[i] = Playery;

                    return;
                }
            }
        }
    }

    // Method for moving bullets
    void moveBullet() {
        for (int i = 0; i < 5; i++) {
            if (movingBullet[i]) {
                bullety[i] -= 10;
                if (bullety[i] < 0) {
                    movingBullet[i] = false;
                }
            }
        }
    }

    // Method for moving Multi-Bullet (of power-up) from left wing of the plane
    void moveMultiBulletLeft() {
        for (int i = 0; i < 5; i++) {
            if (movingMBL[i]) {
                MBLy[i] -= 10;
                if (MBLy[i] < 0) {
                    movingMBL[i] = false;
                }
            }
        }
    }

    // Method for moving Multi-Bullet (of power-up) from right wing of the plane
    void moveMultiBulletRight() {
        for (int i = 0; i < 5; i++) {
            if (movingMBR[i]) {
                MBRy[i] -= 10;
                if (MBRy[i] < 0) {
                    movingMBR[i] = false;
                }
            }
        }
    }

    // Method for moving Multi-Bullet power-up
    void powerupMultiBullet() {
        multiBullety += 2;
        if (multiBullety > 590) {
            multiBullet = false;
        }
    }

    // Method for moving extra-life power-up
    void moveAdditionalLifePowerUp() {
        if (additionalLifePowerUp) {
            additionalLifeY += 2;
            if (additionalLifeY > 600) {
                additionalLifePowerUp = false;
            }
        }
    }

    // Method for initilization of enemies
    void initializeEnemies() {
        for (int i = 0; i < 5; i++) {
            if (!EnemyStatus[i]) {
                EnemyStatus[i] = true;
                Enemyx[i] = random.nextInt(550);
                Enemyy[i] = random.nextInt(1);
            }
        }
    }

    // Method for moving enemies
    void moveEnemies() {
        for (int i = 0; i < 5; i++) {
            if (EnemyStatus[i]) {
                Enemyy[i] += 2;
                if (Enemyy[i] > 590) {
                    lives--;
                    EnemyStatus[i] = false;
                    if (lives <= 0) {
                        showGameEndWindow();
                    }
                }
            }
        }
    }

    // Method for checking collisions: enemies to bullets (both normal and multiple), player to enemies, player to power-ups
    void Collision() {

        // Enemies to normal bullets
        for (int i = 0; i < 5; i++) {
            if (movingBullet[i]) {
                for (int j = 0; j < 5; j++) {
                    if (EnemyStatus[j]) {
                        Rectangle bulletBox = new Rectangle(bulletx[i], bullety[i], 3, 3);

                        Rectangle enemyBox = new Rectangle(Enemyx[j], Enemyy[j], Enemywidth[j], Enemyheight[j]);
                        if (bulletBox.intersects(enemyBox)) {
                            score += 10;
                            EnemyStatus[j] = false;
                            movingBullet[i] = false;
                        }
                    }
                }
            }
        }

        // Enemies to Multi-Bullets bullets (Left)
        for (int i = 0; i < 5; i++) {
            if (movingMBL[i]) {
                Rectangle bulletBox = new Rectangle(MBLx[i], MBLy[i], 3, 12);
                for (int j = 0; j < 5; j++) {
                    if (EnemyStatus[j]) {
                        Rectangle enemyBox = new Rectangle(Enemyx[j], Enemyy[j], Enemywidth[j], Enemyheight[j]);
                        if (bulletBox.intersects(enemyBox)) {
                            score += 10;
                            EnemyStatus[j] = false;
                            movingMBL[i] = false;
                        }
                    }
                }
            }
        }

        // Enemies to Multi-Bullets bullets (Right)
        for (int i = 0; i < 5; i++) {
            if (movingMBR[i]) {
                Rectangle bulletBox = new Rectangle(MBRx[i], MBRy[i], 3, 12);
                for (int j = 0; j < 5; j++) {
                    if (EnemyStatus[j]) {
                        Rectangle enemyBox = new Rectangle(Enemyx[j], Enemyy[j], Enemywidth[j], Enemyheight[j]);
                        if (bulletBox.intersects(enemyBox)) {
                            score += 10;
                            EnemyStatus[j] = false;
                            movingMBR[i] = false;
                        }
                    }
                }
            }
        }

        // player to Enemy
        Rectangle playerBox = new Rectangle(Playerx, Playery, Playerwidth - 20, Playerheight - 20);
        for (int i = 0; i < 5; i++) {
            Rectangle enemyBox = new Rectangle(Enemyx[i], Enemyy[i], Enemywidth[i] - 20, Enemyheight[i] - 20);
            if (playerBox.intersects(enemyBox)) {
                DBHelper.updatePlayerScore(Main.username, score);
                showGameEndWindow();
                score = 0;
            }
        }

        // player to Multi-Bullets power-up
        Rectangle multiBulletBox = new Rectangle(multiBulletx, multiBullety, multiBulletwidth + 20, multiBulletheight + 20);
        if (playerBox.intersects(multiBulletBox)) {
            multiBullet = false;
            multiBulletEffect = true;
            multiBulletEffectStartTime = System.currentTimeMillis();
            nextMultiBulletSpawnScore = score + 1000;
            multiBullety = 600;
        }

        // player to Extra-life power-up
        Rectangle addLifeBox = new Rectangle(additionalLifeX, additionalLifeY, additionalLifeWidth, additionalLifeHeight);
        if (playerBox.intersects(addLifeBox) && additionalLifePowerUp) {
            if (lives < 5) {
                lives++;
            }
            additionalLifePowerUp = false;
            nextAdditionalLifeSpawnScore = score + 500;
        }
    }

    // Inner class for drawing the game graphics
    class Rect extends JPanel {
        // overriding to use our defined methods not the adstract ones
        @Override
        protected void paintComponent(Graphics g) {
            // called for not to alter any of the changes being made 
            super.paintComponent(g);

            // upcasted to Graphics2D for better rendering
            Graphics2D g2d = (Graphics2D) g;

            // setting up the anti aliasing for smooth finishing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // drawing player's image
            if (playerImage != null) {
                g2d.drawImage(playerImage, Playerx, Playery, null);
            } else {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(Playerx, Playery, Playerwidth, Playerheight);
            }

            // drawing Enemy's image
            for (int i = 0; i < 5; i++) {
                if (EnemyStatus[i]) {
                    if (EnemyImage != null) {
                        g2d.drawImage(EnemyImage, Enemyx[i], Enemyy[i], Enemywidth[i], Enemyheight[i], null);
                    } else {
                        g2d.setColor(Color.RED);
                        g2d.fillRect(Enemyx[i], Enemyy[i], Enemywidth[i], Enemyheight[i]);
                    }
                }
            }

            // drawing normal-bullets with gradient effect to make it feel realistic
            for (int i = 0; i < bulletx.length; i++) {
                if (movingBullet[i]) {
                    GradientPaint bulletGradient = new GradientPaint(bulletx[i], bullety[i], Color.WHITE,
                            bulletx[i], bullety[i] + 10, Color.YELLOW);
                    g2d.setPaint(bulletGradient);
                    g2d.fillRect(bulletx[i], bullety[i], 3, 12);
                }
            }

            // drawing Multi-bullets (Right) with gradient effect to make it feel realistic
            for (int i = 0; i < MBRx.length; i++) {
                if (movingMBR[i]) {
                    GradientPaint bulletGradient = new GradientPaint(MBRx[i], MBRy[i], Color.WHITE,
                            MBRx[i], MBRy[i] + 10, Color.YELLOW);
                    g2d.setPaint(bulletGradient);
                    g2d.fillRect(MBRx[i], MBRy[i], 3, 12);
                }
            }

            // drawing Multi-bullets (Left) with gradient effect to make it feel realistic
            for (int i = 0; i < MBLx.length; i++) {
                if (movingMBL[i]) {
                    GradientPaint bulletGradient = new GradientPaint(MBLx[i], MBLy[i], Color.WHITE,
                            MBLx[i], MBRy[i] + 10, Color.YELLOW);
                    g2d.setPaint(bulletGradient);
                    g2d.fillRect(MBLx[i], MBLy[i], 3, 12);
                }
            }

            // drawing Score counter
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(Color.WHITE);
            g2d.drawString("Score: " + score, 5, 20);

            // importing health bar image
            try {
                healthBar = ImageIO.read(new File("health.png"));
            } catch (IOException e) {
                System.out.println("Error loading healthBar image: " + e.getMessage());
            }

            // drawing health bar image
            if (healthBar != null) {
                for (int i = 0; i < lives; i++) {
                    g2d.drawImage(healthBar, 5 + (i * 25), 30, 20, 20, null);
                }
            } else {
                g2d.drawString("Lives: " + lives, 5, 40);
            }

            // drawing Multi-Bullet power-up
            if (score >= nextMultiBulletSpawnScore && !multiBulletEffect) {
                multiBullet = true;

                if (!multiBulletEffect) {
                    g2d.drawImage(multiBullets, multiBulletx, multiBullety, multiBulletwidth, multiBulletheight, null);
                }
            }

            // drawing Extra-life power-up
            if (score >= nextAdditionalLifeSpawnScore && lives < 5 && !additionalLifePowerUp) {
                additionalLifePowerUp = true;
                additionalLifeX = random.nextInt(600 - additionalLifeWidth);
                additionalLifeY = 0;
            }
            if (additionalLifePowerUp) {
                g2d.drawImage(healthBar, additionalLifeX, additionalLifeY, additionalLifeWidth, additionalLifeHeight, null);
            }
        }
    }
}
