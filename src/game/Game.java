package game;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import javax.swing.*;

class Game {
    private int rows, columns;
    private int numMines;
    private boolean firstClicked;
    private boolean won;
    private boolean paused;
    private boolean active;
    private int redX, redY;
    private int minesLeft, seconds;
    private int clickedCells;
    private Timer timer;
    private Cell[][] cells;
    private final GameView gameView;

    Game(GameView gameView){
        this.gameView = gameView;
        paused = false;
        active = true;
        clickedCells = 0;
        setLevel(Settings.getCurrentLevel());
    }

    private void setLevel(Level level) {
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

    void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gameView.getLabelSeconds().setText(Integer.toString(seconds++));
            }
        }, 0, 1000);
    }

    void restartGame() {
        if (!active) {
            active = true;
            if (!won) {
                cells[redX][redY].getButton().setBackground(gameView.getBackgroundColor());
            } else {
                won = false;
            }
        }
        resetAllMines();
        gameView.getLabelMines().setText(Integer.toString(numMines));
        gameView.getLabelSeconds().setText("0");
        if (firstClicked) {
            resetCounters();
        }
        checkPauseRestart();
    }

    private void resetAllMines(){
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
    }

    private void resetCounters(){
        stopTimer();
        seconds = 0;
        clickedCells = 0;
        minesLeft = numMines;
        firstClicked = false;
    }

    private void checkPauseRestart() {
        if (paused) {
            paused = false;
            gameView.getGamePause().setText("Pause");
            gameView.getPanelMain().setVisible(true);
        }
    }

    void generateCells() {
        cells = new Cell[rows][columns];
        Cell.setGameReference(this);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(i, j);
                gameView.getPanelMain().add(cells[i][j].makeCell());
            }
        }
    }

    void clickedNeighbours(int row, int col) {
        int value = cells[row][col].getValue();
        int numFlagged = 0;

        List<Coordinates> neighbours = cells[row][col].getNeighbours();
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

    void addClickedCellsCounter() {
        clickedCells++;
    }

    void generateMines(int numMines, int row, int col) {
        Random random = new Random();
        List<Coordinates> occupiedCoordinates = new ArrayList<>();
        if (Settings.isSaferFirstClick()) {
            occupiedCoordinates.addAll(cells[row][col].getNeighbours());
        }
        occupiedCoordinates.add(new Coordinates(row, col));

        int generatedMinesCounter = 0;
        while (generatedMinesCounter < numMines) {
            int randomPosition = random.nextInt(rows * columns);
            Coordinates coordinates = convertPositionToCoordinates(randomPosition);
            if (!occupiedCoordinates.contains(coordinates)) {
                occupiedCoordinates.add(coordinates);
                cells[coordinates.getRow()][coordinates.getCol()].setMined(true);
                generatedMinesCounter++;
            }
        }
    }

    private Coordinates convertPositionToCoordinates(int position) {
        int row = position / columns;
        int col = position % columns;
        return new Coordinates(row, col);
    }

    void checkValues() {
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

    private int countNearbyMines(int row, int col) {
        List<Coordinates> neighbours = cells[row][col].getNeighbours();
        int count = 0;
        for (Coordinates neighbour : neighbours) {
            count += cells[neighbour.getRow()][neighbour.getCol()].isMined();
        }
        return count;
    }

    void startTimerOnFirstClick() {
        seconds = 0;
        startTimer();
    }

    void revealNeighbours(int row, int col) {
        List<Coordinates> neighbours = cells[row][col].getNeighbours();
        for (Coordinates neighbour : neighbours) {
            if (!cells[neighbour.getRow()][neighbour.getCol()].isClicked()) {
                cells[neighbour.getRow()][neighbour.getCol()].getButton().doClick(0);
            }
        }
    }

    void checkWin() {
        if (clickedCells == rows * columns - numMines && !won) {
            won = true;
            freezeGame();
            new Win(Integer.parseInt(gameView.getLabelSeconds().getText()), gameView.getWindowLocation(), Settings.getCurrentLevel());
        }
    }

    void freezeGame() {
        stopTimer();
        active = false;
    }

    void makeBackgroundRed(int row, int col) {
        redX = row;
        redY = col;
        cells[row][col].getButton().setOpaque(true);
        cells[row][col].getButton().setBackground(Color.RED);
    }

    void revealMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 1 && !cells[i][j].isFlagged()) {
                    cells[i][j].getButton().setIcon(new ImageIcon(gameView.getImgMine()));
                }
            }
        }
    }

    void revealWrongFlagged() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 0 && cells[i][j].isFlagged()) {
                    cells[i][j].setImgMineCrossed();
                }
            }
        }
    }

    void plusMine() {
        gameView.getLabelMines().setText(Integer.toString(++minesLeft));
    }

    void minusMine() {
        gameView.getLabelMines().setText(Integer.toString(--minesLeft));
    }

    void restartFrame(Level level, Point point) {
        gameView.getFrame().removeAll();
        gameView.getFrame().dispose();
        if (timer != null) {
            stopTimer();
        }
        Settings.setCurrentLevel(level);
        checkPauseRestart();
        new GameView(point);
    }

    void setSaferFirstClickText() {
        if (Settings.isSaferFirstClick()) {
            gameView.getSettingsSaferFirstClick().setText("\u221ASafer first click");
        } else {
            gameView.getSettingsSaferFirstClick().setText("Safer first click");
        }
    }

    void setSafeRevealText() {
        if (Settings.isSafeReveal()) {
            gameView.getSettingsSafeReveal().setText("\u221ASafe reveal");
        } else {
            gameView.getSettingsSafeReveal().setText("Safe reveal");
        }
    }

    void clickCheckForUpdates() {
        boolean upToDate;
        try {
            upToDate = VersionCheck.checkForNewestVersion();
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(gameView.getFrame(), "Unable to check for updates.");
            return;
        }
        String message;
        if (upToDate) {
            message = "You are using the latest version: " + VersionCheck.getCurrentVersion();
        } else {
            message = "There is a new version released.\nVisit github.com/exusar/Minesweeper/releases/latest to download.";
        }
        JOptionPane.showMessageDialog(gameView.getFrame(), message);
    }

    boolean isFirstClicked() {
        return firstClicked;
    }

    void setFirstClicked(boolean firstClicked) {
        this.firstClicked = firstClicked;
    }

    int getNumMines() {
        return numMines;
    }

    int getRows() {
        return rows;
    }

    int getColumns() {
        return columns;
    }

    void setMinesLeft(int minesLeft) {
        this.minesLeft = minesLeft;
    }

    void setWon(boolean won) {
        this.won = won;
    }

    boolean isPaused() {
        return paused;
    }

    void setPaused(boolean paused) {
        this.paused = paused;
    }

    boolean isActive() {
        return active;
    }
}