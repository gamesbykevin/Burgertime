package com.gamesbykevin.burgertime.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.characters.Character.Speed;
import com.gamesbykevin.burgertime.characters.Hero;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.food.Food;
import com.gamesbykevin.burgertime.resources.GameAudio;
import com.gamesbykevin.burgertime.resources.GameImage;
import com.gamesbykevin.burgertime.shared.IElement;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;
import com.gamesbykevin.burgertime.manager.Manager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The board contains the balls
 * @author GOD
 */
public class Board extends Sprite implements Disposable, IElement
{
    //all of the objects on the board
    private List <LevelObject> objects;
    
    //list of foods on the board
    private List<Food> foods;
    
    //image containing all board images
    private Image image;
    
    //dimensions of the board
    public static final int COLUMNS = 17;
    public static final int ROWS = 15;
    
    //rows where the characters can move amungst
    private static final int VALID_ROWS = 10;
    
    //the number of rows to reserve for food
    private static final int FOOD_COUNT = Food.getTypeFood().size();
    
    //list of valid rows used to generate random level
    final List<Integer> validRows;
    
    //dimensions of each cell on the board
    public static final int WIDTH = 24;
    public static final int HEIGHT = 20;
    
    public Board(final Engine engine, final Random random)
    {
        super();
        
        //set the boundaries
        super.setBounds(0, COLUMNS, 0, ROWS);
        
        //create empty list of level objects
        this.objects = new ArrayList<>();
        
        //create empty list of valid rows
        this.validRows = new ArrayList<>();
        
        //create empty list of food
        this.foods = new ArrayList<>();
        
        //store sprite sheet
        this.image = engine.getResources().getGameImage(GameImage.Keys.SpriteSheet);
        
        //create a full empty board
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
            {
                LevelObject obj;
                
                //bottom area, there will be empty space and the container
                if (row > VALID_ROWS)
                {
                    //make sure we are on the bottom
                    if (row == ROWS - 1)
                    {
                        if (col > 0 && col % 4 == 0)
                        {
                            obj = new LevelObject(Type.BurgerContainer);
                            obj.setLocation(((col - 2) * WIDTH) + (WIDTH / 2) - (obj.getWidth() / 2), (row * HEIGHT) + HEIGHT);
                            obj.setCol(col - 2);
                            obj.setRow(row);
                            objects.add(obj);
                        }
                    }
                }
                else
                {
                    //ladders don't get placed on the first row
                    if (row > 0)
                    {
                        //the ladder will be placed on every even column
                        if (col % 2 == 0)
                        {
                            obj = new LevelObject(Type.Ladder);
                            obj.setLocation((col * WIDTH) + (WIDTH / 2) - (obj.getWidth() / 2), (row * HEIGHT));
                            obj.setCol(col);
                            obj.setRow(row);
                            objects.add(obj);
                        }
                    }
                    
                    //add a plaform everywhere
                    obj = new LevelObject(Type.Platform);
                    obj.setLocation(col * WIDTH, (row * HEIGHT) + HEIGHT);
                    obj.setCol(col);
                    obj.setRow(row);
                    objects.add(obj);
                }
            }
        }
        
        //temporary food object
        Food food;
        
        //get all food types that will be placed on the board
        final List<Type> foodTypes = Food.getTypeFood();
        
        //now randomly place the food pieces
        for (int col = 0; col < COLUMNS; col++)
        {
            //we place food every 4 columns
            if (col > 0 && col % 4 == 0)
            {
                //create random list of rows to place all food
                resetValidRows(random);
                
                //now add food for every existing row left
                for (int index=0; index < validRows.size(); index++)
                {
                    final int row = validRows.get(index);
                    
                    //add food
                    food = new Food(foodTypes.get(index));
                    food.setLocation(((col - 2) * WIDTH) + (WIDTH / 2) - (food.getWidth() / 2), (row * HEIGHT) + HEIGHT - food.getHeight());
                    food.setCol(col - 2);
                    food.setRow(row);
                    foods.add(food);
                }
            }
        }
        
        /*
        //then randomly remove unused platforms and some of the ladders
        final int limit = objects.size() - 30;
        
        while(objects.size() > limit)
        {
            final int index = random.nextInt(objects.size());
            
            objects.remove(index);
        }
        */
    }
    
    private void resetValidRows(final Random random)
    {
        //clear list
        validRows.clear();
        
        //populate the list with valid rows
        for (int i=0; i <= VALID_ROWS; i++)
        {
            validRows.add(i);
        }
        
        //remove random rows until we reach the amount needed to reserve
        while(validRows.size() > FOOD_COUNT)
        {
            //get random index
            final int index = random.nextInt(validRows.size());

            //remove random row
            validRows.remove(index);
        }
    }
    
    @Override
    public void dispose()
    {
        
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        for (Food food : foods)
        {
            //if the food isn't falling check if gravity needs to be applied
            if (!food.hasVelocity())
            {
                //if the food qualifies to drop
                if (food.hasDrop())
                {
                    //set drop speed
                    food.setVelocityY(Speed.MEDIUM.getVelocity());
                    
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
        for (Food tmp : foods)
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
    
    public LevelObject getObject(final Type type, final LevelObject object)
    {
        return getObject(type, object.getCol(), object.getRow());
    }
    
    /**
     * Get the LevelObject of the specified type at the specified location.
     * If the column, row is out of bounds null will be returned.
     * @param type The type of object we are looking for
     * @param column - Self explanatory
     * @param row - Self explanatory
     * @return LevelObject if the specified Type was not found at the specified location null is returned
     */
    public LevelObject getObject(final Type type, final double column, final double row)
    {
        //set the location we are checking as the current
        super.setCol(column);
        super.setRow(row);
        
        //if the location is out of bounds return null
        if (!super.hasBounds())
            return null;
        
        for (LevelObject object : objects)
        {
            if (object.getType() != type)
                continue;
            
            if ((int)object.getCol() == (int)column && (int)object.getRow() == (int)row)
                return object;
        }
        
        return null;
    }
    
    public boolean hasCompleted()
    {
        for (Food food : foods)
        {
            //if the food row is not at the bottom yet
            if (food.getRow() <= VALID_ROWS)
                return false;
        }
        return true;
    }
    
    private void checkDrop(final Food tmp)
    {
        for (Food food : foods)
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
        for (LevelObject object : objects)
        {
            if (object.getType() == Type.Ladder)
                object.draw(graphics, image);
        }
        
        //then draw platforms
        for (LevelObject object : objects)
        {
            if (object.getType() == Type.Platform)
                object.draw(graphics, image);
        }
        
        //draw the rest
        for (LevelObject object : objects)
        {
            if (object.getType() == Type.Platform || object.getType() == Type.Ladder)
                continue;
            
            object.draw(graphics, image);
        }
        
        //draw all the food pieces
        for (Food food : foods)
        {
            food.render(graphics, image);
        }
    }
}