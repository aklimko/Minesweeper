package game;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

class Game {
    private static int rows, columns;
    private static int numMines;
    private static boolean firstClicked, won;
    private static int redX, redY;
    private static Timer timer;
    private static int minesLeft, seconds, clickedCells;
    private static boolean paused = false;
    private static Cell[][] cells;

    public static void setLevel(Level level) {
        switch (level) {
            case EASY:
                rows = 8;
                columns = 8;
                numMines = 10;
                break;
            case INTERMEDIATE:
                rows = 16;
                columns = 16;
                numMines = 40;
                break;
            case EXPERT:
                rows = 16;
                columns = 30;
                numMines = 99;
                break;
            default:
                break;
        }
    }

    public static void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    public static void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GameView.getLabelSeconds().setText(Integer.toString(seconds++));
            }
        }, 0, 1000);
    }

    public static void restartGame() {
        if (!Cell.isActive()) {
            Cell.setActive(true);
            if (!won) {
                cells[redX][redY].getButton().setBackground(GameView.getBackgroundColor());
            } else {
                won = false;
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j].setMined(false);
                cells[i][j].getButton().setContentAreaFilled(true);
                cells[i][j].getButton().setIcon(null);
                cells[i][j].clearText();
                cells[i][j].setClickedToFalse();
                cells[i][j].setFlaggedToFalse();
            }
        }
        GameView.getLabelMines().setText(Integer.toString(numMines));
        GameView.getLabelSeconds().setText("0");
        if (firstClicked) {
            stopTimer();
            seconds = 0;
            clickedCells = 0;
            minesLeft = numMines;
            firstClicked = false;
        }
        checkPauseRestart();
    }

    private static void checkPauseRestart() {
        if (paused) {
            paused = false;
            GameView.getGamePause().setText("Pause");
            GameView.getPanelMain().setVisible(true);
        }
    }

    public static void generateCells() {
        cells = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(i, j);
                GameView.getPanelMain().add(cells[i][j].makeCell());
            }
        }
    }

    public static void clickedNeighbours(int row, int col) {
        int value = cells[row][col].getValue();
        int numFlagged = 0;

        ArrayList<Coordinates> neighbours = cells[row][col].getNeighbours();

        for (Coordinates neighbour : neighbours) {
            int x = neighbour.getRow();
            int y = neighbour.getCol();
            if (!cells[x][y].isClicked() && cells[x][y].isFlagged()) {
                numFlagged++;
            }
        }
        if (value == numFlagged) {
            for (Coordinates neighbour : neighbours) {
                int x = neighbour.getRow();
                int y = neighbour.getCol();
                if (!cells[x][y].isClicked() && !cells[x][y].isFlagged()) {
                    cells[x][y].getButton().doClick(0);
                }
            }
        }
    }

    public static void addClickedCellsCounter() {
        clickedCells++;
    }

    @SuppressWarnings("unchecked")
    public static void generateMines(int numMines, int row, int col) {
        Random random = new Random();
        ArrayList<Coordinates> occupiedCoordinates = new ArrayList<>();

        if (Settings.isSaferFirstClick()) {
            occupiedCoordinates = (ArrayList<Coordinates>) cells[row][col].getNeighbours().clone();
        }
        occupiedCoordinates.add(new Coordinates(row, col));

        int generatedMinesCounter = 0;
        while (generatedMinesCounter < numMines) {
            Coordinates coordinates = convertPositionToCoordinates(random.nextInt(rows * columns));
            if (!occupiedCoordinates.contains(coordinates)) {
                occupiedCoordinates.add(coordinates);
                cells[coordinates.getRow()][coordinates.getCol()].setMined(true);
                generatedMinesCounter++;
            }
        }
        checkValues();
    }

    private static Coordinates convertPositionToCoordinates(int position) {
        int row = position / columns;
        int col = position % columns;
        return new Coordinates(row, col);
    }

    private static void checkValues() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 0) {
                    int number = countNearbyMines(i, j);
                    cells[i][j].setColor(number);
                    cells[i][j].setValue(number);
                }
            }
        }
    }

    private static int countNearbyMines(int row, int col) {
        ArrayList<Coordinates> neighbours;
        neighbours = cells[row][col].getNeighbours();
        int count = 0;
        for (Coordinates neighbour : neighbours) {
            count += cells[neighbour.getRow()][neighbour.getCol()].isMined();
        }
        return count;
    }

    public static void startTimerOnFirstClick() {
        seconds = 0;
        startTimer();
    }

    public static void revealNeighbours(int row, int col) {
        ArrayList<Coordinates> neighbours;
        neighbours = cells[row][col].getNeighbours();
        for (Coordinates neighbour : neighbours) {
            if (!cells[neighbour.getRow()][neighbour.getCol()].isClicked()) {
                cells[neighbour.getRow()][neighbour.getCol()].getButton().doClick(0);
            }
        }
    }

    public static void checkWin() {
        if (clickedCells == rows * columns - numMines && !won) {
            won = true;
            freezeGame();
            new Win(Integer.parseInt(GameView.getLabelSeconds().getText()), GameView.getWindowLocation(), Settings.getCurrentLevel());
        }
    }

    public static void freezeGame() {
        stopTimer();
        Cell.setActive(false);
    }

    public static void makeBackgroundRed(int row, int col) {
        redX = row;
        redY = col;
        cells[row][col].getButton().setOpaque(true);
        cells[row][col].getButton().setBackground(Color.RED);
    }

    public static void revealMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 1 && !cells[i][j].isFlagged()) {
                    cells[i][j].getButton().setIcon(new ImageIcon(GameView.getImgMine()));
                }
            }
        }
    }

    public static void revealWrongFlagged() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 0 && cells[i][j].isFlagged()) {
                    cells[i][j].setImgMineCrossed();
                }
            }
        }
    }

    public static void plusMine() {
        GameView.getLabelMines().setText(Integer.toString(++minesLeft));
    }

    public static void minusMine() {
        GameView.getLabelMines().setText(Integer.toString(--minesLeft));
    }

    public static void restartFrame(Level level, Point point) {
        GameView.getFrame().removeAll();
        GameView.getFrame().dispose();
        if (timer != null) {
            stopTimer();
        }
        Settings.setCurrentLevel(level);
        checkPauseRestart();
        new GameView(point);
    }

    public static void setSaferFirstClickText() {
        if (Settings.isSaferFirstClick()) {
            GameView.getSettingsSaferFirstClick().setText("\u221ASafer first click");
        } else {
            GameView.getSettingsSaferFirstClick().setText("Safer first click");
        }
    }

    public static void setSafeRevealText() {
        if (Settings.isSafeReveal()) {
            GameView.getSettingsSafeReveal().setText("\u221ASafe reveal");
        } else {
            GameView.getSettingsSafeReveal().setText("Safe reveal");
        }
    }

    public static void clickCheckForUpdates() {
        boolean upToDate;
        try {
            upToDate = VersionCheck.checkForNewestVersion();
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(GameView.getFrame(), "Unable to check for updates.");
            return;
        }
        String message;
        if (upToDate) {
            message = "You are using the latest version: " + VersionCheck.getCurrentVersion();
        } else {
            message = "There is a new version released.\nVisit github.com/exusar/Minesweeper/releases/latest to download.";
        }
        JOptionPane.showMessageDialog(GameView.getFrame(), message);
    }

    public static boolean isFirstClicked() {
        return firstClicked;
    }

    public static void setFirstClicked(boolean firstClicked) {
        Game.firstClicked = firstClicked;
    }

    public static int getNumMines() {
        return numMines;
    }

    public static int getRows() {
        return rows;
    }

    public static int getColumns() {
        return columns;
    }

    public static void setMinesLeft(int minesLeft) {
        Game.minesLeft = minesLeft;
    }

    public static void setWon(boolean won) {
        Game.won = won;
    }

    public static void setClickedCells(int clickedCells) {
        Game.clickedCells = clickedCells;
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean paused) {
        Game.paused = paused;
    }
}