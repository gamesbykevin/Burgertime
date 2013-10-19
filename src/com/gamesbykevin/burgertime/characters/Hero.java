package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.levelobject.LevelObject;

import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Here is where we will manage the keyboard input among other things for our hero
 * @author GOD
 */
public final class Hero extends Character implements ICharacter
{
    //the salt the hero can dump on the enemies
    private List<LevelObject> projectiles;
    
    //the number of lives
    private int lives = -1;
    
    //the number of pepper throws
    private int pepper = -1;
    
    //sit there for 3 seconds after death
    private static final long DELAY_DEATH = TimerCollection.toNanoSeconds(3000L);
    
    //our timer object
    private Timer timer;
    
    public Hero()
    {
        super(Type.Hero);
        
        //create our list for the projectiles
        this.projectiles = new ArrayList<>();
        
        //create our timer
        this.timer = new Timer(DELAY_DEATH);
        
        //set our start location
        reset();
    }
    
    /**
     * Reset the location
     */
    public void reset()
    {
        //reset start point
        super.setCol(8.5);
        super.setRow(10.5);
        
        //reset animation
        setState(State.MoveSouth);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        for (LevelObject projectile : projectiles)
        {
            if (projectile != null)
                projectile.dispose();
            
            projectile = null;
        }
        
        projectiles.clear();
        projectiles = null;
    }
    
    public void addPepper()
    {
        this.pepper++;
    }
    
    public void removePepper()
    {
        this.pepper--;
    }
    
    public void addLife()
    {
        this.lives++;
    }
    
    public void removeLife()
    {
        this.lives--;
    }
    
    public boolean hasPepper()
    {
        return (pepper != 0);
    }
    
    public boolean hasLives()
    {
        return (lives != 0);
    }
    
    public void setLives(final int lives)
    {
        this.lives = lives;
    }
    
    public void setPepper(final int pepper)
    {
        this.pepper = pepper;
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        super.update(engine.getMain().getTime());
        
        //if we are able to move
        if (canMove())
        {
            //manage the keyboard input
            manageInput(engine.getKeyboard());
        }
        else
        {
            //if we can't move reset the velocity
            resetVelocity();
            
            //don't continue if we are dead
            if (isDead())
            {
                //update our timer
                timer.update(engine.getMain().getTime());
                
                if (timer.hasTimePassed())
                {
                    //lose a life
                    this.removeLife();
                    
                    //reset our location
                    this.reset();
                    
                    //reset the enemies as well
                    engine.getManager().getEnemies().reset();
                }
                
                return;
            }
        }
        
        //if moving, check for collision with the board
        if (hasVelocity())
        {
            checkCollision(engine.getManager().getBoard());
        }
        
        //if the projectile exists
        if (!projectiles.isEmpty())
        {
            //update all projectiles
            for (int i=0; i < projectiles.size(); i++)
            {
                //get the current projectile
                LevelObject projectile = projectiles.get(i);
                
                //update the location and animation
                projectile.update(engine);

                //if the animation has finished we will remove
                if (projectile.getSpriteSheet().hasFinished())
                {
                    projectiles.remove(i);
                    i--;
                }
                else
                {
                    //check if the projectile has hit any of the enemies
                    engine.getManager().getEnemies().checkProjectileCollision(projectile);
                }
            }
        }
        
        //check if any enemies have hit the hero
        engine.getManager().getEnemies().checkHeroCollision(this);
        
        //manage the current animation
        checkAnimation();
    }
    
    private void checkAnimation()
    {
        //if we are attacking and the animation has finished
        if (isAttacking() && getSpriteSheet().hasFinished())
        {
            switch(getState())
            {
                case AttackSouth:
                    setState(State.MoveSouth);
                    break;

                case AttackEast:
                    setState(State.MoveEast);
                    break;

                case AttackWest:
                    setState(State.MoveWest);
                    break;

                case AttackNorth:
                    setState(State.MoveNorth);
                    break;
            }
        }
    }
    
    private void manageInput(Keyboard keyboard) throws Exception
    {
        //want to move west
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            super.setVelocityX(-getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveWest);
            super.getSpriteSheet().setPause(false);
        }
        
        //want to move east
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            super.setVelocityX(getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveEast);
            super.getSpriteSheet().setPause(false);
        }
        
        //want to move north
        if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
        {
            super.setVelocityY(-getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveNorth);
            super.getSpriteSheet().setPause(false);
        }
        
        //want to move south
        if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
        {
            super.setVelocityY(getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveSouth);
            super.getSpriteSheet().setPause(false);
        }
        
        //if any of the keys were released stop animation and stop movement
        if (keyboard.isKeyReleased())
        {
            //stop movement
            super.resetVelocity();
            
            //reset all keyboard events
            keyboard.reset();
            
            //pause animation since no movement
            super.getSpriteSheet().setPause(true);
        }
        
        //we want to throw pepper
        if (keyboard.hasKeyPressed(KeyEvent.VK_SPACE) && projectiles.isEmpty())
        {
            if (hasPepper())
            {
                //remove pepper from our count
                removePepper();

                switch(getState())
                {
                    case MoveSouth:
                        super.setState(State.AttackSouth);
                        addProjectile(getX(), getY() + DIMENSION, super.getCol(), super.getRow() + .5);
                        break;

                    case MoveEast:
                        super.setState(State.AttackEast);
                        addProjectile(getX() + DIMENSION, getY(), super.getCol() + .5, super.getRow());
                        break;

                    case MoveWest:
                        super.setState(State.AttackWest);
                        addProjectile(getX() - DIMENSION, getY(), super.getCol() - .5, super.getRow());
                        break;

                    case MoveNorth:
                        super.setState(State.AttackNorth);
                        addProjectile(getX(), getY() - DIMENSION, super.getCol(), super.getRow() - .5);
                        break;
                }
            }
        }
    }
    
    /**
     * Add a projectile at the specified x,y coordinates and column, row
     * @param x 
     * @param y
     * @param col
     * @param row
     */
    private void addProjectile(final double x, final double y, final double col, final double row)
    {
        //don't pause hero animation
        super.getSpriteSheet().setPause(false);
        
        //create our projectile
        LevelObject projectile = new LevelObject(Type.Salt);
        
        //set the location
        projectile.setLocation(x, y);
                
        //set the column and row
        projectile.setCol(col);
        projectile.setRow(row);
        
        //set projectile dimensions
        projectile.setDimensions(DIMENSION, DIMENSION);
        
        //set the image
        projectile.setImage(super.getImage());

        //don't pause animation
        projectile.getSpriteSheet().setPause(false);
        
        //add to list
        projectiles.add(projectile);
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
        
        //draw the projectiles
        for (LevelObject projectile : projectiles)
        {
            projectile.draw(graphics);
        }
    }
}