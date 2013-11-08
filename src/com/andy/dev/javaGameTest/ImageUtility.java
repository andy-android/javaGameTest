package com.andy.dev.javaGameTest;

import java.awt.Image;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

public class ImageUtility
{
    public static Image getImage(String fileName)
    {
        Image image = (new ImageIcon(fileName)).getImage();
        return image;
    }
    
    public static int getCenteredText(String string, Font font)
    {
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false);
        return (int)((Frame.DIM.width / 2) - (font.getStringBounds(string, frc).getWidth() / 2));
    }
}