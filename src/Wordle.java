// Import necessary packages
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

// Main Wordle class that extends JFrame for GUI and implements ActionListener for event handling
public class Wordle extends JFrame implements ActionListener {

    // Constants for game settings
    private static final int ALPHABET_COUNT = 26;
    private static final int INPUT_FIELD_COUNT = 25;
    private static final int MAX_GUESSES = 5;

    // Game state variables
    private String wordleWord;
    private JTextField[] inputField = new JTextField[INPUT_FIELD_COUNT];
    private JButton[] alphabetButton = new JButton[ALPHABET_COUNT];
    private JButton enterButton;
    private JButton deleteButton;
    private int currentIndex = 0;
    private int currentRow = 0;
    private static int gameSeconds = 0;

    // Thread to keep track of the time elapsed during the game
    public static Thread Timer = new Thread() {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++Wordle.gameSeconds;
            }
        }
    };

    // Constructor: loads the word bank and initializes the game layout
    private Wordle() throws IOException {
        loadWordBank();
        initializeLayout();
    }

    // Load words from a file into the word bank and select a random word for the current game
    private void loadWordBank() throws IOException {
        try (FileInputStream fileByteStream = new FileInputStream("C:\\Users\\dikea\\Desktop\\WordleClone\\Word_Bank");
             Scanner scanFile = new Scanner(fileByteStream)) {
            String[] wordBank = scanFile.useDelimiter("\\A").next().split("\\s+");
            wordleWord = wordBank[(int) (Math.random() * wordBank.length)].toUpperCase();
            System.out.println(wordleWord);
        }
    }

    // Set up the game layout including input fields, alphabet buttons, and control buttons
    private void initializeLayout() {
        // Set layout manager and window title
        this.setLayout(new GridBagLayout());
        GridBagConstraints positionConst = new GridBagConstraints();
        this.setTitle("My Wordle Clone");

        // Initialize input fields for player guesses
        for (int i = 0; i < INPUT_FIELD_COUNT; ++i) {
            inputField[i] = new JTextField(1);
            inputField[i].setEditable(false);
            inputField[i].setDocument(new SingleCharDocument()); // Ensure only one character can be entered
            inputField[i].setFont(new Font("Arial", Font.BOLD, 34));
            inputField[i].setBackground(Color.white);
            positionConst.gridx = i % 5;
            positionConst.gridy = i / 5;
            this.add(inputField[i], positionConst);
            inputField[i].addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent event) {
                    handleKeyTyped(event);
                }
            });
        }

        // Enable the first row for user input
        enableRow(0);

        // Initialize buttons for the alphabet
        for (int i = 0; i < ALPHABET_COUNT; ++i) {
            alphabetButton[i] = new JButton(Character.toString((char) (i + 65)));
            alphabetButton[i].setFont(new Font("Arial", Font.PLAIN, 10));
            alphabetButton[i].setPreferredSize(new Dimension(60, 40));
            positionConst.gridx = i % 7 + 6;
            positionConst.gridy = i / 7;
            this.add(alphabetButton[i], positionConst);
            alphabetButton[i].setBackground(Color.lightGray);
            alphabetButton[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleAlphabetButtonClick(e);
                }
            });
        }

        // Initialize the delete button
        deleteButton = new JButton("Delete");
        positionConst.gridx = 6;
        positionConst.gridy = 6;
        this.add(deleteButton, positionConst);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteButtonClick();
            }
        });

        // Initialize the enter button
        enterButton = new JButton("Enter");
        positionConst.gridx = 7;
        positionConst.gridy = 6;
        this.add(enterButton, positionConst);
        enterButton.addActionListener(this);
        enterButton.setEnabled(false);
    }

    // Enable the specified row for user input
    private void enableRow(int row) {
        for (int i = row * 5; i < (row + 1) * 5; ++i) {
            inputField[i].setEditable(true);
        }
    }

    // Handle key typed events in the input fields
    private void handleKeyTyped(KeyEvent event) {
        if (currentIndex < (currentRow + 1) * 5 - 1) {
            currentIndex++;
            inputField[currentIndex].requestFocus();
        }
        checkRowFilled();
    }

    // Handle click events on the alphabet buttons
    private void handleAlphabetButtonClick(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        inputField[currentIndex].setText(clickedButton.getText());
        if (currentIndex < (currentRow + 1) * 5 - 1) {
            currentIndex++;
        }
        checkRowFilled();
    }

    // Handle click events on the delete button
    private void handleDeleteButtonClick() {
        if (currentIndex >= currentRow * 5 && inputField[currentIndex].getText().isEmpty() && currentIndex > 0) {
            currentIndex--;
        }
        inputField[currentIndex].setText("");
        checkRowFilled();
    }

    // Check if the current row is completely filled
    private void checkRowFilled() {
        boolean rowFilled = true;
        for (int i = currentRow * 5; i < (currentRow + 1) * 5; ++i) {
            if (inputField[i].getText().isEmpty()) {
                rowFilled = false;
                break;
            }
        }
        enterButton.setEnabled(rowFilled);
    }

    // Handle click events on the enter button
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterButton) {
            String userWord = getUserInput();
            if (userWord.length() == 5) {
                updateUI(userWord);
                enterButton.setEnabled(false);
                if (currentRow < MAX_GUESSES - 1) {
                    currentRow++;
                    currentIndex = currentRow * 5;
                    enableRow(currentRow);
                    inputField[currentIndex].requestFocus();
                }
            }
        }
    }

    // Get the user's input for the current row
    private String getUserInput() {
        StringBuilder userWord = new StringBuilder();
        for (int i = currentRow * 5; i < (currentRow + 1) * 5; ++i) {
            userWord.append(inputField[i].getText());
        }
        return userWord.toString();
    }

    // Update the UI based on the user's guess
    private void updateUI(String userWord) {
        boolean isCorrectGuess = true;
        for (int i = 0; i < 5; ++i) {
            char userChar = userWord.charAt(i);
            char correctChar = wordleWord.charAt(i);
            if (userChar == correctChar) {
                inputField[currentRow * 5 + i].setBackground(Color.GREEN);
            } else if (wordleWord.indexOf(userChar) != -1) {
                inputField[currentRow * 5 + i].setBackground(Color.YELLOW);
                isCorrectGuess = false;
            } else {
                inputField[currentRow * 5 + i].setBackground(Color.RED);
                isCorrectGuess = false;
            }
        }

        if (isCorrectGuess) {
            gameWon();
        } else if (currentRow == MAX_GUESSES - 1) {
            gameLost();
        }
    }

    // Actions to perform when the game is won
    private void gameWon() {
        JOptionPane.showMessageDialog(this, "Congratulations, you guessed the word in " + gameSeconds + " seconds!");
        askToPlayAgain();
    }

    // Actions to perform when the game is lost
    private void gameLost() {
        JOptionPane.showMessageDialog(this, "Sorry, you didn't guess the word. The word was: " + wordleWord);
        askToPlayAgain();
    }

    // Prompt the user to play again or exit the game
    private void askToPlayAgain() {
        int reply = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            currentRow = 0;
            currentIndex = 0;
            resetGame();
        } else {
            System.exit(0);
        }
    }

    // Reset the game state to its initial configuration
    private void resetGame() {
        currentRow = 0;
        currentIndex = 0;
        for (JTextField field : inputField) {
            field.setText("");
            field.setBackground(Color.white);
            field.setEditable(false);
        }
        enableRow(currentRow);
        inputField[currentIndex].requestFocus();
        gameSeconds = 0;
    }

    // Main method to launch the game
    public static void main(String[] args) throws IOException {
        Timer.start();
        Wordle wordle = new Wordle();
        wordle.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wordle.setPreferredSize(new Dimension(900, 500));
        wordle.pack();
        wordle.setVisible(true);
    }
}

// Separate class to ensure only one character can be entered into the JTextField
class SingleCharDocument extends PlainDocument {
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str != null && str.length() > 1) {
            str = str.substring(0, 1);
        }
        super.insertString(offs, str, a);
    }
}
