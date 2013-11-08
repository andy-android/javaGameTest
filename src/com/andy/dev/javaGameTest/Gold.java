package com.andy.dev.javaGameTest;

import java.awt.Image;

public class Gold extends Sprite
{
    public Gold(int x, int y)
    {
        setX(x);
        setY(y);
        Image[] image = new Image[4];
        for (int i = 0; i < image.length; i++)
            image[i] = ImageUtility.getImage("sprites/Gold/gold" + (i+1) + ".png");
        setImages(image);
    }
}