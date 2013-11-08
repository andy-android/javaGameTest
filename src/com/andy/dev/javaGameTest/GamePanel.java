package com.andy.dev.javaGameTest;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	//Constants
    private final static int FPS = 50;
    
    //Custom objects
    private GameListener gameListener;
    private Character digger;
    
    //Utilities
    private Timer introTimer, gameTimer, winTimer;
    private ArrayList<Block> blockList;
    private ArrayList<Ladder> ladderList, exitLadderList;
    private ArrayList<Gold> goldList;
    private ArrayList<Rope> ropeList;
    private ArrayList<Character> ninjaList;
    
    //Game variables
    private short iteration = 0, winIteration = 0;
    private int score = 0, highScore = 0, goldCollected = 0, ninjasKilled = 0, level = 1, scrollX = 32 * 16;
    private int player = 1, lives = 5, currentScreen = 0; // 0=intro, 1=game, 2=end
    private boolean portalShown = false;
    private Font gameFont;
    
    private Gold winGold = new Gold(100, Frame.DIM.height / 3 - 30);
    private Character winNinja = new Character(Character.NINJA, 100, Frame.DIM.height / 3 + 20);
    private Block[] winBlock;
    private Ladder[] winLadder;
    private Character winDigger;
    
    public GamePanel()
    {
        winBlock = new Block[5];
        for (int i = 0; i < 5; i++)
        {
            if (i < 2)
                winBlock[i] = new Block(Block.REGULAR_TYPE, 32 * i, Frame.DIM.height - 96);
            else
                winBlock[i] = new Block(Block.REGULAR_TYPE, 32 * i + 32, Frame.DIM.height - 96);
        }
        winLadder = new Ladder[4];
        for (int i = 0; i < 4; i++)
            winLadder[i] = new Ladder(64, Frame.DIM.height - 32 * i);
        winDigger = new Character(Character.DIGGER, 64, Frame.DIM.height);
        
        setPreferredSize(Frame.DIM);
        setBackground(Color.black);
        
        digger = new Character(Character.DIGGER, 0, 0);
        
        introTimer = new Timer(4000, new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                startLevel();
                currentScreen = 1;
                introTimer.stop();
            }
        });
        gameTimer = new Timer(1000 / FPS, new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                iteration++;
                cycleSprites();
                doPhysics();
                moveElements();
                scrollScreen();
                repaint();
            }
        });
        winTimer = new Timer(1000 / FPS, new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                winIteration++;
                if (winIteration % 6 == 0)
                {
                    for (Block b : winBlock)
                        b.cycleSprite();
                    for (Ladder l : winLadder)
                        l.cycleSprite();
                    winGold.cycleSprite();
                    winNinja.setDirection(Character.LEFT);
                    winNinja.setState(Character.WALKING);
                    winNinja.cycleSprite();
                    winDigger.cycleSprite();
                    winDigger.move();
                }
                if (winIteration == 1)
                {
                    winDigger.setLocation(64, Frame.DIM.height);
                    winDigger.setState(Character.CLIMBING_UP);
                }
                if (winIteration == 157)
                    winDigger.setState(Character.WALKING);
                if (winIteration == 200)
                    winDigger.setState(Character.WINNING);
                if (winIteration >= 500)
                {
                    currentScreen = 0;
                    winIteration = 0;
                    winTimer.stop();
                    lives++;
                    score += ((goldCollected * 100) + (ninjasKilled * 100));
                    if (score > highScore)
                        highScore = score;
                    level++;
                    resetElements();
                    begin();
                }
                repaint();
            }
        });
        
        gameListener = new GameListener();
        setFocusable(true);
        
        blockList = new ArrayList<Block>();
        ladderList = new ArrayList<Ladder>();
        exitLadderList = new ArrayList<Ladder>();
        goldList = new ArrayList<Gold>();
        ropeList = new ArrayList<Rope>();
        ninjaList = new ArrayList<Character>(5);
        
        try
        {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/gameFont.ttf"));
            gameFont = font.deriveFont(23f);
        }
        catch (FontFormatException ex) {ex.printStackTrace();}
        catch (IOException ex) {ex.printStackTrace();}
    }
    
    public void paintComponent(Graphics gc)
    {
        super.paintComponent(gc);
        gc.setFont(gameFont);
        gc.setColor(Color.white);
        
        switch (currentScreen)
        {
            case 0:
            setBackground(Color.black);
            String p = "PLAYER " + player, stage = "STAGE  " + level + "  LEFT " + lives;
            String s = "SCORE " + score, hiscore = "HISCORE " + highScore;
            gc.drawString(p, ImageUtility.getCenteredText(p, gameFont), Frame.DIM.height / 3);
            gc.drawString(stage, ImageUtility.getCenteredText(stage, gameFont), Frame.DIM.height / 2);
            gc.drawString(s, ImageUtility.getCenteredText(s, gameFont), Frame.DIM.height * 2 / 3);
            gc.drawString(hiscore, ImageUtility.getCenteredText(hiscore, gameFont), Frame.DIM.height * 2 / 3 + 30);
            break;
            
            case 1:
            for (int i = 0; i < ladderList.size(); i++)
                ladderList.get(i).draw(gc);
            for (int i = 0; i < blockList.size(); i++)
                blockList.get(i).draw(gc);
            for (int i = 0; i < goldList.size(); i++)
                goldList.get(i).draw(gc);
            for (int i = 0; i < ropeList.size(); i++)
                ropeList.get(i).draw(gc);
            for (int i = 0; i < ninjaList.size(); i++)
                ninjaList.get(i).draw(gc);
            digger.draw(gc);
            break;
            
            case 2:
            String g = (goldCollected * 100) + " POINTS", n = (ninjasKilled * 100) + " POINTS";
            String t = "TOTAL  " + ((goldCollected * 100) + (ninjasKilled * 100)) + " POINTS";
            winGold.draw(gc);
            winNinja.draw(gc);
            for (Block b : winBlock)
                b.draw(gc);
            for (Ladder l : winLadder)
                l.draw(gc);
            winDigger.draw(gc);
            gc.drawString(g, ImageUtility.getCenteredText(g, gameFont) + 50, Frame.DIM.height / 3);
            gc.drawString(n, ImageUtility.getCenteredText(n, gameFont) + 50, Frame.DIM.height / 3 + 50);
            gc.drawString(t, ImageUtility.getCenteredText(t, gameFont), Frame.DIM.height * 2 / 3);
            break;
            
            case 3:
            String go = "Game Over";
            String ys = "Your score: " + score;
            gc.drawString(go, ImageUtility.getCenteredText(go, gameFont), Frame.DIM.height / 2);
            gc.drawString(ys, ImageUtility.getCenteredText(ys, gameFont), Frame.DIM.height / 2 + 50);
            break;
        }
    }
    
    public void begin()
    {
        currentScreen = 0;
        introTimer.start();
    }
    
    public void startLevel()
    {
        LevelLoader.loadLevel(level, digger, blockList, ladderList, exitLadderList, goldList, ropeList, ninjaList);
        addKeyListener(gameListener);
        requestFocus();
        gameTimer.start();
    }
    
    public void winLevel()
    {
        gameTimer.stop();
        removeKeyListener(gameListener);
        currentScreen = 2;
        winTimer.start();
        repaint();
    }
    
    public void loseLevel()
    {
        gameTimer.stop();
        removeKeyListener(gameListener);
        lives--;
        resetElements();
        if (lives == 0)
            currentScreen = 3;
        else
        {
            currentScreen = 0;
            introTimer.start();
        }
        repaint();
    }
    
    private void resetElements()
    {
        portalShown = false;
        goldCollected = 0;
        ninjasKilled = 0;
        iteration = 0;
        scrollX = 16 * 32;
        blockList.clear();
        ladderList.clear();
        exitLadderList.clear();
        ropeList.clear();
        goldList.clear();
        ninjaList.clear();
        digger.setState(Character.IDLE);
    }
    
    public void cycleSprites()
    {
        if (iteration % 4 == 0)
        {
            digger.cycleSprite();
            for (int i = 0; i < ninjaList.size(); i++)
                ninjaList.get(i).cycleSprite();
            for (int i = 0; i < blockList.size(); i++)
                if (blockList.get(i).getType() == Block.REGULAR_TYPE)
                    blockList.get(i).cycleSprite();
        }
        if (iteration % 8 == 0)
            for (int i = 0; i < goldList.size(); i++)
                goldList.get(i).cycleSprite();
        if (iteration % 18 == 0)
            for (int i = 0; i < ladderList.size(); i++)
                ladderList.get(i).cycleSprite();
    }
    
    public void doPhysics()
    {
        if (digger.getState() != Character.DYING && digger.getState() != Character.DEAD)
            determineDiggerState();
        determineNinjaStates();
        
        for (int i = 0; i < goldList.size(); i++)
        {
            if (digger.isOn(goldList.get(i)))
            {
                goldList.remove(i);
                goldCollected++;
            }
        }
        
        //Win condition
        if (iteration > 100 && goldList.size() == 0 && !portalShown)
        {
            portalShown = true;
            for (int i = 0; i < exitLadderList.size(); i++)
                ladderList.add(exitLadderList.get(i));
            exitLadderList.clear();
        } 
        //Lose condition
        if (digger.getState() == Character.DEAD)
            loseLevel();
        
        if (digger.getY() < -5 && portalShown)
            winLevel();
        
        if (digger.getY() > Frame.DIM.height)
            digger.setLocation(digger.getSpawnX(), digger.getSpawnY());
    }
    
    public void scrollScreen()
    {
        int speed = digger.getSpeed();
        if (digger.getX() > Frame.DIM.width * 3/4)
        {
            if (scrollX >= 32 * 28)
                scrollX = 32 * 28;
            else
            {
                scrollX += speed;
                digger.setX(digger.getX() - speed);
                digger.setSpawnLocation(digger.getSpawnX() - speed, digger.getSpawnY());
                for (Ladder l : ladderList)
                    l.setX(l.getX() - speed);
                for (Ladder l : exitLadderList)
                    l.setX(l.getX() - speed);
                for (Block b : blockList)
                    b.setX(b.getX() - speed);
                for (Rope r : ropeList)
                    r.setX(r.getX() - speed);
                for (Gold g : goldList)
                    g.setX(g.getX() - speed);
                for (Character n : ninjaList)
                {
                    n.setX(n.getX() - speed);
                    n.setSpawnLocation(n.getSpawnX() - speed, n.getSpawnY());
                }
            }
        }
        else if (digger.getX() < Frame.DIM.width * 1/4)
        {
            if (scrollX <= 32 * 16)
                scrollX = 32 * 16;
            else
            {
                scrollX -= speed;
                digger.setX(digger.getX() + speed);
                digger.setSpawnLocation(digger.getSpawnX() + speed, digger.getSpawnY());
                for (Ladder l : ladderList)
                    l.setX(l.getX() + speed);
                for (Ladder l : exitLadderList)
                    l.setX(l.getX() + speed);
                for (Block b : blockList)
                    b.setX(b.getX() + speed);
                for (Rope r : ropeList)
                    r.setX(r.getX() + speed);
                for (Gold g : goldList)
                    g.setX(g.getX() + speed);
                for (Character n : ninjaList)
                {
                    n.setX(n.getX() + speed);
                    n.setSpawnLocation(n.getSpawnX() + speed, n.getSpawnY());
                }
            }
        }
    }
    
    public void determineDiggerState()
    {
        @SuppressWarnings("unused")
		boolean onBlock = false, underBlock = false, toRightOfBlock = false, toLeftOfBlock = false, onNinja = false;
        for (int i = 0; i < blockList.size(); i++)
        {
            if (blockList.get(i).getType() != Block.TRAP_TYPE && blockList.get(i).getState() == Block.VISIBLE)
            {
                if (digger.isOn(blockList.get(i)))
                    onBlock = true;
                if (digger.isUnder(blockList.get(i)))
                    underBlock = true;
                if (digger.isToRightOf(blockList.get(i)))
                    toRightOfBlock = true;
                if (digger.isToLeftOf(blockList.get(i)))
                    toLeftOfBlock = true;
                if (digger.isInside(blockList.get(i)))
                    digger.setState(Character.DYING);
            }
        }
        for (int i = 0; i < ninjaList.size(); i++)
            if (digger.isOn(ninjaList.get(i)))
                onNinja = true;
        
        if (onBlock || onNinja)
        {
            if (digger.getState() == Character.FALLING)
                digger.setState(Character.IDLE);
            if (digger.getState() == Character.CLIMBING_DOWN)
                digger.setState(Character.CLIMBING_IDLE);
        }
        else
        {
            boolean aboveLadder = false, onLadder = false, onRope = false;
            Ladder ladder = null;
            for (int i = 0; i < ladderList.size(); i++)
            {
                if (digger.isOn(ladderList.get(i)))
                    onLadder = true;
                else if (digger.isAbove(ladderList.get(i)))
                {
                    aboveLadder = true;
                    ladder = ladderList.get(i);
                }
            }
            if (!onLadder && !aboveLadder)
                for (int i = 0; i < ropeList.size(); i++)
                    if (digger.isOn(ropeList.get(i)))
                        onRope = true;
            if (onRope)
            {
                if (digger.getState() == Character.WALKING)
                    digger.setState(Character.SHUFFLING);
                else if (digger.getState() == Character.FALLING)
                    digger.setState(Character.SHUFFLING_IDLE);
            }
            else if (ladder != null && !onLadder && aboveLadder && digger.getState() != Character.WALKING && digger.getState() != Character.CLIMBING_DOWN)
            {
                digger.setState(Character.IDLE);
                digger.setY(ladder.getY() - digger.getHeight());
            }
            if (!onBlock && !onNinja && !onRope && !onLadder && !aboveLadder && digger.getState() != Character.DYING)
                digger.setState(Character.FALLING);
        }
        
        for (int i = 0; i < ninjaList.size(); i++)
            if (digger.hitBy(ninjaList.get(i)))
                digger.setState(Character.DYING);
    }
    
    public void determineNinjaStates()
    {
        for (int i = 0; i < ninjaList.size(); i++)
        {
            @SuppressWarnings("unused")
			boolean onBlock = false, underBlock = false, toRightOfBlock = false, toLeftOfBlock = false;
            if (ninjaList.get(i).getState() != Character.SPAWNING && ninjaList.get(i).getState() != Character.STUCK &&
                ninjaList.get(i).getState() != Character.CLIMBING_OUT)
            {
                for (int k = 0; k < blockList.size(); k++)
                {
                    if (blockList.get(k).getType() != Block.TRAP_TYPE && blockList.get(k).getState() == Block.VISIBLE)
                    {
                        if (ninjaList.get(i).isOn(blockList.get(k)))
                            onBlock = true;
                        if (ninjaList.get(i).isUnder(blockList.get(k)))
                            underBlock = true;
                        if (ninjaList.get(i).isToRightOf(blockList.get(k)))
                            toRightOfBlock = true;
                        if (ninjaList.get(i).isToLeftOf(blockList.get(k)))
                            toLeftOfBlock = true;
                    }
                }
                boolean aboveLadder = false, onLadder = false, onRope = false;
                Ladder ladder = null;
                for (int k = 0; k < ladderList.size(); k++)
                {
                    if (ninjaList.get(i).isOn(ladderList.get(k)))
                        onLadder = true;
                    else if (ninjaList.get(i).getX() >= ladderList.get(k).getX() - 3 &&
                             ninjaList.get(i).getX() + ninjaList.get(i).getWidth() <= ladderList.get(k).getX() + ladderList.get(k).getWidth() + 3 &&
                             ninjaList.get(i).getY() + ninjaList.get(i).getHeight() <= ladderList.get(k).getY() &&
                             ninjaList.get(i).getY() + ninjaList.get(i).getHeight() >= ladderList.get(k).getY() - 4)
                    {
                        aboveLadder = true;
                        ladder = ladderList.get(k);
                    }
                }
                boolean onNinja = false;
                for (int k = 0; k < ninjaList.size(); k++)
                    if (i != k && ninjaList.get(i).isOn(ninjaList.get(k)))
                        onNinja = true;
                
                if (!onLadder && !aboveLadder)
                    for (int k = 0; k < ropeList.size(); k++)
                        if (ninjaList.get(i).isOn(ropeList.get(k)))
                            onRope = true;
                
                if (digger.getX() + (digger.getWidth() / 2) > ninjaList.get(i).getX() + (ninjaList.get(i).getWidth() / 2) + 2)
                {
                    if (digger.getY() + (digger.getHeight() / 2) < ninjaList.get(i).getY() + (ninjaList.get(i).getHeight() / 2) - 2)
                    {
                        if (onLadder)
                            ninjaList.get(i).setState(Character.CLIMBING_UP);
                        else if (aboveLadder)
                        {
                            ninjaList.get(i).setDirection(Character.RIGHT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                        else if (onRope)
                        {
                            ninjaList.get(i).setDirection(Character.RIGHT);
                            ninjaList.get(i).setState(Character.SHUFFLING);
                        }
                        else
                        {
                            ninjaList.get(i).setDirection(Character.RIGHT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                    }
                    else if (digger.getY() + (digger.getHeight() / 2) > ninjaList.get(i).getY() + ninjaList.get(i).getHeight() + 2)
                    {
                        if (onBlock || onNinja)
                        {
                            ninjaList.get(i).setDirection(Character.RIGHT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                        if (onLadder || aboveLadder)
                        {
                            if (toLeftOfBlock)
                                ninjaList.get(i).setState(Character.CLIMBING_DOWN);
                            else
                            {
                                ninjaList.get(i).setDirection(Character.RIGHT);
                                ninjaList.get(i).setState(Character.WALKING);
                            }
                        }
                        if (onRope && !onBlock)
                            ninjaList.get(i).setY(ninjaList.get(i).getY() + 20);
                    }
                    else
                    {
                        if (onBlock || onNinja || onLadder || aboveLadder)
                        {
                            if (aboveLadder)
                                ninjaList.get(i).setY(ladder.getY() - ninjaList.get(i).getHeight());
                            ninjaList.get(i).setDirection(Character.RIGHT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                        if (onRope)
                        {
                            ninjaList.get(i).setDirection(Character.RIGHT);
                            ninjaList.get(i).setState(Character.SHUFFLING);
                        }
                    }
                }
                else if (digger.getX() + (digger.getWidth() / 2) < ninjaList.get(i).getX() + (ninjaList.get(i).getWidth() / 2) - 2)
                {
                    if (digger.getY() + (digger.getHeight() / 2) < ninjaList.get(i).getY() + (ninjaList.get(i).getHeight() / 2) - 2)
                    {
                        if (onLadder)
                            ninjaList.get(i).setState(Character.CLIMBING_UP);
                        else if (aboveLadder)
                        {
                            ninjaList.get(i).setDirection(Character.LEFT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                        else
                        {
                            ninjaList.get(i).setDirection(Character.LEFT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                    }
                    else if (digger.getY() + (digger.getHeight() / 2) > ninjaList.get(i).getY() + ninjaList.get(i).getHeight() + 2)
                    {
                        if (onLadder || aboveLadder)
                            ninjaList.get(i).setState(Character.CLIMBING_DOWN);
                        else if (onRope && !onBlock)
                            ninjaList.get(i).setY(ninjaList.get(i).getY() + 20);
                        else
                        {
                            ninjaList.get(i).setDirection(Character.LEFT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                    }
                    else
                    {
                        if (onBlock || onNinja || onLadder || aboveLadder)
                        {
                            if (aboveLadder)
                                ninjaList.get(i).setY(ladder.getY() - ninjaList.get(i).getHeight());
                            ninjaList.get(i).setDirection(Character.LEFT);
                            ninjaList.get(i).setState(Character.WALKING);
                        }
                        if (onRope)
                        {
                            ninjaList.get(i).setDirection(Character.LEFT);
                            ninjaList.get(i).setState(Character.SHUFFLING);
                        }
                    }
                }
                else
                {
                    if (digger.getY() + (digger.getHeight() / 2) < ninjaList.get(i).getY() + (ninjaList.get(i).getHeight() / 2) - 2)
                    {
                        if (onLadder)
                            ninjaList.get(i).setState(Character.CLIMBING_UP);
                        if (ninjaList.get(i).getState() == Character.CLIMBING_UP && !aboveLadder && !onLadder)
                            ninjaList.get(i).setState(Character.WALKING);
                    }
                    else if (digger.getY() + (digger.getHeight() / 2) > ninjaList.get(i).getY() + ninjaList.get(i).getHeight() + 2)
                    {
                        if (onLadder || aboveLadder)
                            ninjaList.get(i).setState(Character.CLIMBING_DOWN);
                        if (onBlock || onNinja)
                            ninjaList.get(i).setState(Character.IDLE);
                        if (onRope && !onBlock)
                            ninjaList.get(i).setY(ninjaList.get(i).getY() + 20);
                    }
                    else
                    {
                        if (onBlock || onNinja || onLadder || aboveLadder)
                            ninjaList.get(i).setState(Character.IDLE);
                        if (onRope)
                            ninjaList.get(i).setState(Character.SHUFFLING_IDLE);
                    }
                }
                
                if (onRope && ninjaList.get(i).getState() == Character.WALKING)
                    ninjaList.get(i).setState(Character.SHUFFLING);
                
                if (!onBlock && !onRope && !onLadder && !aboveLadder && !onNinja)
                    ninjaList.get(i).setState(Character.FALLING);
            }
            
            boolean stuck = false, insideBlock = false;
            for (int k = 0; k < blockList.size(); k++)
            {
                if (blockList.get(k).getType() != Block.TRAP_TYPE && ninjaList.get(i).isInside(blockList.get(k)))
                {
                    if (blockList.get(k).getState() != Block.VISIBLE)
                    {
                        stuck = true;
                        if (ninjaList.get(i).getState() != Character.CLIMBING_OUT)
                            ninjaList.get(i).setY(blockList.get(k).getY());
                    }
                    else
                        insideBlock = true;
                }
            }
            if (stuck && ninjaList.get(i).getState() != Character.CLIMBING_OUT)
                ninjaList.get(i).setState(Character.STUCK);
            if (insideBlock)
            {
                ninjasKilled++;
                ninjaList.get(i).setLocation(ninjaList.get(i).getSpawnX(), ninjaList.get(i).getSpawnY());
                ninjaList.get(i).setState(Character.SPAWNING);
            }
            
            if (ninjaList.get(i).getState() == Character.CLIMBING_OUT && 
                ninjaList.get(i).getCurrentSprite() == ninjaList.get(i).getImages().length - 1)
            {
                if (digger.getX() > ninjaList.get(i).getX())
                    ninjaList.get(i).setDirection(Character.RIGHT);
                else
                    ninjaList.get(i).setDirection(Character.LEFT);
                ninjaList.get(i).setState(Character.WALKING);
                ninjaList.get(i).move();
            }
            
            if (digger.isOn(ninjaList.get(i)) && !stuck && !insideBlock)
            {
                if (toLeftOfBlock)
                    ninjaList.get(i).setX(ninjaList.get(i).getX() - ninjaList.get(i).getWidth());
                else if (toRightOfBlock)
                    ninjaList.get(i).setX(ninjaList.get(i).getX() + ninjaList.get(i).getWidth());
                else
                {
                    ninjaList.get(i).setDirection(Character.RIGHT);
                    ninjaList.get(i).setState(Character.WALKING);
                }
            }
        }
    }
    
    public void moveElements()
    {
        digger.move();
        for (int i = 0; i < ninjaList.size(); i++)
            ninjaList.get(i).move();
    }
    
    public Block getLeftBlock()
    {
        Block leftBlock = null;
        
        int x = digger.getX(), y = digger.getY();
        for (Block b : blockList)
            if (x >= b.getX() + b.getWidth() - 16 && x <= b.getX() + b.getWidth() + 16 &&
                y + digger.getHeight() <= b.getY() + 2 && y + digger.getHeight() >= b.getY() - 2)
                leftBlock = b;
        
        return leftBlock;
    }
    
    public Block getRightBlock()
    {
        Block rightBlock = null;
        
        int x = digger.getX(), y = digger.getY();
        for (Block b : blockList)
            if (x + digger.getWidth() >= b.getX() - 16 && x + digger.getWidth() <= b.getX() + 16 &&
                y + digger.getHeight() <= b.getY() + 2 && y + digger.getHeight() >= b.getY() - 2)
                rightBlock = b;
        
        return rightBlock;
    }
    
    public Block getBlockBelow()
    {
        Block blockBelow = null;
        int x = digger.getRoundedX();
        
        for (int i = 0; i < blockList.size(); i++)
            if (x == blockList.get(i).getX() &&
                digger.getY() + digger.getHeight() >= blockList.get(i).getY() - 3 &&
                digger.getY() + digger.getHeight() <= blockList.get(i).getY() + 3)
                blockBelow = blockList.get(i);
        
        return blockBelow;
    }
    
    public boolean isDiggable(Block block)
    {
        boolean ladderAbove = false, blockAbove = false, goldAbove = false;
        for (int i = 0; i < ladderList.size(); i++)
            if (ladderList.get(i).getX() == block.getX() &&
                ladderList.get(i).getY() + ladderList.get(i).getHeight() == block.getY())
                ladderAbove = true;
        for (int i = 0; i < blockList.size(); i++)
            if (blockList.get(i).getX() == block.getX() &&
                blockList.get(i).getY() + blockList.get(i).getHeight() == block.getY() &&
                blockList.get(i).getState() == Block.VISIBLE)
                blockAbove = true;
        for (int i = 0; i < goldList.size(); i++)
            if (goldList.get(i).getX() == block.getX() &&
                goldList.get(i).getY() + goldList.get(i).getHeight() == block.getY())
                goldAbove = true;
        
        return (block.getType() == Block.REGULAR_TYPE && block.getState() == Block.VISIBLE && !ladderAbove && !blockAbove && !goldAbove);
    }
    
    private class GameListener implements KeyListener
    {
        public void keyPressed(KeyEvent event)
        {
            int key = event.getKeyCode();
            
            if (digger.getState() != Character.DIGGING && digger.getState()!= Character.DYING && digger.getState() != Character.DEAD)
            {
                boolean aboveLadder = false, onLadder = false, onRope = false;
                for (int i = 0; i < ladderList.size(); i++)
                {
                    if (digger.isOn(ladderList.get(i)))
                        onLadder = true;
                    else if (digger.isAbove(ladderList.get(i)))
                        aboveLadder = true;
                }
                if (!onLadder && !aboveLadder)
                    for (int i = 0; i < ropeList.size(); i++)
                        if (digger.isOn(ropeList.get(i)))
                            onRope = true;
                
                Block leftBlock = getLeftBlock();
                Block rightBlock = getRightBlock();
                
                switch (key)
                {
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                    digger.setDirection(Character.LEFT);
                    if (onRope)
                        digger.setState(Character.SHUFFLING);
                    else
                        digger.setState(Character.WALKING);
                    break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                    digger.setDirection(Character.RIGHT);
                    if (onRope)
                        digger.setState(Character.SHUFFLING);
                    else
                        digger.setState(Character.WALKING);
                    break;
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                    if (onLadder)
                        digger.setState(Character.CLIMBING_UP);
                    break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                    if (onLadder || aboveLadder)
                        digger.setState(Character.CLIMBING_DOWN);
                    else if (onRope && getBlockBelow() == null)
                    {
                        digger.setX(digger.getRoundedX());
                        digger.setState(Character.FALLING);
                        digger.setY(digger.getY() + 20);
                    }
                    break;
                    case KeyEvent.VK_Q:
                    case KeyEvent.VK_Z:
                    if (leftBlock != null && isDiggable(leftBlock) && digger.getState() != Character.FALLING)
                    {
                        digger.setDirection(Character.LEFT);
                        digger.setState(Character.DIGGING);
                        digger.setX(leftBlock.getX() + leftBlock.getWidth());
                        leftBlock.setState(Block.CRUMBLING);
                    }
                    break;
                    case KeyEvent.VK_E:
                    case KeyEvent.VK_X:
                    if (rightBlock != null && isDiggable(rightBlock) && digger.getState() != Character.FALLING)
                    {
                        digger.setDirection(Character.RIGHT);
                        digger.setState(Character.DIGGING);
                        digger.setX(rightBlock.getX() - digger.getWidth());
                        rightBlock.setState(Block.CRUMBLING);
                    }
                    break;
                }
            }
            if (key == KeyEvent.VK_ENTER)
            {
                if (gameTimer.isRunning())
                    gameTimer.stop();
                else
                    gameTimer.start();
            }
        }
        public void keyReleased(KeyEvent event)
        {
            int key = event.getKeyCode();
            
            if (digger.getState() != Character.DIGGING && digger.getState()!= Character.DYING && digger.getState() != Character.DEAD)
            {
                boolean onLadder = false, onRope = false;
                for (int i = 0; i < ladderList.size(); i++)
                    if (digger.isOn(ladderList.get(i)))
                        onLadder = true;
                if (!onLadder && !onRope)
                    for (int i = 0; i < ropeList.size(); i++)
                        if (digger.isOn(ropeList.get(i)))
                            onRope = true;
                switch (key)
                {
                    case KeyEvent.VK_Q:
                    case KeyEvent.VK_E:
                    case KeyEvent.VK_Z:
                    case KeyEvent.VK_X:
                    break;
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    if (onLadder)
                        digger.setState(Character.CLIMBING_IDLE);
                    break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                    if (onRope)
                        digger.setState(Character.SHUFFLING_IDLE);
                    else
                        digger.setState(Character.IDLE);
                    break;
                    default:
                    digger.setState(Character.IDLE);
                    break;
                }
            }
        }
        public void keyTyped(KeyEvent event) {}
    }
}