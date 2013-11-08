package com.andy.dev.javaGameTest;

import java.awt.Image;

public class Ladder extends Sprite
{
    public Ladder(int x, int y)
    {
        setX(x);
        setY(y);
        Image[] image = new Image[4];
        for (int i = 0; i < image.length; i++)
            image[i] = ImageUtility.getImage("sprites/Ladder/ladder" + (i+1) + ".png");
        setImages(image);
    }
}