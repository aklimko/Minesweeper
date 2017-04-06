package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class Game {
    private static int rows, columns;
    private static final int sizeButton = 25;
    private static int panelWidth, panelMainHeight;
    private static final int panelInfoHeight = 80;
    private static int width, height;
    private static int numMines;
    private static boolean firstClicked, won;
    private static Color backgroundColor;
    private static Image imgFlag, imgMine, imgMineCrossed;
    private static int redX, redY;
    private static Timer timer;
    private static TimerTask timerTask;
    private static int minesLeft, seconds, clickedCells;
    
    private static JFrame frame;
    private static JPanel panelMain, panelInfo;
    private static JButton button, buttonRetry;
    private static JLabel labelMines, labelSeconds;
    private static JMenuBar menuBar;
    private static JMenu menuGame, menuSettings;
    private static JMenuItem gameEasy, gameIntermediate, gameExpert, gameTop, settingsSaferFirstClick, settingsSafeReveal;
    
    private static Cell[][] cells;

    public Game() {
        setLevel(Settings.getCurrentLevel());
        setWindowSize();
        start();
        showFrame();
    }
    
    public Game(Point point){
        setLevel(Settings.getCurrentLevel());
        setWindowSize();
        start();
        frame.setLocation(point);
        showFrame();
    }
    
    public static int getWindowWidth(){
        return width;
    }
    
    public static int getWindowHeight(){
        return height;
    }
    
    public static void setWindowSize(){
        panelWidth = sizeButton*columns;
        panelMainHeight = sizeButton*rows;
        width = panelWidth + 50;
        height = panelInfoHeight + panelMainHeight + 80;
    }
    
    public static Point getWindowLocation(){
        return frame.getLocationOnScreen();
    }
    
    public static void setLevel(int level){
        clickedCells = 0;
        switch(level){
            case 1:
                rows = 8;
                columns = 8;
                numMines = 10;
                break;
            case 2:
                rows = 16;
                columns = 16;
                numMines = 40;
                break;
            case 3:
                rows = 16;
                columns = 30;
                numMines = 99;
                break;
            default:
                break;
        }
    }
    
    public static ArrayList findNeighbours(int row, int col){
        ArrayList<Coordinates> neighbours = new ArrayList<>();
        if((row>0 && row<rows-1) && (col>0 && col<columns-1)){
            neighbours.add(new Coordinates(row-1,col-1));
            neighbours.add(new Coordinates(row,col-1));
            neighbours.add(new Coordinates(row+1,col-1));
            neighbours.add(new Coordinates(row-1,col+1));
            neighbours.add(new Coordinates(row,col+1));
            neighbours.add(new Coordinates(row+1,col+1));
            neighbours.add(new Coordinates(row-1,col));
            neighbours.add(new Coordinates(row+1,col));
        }
        else if(row==0 && col==0){
            neighbours.add(new Coordinates(0,1));
            neighbours.add(new Coordinates(1,0));
            neighbours.add(new Coordinates(1,1));
        }
        else if(row==0 && col==columns-1){
            neighbours.add(new Coordinates(0,col-1));
            neighbours.add(new Coordinates(1,col-1));
            neighbours.add(new Coordinates(1,col));
        }
        else if(row==rows-1 && col==columns-1){
            neighbours.add(new Coordinates(row,col-1));
            neighbours.add(new Coordinates(row-1,col-1));
            neighbours.add(new Coordinates(row-1,col));
        }
        else if(row==rows-1 && col==0){
            neighbours.add(new Coordinates(row,1));
            neighbours.add(new Coordinates(row-1,1));
            neighbours.add(new Coordinates(row-1,0));
        }
        else if(row==0){
            neighbours.add(new Coordinates(row,col-1));
            neighbours.add(new Coordinates(row,col+1));
            neighbours.add(new Coordinates(row+1,col-1));
            neighbours.add(new Coordinates(row+1,col));
            neighbours.add(new Coordinates(row+1,col+1));
        }
        else if(row==rows-1){
            neighbours.add(new Coordinates(row,col-1));
            neighbours.add(new Coordinates(row,col+1));
            neighbours.add(new Coordinates(row-1,col-1));
            neighbours.add(new Coordinates(row-1,col));
            neighbours.add(new Coordinates(row-1,col+1));
        }
        else if(col==0){
            neighbours.add(new Coordinates(row-1,col));
            neighbours.add(new Coordinates(row+1,col));
            neighbours.add(new Coordinates(row-1,col+1));
            neighbours.add(new Coordinates(row,col+1));
            neighbours.add(new Coordinates(row+1,col+1));
        }
        else if(col==columns-1){
            neighbours.add(new Coordinates(row-1,col));
            neighbours.add(new Coordinates(row+1,col));
            neighbours.add(new Coordinates(row-1,col-1));
            neighbours.add(new Coordinates(row,col-1));
            neighbours.add(new Coordinates(row+1,col-1));
        }
        return neighbours;
    }
    
    public static void revealNeighbours(int row, int col){
        ArrayList<Coordinates> neighbours;
        neighbours = findNeighbours(row, col);
        for(int i = 0; i < neighbours.size(); i++){
            int x = neighbours.get(i).getRow();
            int y = neighbours.get(i).getCol();
            if(!cells[x][y].isClicked()){
                cells[x][y].getButton().doClick(0);
            }
        }
    }
    
    public static void checkWin(){
        if (!won) {
            if (clickedCells==rows*columns-numMines){
                won = true;
                freezeGame();
                new Win(Integer.parseInt(labelSeconds.getText()), getWindowLocation(), Settings.getCurrentLevel());
            }
        }
    }
    
    public static void checkValue(){
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++) {
                if(cells[i][j].isMined()==0){
                    int number = countNearbyMines(i,j);
                    cells[i][j].setColor(number);
                    cells[i][j].setValue(number);
                }
            }
        }
    }
    
    public static int countNearbyMines(int row, int col){
        int count = 0;
        ArrayList<Coordinates> neighbours;
        neighbours = findNeighbours(row, col);
        for(int i = 0; i < neighbours.size(); i++){
            count += cells[neighbours.get(i).getRow()][neighbours.get(i).getCol()].isMined();
        }
        return count;
    }
    
    public static int getPosition(int row, int col){
        return row*columns+col;
    }
    
    public static void clickedNeighbours(int row, int col){
        int value = cells[row][col].getValue();
        int numFlagged = 0;
        
        ArrayList<Coordinates> neighbours = findNeighbours(row, col);
        
        for(int i = 0; i < neighbours.size(); i++){
            int x = neighbours.get(i).getRow();
            int y = neighbours.get(i).getCol();
            if(!cells[x][y].isClicked() && cells[x][y].isFlagged()){
                numFlagged++;
            }
        }
        if (value == numFlagged){
            for(int i = 0; i < neighbours.size(); i++){
                int x = neighbours.get(i).getRow();
                int y = neighbours.get(i).getCol();
                if(!cells[x][y].isClicked() && !cells[x][y].isFlagged()){
                    cells[x][y].getButton().doClick(0);
                }
            }
        }
    }
    
    public static Coordinates convertPositionToCoordinates(int position){
        int row = position/columns;
        while(position>=columns){
            position%=columns;
        }
        int col = position;
        return new Coordinates(row, col);
    }
    
    public static void generateMines(int numMines, int row, int col){
        Random rndm = new Random();
        ArrayList<Coordinates> temp = new ArrayList<>();
        
        if(Settings.isSaferFirstClick()){
            temp = findNeighbours(row, col);
        }
        temp.add(new Coordinates(row, col));
        
        int generatedMinesCounter = 0;
        while(generatedMinesCounter < numMines){
            Coordinates coord = convertPositionToCoordinates(rndm.nextInt(rows*columns));
            if(!temp.contains(coord)){
                temp.add(coord);
                cells[coord.getRow()][coord.getCol()].setMined(true);
                generatedMinesCounter++;
            }
        }
        checkValue();
    }
    
    public static void generateCells(){
        cells = new Cell[rows][columns];
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell();
                button = cells[i][j].makeCell(i, j, sizeButton);
                panelMain.add(button);
            }
        }
    }
    
    public static void restartGame(){
        Cell.setActive(true);
        cells[redX][redY].getButton().setBackground(backgroundColor);
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++) {
                cells[i][j].setMined(false);
                cells[i][j].getButton().setContentAreaFilled(true);
                cells[i][j].getButton().setIcon(null);
                cells[i][j].setText("");
                cells[i][j].setClicked(false);
                cells[i][j].setFlagged(false);
                cells[i][j].setValue(0);
                cells[i][j].setColor(0);
            }
        }
        won = false;
        labelMines.setText(Integer.toString(numMines));
        labelSeconds.setText("0");
        timer.cancel();
        seconds = 0;
        clickedCells = 0;
        minesLeft = numMines;
        firstClicked = false;
    }
    
    public static void addClickedCellsCounter(){
        clickedCells++;
    }
    
    public static void freezeGame(){
        timer.cancel();
        Cell.setActive(false);
    }
    
    public static void makeRed(int row, int col){
        redX = row;
        redY = col;
        cells[row][col].getButton().setOpaque(true);
        cells[row][col].getButton().setBackground(Color.RED);
    }
    
    public static void revealMines(){
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++) {
                if(cells[i][j].isMined()==1 && !cells[i][j].isFlagged()){
                    cells[i][j].getButton().setIcon(new ImageIcon(imgMine));
                }
            }
        }
    }
    
    public void loadImages(){
        try {
            imgFlag = ImageIO.read(getClass().getResource("/img/flag.png"));
        } catch (IOException ex) {
            Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            imgMine = ImageIO.read(getClass().getResource("/img/mine.png"));
        } catch (IOException ex) {
            Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void initTimer(){
        seconds = 0;
        timer = new Timer();
        timerTask = new TimerTask(){
            @Override
            public void run(){
                labelSeconds.setText(Integer.toString(++seconds));
            }
        };
    }
    
    public static void startTimer(){
        seconds = 0;
        timer = new Timer();
        timerTask.cancel();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                labelSeconds.setText(Integer.toString(seconds++));
            }
        }, 0, 1000);
    }
    
    public static void plusMine(){
        labelMines.setText(Integer.toString(++minesLeft));
    }
    
    public static void minusMine(){
        labelMines.setText(Integer.toString(--minesLeft));
    }
    
    public static void drawPanelMain(){
        panelMain = new JPanel(new GridLayout(rows, columns, 0, 0));
        panelMain.setBounds(20, 80, panelWidth, panelMainHeight);
        panelMain.setBackground(Color.GRAY.brighter());
        frame.add(panelMain);
    }
    
    public static void drawPanelInfo(){
        panelInfo = new JPanel();
        panelInfo.setLayout(new GridBagLayout());
        panelInfo.setBounds(20, 0, panelWidth, panelInfoHeight);
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        buttonRetry = new JButton();
        buttonRetry.setMargin(new Insets(0,0,0,0));
        buttonRetry.setPreferredSize(new Dimension(40, 40));
        buttonRetry.setFont(buttonRetry.getFont().deriveFont(12f));
        backgroundColor = buttonRetry.getBackground();
        buttonRetry.setText("Retry");
        buttonRetry.setFocusable(false);
        buttonRetry.addActionListener((ActionEvent e) -> {
            restartGame();
        });
        buttonRetry.setVisible(true);
        
        labelMines = new JLabel();
        labelMines.setPreferredSize(new Dimension(90, 60));
        labelMines.setText(Integer.toString(numMines));
        labelMines.setFont(buttonRetry.getFont().deriveFont(28f));
        
        labelSeconds = new JLabel("0", SwingConstants.RIGHT);
        labelSeconds.setPreferredSize(new Dimension(90, 60));
        labelSeconds.setText(Integer.toString(0));
        labelSeconds.setFont(buttonRetry.getFont().deriveFont(28f));
        
        int gap = (panelWidth-230)/4;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, gap);
        panelInfo.add(labelMines, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, gap, 0, gap);
        panelInfo.add(buttonRetry, gbc);
        
        gbc.gridx = 2;
        gbc.insets = new Insets(0, gap, 0, 0);
        panelInfo.add(labelSeconds, gbc);
        frame.add(panelInfo);
    }
    
    public static void restartFrame(int level, Point point){
        frame.removeAll();
        frame.dispose();
        timer.cancel();
        Settings.setCurrentLevel(level);
        new Game(point);
    }
    
    public static void setSaferFirstClickText(){
        if (Settings.isSaferFirstClick()) {
            settingsSaferFirstClick.setText("\u221ASafer first click");
        }
        else{
            settingsSaferFirstClick.setText("Safer first click");
        }
    }
    
    public static void setSafeRevealText(){
        if (Settings.isSafeReveal()) {
            settingsSafeReveal.setText("\u221ASafe reveal");
        }
        else{
            settingsSafeReveal.setText("Safe reveal");
        }
    }
    
    public static void drawMenu(){
        menuBar = new JMenuBar();
        menuBar.setSize(width, 20);
        menuGame = new JMenu("Game");
        menuBar.add(menuGame);
        
        gameEasy = new JMenuItem("Easy");
        gameEasy.addActionListener((ActionEvent e) -> {
            if(Settings.getCurrentLevel() != 1){
                restartFrame(1, getWindowLocation());
            }
            else{
                restartGame();
            }
        });
        menuGame.add(gameEasy);
        
        gameIntermediate = new JMenuItem("Intermediate");
        gameIntermediate.addActionListener((ActionEvent e) -> {
            if(Settings.getCurrentLevel() != 2){
                restartFrame(2, getWindowLocation());
            }
            else{
                restartGame();
            }
        });
        menuGame.add(gameIntermediate);
        
        gameExpert = new JMenuItem("Expert");
        gameExpert.addActionListener((ActionEvent e) -> {
            if(Settings.getCurrentLevel() != 3){
                restartFrame(3, getWindowLocation());
            }
            else{
                restartGame();
            }
        });
        menuGame.add(gameExpert);
        menuGame.add(new JSeparator(SwingConstants.HORIZONTAL));
        
        gameTop = new JMenuItem("Top scores");
        gameTop.addActionListener((ActionEvent e) -> {
            new TopScores(getWindowLocation());
        });
        menuGame.add(gameTop);
        
        menuSettings = new JMenu("Settings");
        menuBar.add(menuSettings);
        
        settingsSaferFirstClick = new JMenuItem();
        setSaferFirstClickText();
        
        settingsSaferFirstClick.addActionListener((ActionEvent e) -> {
            Settings.setSaferFirstClick(!Settings.isSaferFirstClick());
            setSaferFirstClickText();
        });
        menuSettings.add(settingsSaferFirstClick);
        
        settingsSafeReveal = new JMenuItem();
        setSafeRevealText();
        settingsSafeReveal.addActionListener((ActionEvent e) -> {
            Settings.setSafeReveal(!Settings.isSafeReveal());
            setSafeRevealText();
        });
        menuSettings.add(settingsSafeReveal);
        
        frame.setJMenuBar(menuBar);
    }
    
    public static void drawFrame(){
        frame = new JFrame();
        frame.setTitle("Minesweeper");
        frame.setSize(width, height);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                Settings.checkSettingsIfChanged();
            }
        });
        frame.setResizable(false);
        frame.setLayout(null);
    }
    
    public static void showFrame(){
        frame.pack();
        frame.setVisible(true);
    }
    
    public void start(){
        loadImages();
        drawFrame();
        drawPanelMain();
        drawPanelInfo();
        drawMenu();
        panelMain.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_ENTER){
                    restartGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {}
            
            @Override
            public void keyTyped(KeyEvent ke) {}
        });
        
        minesLeft = numMines;
        firstClicked = false;
        
        initTimer();
        generateCells();
        Cell.setImages(imgFlag, imgMineCrossed);
    }
    
    public static boolean isFirstClicked(){
        return firstClicked;
    }
    
    public static void setFirstClicked(boolean firstClicked){
        Game.firstClicked = firstClicked;
    }
    
    public static int getNumMines(){
        return numMines;
    }
    
    public static int getColumns(){
        return columns;
    }

    public static Image getImgMine() {
        return imgMine;
    }

    public static Timer getTimer() {
        return timer;
    }
    
    public static void main(String[] args){
        Settings.loadSettingsFromFile();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Game();
        });
    }
}