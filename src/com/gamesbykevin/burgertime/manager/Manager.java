package com.gamesbykevin.burgertime.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.board.Board;
import com.gamesbykevin.burgertime.bonus.Bonus;
import com.gamesbykevin.burgertime.characters.*;
import com.gamesbykevin.burgertime.characters.Character.Speed;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.menu.CustomMenu.*;
import com.gamesbykevin.burgertime.resources.*;
import com.gamesbykevin.burgertime.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements Disposable, IElement
{
    //seed used to generate random numbers
    private final long seed = System.nanoTime();
    
    //random number generator object
    public final Random random = new Random(seed);
    
    //our Hero object
    private Hero hero;
    
    //our game board object
    private Board board;
    
    //object that manages all the enemies
    private Enemies enemies;
    
    //our object that manages the bonus items
    private Bonus bonus;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //get the menu object
        final Menu menu = engine.getMenu();
        
        //the speed of the characters
        final int speedIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Speed);
        
        //the number of lives
        final int livesIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.HeroLives);
        
        //the number of peppers
        final int pepperIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.HeroPepper);
        
        //do enemies respawn when killed
        final boolean respawn = (Toggle.values()[menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.EnemyRespawn)] == Toggle.On);
        
        //are we timed to solve the  maze
        final boolean timed = (Toggle.values()[menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.TimeLimit)] == Toggle.On);
        
        //the number of enemies that can be out at 1 time
        final int enemyLimit = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.EnemyLimit) + 1;
        
        //the image containing all the sprite images
        final Image image = engine.getResources().getGameImage(GameImage.Keys.SpriteSheet);
        
        //the time delay per update
        final long time = engine.getMain().getTime();
        
        //create new game board
        this.board = new Board(image);
        
        //generate new level
        this.board.reset(random);
        
        //the location for the hero H.U.D.
        final int x = 25;
        final int y = (Board.ROWS * Board.HEIGHT) + Board.HEIGHT;
        
        //create our hero object
        this.hero = new Hero(time, timed, x, y);
        this.hero.setImage(image);

        //set the boundaries so we know what is considered in bounds
        this.hero.setBounds(board);
        
        //set the speed of our hero, NOTE: Hero will always move faster than enemy
        this.hero.setSpeed(Speed.values()[speedIndex + 1]);
        
        //set the number of lives
        if (livesIndex > 0)
            this.hero.setLives(livesIndex);
        
        //set the number of peppers
        if (pepperIndex > 0)
            this.hero.setPepper(pepperIndex);
        
        //create new object to manage our enemies
        this.enemies = new Enemies();
        
        //set the number of enemies
        this.enemies.setLimit(enemyLimit);
        
        //do we respawn enemies after death
        this.enemies.setRespawn(respawn);
        
        //set the speed of all the enemies
        this.enemies.setSpeed(Speed.values()[speedIndex]);
        
        //create new bonus object
        this.bonus = new Bonus(random, time);
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
        hero.dispose();
        hero = null;
        
        board.dispose();
        board = null;
        
        enemies.dispose();
        enemies = null;
        
        bonus.dispose();
        bonus = null;
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
        //if the hero has lives the game will continue
        if (hero.hasLives())
        {
            //has the board been solved
            final boolean solved = board.hasSolved();

            //update the board and its elements
            this.board.update(engine);

            //the board is now solved since the last update
            if (!solved && board.hasSolved())
            {
                //stop all sound
                engine.getResources().stopAllSound();

                //play solved music
                engine.getResources().playGameAudio(GameAudio.Keys.LevelFinish, false);
            }

            //we only update if the board is not solved
            if (!board.hasSolved())
            {
                //update the hero
                this.hero.update(engine);

                //update the enemies
                this.enemies.update(engine);

                //check bonus objects
                this.bonus.update(engine);
            }
            else
            {
                //if the time to wait after the board is solved passed
                if (board.hasFinished())
                {
                    setNext();
                    
                    //reset any events
                    engine.getKeyboard().reset();
                    engine.getMouse().reset();
                }
            }
        }
    }
    
    /**
     * Reset everything for the next level
     * @throws Exception 
     */
    private void setNext() throws Exception
    {
        //generate a new board
        board.reset(random);

        //remove all enemies
        enemies.reset();

        //reset the heroes location
        hero.resetLocation();

        //reset H.U.D. display
        hero.resetImage();

        //reset the heroes timer
        hero.resetTimer();
        
        //reset movement
        hero.resetVelocity();

        //reset any bonuses
        bonus.reset();
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        if (hero.hasLives())
        {
            //draw the board first
            this.board.render(graphics);

            //then draw the enemies
            this.enemies.render(graphics);

            //then draw the bonus object
            this.bonus.render(graphics);

            //then draw the hero
            this.hero.render(graphics);
        }
        else
        {
            //draw game over screen
            this.hero.renderGameOver(graphics);
        }
    }
}