package game;

import db.Highscore;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

public class Win extends JFrame{
    private static int score, level;
    private static Point point;
    
    private static JPanel panel, panel2;
    private static JLabel labelWin, labelScore, labelScore2, labelSubmit;
    private static JTextField textName;
    private static JButton buttonSubmit, buttonRead;
    
    private static SessionFactory factory;
    private static Session session;
    private static Transaction tx;
    
    List<Highscore> list;
    
    public Win(int score, Point point, int level){
        Win.score = score;
        Win.point = point;
        Win.level = level;
        movePoint();
        initComponents();
        getSessionFactory();
    }
    
    public void getSessionFactory(){
        new Thread(() -> {
            factory = HibernateUtil.getSessionFactory();
        }).start();
    }
    
    public void readRecords(){
        session = factory.openSession();
        try {
            Criteria crit = session.createCriteria(Highscore.class);
            crit.addOrder(Order.asc("score"));
            list = crit.list();
        } catch (Exception exp) {
            tx.rollback();
        }
        session.close();
    }
    
    public void showRecords(){
        for(Highscore hs : list){
            System.out.println(hs.getId());
            System.out.println(hs.getName());
            System.out.println(hs.getScore());
        }
    }
    
    public void movePoint(){
        point.x = point.x + 20;
        point.y = point.y + 20;
    }
    
    public String getLevelName(){
        switch(level){
            case 1:
                return "Easy";
            case 2:
                return "Intermediate";
            case 3:
                return "Expert";
            default:
                return null;
        }
    }
    
    public void addToDatabase(){
        session = factory.openSession();
        Highscore hs = new Highscore(textName.getText(), score);
        try {
            tx = session.beginTransaction();
            session.save(hs);
            tx.commit();
        } catch (Exception exp) {
            tx.rollback();
        }
        session.close();
    }
    
    public void initComponents(){
        setTitle("You win");
        setSize(300, 400);
        setPreferredSize(new Dimension(200, 300));
        setLocation(point);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                HibernateUtil.removeSessionFactory();
                factory.close();
            }
        });
        setResizable(false);
        setLayout(null);
        
        panel = new JPanel();
        //panel.setBackground(Color.yellow);
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
        
        buttonSubmit = new JButton("Submit");
        buttonSubmit.setPreferredSize(new Dimension(60, 20));
        buttonSubmit.setMargin(new Insets(0, 0, 0, 0));
        buttonSubmit.addActionListener((ActionEvent e) -> {
            addToDatabase();
            dispose();
        });
        
        buttonRead = new JButton("Read");
        buttonRead.setPreferredSize(new Dimension(60, 20));
        buttonRead.setMargin(new Insets(0, 0, 0, 0));
        buttonRead.addActionListener((ActionEvent e) -> {
            readRecords();
            showRecords();
        });
        
        panel2 = new JPanel();
        //panel2.setBackground(Color.red);
        panel2.setBounds(20, 150, 255, 60);
        panel2.add(buttonSubmit);
        panel2.add(buttonRead);
        add(panel2);
        
        setVisible(true);
    }
    
    public static void main(String[] args){
        new Win(60, new Point(600, 300), 1);
    }
}