package com.andy.dev.javaGameTest;

import java.awt.Image;

public class Character extends Sprite
{
    public final static int IDLE = 0, WALKING = 1, FALLING = 2, DIGGING = 3;
    public final static int CLIMBING_UP = 4, CLIMBING_DOWN = 5, CLIMBING_IDLE = 6;
    public final static int SHUFFLING = 7, SHUFFLING_IDLE = 8, DYING = 9, DEAD = 10;
    public final static int SPAWNING = 11, STUCK = 12, CLIMBING_OUT = 13, WINNING = 14;
    public final static int DIGGER = 0, NINJA = 1;
    
    private Image[] idle, walk, fall, dig, climb, shuffle, die, spawn, stuck, win;
    
	private int state = IDLE;
	@SuppressWarnings("unused")
	private int type = DIGGER;
    private int spawnX, spawnY, speed;
    
    public Character(int type, int x, int y)
    {
        this.type = type;
        setX(x);
        setY(y);
        spawnX = x;
        spawnY = y;
        
        speed = (type == NINJA) ? 3 : 5;
        
        String folder = (type == DIGGER) ? "Digger" : "Ninja";
        idle = new Image[1];
        for (int i = 0; i < idle.length; i++)
            idle[i] = ImageUtility.getImage("sprites/" + folder + "/idle" + (i+1) + ".png");
        walk = new Image[3];
        for (int i = 0; i < walk.length; i++)
            walk[i] = ImageUtility.getImage("sprites/" + folder + "/walk" + (i+1) + ".png");
        fall = new Image[2];
        for (int i = 0; i < fall.length; i++)
            fall[i] = ImageUtility.getImage("sprites/" + folder + "/fall" + (i+1) + ".png");
        dig = new Image[4];
        for (int i = 0; i < dig.length; i++)
            dig[i] = ImageUtility.getImage("sprites/" + folder + "/dig" + (i+1) + ".png");
        climb = new Image[4];
        for (int i = 0; i < climb.length; i++)
            climb[i] = ImageUtility.getImage("sprites/" + folder + "/climb" + (i+1) + ".png");
        shuffle = new Image[3];
        for (int i = 0; i < shuffle.length; i++)
            shuffle[i] = ImageUtility.getImage("sprites/" + folder + "/shuffle" + (i+1) + ".png");
        die = new Image[6];
        for (int i = 0; i < die.length; i++)
            die[i] = ImageUtility.getImage("sprites/" + folder + "/die" + (i+1) + ".png");
        spawn = new Image[4];
        for (int i = 0; i < spawn.length; i++)
            spawn[i] = ImageUtility.getImage("sprites/" + folder + "/spawn" + (i+1) + ".png");
        stuck = new Image[15];
        for (int i = 0; i < stuck.length; i++)
            stuck[i] = ImageUtility.getImage("sprites/" + folder + "/fall1.png");
        win = new Image[4];
        for (int i = 0; i < win.length; i++)
            win[i] = ImageUtility.getImage("sprites/" + folder + "/win" + (i+1) + ".png");
        
        setImages(idle);
    }
    
    public void cycleSprite()
    {
        if (getCurrentSprite() == getImages().length - 1)
        {
            switch (state)
            {
                case DIGGING: setState(IDLE);
                break;
                case DYING: setState(DEAD);
                break;
                case SPAWNING: setState(IDLE);
                break;
                case STUCK: setState(CLIMBING_OUT);
                break;
            }
        }
        super.cycleSprite();
        if (state == CLIMBING_IDLE || state == SHUFFLING_IDLE)
            zeroCurrentSprite();
    }
    
    public void move()
    {
        switch (state)
        {
            case FALLING:
            setY(getY() + 4);
            break;
            case CLIMBING_UP:
            setY(getY() - speed);
            break;
            case CLIMBING_DOWN:
            setY(getY() + speed);
            break;
            case WALKING:
            case SHUFFLING:
            setX(getX() + speed * getDirection());
            break;
            case CLIMBING_OUT:
            setY(getY() - 4);
            break;
        }
    }
    
    public void setState(int state)
    {
        if (this.state != state)
        {
            switch (state)
            {
                case IDLE: setImages(idle);
                break;
                case WALKING: setImages(walk);
                break;
                case FALLING: setImages(fall);
                break;
                case DIGGING: setImages(dig);
                break;
                case CLIMBING_UP:
                case CLIMBING_DOWN:
                case CLIMBING_IDLE: setImages(climb);
                break;
                case SHUFFLING:
                case SHUFFLING_IDLE: setImages(shuffle);
                break;
                case DYING: setImages(die);
                break;
                case SPAWNING: setImages(spawn);
                break;
                case STUCK: setImages(stuck);
                break;
                case CLIMBING_OUT: setImages(climb);
                break;
                case WINNING: setImages(win);
                break;
            }
            zeroCurrentSprite();
            this.state = state;
        }
    }
    
    public int getState()
    {
        return state;
    }
    
    public void setSpawnLocation(int spawnX, int spawnY)
    {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }
    
    public int getSpawnX()
    {
        return spawnX;
    }
    
    public int getSpawnY()
    {
        return spawnY;
    }
    
    public void setSpeed(int speed)
    {
        this.speed = speed;
    }
    
    public int getSpeed()
    {
        return speed;
    }
    
    public int getRoundedX()
    {
        int x = (getX() % getWidth());
        if (x >= getWidth() / 2)
            x = getX() + getWidth() - x;
        else
            x = getX() - x;
        return x;
    }
    
    public int getRoundedY()
    {
        int y = (getY() % getHeight());
        if (y >= getHeight() / 2)
            y = getY() + getHeight() - y;
        else
            y = getY() - y;
        return y;
    }
    
    public boolean isOn(Block block)
    {
        if (getY() + getHeight() >= block.getY() &&
            getY() + getHeight() <= block.getY() + 10 &&
            getX() < block.getX() + block.getWidth() - 3 &&
            getX() + getWidth() > block.getX() + 3)
        {
            if (state != CLIMBING_OUT)
                setY(block.getY() - getWidth());
            return true;
        }
        return false;
    }
    
    public boolean isToLeftOf(Block block)
    {
        if (getX() + getWidth() >= block.getX() &&
            getX() + getWidth() <= block.getX() + 5 &&
            getY() + 1 <= block.getY() + block.getHeight() &&
            getY() + getHeight() - 1 >= block.getY())
        {
            setX(block.getX() - getWidth());
            return true;
        }
        return false;
    }
    
    public boolean isToRightOf(Block block)
    {
        if (getX() <= block.getX() + block.getWidth() &&
            getX() >= block.getX() + block.getWidth() - 5 &&
            getY() + 1 <= block.getY() + block.getHeight() &&
            getY() + getHeight() - 1 >= block.getY())
        {
            setX(block.getX() + block.getWidth());
            return true;
        }
        return false;
    }
    
    public boolean isUnder(Block block)
    {
        if (getY() <= block.getY() + block.getHeight() &&
            getY() >= block.getY() + block.getHeight() - 10 &&
            getX() <= block.getX() + block.getWidth() - 1 &&
            getX() + getWidth() >= block.getX() + 1)
        {
            setY(block.getY() + block.getHeight());
            return true;
        }
        return false;
    }
    
    public boolean isInside(Block block)
    {
        return (getY() < block.getY() + 3 &&
                getY() > block.getY() - 3 &&
                getX() + (getWidth() / 2) > block.getX() &&
                getX() + (getWidth() / 2) < block.getX() + block.getWidth());
    }
    
    public boolean isOn(Ladder ladder)
    {
        if (getY() + getHeight() >= ladder.getY() + 1 &&
            getY() + (getHeight() * 2 / 3) <= ladder.getY() + ladder.getHeight() &&
            getX() + 2 <= ladder.getX() + ladder.getWidth() &&
            getX() + getWidth() - 2 >= ladder.getX())
        {
            if (state == CLIMBING_UP || state == CLIMBING_DOWN)
                setX(ladder.getX());
            return true;
        }
        return false;
    }
    
    public boolean isAbove(Ladder ladder)
    {
        if (getY() + getHeight() <= ladder.getY() &&
            getY() + getHeight() >= ladder.getY() - 4 &&
            getX() + 2 <= ladder.getX() + ladder.getWidth() &&
            getX() + getWidth() - 2 >= ladder.getX())
        {
            if (state == CLIMBING_UP || state == CLIMBING_DOWN)
                setX(ladder.getX());
            return true;
        }
        return false;
    }
    
    public boolean isOn(Gold gold)
    {
        return (getX() + ((getWidth()) / 2) >= gold.getX() + ((getWidth()) / 2) - 3 &&
                getX() + ((getWidth()) / 2) <= gold.getX() + ((getWidth()) / 2) + 3 &&
                getY() + getHeight() > gold.getY() && getY() < gold.getY() + gold.getHeight() - 2);
    }
    
    public boolean isOn(Rope rope)
    {
        if (getY() >= rope.getY() &&
            getY() <= rope.getY() + ((rope.getHeight()) / 2) &&
            getX() + ((getWidth()) / 2) >= rope.getX() &&
            getX() + ((getWidth()) / 2) <= rope.getX() + rope.getWidth())
        {
            setY(rope.getY());
            return true;
        }
        return false;
    }
    
    public boolean isOn(Character ninja)
    {
        if (getX() + (getWidth() / 2) > ninja.getX() &&
            getX() + (getWidth() / 2) < ninja.getX() + ninja.getWidth() &&
            getY() + getHeight() <= ninja.getY() + 2 &&
            getY() + getHeight() >= ninja.getY() - 2)
        {
            setY(ninja.getY() - getHeight());
            return true;
        }
        return false;
    }
    
    public boolean hitBy(Character ninja)
    {
        return (getX() + getWidth() > ninja.getX() + 2 &&
                getX() < ninja.getX() + ninja.getWidth() - 2 &&
                getY() + getHeight() > ninja.getY() + 4 &&
                getY() < ninja.getY() + ninja.getHeight() - 2);
    }
}