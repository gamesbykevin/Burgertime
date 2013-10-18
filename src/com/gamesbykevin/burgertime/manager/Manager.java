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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gamesbykevin.burgertime.resources.*;

import com.gamesbykevin.burgertime.characters.*;
import com.gamesbykevin.burgertime.levelobject.LevelObject;

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
    
    //our Hero object
    private Hero hero;
    
    //our game board object
    private Board board;
    
    //object that manages all the enemies
    private Enemies enemies;
    
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
        
        //create new game board
        this.board = new Board(engine, random);
        
        //create our hero object
        this.hero = new Hero();
        this.hero.setImage(engine.getResources().getGameImage(GameImage.Keys.SpriteSheet));
        this.hero.setCol(8.5);
        this.hero.setRow(10.5);

        //set the boundaries so we know what is considered in bounds
        this.hero.setBounds(board);
        
        //create new object to manage our enemies
        this.enemies = new Enemies();
    }
    
    public Enemies getEnemies()
    {
        return this.enemies;
    }
    
    public Board getBoard()
    {
        return this.board;
    }
    
    public Hero getHero()
    {
        return this.hero;
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
        //update the hero
        this.hero.update(engine);
        
        //update the board and its elements
        this.board.update(engine);
        
        //update the enemies
        this.enemies.update(engine);
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        //draw the board first
        this.board.render(graphics);
        
        //then draw the enemies
        this.enemies.render(graphics);
        
        //then draw the hero
        this.hero.render(graphics);
    }
}