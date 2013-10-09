package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.burgertime.board.Board;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.food.Food;
import com.gamesbykevin.burgertime.levelobject.LevelObject;

import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author GOD
 */
public final class Hero extends Character implements ICharacter
{
    //the salt the hero can dump on the enemies
    private List<LevelObject> projectiles;
    
    //the feet area of the player used for collision detection with the board
    private Rectangle feet;
    
    public Hero()
    {
        super(Type.Hero);
        
        this.projectiles = new ArrayList<>();
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if we are able to move check for input
        if (canMove())
        {
            manageInput(engine.getKeyboard());
        }
        else
        {
            //if we can't move reset the velocity
            super.resetVelocity();
        }
        
        //check for collision
        checkCollision(engine.getManager().getBoard());
        
        //update sprite location based on velocity
        super.update();
        
        //if the projectile exists
        if (!projectiles.isEmpty())
        {
            //update all projectiles
            for (int i=0; i < projectiles.size(); i++)
            {
                //update the location and animation
                projectiles.get(i).update(engine);

                //if the animation has finished we will remove
                if (projectiles.get(i).getSpriteSheet().hasFinished())
                {
                    projectiles.remove(i);
                    i--;
                }
            }
        }
        
        //manage the current animation
        checkAnimation();
        
        //update sprite sheet
        getSpriteSheet().update(engine.getMain().getTime());
    }
    
    private void checkCollision(final Board board)
    {
        //if we are not moving don't bother checking for board collision
        if (!hasVelocity())
            return;
        
        //get original location
        final Point original = super.getPoint();
        
        //update location temporary
        super.update();
        
        final int width  = (int)(getWidth() * .5);
        final int height = (int)(getSpeed() * 5);
        
        if (feet == null)
            feet = new Rectangle();
        
        //update the feet location
        this.feet.x = (int)(getX() + (getWidth() / 2) - (width / 2));
        this.feet.y = (int)(getY() + getHeight() - height);
        this.feet.width  = width;
        this.feet.height = (height * 2);
        
        final LevelObject ladder   = board.getCollision(Type.Ladder,   feet);
        final LevelObject platform = board.getCollision(Type.Platform, feet);
        
        if (ladder == null && platform == null)
        {
            super.resetVelocity();
            
            //reset location back
            super.setLocation(original);
        }
        else
        {
            if (super.hasVelocityX())
            {
                if (platform != null)
                {
                    //correct y cooridnate while walking on platform
                    super.setX(original.x);
                    super.setY(platform.getY() + platform.getHeight() - super.getHeight());
                }
                else
                {
                    super.resetVelocityX();
                    super.setX(original.x);
                }
            }
            
            if (super.hasVelocityY())
            {
                if (ladder != null)
                {
                    //also center position on ladder
                    super.setX(ladder.getX() + (ladder.getWidth() / 2) - (super.getWidth() / 2));
                    super.setY(original.y);
                }
                else
                {
                    super.resetVelocityY();
                    super.setY(original.y);
                }
            }
        }
        
        //check all food for collision
        for (Food food : board.getFoods())
        {
            //check every piece of food for collision
            food.checkCollision(this);
        }
    }
    
    public Rectangle getFeet()
    {
        return this.feet;
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
            switch(getState())
            {
                case MoveSouth:
                    super.setState(State.AttackSouth);
                    addProjectile(getX(), getY() + DIMENSION);
                    break;
                    
                case MoveEast:
                    super.setState(State.AttackEast);
                    addProjectile(getX() + DIMENSION, getY());
                    break;
                    
                case MoveWest:
                    super.setState(State.AttackWest);
                    addProjectile(getX() - DIMENSION, getY());
                    break;
                    
                case MoveNorth:
                    super.setState(State.AttackNorth);
                    addProjectile(getX(), getY() - DIMENSION);
                    break;
            }
        }
    }
    
    /**
     * Add a projectile at the specified x,y coordinates
     * @param x
     * @param y 
     */
    private void addProjectile(final double x, final double y)
    {
        //make sure we aren't pausing the animation
        super.getSpriteSheet().setPause(false);
        
        LevelObject projectile = new LevelObject(Type.Salt);
        
        //set the location
        projectile.setLocation(x, y);
                
        //set projectile dimensions
        projectile.setDimensions(DIMENSION, DIMENSION);
        
        //set the image
        projectile.setImage(super.getImage());
                
        //add to list
        projectiles.add(projectile);
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
        
        for (LevelObject projectile : projectiles)
        {
            projectile.draw(graphics);
        }
    }
}