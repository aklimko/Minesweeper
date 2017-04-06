package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Cell extends Coordinates {
    private JButton button;
    private int value;
    private Color color;
    private static Image imgFlag, imgBombCrossed;
    
    private boolean mined, clicked, flagged;
    private static boolean active = true;
    
    public Cell(){
        mined = false;
        clicked = false;
        flagged = false;
    }
    
    public JButton makeCell(int i, int j, int sizeButton){
        row=i;
        col=j;
        button = new JButton();
        button.setMaximumSize(new Dimension(sizeButton, sizeButton));
        button.setMargin(new Insets(0,0,0,0));
        button.setFont(button.getFont().deriveFont(24f));
        button.setPreferredSize(new Dimension(sizeButton, sizeButton));
        button.setFocusPainted(false);
        button.setVisible(true);
        
        button.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                if(e.getButton() == 3) {
                    if(!clicked && active){
                        if(!flagged){
                            flagged = true;
                            Game.minusMine();
                            button.setIcon(new ImageIcon(imgFlag));
                        }
                        else{
                            flagged = false;
                            Game.plusMine();
                            button.setIcon(null);
                        }
                    }
                }
            }
        });
        
        button.addActionListener((ActionEvent e) -> {
            if(!flagged && active){
                button.setContentAreaFilled(false);
                if (clicked && Settings.isSafeReveal()){
                    Game.clickedNeighbours(row, col);
                }
                else{
                    clicked = true;
                    Game.addClickedCellsCounter();
                    if(!Game.isFirstClicked()){
                        Game.generateMines(Game.getNumMines(), row, col);
                        Game.setFirstClicked(true);
                        Game.startTimer();
                    }
                    if(!mined){
                        if(value!=0){
                            button.setText(Integer.toString(value));
                        }
                        else{
                            Game.revealNeighbours(row, col);
                        }
                        Game.checkWin();
                    }
                    else{
                        Game.freezeGame();
                        Game.makeRed(row, col);
                        Game.revealMines();
                    }
                }
            }
        });
        return button;
    }

    public void setColor(int val){
        switch(val){
            case 0:
                color = Color.BLACK;
                button.setForeground(color);
                break;
            case 1:
                color = Color.BLUE;
                button.setForeground(color);
                break;
            case 2:
                color = Color.GREEN.darker();
                button.setForeground(color);
                break;
            case 3:
                color = Color.RED;
                button.setForeground(color);
                break;
            case 4:
                color = Color.BLUE.darker();
                button.setForeground(color);
                break;
            case 5:
                color = Color.RED.darker();
                button.setForeground(color);
                break;
            case 6:
                color = Color.CYAN.darker();
                button.setForeground(color);
                break;
            case 7:
                color = Color.BLACK;
                button.setForeground(color);
                break;
            case 8:
                color = Color.GRAY;
                button.setForeground(color);
                break;
            default:
                color = Color.BLACK;
                button.setForeground(color);
                break;
        }
    }
    
    public static void setImages(Image imgFlag, Image imgMineCrossed){
        Cell.imgFlag = imgFlag;
        Cell.imgBombCrossed = imgMineCrossed;
    }
    
    public void setText(String text){
        if(!text.equals("0")){
            button.setText(text);
        }
    }
    
    public void setMined(boolean mined){
        this.mined = mined;
    }
    
    public int isMined(){
        return mined ? 1 : 0;
    }
    
    public void setValue(int value){
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
    
    public JButton getButton(){
        return button;
    }
    
    public boolean isClicked() {
        return clicked;
    }
    
    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
    
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public static void setActive(boolean active) {
        Cell.active = active;
    }
}