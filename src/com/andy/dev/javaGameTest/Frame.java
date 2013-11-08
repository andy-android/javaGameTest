package com.andy.dev.javaGameTest;
import javax.swing.JFrame;
import java.awt.Dimension;

public class Frame extends JFrame implements Runnable
{
	private static final long serialVersionUID = 1L;
	public static final Dimension DIM = new Dimension(16 * 32, 16 * 32);
    public static JFrame FRAME;
    
    private TitleScreen titleScreen;
    private GamePanel gamePanel;
    private boolean running = true;
    
    public static void main(String[] args)
    {
        FRAME = new Frame();
    }
    
    public Frame()
    {
        super("Lode Runner");
        titleScreen = new TitleScreen();
        gamePanel = new GamePanel();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(titleScreen);
        pack();
        setLocation(50, 50);
        setIconImage(ImageUtility.getImage("sprites/Digger/idle1.png"));
        setResizable(false);
        setVisible(true);
        new Thread(this).start();
    }
    
    public void newGame()
    {
        getContentPane().remove(titleScreen);
        getContentPane().add(gamePanel);
        gamePanel.begin();
        validate();
    }
    
    public void run()
    {
        while (running)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException ex) {}
            if (titleScreen.enterPressed())
            {
                running = false;
                newGame();
            }
        }
    }
}