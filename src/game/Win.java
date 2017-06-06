package game;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Win extends JFrame {
    private static Level level;
    private static int score;
    private static Point point;

    private static JPanel panel, panel2;
    private static JLabel labelWin, labelScore, labelScore2;
    private static JButton buttonClose;

    public Win(int score, Point point, Level level) {
        Win.score = score;
        Win.point = point;
        Win.level = level;
        movePoint();
        initComponents();
    }

    private void movePoint() {
        point.x += 20;
        point.y += 20;
    }

    private String getLevelName() {
        return level.toString();
    }

    private void initComponents() {
        setTitle("You win");
        setSize(300, 300);
        setPreferredSize(new Dimension(300, 300));
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

        buttonClose = new JButton("Close");
        buttonClose.setPreferredSize(new Dimension(60, 20));
        buttonClose.setMargin(new Insets(0, 0, 0, 0));
        buttonClose.addActionListener((ActionEvent e) -> dispose());

        panel2 = new JPanel();
        panel2.setBounds(20, 150, 255, 60);
        panel2.add(buttonClose);
        add(panel2);

        setVisible(true);
    }
}