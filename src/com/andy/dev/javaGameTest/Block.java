package com.andy.dev.javaGameTest;

import java.awt.Image;

public class Block extends Sprite
{
    public final static int REGULAR_TYPE = 1, SOLID_TYPE = 2, TRAP_TYPE = 3;
    public final static int VISIBLE = 1, CRUMBLING = 2, INVISIBLE = 3, FORMING = 4;
    
    private Image[] visible, crumbling, invisible, forming;
    private int state = VISIBLE, type = REGULAR_TYPE;
    
    public Block(int type, int x, int y)
    {
        this.type = type;
        setX(x);
        setY(y);
        if (type == REGULAR_TYPE)
        {
            visible = new Image[1];
            for (int i = 0; i < visible.length; i++)
                visible[i] = ImageUtility.getImage("sprites/Block/visible1.png");
            crumbling = new Image[11];
            for (int i = 0; i < crumbling.length; i++)
                crumbling[i] = ImageUtility.getImage("sprites/Block/crumbling" + (i+1) + ".png");
            invisible = new Image[30];
            for (int i = 0; i < invisible.length; i++)
                invisible[i] = ImageUtility.getImage("sprites/Block/invisible1.png");
            forming = new Image[3];
            for (int i = 0; i < forming.length; i++)
                forming[i] = ImageUtility.getImage("sprites/Block/forming" + (i+1) + ".png");
            
            setImages(visible);
        }
        else if (type == SOLID_TYPE)
            setImage(ImageUtility.getImage("sprites/Block/SOLID_TYPE.png"));
        else if (type == TRAP_TYPE)
            setImage(ImageUtility.getImage("sprites/Block/visible1.png"));
    }
    
    public void cycleSprite()
    {
        if (getCurrentSprite() == getImages().length - 1)
        {
            switch (state)
            {
                case CRUMBLING:
                setState(INVISIBLE);
                setImages(invisible);
                break;
                case INVISIBLE:
                setState(FORMING);
                setImages(forming);
                break;
                case FORMING:
                setState(VISIBLE);
                setImages(visible);
                break;
            }
        }
        super.cycleSprite();
    }
    
    public void setState(int state)
    {
        if (this.state != state)
        {
            this.state = state;
            switch (state)
            {
                case VISIBLE: setImages(visible);
                break;
                case CRUMBLING: setImages(crumbling);
                break;
                case INVISIBLE: setImages(invisible);
                break;
                case FORMING: setImages(forming);
                break;
            }
            zeroCurrentSprite();
        }
    }
    
    public int getState()
    {
        return state;
    }
    
    public int getType()
    {
        return type;
    }
}