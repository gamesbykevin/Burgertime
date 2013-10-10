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
    private static final int COLUMNS = 17;
    private static final int ROWS = 14;
    
    //rows where the characters can move amungst
    private static final int VALID_ROWS = 10;
    
    //the number of rows to reserve for food
    private static final int FOOD_COUNT = Food.getTypeFood().size();
    
    //list of valid rows used to generate random level
    final List<Integer> validRows;
    
    //dimensions of each cell on the board
    private static final int WIDTH = 24;
    private static final int HEIGHT = 20;
    
    public Board(final Engine engine, final Random random)
    {
        super();
        
        //set the boundaries
        super.setBounds(0, COLUMNS - 1, 0, ROWS - 1);
        
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
        
        //then randomly remove unused platforms and some of the ladders
    }
    
    public List<Food> getFoods()
    {
        return this.foods;
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
    
    public LevelObject getCollision(final Type type, final Rectangle area)
    {
        for (LevelObject object : objects)
        {
            if (object.getType() == type && object.getRectangle().intersects(area))
                return object;
        }
        
        return null;
    }
    
    @Override
    public void dispose()
    {
        
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        for (int index = 0; index < foods.size(); index++)
        {
            //get the current piece of food
            final Food food = foods.get(index);
            
            //if the food isn't falling check if gravity needs to be applied
            if (!food.hasVelocity())
            {
                //if the food qualifies to drop
                if (food.hasDrop())
                {
                    //set drop speed
                    food.setVelocityY(Speed.MEDIUM.getVelocity());
                }
                
                //skip to next object
                continue;
            }
            
            //update location based on current velocity set
            food.update();

            //check if hit platform
            final LevelObject platform = this.getCollision(Type.Platform, food.getMiddle());

            //if platform exists then we hit one 
            if (platform != null)
            {
                //we also want to make sure the platform is below the current food row and the columns are the same
                if (food.getCol() == platform.getCol() && platform.getRow() > food.getRow())
                {
                    //set the new row for food
                    food.setRow(platform.getRow());

                    //no longer drop so stop velocity
                    food.resetVelocity();

                    //reset food parts
                    food.resetDrop();

                    //check if any other pieces of food need to be dropped
                    checkDrop(food);
                    
                    //skip to the next object
                    continue;
                }
            }
            
            //now check if hit another piece of food in the burger container
            for (Food tmp : foods)
            {
                //don't check the same food piece, or a piece in a different column, or a piece that is not at the bottom
                if (food.getId() == tmp.getId() || food.getCol() != tmp.getCol() || tmp.getRow() <= VALID_ROWS)
                    continue;
                
                if (tmp.getRectangle().intersects(food.getRectangle()))
                {
                    //set the new row
                    food.setRow(tmp.getRow());
                    
                    //no longer drop piece
                    food.resetVelocity();
                    
                    //reset food parts
                    food.resetDrop();
                    
                    //exit loop
                    break;
                }
            }
            
            //check if hit burger container
            LevelObject container = this.getCollision(Type.BurgerContainer, food.getRectangle());

            //if container exists then we hit it
            if (container != null)
            {
                //set the new row for food
                food.setRow(container.getRow());

                //no longer drop so stop velocity
                food.resetVelocity();

                //reset food parts
                food.resetDrop();
                
                //skip to the next object
                continue;
            }
        }
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
    
    /**
     * Update the level object column, row based on their x,y coordinate
     * @param object 
     */
    public void updateLocation(final LevelObject object)
    {
        final int col = (int)(object.getX() / WIDTH);
        final int row = (int)(object.getY() / HEIGHT);
        
        object.setCol(col);
        object.setRow(row);
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
                    //set drop speed
                    food.setVelocityY(Speed.MEDIUM.getVelocity());
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