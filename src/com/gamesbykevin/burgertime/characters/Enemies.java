package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.burgertime.board.Board;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.food.Food;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.resources.GameImage;
import com.gamesbykevin.burgertime.shared.IElement;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.*;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will manage the collection of enemies
 * @author GOD
 */
public final class Enemies implements Disposable, IElement
{
    //list of enemies
    private List<Enemy> enemies;
    
    //enemy limit, default is 1
    private int limit = 6;
    
    //wait 3 seconds to add each enemy
    private final long DELAY_ADD_ENEMY = TimerCollection.toNanoSeconds(3000L);
    
    //our timer to check
    private final Timer timer;
    
    public Enemies()
    {
        //create empty list for the enemies
        this.enemies = new ArrayList<>();
        
        //create our timer
        this.timer = new Timer(DELAY_ADD_ENEMY);
    }
    
    @Override
    public void dispose()
    {
        if (!enemies.isEmpty())
        {
            for (Enemy enemy : enemies)
            {
                if (enemy != null)
                {
                    enemy.dispose();
                }
                
                enemy = null;
            }
            
            enemies.clear();
        }
        
        enemies = null;
    }
    
    /**
     * Remove all enemies
     */
    public void reset()
    {
        this.enemies.clear();
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if the hero is dead don't do anything
        if (engine.getManager().getHero().isDead())
            return;
        
        //if the number of enemies is less than our limit
        if (enemies.size() < limit)
        {
            //update our timer
            timer.update(engine.getMain().getTime());
            
            //if time has passed
            if (timer.hasTimePassed())
            {
                //reset timer
                timer.reset();
                
                //our random generator object
                final Random random = engine.getManager().getRandom();

                //our game board object
                final Board board = engine.getManager().getBoard();

                //create random enemy
                Enemy enemy = new Enemy(Enemy.getRandom(random));

                //pick random location
                board.setRandomSpawn(random);
                enemy.setCol(board.getCol());
                enemy.setRow(board.getRow());

                //set the image
                enemy.setImage(engine.getResources().getGameImage(GameImage.Keys.SpriteSheet));

                //add enemy to the list
                enemies.add(enemy);
            }
        }
        
        for (int index = 0; index < enemies.size(); index++)
        {
            Enemy enemy = enemies.get(index);
            
            enemy.update(engine);
            
            if (enemy.isDead() && enemy.getSpriteSheet().hasFinished())
            {
                enemies.remove(index);
                index--;
            }
        }
    }
    
    /**
     * Check to see if the projectile has hit any of the enemies.<br>
     * If so then the enemy(s) will be set to stunned
     * @param projectile 
     */
    public void checkProjectileCollision(final LevelObject projectile)
    {
        //check every enemy for collision
        for (Enemy enemy : enemies)
        {
            //don't check if already dead or stunned
            if (enemy.isDead() || enemy.isStunned())
                continue;

            //make sure we are in the same row when checking horizontal collision
            if ((int)enemy.getRow() == (int)projectile.getRow())
            {
                //make sure the column is within range
                if (projectile.getCol() >= enemy.getCol() - .5 && projectile.getCol() <= enemy.getCol() + .5)
                {
                    //the enemy will no longer move
                    enemy.resetVelocity();

                    //set the animation to stunned
                    enemy.setState(Character.State.Stunned);
                    
                    //check the next enemy
                    continue;
                }
            }
            
            //make sure we are in the same column when checking vertical collision
            if ((int)enemy.getCol() == (int)projectile.getCol())
            {
                //make sure the row is within range
                if (projectile.getRow() >= enemy.getRow() - .5 && projectile.getRow() <= enemy.getRow() + .5)
                {
                    //the enemy will no longer move
                    enemy.resetVelocity();

                    //set the animation to stunned
                    enemy.setState(Character.State.Stunned);
                    
                    //check the next enemy
                    continue;
                }
            }
        }
    }
    
    /**
     * Check the falling food piece for collision with any of the enemies
     * @param food 
     */
    public void checkFoodCollision(final Food food)
    {
        //make sure food is falling before we check collision
        if (!food.hasVelocity())
            return;
        
        //check the list of enemies for food collision
        for (Enemy enemy : enemies)
        {
            //don't check collision if already dead
            if (enemy.isDead())
                continue;

            //make sure both are in the same row
            if ((int)food.getRow() == (int)enemy.getRow())
            {
                //make sure the columns are within range
                if (enemy.getCol() >= food.getCol() - .25 && enemy.getCol() <= food.getCol() + 1.25)
                {
                    enemy.setState(Character.State.Die);
                    enemy.resetVelocity();
                }
            }
        }
    }
    
    /**
     * Here we will determine if the enemy has killed the hero
     * @param hero
     */
    public void checkHeroCollision(final Hero hero)
    {
        //check the list of enemies for collision
        for (Enemy enemy : enemies)
        {
            //don't check collision if already dead or stunned
            if (enemy.isDead() || enemy.isStunned())
                continue;

            //make sure both are in the same row
            if ((int)hero.getRow() == (int)enemy.getRow())
            {
                //make sure the columns are within range
                if (enemy.getCol() >= hero.getCol() - .25 && enemy.getCol() <= hero.getCol() + .25)
                {
                    //don't pause animation
                    hero.getSpriteSheet().setPause(false);
                    
                    //set the death animation
                    hero.setState(Character.State.Die);
                    
                    //can't move
                    hero.resetVelocity();
                }
            }
        }
    }
    
    public void setLimit(final int limit)
    {
        this.limit = limit;
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        for (Enemy enemy : enemies)
        {
            enemy.render(graphics);
        }
    }
}