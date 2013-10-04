package com.gamesbykevin.burgertime.board;

import com.gamesbykevin.burgertime.resources.GameAudio;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.shared.IElement;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The board contains the balls
 * @author GOD
 */
public class Board extends Sprite implements Disposable, IElement
{
    //seed used to generate random numbers
    private final long seed = System.nanoTime();
    
    //random number generator object
    private Random random;
    
    //each puzzle is 13 columns/rows across
    private static final int DIMENSIONS = 13;
    
    public Board()
    {
        //our random number generator
        random = new Random(seed);
    }
    
    @Override
    public void dispose()
    {
        
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        
    }
}