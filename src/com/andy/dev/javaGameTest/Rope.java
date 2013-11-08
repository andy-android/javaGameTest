package com.andy.dev.javaGameTest;

public class Rope extends GameElement
{
    public Rope(int x, int y)
    {
        setImage(ImageUtility.getImage("sprites/Rope/rope1.png"));
        setX(x);
        setY(y);
    }
}