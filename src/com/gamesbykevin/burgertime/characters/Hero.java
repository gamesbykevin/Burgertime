package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.burgertime.board.Board;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.food.Food;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import java.awt.Color;

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
    
    public Hero()
    {
        super(Type.Hero);
        
        this.projectiles = new ArrayList<>();
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        super.update(engine.getManager().getBoard(), engine.getMain().getTime());
        
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
        
        graphics.setColor(Color.WHITE);
        graphics.drawString("(" + getCol() + "," + getRow() + ")", 150, 400);
    }
}