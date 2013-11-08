package com.andy.dev.javaGameTest;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Image;

public abstract class GameElement
{
    public final static int LEFT = -1, RIGHT = 1;
    
    private final static int WIDTH = 32, HEIGHT = 32;
    private int x = 0, y = 0, direction = RIGHT;
    private Image currentImage;
    
    public void draw(Graphics gc)
    {
        try
        {
            if (getDirection() == RIGHT)
                gc.drawImage(currentImage, getX(), getY(), getWidth(), getHeight(), null);
            else
                gc.drawImage(currentImage, getX() + getWidth(), getY(), getX(), getY() + getHeight(),
                0, 0, currentImage.getWidth(null), currentImage.getHeight(null), null);
        }
        catch (NullPointerException ex) {}
    }
    
    public void setImage(Image image)
    {
        currentImage = image;
    }
    
    public void setX(int x)
    {
        this.x = x;
    }
    
    public int getX()
    {
        return x;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void setLocation(int x, int y)
    {
        setX(x);
        setY(y);
    }
    
    public void setLocation(Point location)
    {
        setX((int)location.getX());
        setY((int)location.getY());
    }
    
    public Point getLocation()
    {
        return new Point(x, y);
    }
    
    public int getWidth()
    {
        return WIDTH;
    }
    
    public int getHeight()
    {
        return HEIGHT;
    }
    
    public Rectangle getBounds()
    {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
    
    public void setDirection(int direction)
    {
        this.direction = direction;
    }
    
    public int getDirection()
    {
        return direction;
    }
}