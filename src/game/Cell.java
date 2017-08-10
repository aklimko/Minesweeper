package game;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import static java.awt.Color.*;

class Cell extends Coordinates {
    private int value;
    private boolean mined, clicked, flagged;
    private JButton button;
    private List<Coordinates> neighbours;

    private static Game game;
    private static Image imgFlag, imgMineCrossed;

    Cell(int row, int col) {
        super(row, col);
        mined = false;
        clicked = false;
        flagged = false;
        findNeighbours();
    }

    private void findNeighbours() {
        int row = super.getRow();
        int col = super.getCol();
        int rows = game.getRows();
        int columns = game.getColumns();
        neighbours = new ArrayList<>(8);
        if ((row > 0 && row < rows - 1) && (col > 0 && col < columns - 1)) {
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row + 1, col - 1));
            neighbours.add(new Coordinates(row - 1, col + 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row + 1, col + 1));
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row + 1, col));
        } else if (row == 0 && col == 0) {
            neighbours.add(new Coordinates(0, 1));
            neighbours.add(new Coordinates(1, 0));
            neighbours.add(new Coordinates(1, 1));
        } else if (row == 0 && col == columns - 1) {
            neighbours.add(new Coordinates(0, col - 1));
            neighbours.add(new Coordinates(1, col - 1));
            neighbours.add(new Coordinates(1, col));
        } else if (row == rows - 1 && col == columns - 1) {
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row - 1, col));
        } else if (row == rows - 1 && col == 0) {
            neighbours.add(new Coordinates(row, 1));
            neighbours.add(new Coordinates(row - 1, 1));
            neighbours.add(new Coordinates(row - 1, 0));
        } else if (row == 0) {
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row + 1, col - 1));
            neighbours.add(new Coordinates(row + 1, col));
            neighbours.add(new Coordinates(row + 1, col + 1));
        } else if (row == rows - 1) {
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row - 1, col + 1));
        } else if (col == 0) {
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row + 1, col));
            neighbours.add(new Coordinates(row - 1, col + 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row + 1, col + 1));
        } else if (col == columns - 1) {
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row + 1, col));
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row + 1, col - 1));
        }
    }

    JButton makeCell() {
        button = new JButton();
        button.setMaximumSize(new Dimension(GameView.getSizeButton(), GameView.getSizeButton()));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFont(button.getFont().deriveFont(24f));
        button.setPreferredSize(new Dimension(GameView.getSizeButton(), GameView.getSizeButton()));
        button.setFocusPainted(false);
        button.setVisible(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 3) {
                    if (!clicked && game.isActive()) {
                        if (!flagged) {
                            flagged = true;
                            game.minusMine();
                            button.setIcon(new ImageIcon(imgFlag));
                        } else {
                            flagged = false;
                            game.plusMine();
                            button.setIcon(null);
                        }
                    }
                }
            }
        });
        button.addActionListener(e -> clickLeftMouseButton());
        return button;
    }

    private void clickLeftMouseButton() {
        if (!flagged && game.isActive()) {
            button.setContentAreaFilled(false);
            if (clicked && Settings.isSafeReveal()) {
                game.clickedNeighbours(super.getRow(), super.getCol());
            } else {
                clicked = true;
                game.addClickedCellsCounter();
                if (!game.isFirstClicked()) {
                    game.generateMines(game.getNumMines(), super.getRow(), super.getCol());
                    game.checkValues();
                    game.setFirstClicked(true);
                    game.startTimerOnFirstClick();
                }
                if (!mined) {
                    if (value != 0) {
                        button.setText(Integer.toString(value));
                    } else {
                        game.revealNeighbours(super.getRow(), super.getCol());
                    }
                    game.checkWin();
                } else {
                    game.freezeGame();
                    game.makeBackgroundRed(super.getRow(), super.getCol());
                    game.revealMines();
                    game.revealWrongFlagged();
                }
            }
        }
    }

    void setColor(int value) {
        Color color;
        switch (value) {
            case 1:
                color = BLUE;
                break;
            case 2:
                color = GREEN.darker();
                break;
            case 3:
                color = RED;
                break;
            case 4:
                color = BLUE.darker();
                break;
            case 5:
                color = RED.darker();
                break;
            case 6:
                color = CYAN.darker();
                break;
            case 7:
                color = BLACK;
                break;
            case 8:
                color = GRAY;
                break;
            default:
                color = WHITE;
                break;
        }
        button.setForeground(color);
    }

    static void setImages(Image imgFlag, Image imgMineCrossed) {
        Cell.imgFlag = imgFlag;
        Cell.imgMineCrossed = imgMineCrossed;
    }

    static void setGameReference(Game game){
        Cell.game = game;
    }

    void clearText() {
        button.setText("");
    }

    void setMined(boolean mined) {
        this.mined = mined;
    }

    int isMined() {
        return mined ? 1 : 0;
    }

    void setValue(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    JButton getButton() {
        return button;
    }

    boolean isClicked() {
        return clicked;
    }

    void setClickedToFalse() {
        this.clicked = false;
    }

    void setFlaggedToFalse() {
        this.flagged = false;
    }

    void setImgMineCrossed() {
        button.setIcon(new ImageIcon(imgMineCrossed));
    }

    boolean isFlagged() {
        return flagged;
    }

    List<Coordinates> getNeighbours() {
        return neighbours;
    }
}