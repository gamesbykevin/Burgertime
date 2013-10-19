package com.gamesbykevin.burgertime.board;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.food.Food;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;

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
    
    //dimensions of each cell on the board
    public static final int WIDTH = 24;
    public static final int HEIGHT = 20;
    
    public Generator(final Random random)
    {
        //set the boundaries
        super.setBounds(0, COLUMNS, 0, ROWS);
        
        //create empty list of level objects
        this.objects = new ArrayList<>();
        
        //create empty list of food
        this.foods = new ArrayList<>();
        
        try
        {
            //use random maze algorithm to generate
            Algorithm algorithm = Algorithm.values()[random.nextInt(Algorithm.values().length)];
            
            //create new maze object with the given number of columns and rows
            Labyrinth maze = new Labyrinth(COLUMNS, VALID_ROWS + 1, algorithm);
            
            //this is also the same start column/row for the hero
            maze.setStart(COLUMNS / 2, VALID_ROWS);
            
            //now generate our maze
            maze.generate();
            
            //get the cells from the generated maze
            List<Location> cells = maze.getLocations();
            
            for (Location cell : cells)
            {
                //if there is no wall to the west or east add platform
                if (!cell.hasWall(Wall.East) || !cell.hasWall(Wall.West))
                    addPlatform(cell.getCol(), cell.getRow());
                
                //if there is no wall to the north or south add ladder
                if (!cell.hasWall(Wall.South) || !cell.hasWall(Wall.North))
                {
                    //we won't put a ladder in the first row
                    if (cell.getRow() != 0)
                        addLadder(cell.getCol(), cell.getRow());
                }
            }
            
            //valid rows
            final List<Integer> valid = new ArrayList<>();
            
            //all of the types of food
            final List<Type> types = Food.getTypeFood();
            
            //add burger container on the very bottom
            for (int col = 0; col < COLUMNS; col++)
            {
                //all columns in the top row will have a platform
                addPlatform(col, 0);
                
                //all columns in the bottom row will have a platform
                addPlatform(col, VALID_ROWS);
                
                //place burger container every 4 columns skipping the first column
                if (col > 0 && col % 4 == 0)
                {
                    final int column = col - 2;
                    
                    //add burger container
                    addBurgerContainer(column, ROWS - 1);

                    //clear list
                    valid.clear();
                    
                    //locate valid rows to place food on
                    for (int row = 0; row <= VALID_ROWS; row++)
                    {
                        //if there is a platform or a ladder we can add food here
                        if (hasObject(Type.Platform, column, row) || hasObject(Type.Ladder, column, row))
                            valid.add(row);
                        
                        //if there is a platform here
                        if (hasObject(Type.Platform, column, row))
                        {
                            //if there is no platform to the east or west
                            if (!hasObject(Type.Platform, column - 1, row) && !hasObject(Type.Platform, column + 1, row))
                            {
                                //remove platform
                                removeObject(Type.Platform, column, row);
                            }
                            else
                            {
                                //add platform
                                addPlatform(column - 1, row);
                                addPlatform(column + 1, row);
                            }
                        }
                    }
                    
                    while(valid.size() > types.size())
                    {
                        //remove random index
                        valid.remove(random.nextInt(valid.size()));
                    }
                    
                    for (int index = 0; index < valid.size(); index++)
                    {
                        final int row = valid.get(index);
                        
                        final Food.Type type;
                        
                        if (index == 0)
                        {
                            type = Food.Type.BurgerTop;
                        }
                        else if (index == valid.size() - 1)
                        {
                            type = Food.Type.BurgerBottom;
                        }
                        else
                        {
                            type = types.get(index);
                        }
                        
                        //add food
                        addFood(type, column, row);
                        
                        //make sure there are platforms here so we can make food drop
                        addPlatform(column, row);
                        addPlatform(column - 1, row);
                        addPlatform(column + 1, row);
                        
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void addFood(Food.Type type, final double col, final double row)
    {
        Food food = new Food(type);
        food.setLocation((col * WIDTH) + (WIDTH / 2) - (food.getWidth() / 2), (row * HEIGHT) + HEIGHT - food.getHeight());
        food.setCol(col);
        food.setRow(row);
        foods.add(food);
    }
    
    private void addPlatform(final double col, final double row)
    {
        LevelObject obj = new LevelObject(Type.Platform);
        obj.setLocation(col * WIDTH, (row * HEIGHT) + HEIGHT);
        obj.setCol(col);
        obj.setRow(row);
        
        addObject(obj);
    }
    
    private void addLadder(final double col, final double row)
    {
        LevelObject obj = new LevelObject(Type.Ladder);
        obj.setLocation((col * WIDTH) + (WIDTH / 2) - (obj.getWidth() / 2), (row * HEIGHT));
        obj.setCol(col);
        obj.setRow(row);
        
        addObject(obj);
    }
    
    private void addBurgerContainer(final double col, final double row)
    {
        LevelObject obj = new LevelObject(Type.BurgerContainer);
        obj.setLocation((col * WIDTH) + (WIDTH / 2) - (obj.getWidth() / 2), (row * HEIGHT) + HEIGHT);
        obj.setCol(col);
        obj.setRow(row);
        
        addObject(obj);
    }
    
    private void addObject(final LevelObject object)
    {
        //if object exists at the location don't add
        if (hasObject(object.getType(), object.getCol(), object.getRow()))
            return;

        //add object to List
        objects.add(object);
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
    }
    
    protected List<LevelObject> getObjects()
    {
        return this.objects;
    }
    
    protected List<Food> getFoods()
    {
        return this.foods;
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
                    if (getObject(Type.Platform, current.getCol() - 1, current.getRow()) != null)
                        current.remove(Location.Wall.West);
                    
                    //if there is a platform to the east, remove wall
                    if (getObject(Type.Platform, current.getCol() + 1, current.getRow()) != null)
                        current.remove(Location.Wall.East);
                    
                    //if there is a ladder in the current location, remove wall
                    if (getObject(Type.Ladder, current.getCol(), current.getRow()) != null)
                        current.remove(Location.Wall.North);
                    
                    //if there is a ladder to the south, remove wall
                    if (getObject(Type.Ladder, current.getCol(), current.getRow() + 1) != null)
                        current.remove(Location.Wall.South);
                    
                    //add location to map
                    map.add(current);
                }
            }
        }
        
        return this.map;
    }
    
    private boolean hasObject(final Type type, final double column, final double row)
    {
        return (getObject(type, column, row) != null);
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
        
        for (LevelObject object : getObjects())
        {
            if (object.getType() != type)
                continue;
            
            if ((int)object.getCol() == (int)column && (int)object.getRow() == (int)row)
                return object;
        }
        
        return null;
    }
    
    private void removeObject(final Type type, final double column, final double row)
    {
        for (int index = 0; index < getObjects().size(); index++)
        {
            LevelObject object = getObjects().get(index);
            
            //if the type is not the same skip to next
            if (object.getType() != type)
                continue;
            
            //if the column and row are equal
            if ((int)object.getCol() == (int)column && (int)object.getRow() == (int)row)
            {
                //remove object
                objects.remove(index);
            }
        }
        
    }
}