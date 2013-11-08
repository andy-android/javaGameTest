package com.andy.dev.javaGameTest;

import java.awt.Image;

public class Sprite extends GameElement
{
    private Image[] image;
    private int currentSprite = 0;
    
    public void setImages(Image[] image)
    {
        this.image = image;
    }
    
    public Image[] getImages()
    {
        return image;
    }
    
    public void cycleSprite()
    {
        currentSprite++;
        if (currentSprite >= image.length)
            currentSprite = 0;
        setImage(image[currentSprite]);
    }
    
    public int getCurrentSprite()
    {
        return currentSprite;
    }
    
    public void zeroCurrentSprite()
    {
        currentSprite = 0;
    }
}