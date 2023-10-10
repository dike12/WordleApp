import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class myJFrame extends JFrame implements ActionListener {
    private String keyboardButton;
    private String wordleWord;
    private String[] wordBank = new String[2500];
    private int[] letterCount = new int[26];
    private int[] lettersUsed = new int[26];
    private char[] alphabet = new char[26];
    private JTextField[] inputField = new JTextField[30];
    private JButton[] alphabetButton = new JButton[26];
    private JButton enterButton;
    private int currentIndex = 0;
    private int guessNumber = 0;
    private int numWords;
    private static int gameSeconds = 0;
    private int arraySize = 0;
    public static Thread Timer = new Thread() {
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }

                ++myJFrame.gameSeconds;
            }
        }
    };

    private myJFrame() throws IOException {
        int i;
        for(i = 0; i < 26; ++i) {
            this.alphabet[i] = (char)(i + 65);
            this.letterCount[i] = 0;
            this.lettersUsed[i] = 0;
        }

        FileInputStream fileByteStream = new FileInputStream("C:\\Users\\dikea\\Desktop\\WordleClone\\Word_Bank");
        Scanner scanFile = new Scanner(fileByteStream);

        for(i = 0; scanFile.hasNext(); ++i) {
            this.wordBank[i] = scanFile.next().toUpperCase();
            ++this.arraySize;
        }

        this.numWords = i - 1;
        this.wordleWord = this.wordBank[(int)Math.round(Math.random() * (double)this.numWords)];
        System.out.println(this.wordleWord);

        for(i = 0; i < 26; ++i) {
            for(int j = 0; j < this.wordleWord.length(); ++j) {
                if (this.wordleWord.charAt(j) == this.alphabet[i]) {
                    int var10002 = this.letterCount[i]++;
                }
            }
        }

        this.setLayout(new GridBagLayout());
        GridBagConstraints positionConst = new GridBagConstraints();
        this.setTitle("Our Wordle with a bug in it (LLC)");

        for(i = 0; i < 30; ++i) {
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
                    Object source = event.getSource();
                    JTextField field = (JTextField)source;
                    char inputChar = event.getKeyChar();
                    if (Character.isLowerCase(inputChar)) {
                        event.setKeyChar(Character.toUpperCase(inputChar));
                    }

                    if (inputChar == '\b') {
                        if (Math.floorMod(myJFrame.this.currentIndex, 5) != 0) {
                            --myJFrame.this.currentIndex;
                        }

                        myJFrame.this.inputField[myJFrame.this.currentIndex].requestFocus();
                    } else if (Math.floorMod(myJFrame.this.currentIndex, 5) != 4) {
                        ++myJFrame.this.currentIndex;
                        myJFrame.this.inputField[myJFrame.this.currentIndex].requestFocus();
                    } else {
                        myJFrame.this.inputField[myJFrame.this.currentIndex].setText(field.getText().substring(0, 0));
                        myJFrame.this.inputField[myJFrame.this.currentIndex].requestFocus();
                    }

                }
            });
        }

        for(i = 0; i < 26; ++i) {
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

        for(i = 0; i < 30; ++i) {
            this.inputField[i].setPreferredSize(new Dimension(60, 40));
            int row = Math.floorDiv(i, 5);
            int column = Math.floorMod(i, 5);
            positionConst.gridx = column;
            positionConst.gridy = row;
            positionConst.insets = new Insets(4, 4, 4, 4);
            this.add(this.inputField[i], positionConst);
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

    public void actionPerformed(ActionEvent e) {
        char[] inputChar = new char[5];
        String userWord = "";
        boolean inWordBank = false;

        int i;
        for(i = 0; i < 5; ++i) {
            inputChar[i] = this.inputField[5 * this.guessNumber + i].getText().charAt(0);
            userWord = userWord + inputChar[i];
        }

        for(i = 0; i < this.arraySize; ++i) {
            if (userWord.equals(this.wordBank[i])) {
                inWordBank = true;
                break;
            }
        }

        if (inWordBank) {
            int var10002;
            int indexTemp;
            for(i = 0; i < 5; ++i) {
                if (inputChar[i] == this.wordleWord.charAt(i)) {
                    this.inputField[5 * this.guessNumber + i].setBackground(Color.GREEN);
                    indexTemp = Character.getNumericValue(inputChar[i]) - 10;
                    var10002 = this.lettersUsed[indexTemp]++;
                    this.alphabetButton[indexTemp].setBackground(Color.GREEN);
                    this.alphabetButton[i].setOpaque(true);
                }
            }

            for(i = 0; i < 5; ++i) {
                if (inputChar[i] != this.wordleWord.charAt(i)) {
                    int charIndex = this.wordleWord.indexOf(inputChar[i]);
                    if (charIndex != -1) {
                        indexTemp = Character.getNumericValue(inputChar[i]) - 10;
                        if (this.lettersUsed[indexTemp] < this.letterCount[indexTemp]) {
                            this.inputField[5 * this.guessNumber + i].setBackground(Color.YELLOW);
                            var10002 = this.lettersUsed[indexTemp]++;
                            this.alphabetButton[indexTemp].setBackground(Color.YELLOW);
                            this.alphabetButton[i].setOpaque(true);
                        } else {
                            this.inputField[5 * this.guessNumber + i].setBackground(Color.GRAY);
                            this.alphabetButton[indexTemp].setBackground(Color.GRAY);
                            this.alphabetButton[i].setOpaque(true);
                        }
                    }
                }
            }

            if (userWord.equals(this.wordleWord)) {
                this.enterButton.setVisible(false);
                int var10001;
                if (this.guessNumber == 0) {
                    var10001 = this.guessNumber + 1;
                    JOptionPane.showMessageDialog(this, "Congrats it took you " + var10001 + " guess to get it right and " + gameSeconds + " seconds");
                } else if (this.guessNumber >= 1) {
                    var10001 = this.guessNumber + 1;
                    JOptionPane.showMessageDialog(this, "Congrats it took you " + var10001 + " guesses to get it right and " + gameSeconds + " seconds");
                }

                for(i = 0; i < 5; ++i) {
                    if (inputChar[i] == this.wordleWord.charAt(i)) {
                        this.inputField[5 * this.guessNumber + i].setBackground(Color.GREEN);
                        indexTemp = Character.getNumericValue(inputChar[i]) - 10;
                        var10002 = this.lettersUsed[indexTemp]++;
                    }
                }
            }

            for(i = 0; i < 26; ++i) {
                this.lettersUsed[i] = 0;
            }

            if (this.guessNumber < 5) {
                ++this.guessNumber;
            } else if (!userWord.equals(this.wordleWord)) {
                JOptionPane.showMessageDialog(this, "You Fail, the correct word was " + this.wordleWord);
                this.enterButton.setVisible(false);
                System.exit(0);
            }

            this.currentIndex = 5 * this.guessNumber;
            this.inputField[this.currentIndex].requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Word Not in DataBank");
            this.inputField[this.currentIndex].requestFocus();
        }

    }

    public static void main(String[] args) throws IOException {
        Timer.start();
        myJFrame myJFrame = new myJFrame();
        myJFrame.setDefaultCloseOperation(3);
        myJFrame.setPreferredSize(new Dimension(900, 500));
        myJFrame.pack();
        myJFrame.setVisible(true);
        myJFrame.inputField[0].requestFocus();
    }
}
