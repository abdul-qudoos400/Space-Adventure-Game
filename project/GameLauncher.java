import javax.swing.*; // GUI components
import java.awt.*; // Font and Color
import java.io.File; // File handling for font
import java.io.IOException; // IO exceptions
import java.awt.event.*; // ActionListener for button events

public class GameLauncher {
    public static void main(String[] args) {
        JFrame frame = new JFrame("SAPCE SHOOTERS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setFocusable(false);
        
        frame.setLayout(null);

        // Set the background of the entire frame to black:
        frame.getContentPane().setBackground(Color.BLACK);

        JLabel label = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();

        // Set the font for the labels using a custom font (ARCADECLASSIC.TTF)
        try {
            Font retroFont = Font.createFont(Font.TRUETYPE_FONT, new File("ARCADECLASSIC.TTF")).deriveFont(36f);
            label.setFont(retroFont);
            label2.setFont(retroFont.deriveFont(24f));
            label3.setFont(retroFont.deriveFont(20f));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            label.setFont(new Font("Monospaced", Font.BOLD, 24)); // fallback font
        }

        
        label.setForeground(Color.GREEN); // Set text color to green
        label.setText("SPACE SHOOTERS"); // Set the text to be drawn
        label.setHorizontalAlignment(SwingConstants.CENTER); // Center the text horizontally
        label.setVerticalAlignment(SwingConstants.TOP); // Center the text vertically
        label.setBounds(0, 0, 600, 250);

        label2.setForeground(Color.WHITE);
        label2.setText("Enter your name");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setVerticalAlignment(SwingConstants.CENTER);
        label2.setBounds(0, 0, 600, 250);

        label3.setForeground(Color.WHITE);
        label3.setText("DEVELOPED BY : ABDUL QUDOOS AND SADIA FATIMA");
        label3.setHorizontalAlignment(SwingConstants.CENTER);
        label3.setVerticalAlignment(SwingConstants.BOTTOM);
        label3.setBounds(0, 0, 600, 550);

        JTextField Jf = new JTextField();
        Jf.setLocation(200, 150); // Set the position of the JTextField
        Jf.setSize(200, 30); // Set the size of the JTextField
    
        Jf.addActionListener(new Handler());
    
        frame.add(label);
        frame.add(label2);
        frame.add(label3);
        frame.add(Jf);
        frame.setVisible(true);
    }
    
    // When the user presses enter in the text field, save the name to the database and launch the game.
    static class Handler implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            JTextField Jf = (JTextField) ae.getSource(); // Get the source of the event
             String name = Jf.getText().trim(); // Get the text from the JTextField
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter your name.");
                return;
            }
            
            
            // Save the name (with an initial score 0) into your database.
            DBHelper.updatePlayerScore(name, Game.score);
            
            // Hide the current frame 
            SwingUtilities.getWindowAncestor(Jf).dispose();
            
            // Start the game by instantiating Main (or another game window)
            Main.username = name; // Main class has a static variable for username
            new Main(); // Main class will use the saved username and score and will proceed with Game management.
        }
    }
}
