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
 *
 * @author GOD
 */
public class Food extends LevelObject
{
    //list of cooridnates on image
    private List<Rectangle> parts;
    
    //flag to check if the player hit each part
    private List<Boolean> intersects;
    
    //each piece of food will be separate into 4 parts
    private static final int PARTS = 4;
    
    //middle area of food, since food is wide enough to cover many paltforms
    private Rectangle middle;
    
    //each food object is unique
    private long id = System.nanoTime();
    
    public Food(final Type type)
    {
        super(type);
        
        final int width = (int)(super.getWidth() / PARTS);
        
        //create new list consisting of parts of the entire image
        this.parts = new ArrayList<>();
        
        //create new list that will determine if the hero hit each part
        this.intersects = new ArrayList<>();
        
        //continue to add parts until we reached our expected limit
        while(parts.size() < PARTS)
        {
            //add the part to the list
            parts.add(new Rectangle(parts.size() * width, 0, width, (int)getHeight()));
            
            //by default we do not intersect the part
            intersects.add(false);
        }
    }
    
    public long getId()
    {
        return this.id;
    }
    
    public static List<Type> getTypeFood()
    {
        List<Type> food = new ArrayList<>();
        
        food.add(Type.BurgerTop);
        
        for (Type type : Type.values())
        {
            if (type == Type.BurgerTop || type == Type.BurgerBottom)
                continue;
            
            if (isType(type))
                food.add(type);
        }
        
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
    
    public Rectangle getMiddle()
    {
        if (this.middle == null)
            middle = new Rectangle();
        
        final int width = (int)(getWidth() * .25);

        this.middle.x = (int)(getX() + (getWidth() / 2) - (width / 2));
        this.middle.y = (int)(getY());
        this.middle.width = width;
        this.middle.height = (int)getHeight();
        
        return this.middle;
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
    
    public void checkCollision(final Hero hero)
    {
        Rectangle tmp = new Rectangle();
        
        for (int index = 0; index < parts.size(); index++)
        {
            //don't check for collision if we have already detected a collision
            if (intersects.get(index))
                continue;
            
            tmp.x = (int)(getX() + parts.get(index).x);
            tmp.y = (int)(getY() + parts.get(index).y);
            tmp.width = parts.get(index).width;
            tmp.height = parts.get(index).height;
            
            //if the pieces intersect set flag to true
            if (hero.getFeet().intersects(tmp))
                intersects.set(index, true);
        }

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
    
    public void render(final Graphics graphics, final Image image)
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