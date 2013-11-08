package com.andy.dev.javaGameTest;


import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class TitleScreen extends JPanel implements KeyListener
{	private Font gameFont;
    private Image logo, title, blockBorder, leftPic, rightPic, selector;
    private Point[] selectorPoints = {new Point(165, 185), new Point(165, 225)};
    private int currentPoint = 0;
    private boolean enterPressed = false;
    
    public TitleScreen()
    {
        setPreferredSize(Frame.DIM);
        setBackground(new Color(32, 56, 236));
        
        logo = ImageUtility.getImage("sprites/logo.png");
        title = ImageUtility.getImage("sprites/title.png");
        blockBorder = ImageUtility.getImage("sprites/blockBorder.png");
        leftPic = ImageUtility.getImage("sprites/Digger/dig1.png");
        rightPic = ImageUtility.getImage("sprites/Digger/climb1.png");
        selector = ImageUtility.getImage("sprites/selector.png");
        
        try
        {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/gameFont.ttf"));
            gameFont = font.deriveFont(17f);
        }
        catch (FontFormatException ex) {ex.printStackTrace();}
        catch (IOException ex) {ex.printStackTrace();}
        
        addKeyListener(this);
        setFocusable(true);
    }
    
    public void paintComponent(Graphics gc)
    {
        super.paintComponent(gc);
        gc.setColor(Color.white);
        gc.setFont(gameFont);
        

        
        gc.drawString("1 Player", 190, 200);
        gc.drawString("2 Player", 190, 240);
        gc.drawImage(selector, selectorPoints[currentPoint].x, selectorPoints[currentPoint].y,
                     selector.getWidth(null) * 2, selector.getHeight(null) * 2, null);
        
        Font smallFont = gameFont.deriveFont(14f);
        gc.setFont(smallFont);
        String string1 = "Test Game";
        String string2 = "Android-andy";
        gc.drawString(string1, ImageUtility.getCenteredText(string1, smallFont), 400);
        gc.drawString(string2, ImageUtility.getCenteredText(string2, smallFont), 420);

    }
    
    public boolean enterPressed()
    {
        return enterPressed;
    }
    
    public void keyPressed(KeyEvent event)
    {
        int key = event.getKeyCode();
        switch (key)
        {
            case KeyEvent.VK_UP:
            currentPoint--;
            if (currentPoint < 0)
                currentPoint = selectorPoints.length - 1;
            break;
            case KeyEvent.VK_DOWN:
            currentPoint++;
            if (currentPoint >= selectorPoints.length)
                currentPoint = 0;
            break;
            case KeyEvent.VK_ENTER:
               enterPressed = true;
               break;
        }
        repaint();
    }
    public void keyReleased(KeyEvent event) {}
    public void keyTyped(KeyEvent event) {}
}