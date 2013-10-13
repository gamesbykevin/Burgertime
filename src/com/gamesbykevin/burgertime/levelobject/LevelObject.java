package com.gamesbykevin.burgertime.levelobject;

import com.gamesbykevin.framework.base.*;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.shared.IElement;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Graphics;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Any object on the screen is a level object
 * @author GOD
 */
public class LevelObject extends Sprite implements IElement
{
    public enum Type
    {
        Hero, 
        EnemyHotdog, EnemyEgg, EnemyPickle, EnemyCarrot, EnemyOnion,
        Ladder,
        Platform,
        BurgerContainer,
        BurgerTop, BurgerBottom,
        Meat,
        Cheese,
        Lettuce,
        Tomato,
        Salt,
    }
    
    private static final long DELAY = TimerCollection.toNanoSeconds(150L);
    
    public LevelObject(final Type type)
    {
        //we need to create the sprite sheet so we can add animations
        super.createSpriteSheet();
        
        try
        {
            setupAnimations(type);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Type getType()
    {
        return (Type)super.getSpriteSheet().getCurrent();
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update location based on velocity
        this.update();
        
        //update animation
        super.getSpriteSheet().update(engine.getMain().getTime());
    }
    
    /**
     * Update the character's column, row position based on the velocity
     */
    @Override
    public void update()
    {
        super.setCol(super.getCol() + super.getVelocityX());
        super.setRow(super.getRow() + super.getVelocityY());
    }
    
    /**
     * Setup the level object animations.
     */
    protected void setupAnimations(final Type type) throws Exception
    {
        final Animation animation = new Animation();
        
        switch(type)
        {
            case BurgerContainer:
                animation.add(new Rectangle(245, 335, 38, 9), 0);
                break;
                
            case Ladder:
                animation.add(new Rectangle(199, 309, 10, 19), 0);
                break;
                
            case Platform:
                animation.add(new Rectangle(96, 325, 24, 2), 0);
                break;
                
            case Salt:
                animation.add(new Rectangle(00, 40, 20, 20), DELAY);
                animation.add(new Rectangle(20, 40, 20, 20), DELAY);
                animation.add(new Rectangle(40, 40, 20, 20), DELAY);
                animation.add(new Rectangle(60, 40, 20, 20), DELAY);
                break;
                
            default:
                throw new Exception("Type not setup here");
        }
        
        setDefaults(animation, type);
    }
    
    protected void setDefaults(final Animation animation, final Type type)
    {
        //add the animation
        getSpriteSheet().add(animation, type);
        
        //set this animation as the current
        getSpriteSheet().setCurrent(type);
        
        //set the width and height accordingly
        setDimensions(getSpriteSheet().getLocation().width, getSpriteSheet().getLocation().height);
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
    }
}