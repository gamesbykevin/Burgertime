package com.gamesbykevin.burgertime.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.characters.Hero;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.resources.GameAudio;
import com.gamesbykevin.burgertime.resources.GameImage;
import com.gamesbykevin.burgertime.shared.IElement;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;

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
    
    //image containing all board images
    private Image image;
    
    //dimensions of the board
    private static final int COLUMNS = 13;
    private static final int ROWS = 13;
    
    //dimensions of each cell
    private static final int WIDTH = 24;
    private static final int HEIGHT = 20;
    
    public Board(final Engine engine)
    {
        super();
        
        //set the boundaries
        super.setBounds(0, COLUMNS - 1, 0, ROWS - 1);
        
        this.objects = new ArrayList<>();
        
        this.image = engine.getResources().getGameImage(GameImage.Keys.SpriteSheet);
        
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
            {
                LevelObject obj;
                
                //bottom row so add burger container
                if (row >= ROWS - 2)
                {
                    if (col % 3 == 0 && row == ROWS - 1 && col < COLUMNS - 1)
                    {
                        obj = new LevelObject(Type.BurgerContainer);
                        obj.setLocation(((col + 2) * WIDTH) - (obj.getWidth() / 2), (row * HEIGHT) + HEIGHT);
                        obj.setCol(col);
                        obj.setRow(row);
                        objects.add(obj);
                    }
                }
                else
                {
                    //is this the right place for the ladder
                    if (col % 3 == 0 && row > 0)
                    {
                        obj = new LevelObject(Type.Ladder);
                        obj.setLocation((col * WIDTH) + (WIDTH / 2) - (obj.getWidth() / 2), (row * HEIGHT));
                        obj.setCol(col);
                        obj.setRow(row);
                        objects.add(obj);
                    }
                    
                    //for right now we add a plaform everywhere
                    obj = new LevelObject(Type.Platform);
                    obj.setLocation(col * WIDTH, (row * HEIGHT) + HEIGHT);
                    obj.setCol(col);
                    obj.setRow(row);
                    objects.add(obj);
                }
            }
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
        
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        for (LevelObject obj : objects)
        {
            obj.draw(graphics, image);
        }
    }
}