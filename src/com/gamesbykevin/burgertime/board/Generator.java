package com.gamesbykevin.burgertime.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.food.Food;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will be responsible for generating the levels
 * @author GOD
 */
public abstract class Generator extends Sprite implements Disposable
{
    //all of the objects on the board
    private List <LevelObject> objects;
    
    //the map of the board for the AI
    private List<Location> map;
    
    //list of foods on the board
    private List<Food> foods;

    //dimensions of the board
    public static final int COLUMNS = 17;
    public static final int ROWS = 15;
    
    //rows where the characters can move amungst
    protected static final int VALID_ROWS = 10;

    //the number of rows to reserve for food
    private static final int FOOD_COUNT = Food.getTypeFood().size();
    
    //list of valid rows used to generate random level
    private List<Integer> validRows;
    
    //dimensions of each cell on the board
    public static final int WIDTH = 24;
    public static final int HEIGHT = 20;
    
    public Generator(final Random random)
    {
        //set the boundaries
        super.setBounds(0, COLUMNS, 0, ROWS);
        
        //create empty list of level objects
        this.objects = new ArrayList<>();
        
        //create empty list of valid rows
        this.validRows = new ArrayList<>();
        
        //create empty list of food
        this.foods = new ArrayList<>();
        
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
                            obj = new LevelObject(LevelObject.Type.BurgerContainer);
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
                            obj = new LevelObject(LevelObject.Type.Ladder);
                            obj.setLocation((col * WIDTH) + (WIDTH / 2) - (obj.getWidth() / 2), (row * HEIGHT));
                            obj.setCol(col);
                            obj.setRow(row);
                            objects.add(obj);
                        }
                    }
                    
                    //add a plaform everywhere
                    obj = new LevelObject(LevelObject.Type.Platform);
                    obj.setLocation(col * WIDTH, (row * HEIGHT) + HEIGHT);
                    obj.setCol(col);
                    obj.setRow(row);
                    objects.add(obj);
                }
            }
        }
        
        //get all food types that will be placed on the board
        final List<LevelObject.Type> foodTypes = Food.getTypeFood();
        
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
                    Food food = new Food(foodTypes.get(index));
                    food.setLocation(((col - 2) * WIDTH) + (WIDTH / 2) - (food.getWidth() / 2), (row * HEIGHT) + HEIGHT - food.getHeight());
                    food.setCol(col - 2);
                    food.setRow(row);
                    foods.add(food);
                }
            }
        }
        
        //locations where we can't remove level objects (ladder/platform)
        final List<Cell> safe = new ArrayList<>();
        
        for (Food food : getFoods())
        {
            //food location is safe
            safe.add(new Cell(food.getCol(), food.getRow()));
            
            //also directly east and west are safe as well
            safe.add(new Cell(food.getCol() - 1, food.getRow()));
            safe.add(new Cell(food.getCol() + 1, food.getRow()));
        }
        
        
        /**
         * now that a full board has been created with food we need to find a way to remove parts of the level
         * 1. Can't remove any locations where food exists or directly east or west of those locations
         */
        //
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (!objects.isEmpty())
        {
            for (LevelObject object : objects)
            {
                if (object != null)
                    object.dispose();
                
                object = null;
            }
            
            objects.clear();
        }
        
        objects = null;
        
        if (!map.isEmpty())
        {
            for (Location location : map)
            {
                if (location != null)
                    location.dispose();
                
                location = null;
            }
            
            map.clear();
        }
        
        map = null;
    
        if (!foods.isEmpty())
        {
            for (Food food : foods)
            {
                if (food != null)
                    food.dispose();
                
                food = null;
            }
            
            foods.clear();
        }
        
        foods = null;
    
        validRows.clear();
        validRows = null;
    }
    
    protected List<LevelObject> getObjects()
    {
        return this.objects;
    }
    
    protected List<Food> getFoods()
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
    
    /**
     * Here we will generate the map of the level for the AI to calculate the shortest path
     * @return List of locations each with their specified walls
     */
    public List<Location> getMap()
    {
        if (this.map == null || this.map.isEmpty())
        {
            //create a new empty list
            this.map = new ArrayList<>();
   
            for (int row = 0; row < ROWS; row++)
            {
                for (int col = 0; col < COLUMNS; col++)
                {
                    Location current = new Location(col, row);
                    
                    //if there is a platform to the west, remove wall
                    if (getObject(LevelObject.Type.Platform, current.getCol() - 1, current.getRow()) != null)
                        current.remove(Location.Wall.West);
                    
                    //if there is a platform to the east, remove wall
                    if (getObject(LevelObject.Type.Platform, current.getCol() + 1, current.getRow()) != null)
                        current.remove(Location.Wall.East);
                    
                    //if there is a ladder in the current location, remove wall
                    if (getObject(LevelObject.Type.Ladder, current.getCol(), current.getRow()) != null)
                        current.remove(Location.Wall.North);
                    
                    //if there is a ladder to the south, remove wall
                    if (getObject(LevelObject.Type.Ladder, current.getCol(), current.getRow() + 1) != null)
                        current.remove(Location.Wall.South);
                    
                    //add location to map
                    map.add(current);
                }
            }
        }
        
        return this.map;
    }
    
    public LevelObject getObject(final LevelObject.Type type, final LevelObject object)
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
    public LevelObject getObject(final LevelObject.Type type, final double column, final double row)
    {
        //set the location we are checking as the current
        super.setCol(column);
        super.setRow(row);
        
        //if the location is out of bounds return null
        if (!super.hasBounds())
            return null;
        
        for (LevelObject object : getObjects())
        {
            if (object.getType() != type)
                continue;
            
            if ((int)object.getCol() == (int)column && (int)object.getRow() == (int)row)
                return object;
        }
        
        return null;
    }
}