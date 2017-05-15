package game;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class Win extends JFrame {
    private static int score, level;
    private static Point point;

    private static JPanel panel, panel2;
    private static JLabel labelWin, labelScore, labelScore2, labelSubmit;
    private static JTextField textName;
    private static JButton buttonSubmit;

    public Win(int score, Point point, int level) {
        Win.score = score;
        Win.point = point;
        Win.level = level;
        movePoint();
        initComponents();
    }

    private void movePoint() {
        point.x = point.x + 20;
        point.y = point.y + 20;
    }

    private String getLevelName() {
        switch (level) {
            case 1:
                return "Easy";
            case 2:
                return "Intermediate";
            case 3:
                return "Expert";
            default:
                return "Unknown";
        }
    }

    private void initComponents() {
        setTitle("You win");
        setSize(300, 400);
        setPreferredSize(new Dimension(200, 300));
        setLocation(point);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        panel = new JPanel();
        panel.setBounds(20, 10, 255, 140);
        add(panel);

        labelWin = new JLabel("You win!");
        labelWin.setBounds(10, 10, 80, 40);
        labelWin.setFont(labelWin.getFont().deriveFont(28f));
        panel.add(labelWin);

        labelScore = new JLabel("You just beat Minesweeper");
        labelScore.setVisible(true);
        labelScore2 = new JLabel("in " + score + " seconds on " + getLevelName() + " mode.");
        labelScore2.setVisible(true);
        panel.add(labelScore);
        panel.add(labelScore2);

        labelSubmit = new JLabel("\nWrite your name and submit:\n");
        panel.add(labelSubmit);

        textName = new JTextField();
        textName.setSize(100, 20);
        textName.setPreferredSize(new Dimension(160, 20));
        panel.add(textName);

        buttonSubmit = new JButton("Close");
        buttonSubmit.setPreferredSize(new Dimension(60, 20));
        buttonSubmit.setMargin(new Insets(0, 0, 0, 0));
        buttonSubmit.addActionListener((ActionEvent e) -> dispose());

        panel2 = new JPanel();
        panel2.setBounds(20, 150, 255, 60);
        panel2.add(buttonSubmit);
        add(panel2);

        setVisible(true);
    }
}