import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.*;

/**
 * The Wordle class extends JFrame and implements ActionListener
 * to create a GUI for a word guessing game.
 */
public class Wordle extends JFrame implements ActionListener {

    private static final int ALPHABET_COUNT = 26;
    private static final int INPUT_FIELD_COUNT = 30;
    private static final int MAX_GUESSES = 5;

    private String keyboardButton;
    private String wordleWord;
    private String[] wordBank = new String[2500];
    private int[] letterCount = new int[ALPHABET_COUNT];
    private int[] lettersUsed = new int[ALPHABET_COUNT];
    private char[] alphabet = new char[ALPHABET_COUNT];
    private JTextField[] inputField = new JTextField[INPUT_FIELD_COUNT];
    private JButton[] alphabetButton = new JButton[ALPHABET_COUNT];
    private JButton enterButton;
    private int currentIndex = 0;
    private int guessNumber = 0;
    private int numWords;
    private static int gameSeconds = 0;
    private int arraySize = 0;

    public static Thread Timer = new Thread() {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
                ++Wordle.gameSeconds;
            }
        }
    };

    private Wordle() throws IOException {
        initializeAlphabet();
        loadWordBank();
        initializeLayout();
    }

    private void initializeAlphabet() {
        for (int i = 0; i < ALPHABET_COUNT; ++i) {
            this.alphabet[i] = (char) (i + 65);
            this.letterCount[i] = 0;
            this.lettersUsed[i] = 0;
        }
    }

    private void loadWordBank() throws IOException {
        try (FileInputStream fileByteStream = new FileInputStream("C:\\Users\\dikea\\Desktop\\WordleClone\\Word_Bank");
             Scanner scanFile = new Scanner(fileByteStream)) {
            int i = 0;
            while (scanFile.hasNext()) {
                this.wordBank[i] = scanFile.next().toUpperCase();
                ++this.arraySize;
                i++;
            }
            this.numWords = i - 1;
            this.wordleWord = this.wordBank[(int) Math.round(Math.random() * (double) this.numWords)];
            System.out.println(this.wordleWord);
        }
    }

    private void initializeLayout() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints positionConst = new GridBagConstraints();
        this.setTitle("Our Wordle with a bug in it (LLC)");

        for (int i = 0; i < INPUT_FIELD_COUNT; ++i) {
            this.inputField[i] = new JTextField(1);
            this.inputField[i].setEditable(true);
            this.inputField[i].setText("");
            this.inputField[i].setFont(new Font("Arial", 1, 34));
            this.inputField[i].setBackground(Color.white);
            this.inputField[i].setForeground(Color.BLACK);
            positionConst.gridx = i % 5;
            positionConst.gridy = i / 5;
            this.add(this.inputField[i], positionConst);
            this.inputField[i].addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent event) {
                    handleKeyTyped(event);
                }
            });
        }

        for (int i = 0; i < ALPHABET_COUNT; ++i) {
            this.keyboardButton = Character.toString(i + 65);
            this.alphabetButton[i] = new JButton(this.keyboardButton);
            this.alphabetButton[i].setFont(new Font("Aldous Vertical", 0, 10));
            this.alphabetButton[i].setPreferredSize(new Dimension(60, 40));
            positionConst.gridx = i % 7 + 6;
            positionConst.gridy = i / 7;
            this.add(this.alphabetButton[i], positionConst);
            this.alphabetButton[i].setBackground(Color.lightGray);
            this.alphabetButton[i].setOpaque(true);
        }

        this.enterButton = new JButton("Enter");
        this.enterButton.setFont(new Font("Aldous Vertical", 0, 10));
        this.enterButton.setPreferredSize(new Dimension(70, 40));
        positionConst.gridx = 5;
        positionConst.gridy = 5;
        positionConst.insets = new Insets(4, 4, 4, 4);
        this.add(this.enterButton, positionConst);
        this.enterButton.addActionListener(this);
    }

    private void handleKeyTyped(KeyEvent event) {
        Object source = event.getSource();
        JTextField field = (JTextField) source;
        char inputChar = event.getKeyChar();
        if (Character.isLowerCase(inputChar)) {
            event.setKeyChar(Character.toUpperCase(inputChar));
        }

        if (inputChar == '\b') {
            if (Math.floorMod(this.currentIndex, 5) != 0) {
                --this.currentIndex;
            }

            this.inputField[this.currentIndex].requestFocus();
        } else if (Math.floorMod(this.currentIndex, 5) != 4) {
            ++this.currentIndex;
            this.inputField[this.currentIndex].requestFocus();
        } else {
            this.inputField[this.currentIndex].setText(field.getText().substring(0, 0));
            this.inputField[this.currentIndex].requestFocus();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        char[] inputChar = new char[5];
        String userWord = "";
        boolean inWordBank = false;

        int i;
        for (i = 0; i < 5; ++i) {
            inputChar[i] = this.inputField[5 * this.guessNumber + i].getText().charAt(0);
            userWord = userWord + inputChar[i];
        }

        for (i = 0; i < this.arraySize; ++i) {
            if (userWord.equals(this.wordBank[i])) {
                inWordBank = true;
                break;
            }
        }
        // ... rest of the logic for handling action events ...
    }

    /**
     * The main entry point of the application, which initializes the JFrame
     * and starts the game timer.
     */
    public static void main(String[] args) throws IOException {
        Timer.start();
        Wordle wordle = new Wordle();
        wordle.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wordle.setPreferredSize(new Dimension(900, 500));
        wordle.pack();
        wordle.setVisible(true);
        wordle.inputField[0].requestFocus();
    }
}
