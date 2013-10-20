package com.gamesbykevin.burgertime.food;

import com.gamesbykevin.framework.base.Animation;

import com.gamesbykevin.burgertime.characters.Hero;
import com.gamesbykevin.burgertime.levelobject.LevelObject;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the foods current state
 * @author GOD
 */
public class Food extends LevelObject
{
    //each part of the food
    private List<Rectangle> parts;
    
    //flag to check if the player hit each part
    private List<Boolean> intersects;
    
    //each piece of food will be separate into 4 parts
    private static final int PARTS = 4;
    
    //when the food is dropping we need to record the row we can start checking for collision
    private double rowStart;
    
    public Food(final Type type)
    {
        super(type);
        
        final int width = (int)(super.getWidth() / PARTS);
        
        //create new list that will determine if the hero hit each part
        this.intersects = new ArrayList<>();
        
        //create empty list
        this.parts = new ArrayList<>();
        
        //continue to add until we reached our limit
        while(intersects.size() < PARTS)
        {
            parts.add(new Rectangle(intersects.size() * width, 0, width, (int)getHeight()));
            
            //by default nothing intersects
            intersects.add(false);
        }
    }
    
    public double getRowStart()
    {
        return this.rowStart;
    }
    
    public void setRowStart(final double rowStart)
    {
        this.rowStart = rowStart;
    }
    
    public static List<Type> getTypeFood()
    {
        List<Type> food = new ArrayList<>();
        
        //first add burger top
        food.add(Type.BurgerTop);
        
        for (Type type : Type.values())
        {
            //we add the burger top/bottom first and last so don't add it here
            if (type == Type.BurgerTop || type == Type.BurgerBottom)
                continue;
            
            //make sure this is type food, then add
            if (isType(type))
                food.add(type);
        }
        
        //finally add burger bottom
        food.add(Type.BurgerBottom);
        
        return food;
    }
    
    /**
     * Is this type food
     * @param type
     * @return boolean true if food, false otherwise
     */
    public static boolean isType(final Type type)
    {
        switch (type)
        {
            case BurgerTop:
            case BurgerBottom:
            case Meat:
            case Cheese:
            case Lettuce:
            case Tomato:
                return true;
            
            default:
                return false;
        }
    }
    
    /**
     * Forcibly set all parts intersecting so food will be dropped
     */
    public void setDrop()
    {
        for (int index = 0; index < intersects.size(); index++)
        {
            //set intersect to true
            intersects.set(index, true);
        }
    }
    
    /**
     * Checks if all parts of this food has intersected with the hero.
     * @return true if all parts have been intersected, false otherwise
     */
    public boolean hasDrop()
    {
        for (boolean intersect : intersects)
        {
            //if one element has not intersected we can't drop
            if (!intersect)
                return false;
        }
        
        return true;
    }
    
    /**
     * Set all parts as not intersecting
     */
    public void resetDrop()
    {
        for (int index = 0; index < intersects.size(); index++)
        {
            intersects.set(index, false);
        }
    }
    
    /**
     * Check if the hero has collision with part of food<br>
     * @param hero 
     * @return True if collision was detected, false otherwise
     */
    public boolean checkCollision(final Hero hero)
    {
        //if the rows are not equal no need to check for collision
        if ((int)super.getRow() != (int)hero.getRow())
            return false;
        
        boolean collision = false;
        
        final double startCol = super.getCol();
        final double eachLength = (1.0 / intersects.size());
        
        for (int index = 0; index < intersects.size(); index++)
        {
            //don't check for collision if we have already detected a collision
            if (intersects.get(index))
                continue;
            
            final double start = startCol + (index * eachLength);
            final double end = start + eachLength;
            
            final boolean result = (hero.getCol() >= start && hero.getCol() <= end);
            
            if (result)
                collision = true;
            
            intersects.set(index, result);
        }
        
        //return our findings
        return collision;
    }
    
    @Override
    protected void setupAnimations(final Type type) throws Exception
    {
        final Animation animation;
        
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
                
            default:
                throw new Exception("Type not setup here");
        }
        
        super.setDefaults(animation, type);
    }
    
    @Override
    public void draw(final Graphics graphics, final Image image)
    {
        for (int index = 0; index < parts.size(); index++)
        {
            Rectangle part = parts.get(index);
            
            final int dx1 = (int)(getX() + part.x);
            final int dy1 = (int)(getY() + part.y);
            
            final int dx2 = dx1 + part.width;
            final int dy2 = dy1 + part.height;
            
            Rectangle tmp = super.getSpriteSheet().getLocation();
            
            final int sx1 = tmp.x + part.x;
            final int sy1 = tmp.y + part.y;
            
            final int sx2 = sx1 + part.width;
            final int sy2 = sy1 + part.height;
            
            final int extra;
            
            //if the hero has intersected with the part draw it a little lower
            if (intersects.get(index))
            {
                extra = part.height;
            }
            else
            {
                extra = 0;
            }
            
            super.draw(graphics, image, dx1, dy1 + extra, dx2, dy2 + extra, sx1, sy1, sx2, sy2);
        }
    }
}