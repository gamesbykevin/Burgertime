package com.gamesbykevin.burgertime.manager;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.burgertime.board.Board;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.menu.CustomMenu.LayerKey;
import com.gamesbykevin.burgertime.menu.CustomMenu.OptionKey;
import com.gamesbykevin.burgertime.shared.IElement;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.gamesbykevin.burgertime.resources.*;

import com.gamesbykevin.burgertime.characters.Hero;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements Disposable, IElement
{
    //seed used to generate random numbers
    private final long seed = System.nanoTime();
    
    //random number generator object
    public final Random random;
    
    private Hero hero;
    
    private Board board;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //the mode of game play
        //this.mode = Mode.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Mode)];
        
        //our random number generator
        this.random = new Random(seed);
        
        System.out.println("Seed: " + seed);
        
        this.hero = new Hero();
        this.hero.setImage(engine.getResources().getGameImage(GameImage.Keys.SpriteSheet));
        this.hero.setLocation(0, 1);
        
        this.board = new Board(engine, random);
    }
    
    public Board getBoard()
    {
        return this.board;
    }
    
    public Random getRandom()
    {
        return this.random;
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        
    }
    
    /**
     * Update all application elements
     * 
     * @param engine Our main game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        this.hero.update(engine);
        
        this.board.update(engine);
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        this.board.render(graphics);
        
        this.hero.render(graphics);
    }
}