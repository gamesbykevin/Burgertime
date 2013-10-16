package com.gamesbykevin.burgertime.board;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.characters.Character.Speed;
import com.gamesbykevin.burgertime.characters.Character.State;
import com.gamesbykevin.burgertime.characters.Enemy;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.food.Food;
import com.gamesbykevin.burgertime.resources.GameImage;
import com.gamesbykevin.burgertime.shared.IElement;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The board contains the balls
 * @author GOD
 */
public final class Board extends Generator implements Disposable, IElement
{
    //image containing all board images
    private Image image;
    
    public Board(final Engine engine, final Random random)
    {
        super(engine, random);
        
        //store sprite sheet
        this.image = engine.getResources().getGameImage(GameImage.Keys.SpriteSheet);
    }
    
    /**
     * This method will pick a random spawn point on either the west or east side
     */
    public void setRandomSpawn(final Random random)
    {
        final List<Double> rows = new ArrayList<>();
        
        final double column;
        
        //get a random true/false result
        if (random.nextBoolean())
        {
            //here we will do west
            column = 0.5;
        }
        else
        {
            //here we will do east
            column = COLUMNS - 0.5;
        }
        
        for (double row=0; row < ROWS; row++)
        {
            //if platform exists add this as a possibility
            if (getObject(Type.Platform, (int)column, row) != null)
                rows.add(row);
        }
        
        super.setCol(column);
        
        final int index = random.nextInt(rows.size());
        super.setRow(rows.get(index) + 0.5);
    }
    
    
    @Override
    public void dispose()
    {
        
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        for (Food food : getFoods())
        {
            //if the food isn't falling check if gravity needs to be applied
            if (!food.hasVelocity())
            {
                //if the food qualifies to drop
                if (food.hasDrop())
                {
                    //set drop speed
                    food.setVelocityY(Speed.FASTER.getVelocity());
                    
                    //set the start row when dropping starts
                    food.setRowStart(food.getRow() + 1);
                }
                else
                {
                    //make sure the hero is moving before we check collision
                    if (engine.getManager().getHero().hasVelocity())
                    {
                        //if no velocity and no drop check for hero collision
                        food.checkCollision(engine.getManager().getHero());
                    }
                }
                
                //skip to next object
                continue;
            }
            
            //update column, row based on current velocity set
            food.update();
            
            //check if the moving food collides with any of the enemies
            engine.getManager().getEnemies().checkFoodCollision(food);
            
            //now we need to update y based on the row
            food.setY(food.getRow() * (double)HEIGHT);
            
            //check if hit platform
            final LevelObject platform = this.getObject(Type.Platform, food);
            
            //if platform exists then we hit one
            if (platform != null && food.getRow() > food.getRowStart())
            {
                //set the new row for food
                food.setRow(platform.getRow());

                //place food appropriately
                placeFood(food, platform);

                //check if any other pieces of food need to be dropped
                checkDrop(food);

                //skip to the next object
                continue;
            }
            
            //check if the food falls into the buger container
            checkBurgerContainer(food);
        }
    }
    
    /**
     * Check if the piece of food has reached its destination.
     * It will be placed directly above burger container.
     * If other food exists it will stack up
     * 
     * @param food 
     */
    private void checkBurgerContainer(final Food food)
    {
        //if the food is still in the playing area no need to check if it hit the burger container
        if (food.getRow() <= VALID_ROWS)
            return;
        
        //now check if hit another piece of food in the burger container
        for (Food tmp : super.getFoods())
        {
            //don't check the same food piece, and don't check if the food is not at the bottom
            if (food.getId() == tmp.getId() || tmp.getRow() <= VALID_ROWS)
                continue;

            //food must also be in the same column
            if ((int)food.getCol() != (int)tmp.getCol())
                continue;

            //if the food row is greater than another food row it will be stacked
            if (food.getRow() >= tmp.getRow())
            {
                //set the new row
                food.setRow(tmp.getRow() - .5);

                //place food appropriately
                placeFood(food, tmp);

                //we found a destination no need to check anything else
                return;
            }
        }

        //check if hit burger container
        LevelObject container = this.getObject(Type.BurgerContainer, food);

        //if container exists then we hit it
        if (container != null)
        {
            //place food appropriately
            placeFood(food, container);
            
            //no need to check anything else
            return;
        }
    }
    
    /**
     * This method will place the food appropriately above the given LevelObject.
     * We will also stop velocity and reset the food drop
     * 
     * @param food Our food object
     * @param object Object food has come in contact with
     */
    private void placeFood(final Food food, final LevelObject object)
    {
        //place food right above container
        food.setY(object.getY() - food.getHeight());

        //no longer drop so stop velocity
        food.resetVelocity();

        //reset food parts
        food.resetDrop();
    }
    
    public boolean hasCompleted()
    {
        for (Food food : getFoods())
        {
            //if the food row is not at the bottom yet
            if (food.getRow() <= VALID_ROWS)
                return false;
        }
        return true;
    }
    
    private void checkDrop(final Food tmp)
    {
        for (Food food : getFoods())
        {
            //make sure we aren't comparing the same object
            if (food.getId() == tmp.getId())
                continue;
            
            //if the pieces of food have the same column and row
            if (food.getCol() == tmp.getCol() && food.getRow() == tmp.getRow())
            {
                //if we are still in range of the game board apply gravity
                if (tmp.getRow() <= VALID_ROWS)
                {
                    //set food to be dropped
                    food.setDrop();
                }
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw ladders first
        for (LevelObject object : getObjects())
        {
            if (object.getType() == Type.Ladder)
                object.draw(graphics, image);
        }
        
        //then draw platforms
        for (LevelObject object : getObjects())
        {
            if (object.getType() == Type.Platform)
                object.draw(graphics, image);
        }
        
        //draw the rest
        for (LevelObject object : getObjects())
        {
            if (object.getType() == Type.Platform || object.getType() == Type.Ladder)
                continue;
            
            object.draw(graphics, image);
        }
        
        //draw all the food pieces
        for (Food food : getFoods())
        {
            food.render(graphics, image);
        }
    }
}