package com.andy.dev.javaGameTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LevelLoader
{
    /*
     * KEY:
     * 
     * 0 = blank
     * e = ladder that appears at end of level
     * 
     * r = rope
     * n = ninja
     * d = digger
     * b = regular block
     * s = solid block
     * t = trap block
     * l = ladder
     * g = gold
     * 
     */
    
    public static void loadLevel(int level, Character digger, ArrayList<Block> blockList, ArrayList<Ladder> ladderList, ArrayList<Ladder> exitLadderList,
                                 ArrayList<Gold> goldList, ArrayList<Rope> ropeList, ArrayList<Character> ninjaList)
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader("levels/level" + level + ".lvl"));
            for (int i = 0; i < 16; i++)
            {
                char[] line = reader.readLine().toCharArray();
                if (line.length < 28)
                    for (int k = 0; k < 28 - line.length; k++)
                        line[28 - k] = ' ';
                
                for (int k = 0; k < 28; k++)
                {
                    if (line[k] == '6')
                        exitLadderList.add(new Ladder(32 * k, 32 * i));
                    else if (line[k] == '4')
                        ropeList.add(new Rope(32 * k, 32 * i));
                    else if (line[k] == '8')
                        ninjaList.add(new Character(Character.NINJA, 32 * k, 32 * i));
                    else if (line[k] == '9')
                    {
                        digger.setLocation(32 * k, 32 * i);
                        digger.setSpawnLocation(32 * k, 32 * i);
                    }
                    else if (line[k] == '1')
                        blockList.add(new Block(Block.REGULAR_TYPE, 32 * k, 32 * i));
                    else if (line[k] == '2')
                        blockList.add(new Block(Block.SOLID_TYPE, 32 * k, 32 * i));
                    else if (line[k] == '5')
                        blockList.add(new Block(Block.TRAP_TYPE, 32 * k, 32 * i));
                    else if (line[k] == '3')
                        ladderList.add(new Ladder(32 * k, 32 * i));
                    else if (line[k] == '7')
                        goldList.add(new Gold(32 * k, 32 * i));
                }
            }
            
            for (int i = 0; i < 16; i++)
                blockList.add(new Block(Block.SOLID_TYPE, -32, 32 * i));
            for (int i = 0; i < 28; i++)
                blockList.add(new Block(Block.SOLID_TYPE, 32 * i, 512));
            for (int i = 0; i < 16; i++)
                blockList.add(new Block(Block.SOLID_TYPE, 28 * 32, 32 * i));
        }
        catch (IOException ex) {}
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ex) {}
            }
        }
    }
}