package com.gamesbykevin.burgertime.levelobject;

import com.gamesbykevin.framework.base.*;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.shared.IElement;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Graphics;
import java.awt.Rectangle;

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
    
    protected Type getType()
    {
        return (Type)super.getSpriteSheet().getCurrent();
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update location based on velocity
        super.update();
        
        //update animation
        super.getSpriteSheet().update(engine.getMain().getTime());
    }
    
    /**
     * Setup the level object animations.
     */
    protected void setupAnimations(final Type type) throws Exception
    {
        Animation animation = null;
        
        switch(type)
        {
            case BurgerTop:
                animation = new Animation();
                animation.add(new Rectangle(85, 237, 32, 8), 0);
                break;

            case BurgerBottom:
                animation = new Animation();
                animation.add(new Rectangle(117, 237, 32, 8), 0);
                break;
                
            case Meat:
                animation = new Animation();
                animation.add(new Rectangle(149, 237, 32, 8), 0);
                break;
                
            case Lettuce:
                animation = new Animation();
                animation.add(new Rectangle(181, 237, 32, 8), 0);
                break;
                
            case Tomato:
                animation = new Animation();
                animation.add(new Rectangle(213, 237, 32, 8), 0);
                break;
                
            case Cheese:
                animation = new Animation();
                animation.add(new Rectangle(245, 237, 32, 8), 0);
                break;
                
            case BurgerContainer:
                animation = new Animation();
                animation.add(new Rectangle(245, 335, 38, 9), 0);
                break;
                
            case Ladder:
                animation = new Animation();
                animation.add(new Rectangle(194, 328, 20, 16), 0);
                break;
                
            case Platform:
                animation = new Animation();
                animation.add(new Rectangle(0, 342, 24, 6), 0);
                break;
                
            case Salt:
                animation = new Animation();
                animation.add(new Rectangle(0, 40, 20, 20), DELAY);
                animation.add(new Rectangle(20, 40, 20, 20), DELAY);
                animation.add(new Rectangle(40, 40, 20, 20), DELAY);
                animation.add(new Rectangle(60, 40, 20, 20), DELAY);
                break;
                
            default:
                throw new Exception("Type not setup here");
        }
        
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