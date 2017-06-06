package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static game.Level.*;

class GameView {
    private static final int SIZE_BUTTON = 25;
    private static final int PANEL_INFO_HEIGHT = 80;
    private static int panelWidth, panelMainHeight;
    private static int width, height;
    private static Color backgroundColor;
    private static Image imgFlag, imgMine, imgMineCrossed;

    private static JFrame frame;
    private static JPanel panelMain, panelInfo;
    private static JButton buttonRetry;
    private static JLabel labelMines, labelSeconds;
    private static JMenuBar menuBar;
    private static JMenu menuGame, menuSettings;
    private static JMenuItem gameEasy, gameIntermediate, gameExpert, settingsSaferFirstClick, settingsSafeReveal;

    private GameView() {
        Game.setClickedCells(0);
        Game.setLevel(Settings.getCurrentLevel());
        setWindowSize();
        start();
        showFrame();
        Cell.setActive(true);
    }

    public GameView(Point point) {
        Game.setClickedCells(0);
        Game.setLevel(Settings.getCurrentLevel());
        setWindowSize();
        start();
        frame.setLocation(point);
        showFrame();
        Cell.setActive(true);
    }

    private static void setWindowSize() {
        panelWidth = SIZE_BUTTON * Game.getColumns();
        panelMainHeight = SIZE_BUTTON * Game.getRows();
        width = panelWidth + 50;
        height = PANEL_INFO_HEIGHT + panelMainHeight + 80;
    }

    private void start() {
        loadImages();
        drawFrame();
        drawPanelMain();
        drawPanelInfo();
        drawMenu();

        Game.setMinesLeft(Game.getNumMines());
        Game.setFirstClicked(false);
        Game.setWon(false);

        Game.initTimer();
        Game.generateCells();
        Cell.setImages(imgFlag, imgMineCrossed);
    }

    private void loadImages() {
        try {
            imgFlag = ImageIO.read(getClass().getResource("/img/flag.png"));
            imgMine = ImageIO.read(getClass().getResource("/img/mine.png"));
            imgMineCrossed = ImageIO.read(getClass().getResource("/img/mine_crossed.png"));
        } catch (IOException e) {
        }
    }

    private void drawFrame() {
        frame = new JFrame();
        frame.setTitle("Minesweeper");
        frame.setSize(width, height);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Settings.checkIfChanged();
            }
        });
        frame.setResizable(false);
        frame.setLayout(null);
    }

    private static void drawPanelMain() {
        panelMain = new JPanel(new GridLayout(Game.getRows(), Game.getColumns(), 0, 0));
        panelMain.setBounds(20, 80, panelWidth, panelMainHeight);
        panelMain.setBackground(Color.GRAY.brighter());
        frame.add(panelMain);
    }

    private static void drawPanelInfo() {
        panelInfo = new JPanel();
        panelInfo.setLayout(new GridBagLayout());
        panelInfo.setBounds(20, 0, panelWidth, PANEL_INFO_HEIGHT);

        GridBagConstraints gbc = new GridBagConstraints();

        buttonRetry = new JButton();
        buttonRetry.setMargin(new Insets(0, 0, 0, 0));
        buttonRetry.setPreferredSize(new Dimension(40, 40));
        buttonRetry.setFont(buttonRetry.getFont().deriveFont(12f));
        backgroundColor = buttonRetry.getBackground();
        buttonRetry.setText("Retry");
        buttonRetry.setFocusable(false);
        buttonRetry.addActionListener(e -> Game.restartGame());
        buttonRetry.setVisible(true);

        labelMines = new JLabel();
        labelMines.setPreferredSize(new Dimension(90, 60));
        labelMines.setText(Integer.toString(Game.getNumMines()));
        labelMines.setFont(buttonRetry.getFont().deriveFont(28f));

        labelSeconds = new JLabel("0", SwingConstants.RIGHT);
        labelSeconds.setPreferredSize(new Dimension(90, 60));
        labelSeconds.setText(Integer.toString(0));
        labelSeconds.setFont(buttonRetry.getFont().deriveFont(28f));

        int gap = (panelWidth - 230) / 4;
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

    private static void drawMenu() {
        menuBar = new JMenuBar();
        menuBar.setSize(width, 20);
        menuGame = new JMenu("Game");
        menuBar.add(menuGame);

        gameEasy = new JMenuItem("Easy");
        gameEasy.addActionListener(e -> {
            if (Settings.getCurrentLevel() != EASY) {
                Game.restartFrame(EASY, getWindowLocation());
            } else {
                Game.restartGame();
            }
        });
        menuGame.add(gameEasy);

        gameIntermediate = new JMenuItem("Intermediate");
        gameIntermediate.addActionListener(e -> {
            if (Settings.getCurrentLevel() != INTERMEDIATE) {
                Game.restartFrame(INTERMEDIATE, getWindowLocation());
            } else {
                Game.restartGame();
            }
        });
        menuGame.add(gameIntermediate);

        gameExpert = new JMenuItem("Expert");
        gameExpert.addActionListener(e -> {
            if (Settings.getCurrentLevel() != EXPERT) {
                Game.restartFrame(EXPERT, getWindowLocation());
            } else {
                Game.restartGame();
            }
        });
        menuGame.add(gameExpert);

        menuSettings = new JMenu("Settings");
        menuBar.add(menuSettings);

        settingsSaferFirstClick = new JMenuItem();
        Game.setSaferFirstClickText();

        settingsSaferFirstClick.addActionListener(e -> {
            Settings.setSaferFirstClick(!Settings.isSaferFirstClick());
            Game.setSaferFirstClickText();
        });
        menuSettings.add(settingsSaferFirstClick);

        settingsSafeReveal = new JMenuItem();
        Game.setSafeRevealText();
        settingsSafeReveal.addActionListener(e -> {
            Settings.setSafeReveal(!Settings.isSafeReveal());
            Game.setSafeRevealText();
        });
        menuSettings.add(settingsSafeReveal);

        JSeparator separator = new JSeparator();
        menuSettings.add(separator);

        JMenuItem checkForUpdates = new JMenuItem("Check for updates");
        checkForUpdates.addActionListener(e -> new Thread(Game::clickCheckForUpdates).start());
        menuSettings.add(checkForUpdates);

        frame.setJMenuBar(menuBar);
    }

    private static void showFrame() {
        frame.pack();
        frame.setVisible(true);
    }

    public static Point getWindowLocation() {
        return frame.getLocationOnScreen();
    }

    public static JLabel getLabelSeconds() {
        return labelSeconds;
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Image getImgMine() {
        return imgMine;
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static JPanel getPanelMain() {
        return panelMain;
    }

    public static JLabel getLabelMines() {
        return labelMines;
    }

    public static JMenuItem getSettingsSaferFirstClick() {
        return settingsSaferFirstClick;
    }

    public static JMenuItem getSettingsSafeReveal() {
        return settingsSafeReveal;
    }

    public static int getSizeButton() {
        return SIZE_BUTTON;
    }

    public static void main(String[] args) {
        Settings.loadFromFile();
        javax.swing.SwingUtilities.invokeLater(GameView::new);
    }
}
