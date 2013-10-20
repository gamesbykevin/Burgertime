package com.gamesbykevin.burgertime.bonus;

import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.burgertime.characters.Hero;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;
import com.gamesbykevin.burgertime.resources.GameAudio;
import com.gamesbykevin.burgertime.resources.GameImage;
import com.gamesbykevin.burgertime.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will determine if a bonus is to be added.<br>
 * Also manage the collision and reward.
 * 
 * Bonus Items
 * BonusFrenchFries - Extra Life
 * BonusSalt        - Extra Pepper
 * BonusCoffee      - 1,000 points
 * BonusIceCream    - 500 points
 * 
 * @author GOD
 */
public final class Bonus implements IElement
{
    //our bonus object
    private LevelObject object;
    
    //timers to track how long the bonus is visible, and when to add a bonus
    private Timers timers;
    
    //the time the bonus is valid (15 seconds)
    private static final long DELAY_BONUS = Timers.toNanoSeconds(15000L);
    
    //the time to determine when a bonus is to be added
    private static final long DELAY_ADD   = Timers.toNanoSeconds(60000L);
    
    private enum Keys
    {
        DisplayBonus, AddBonus
    }
    
    //the bonus amounts for the different bonuses
    private static final int BONUS_COFFEE = 1000;
    private static final int BONUS_ICECREAM = 500;
    
    private static final double BONUS_COL = 8.5;
    private static final double BONUS_ROW = 3.5;
    
    public Bonus(final Random random, final long time)
    {
        //create our timer collection object
        this.timers = new Timers(time);
        
        //add our timer for how long the bonus is to be displayed for
        this.timers.add(Keys.DisplayBonus, DELAY_BONUS);
        
        //add our timer for how often a bonus is added, will be a random time between 1 - 2 minutes
        this.timers.add(Keys.AddBonus,     DELAY_ADD + (long)(random.nextFloat() * DELAY_ADD));
    }
    
    @Override
    public void dispose()
    {
        if (object != null)
            object.dispose();
        
        object = null;
        
        timers.dispose();
        timers = null;
    }
    
    /**
     * Create a random bonus object
     * @param random
     * @param image 
     */
    private void create(final Random random, final Image image)
    {
        //create new bonus
        object = new LevelObject(getRandom(random));
        
        //set the location
        object.setCol(BONUS_COL);
        object.setRow(BONUS_ROW);
        
        //store the image
        object.setImage(image);
        
        //set the x,y coordinate
        LevelObject.setLocation(object);
    }
    
    /**
     * Verify if the Type is a bonus item
     * @param type
     * @return True if a bonus item, false otherwise
     */
    public static boolean isType(final Type type)
    {
        switch (type)
        {
            //all of these are bonus items
            case BonusFrenchFries:
            case BonusSalt:
            case BonusCoffee:
            case BonusIceCream:
                return true;
                
            //anything else return false
            default:
                return false;
        }
    }
    
    /**
     * Get a random bonus type
     * @param random Object used to make random decisions
     */
    public static Type getRandom(final Random random)
    {
        //create empty list of bonus types
        List<Type> types = new ArrayList<>();
        
        for (Type type : Type.values())
        {
            if (isType(type))
                types.add(type);
        }
        
        //get random bonus item type
        return types.get(random.nextInt(types.size()));
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //our Hero object
        final Hero hero = engine.getManager().getHero();
        
        //update timer
        timers.update();
            
        //is there no bonus object
        if (object == null)
        {
            //make sure time has passed to add one
            if (timers.hasTimePassed(Keys.AddBonus))
            {
                //make sure the hero is not occupying the bonus spawn location
                if ((int)hero.getCol() != (int)BONUS_COL && (int)hero.getRow() != (int)BONUS_ROW)
                {
                    //reset timers and remove bonus (if exists)
                    reset();
                    
                    //create bonus object
                    create(engine.getManager().getRandom(), engine.getResources().getGameImage(GameImage.Keys.SpriteSheet));
                }
            }
        }
        else
        {
            //check if hero has collection bonus
            checkHeroCollision(hero);
            
            //if object doesn't exist, which will be time runs out or hero collects it
            if (object == null)
            {
                //play sound effect to collect the bonus
                engine.getResources().playGameAudio(GameAudio.Keys.CollectBonus, false);
            }
        }
    }
    
    /**
     * Check if our hero has collected the bonus
     * @param hero
     * @throws Exception 
     */
    private void checkHeroCollision(final Hero hero) throws Exception
    {
        //if timer is finished 
        if (timers.hasTimePassed(Keys.DisplayBonus))
        {
            //reset timers and remove bonus
            reset();
            
            //don't check for collision
            return;
        }
        
        //make sure the row is within range
        if (hero.getRow() >= object.getRow() - .3 && hero.getRow() <= object.getRow() + .3)
        {
            //make sure the column is within range
            if (hero.getCol() >= object.getCol() - .3 && hero.getCol() <= object.getCol() + .3)
            {
                //determine which bonus is rewarded
                switch (object.getType())
                {
                    //extra life
                    case BonusFrenchFries:
                        
                        //make sure count is not negative (unlimited) or it may screw up
                        if (hero.getLives() >= 0)
                        {
                            hero.addLife();
                        }
                        break;

                    //extra pepper
                    case BonusSalt:
                        
                        //make sure count is not negative (unlimited) or it may screw up
                        if (hero.getPepper() >= 0)
                        {
                            hero.addPepper();
                        }
                        break;

                    //1,000 bonus points
                    case BonusCoffee:
                        hero.addScore(BONUS_COFFEE);
                        break;

                    //500 bonus points
                    case BonusIceCream:
                        hero.addScore(BONUS_ICECREAM);
                        break;

                    default:
                        throw new Exception("Bonus Type not setup.");
                }
                
                //reset timers and remove bonus
                reset();
            }
        }
    }
    
    /**
     * Reset timers and remove bonus
     */
    public void reset()
    {
        //remove bonus
        object = null;

        //reset timers
        timers.reset();
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //make sure the bonus exists before we attempt to draw
        if (object != null)
        {
            object.render(graphics);
        }
    }
}