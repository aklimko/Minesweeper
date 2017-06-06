package game;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;

import static java.awt.Color.*;

class Cell extends Coordinates {
    private JButton button;
    private int value;
    private static Image imgFlag, imgMineCrossed;

    private boolean mined, clicked, flagged;
    private static boolean active;

    public Cell(int row, int col) {
        super(row, col);
        mined = false;
        clicked = false;
        flagged = false;
    }

    public JButton makeCell() {
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
                    if (!clicked && active) {
                        if (!flagged) {
                            flagged = true;
                            Game.minusMine();
                            button.setIcon(new ImageIcon(imgFlag));
                        } else {
                            flagged = false;
                            Game.plusMine();
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
        if (!flagged && active) {
            button.setContentAreaFilled(false);
            if (clicked && Settings.isSafeReveal()) {
                Game.clickedNeighbours(super.getRow(), super.getCol());
            } else {
                clicked = true;
                Game.addClickedCellsCounter();
                if (!Game.isFirstClicked()) {
                    Game.generateMines(Game.getNumMines(), super.getRow(), super.getCol());
                    Game.setFirstClicked(true);
                    Game.startTimerOnFirstClick();
                }
                if (!mined) {
                    if (value != 0) {
                        button.setText(Integer.toString(value));
                    } else {
                        Game.revealNeighbours(super.getRow(), super.getCol());
                    }
                    Game.checkWin();
                } else {
                    Game.freezeGame();
                    Game.makeBackgroundRed(super.getRow(), super.getCol());
                    Game.revealMines();
                    Game.revealWrongFlagged();
                }
            }
        }
    }

    public void setColor(int value) {
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

    public static void setImages(Image imgFlag, Image imgMineCrossed) {
        Cell.imgFlag = imgFlag;
        Cell.imgMineCrossed = imgMineCrossed;
    }

    public void clearText() {
        button.setText("");
    }

    public void setMined(boolean mined) {
        this.mined = mined;
    }

    public int isMined() {
        return mined ? 1 : 0;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public JButton getButton() {
        return button;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClickedToFalse() {
        this.clicked = false;
    }

    public void setFlaggedToFalse() {
        this.flagged = false;
    }

    public void setImgMineCrossed() {
        button.setIcon(new ImageIcon(imgMineCrossed));
    }

    public boolean isFlagged() {
        return flagged;
    }

    public static void setActive(boolean active) {
        Cell.active = active;
    }

    public static boolean isActive() {
        return active;
    }
}