package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static game.Level.*;

class GameView {
    private static final int SIZE_BUTTON = 25;
    private final int PANEL_INFO_HEIGHT = 80;
    private int panelWidth, panelMainHeight;
    private int width, height;
    private Color backgroundColor;
    private Image imgFlag, imgMine, imgMineCrossed;
    private final Game game;

    private JFrame frame;
    private JPanel panelMain, panelInfo;
    private JButton buttonRetry;
    private JLabel labelMines, labelSeconds;
    private JMenuBar menuBar;
    private JMenu menuGame, menuSettings;
    private JMenuItem gamePause, gameEasy, gameIntermediate, gameExpert, settingsSaferFirstClick, settingsSafeReveal;

    private GameView() {
        game = new Game(this);
        setWindowSize();
        start();
        showFrame();
    }

    GameView(Point point) {
        game = new Game(this);
        setWindowSize();
        start();
        frame.setLocation(point);
        showFrame();
    }

    private void setWindowSize() {
        panelWidth = SIZE_BUTTON * game.getColumns();
        panelMainHeight = SIZE_BUTTON * game.getRows();
        width = panelWidth + 50;
        height = PANEL_INFO_HEIGHT + panelMainHeight + 80;
    }

    private void start() {
        loadImages();
        drawFrame();
        drawPanelMain();
        drawPanelInfo();
        drawMenu();

        game.setMinesLeft(game.getNumMines());
        game.setFirstClicked(false);
        game.setWon(false);
        game.generateCells();
        Cell.setImages(imgFlag, imgMineCrossed);
    }

    private void loadImages() {
        try {
            imgFlag = ImageIO.read(getClass().getResource("/img/flag.png"));
            imgMine = ImageIO.read(getClass().getResource("/img/mine.png"));
            imgMineCrossed = ImageIO.read(getClass().getResource("/img/mine_crossed.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot load images.");
            System.exit(0);
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

    private void drawPanelMain() {
        panelMain = new JPanel(new GridLayout(game.getRows(), game.getColumns(), 0, 0));
        panelMain.setBounds(20, 80, panelWidth, panelMainHeight);
        panelMain.setBackground(Color.GRAY.brighter());
        frame.add(panelMain);
    }

    private void drawPanelInfo() {
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
        buttonRetry.addActionListener(e -> game.restartGame());
        buttonRetry.setVisible(true);

        labelMines = new JLabel();
        labelMines.setPreferredSize(new Dimension(90, 60));
        labelMines.setText(Integer.toString(game.getNumMines()));
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

    private void drawMenu() {
        menuBar = new JMenuBar();
        menuBar.setSize(width, 20);
        menuGame = new JMenu("Game");
        menuBar.add(menuGame);

        gamePause = new JMenuItem("Pause");
        gamePause.addActionListener(e -> {
            if (game.isFirstClicked() && game.isActive()) {
                if (!game.isPaused()) {
                    game.stopTimer();
                    gamePause.setText("Resume");
                    panelMain.setVisible(false);
                } else {
                    game.startTimer();
                    gamePause.setText("Pause");
                    panelMain.setVisible(true);
                }
                game.setPaused(!game.isPaused());
            }
        });
        menuGame.add(gamePause);

        menuGame.add(new JSeparator());

        gameEasy = new JMenuItem("Easy");
        gameEasy.addActionListener(e -> {
            if (Settings.getCurrentLevel() != EASY) {
                game.restartFrame(EASY, getWindowLocation());
            } else {
                game.restartGame();
            }
        });
        menuGame.add(gameEasy);

        gameIntermediate = new JMenuItem("Intermediate");
        gameIntermediate.addActionListener(e -> {
            if (Settings.getCurrentLevel() != INTERMEDIATE) {
                game.restartFrame(INTERMEDIATE, getWindowLocation());
            } else {
                game.restartGame();
            }
        });
        menuGame.add(gameIntermediate);

        gameExpert = new JMenuItem("Expert");
        gameExpert.addActionListener(e -> {
            if (Settings.getCurrentLevel() != EXPERT) {
                game.restartFrame(EXPERT, getWindowLocation());
            } else {
                game.restartGame();
            }
        });
        menuGame.add(gameExpert);

        menuSettings = new JMenu("Settings");
        menuBar.add(menuSettings);

        settingsSaferFirstClick = new JMenuItem();
        game.setSaferFirstClickText();

        settingsSaferFirstClick.addActionListener(e -> {
            Settings.setSaferFirstClick(!Settings.isSaferFirstClick());
            game.setSaferFirstClickText();
        });
        menuSettings.add(settingsSaferFirstClick);

        settingsSafeReveal = new JMenuItem();
        game.setSafeRevealText();
        settingsSafeReveal.addActionListener(e -> {
            Settings.setSafeReveal(!Settings.isSafeReveal());
            game.setSafeRevealText();
        });
        menuSettings.add(settingsSafeReveal);

        menuSettings.add(new JSeparator());

        JMenuItem checkForUpdates = new JMenuItem("Check for updates");
        checkForUpdates.addActionListener(e -> new Thread(game::clickCheckForUpdates).start());
        menuSettings.add(checkForUpdates);

        frame.setJMenuBar(menuBar);
    }

    private void showFrame() {
        frame.pack();
        frame.setVisible(true);
    }

    Point getWindowLocation() {
        return frame.getLocationOnScreen();
    }

    JLabel getLabelSeconds() {
        return labelSeconds;
    }

    Color getBackgroundColor() {
        return backgroundColor;
    }

    Image getImgMine() {
        return imgMine;
    }

    JFrame getFrame() {
        return frame;
    }

    JPanel getPanelMain() {
        return panelMain;
    }

    JLabel getLabelMines() {
        return labelMines;
    }

    JMenuItem getSettingsSaferFirstClick() {
        return settingsSaferFirstClick;
    }

    JMenuItem getSettingsSafeReveal() {
        return settingsSafeReveal;
    }

    static int getSizeButton() {
        return SIZE_BUTTON;
    }

    JMenuItem getGamePause() {
        return gamePause;
    }

    public static void main(String[] args) {
        Settings.loadFromFile();
        javax.swing.SwingUtilities.invokeLater(GameView::new);
    }
}
