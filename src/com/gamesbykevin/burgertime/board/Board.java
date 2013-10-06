package com.gamesbykevin.burgertime.board;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.resources.GameAudio;
import com.gamesbykevin.burgertime.resources.GameImage;
import com.gamesbykevin.burgertime.shared.IElement;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The board contains the balls
 * @author GOD
 */
public class Board extends Sprite implements Disposable, IElement
{
    List <LevelObject> objects;
    
    //dimensions of the board
    private static final int COLUMNS = 17;
    private static final int ROWS = 13;
    
    public Board(final Engine engine)
    {
        objects = new ArrayList<>();
        
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
            {
                LevelObject obj;
                
                //bottom row so add burger container
                if (row == ROWS - 1)
                {
                    if (col % 3 == 0)
                    {
                        obj = new LevelObject(Type.BurgerContainer);
                        obj.setLocation(col * 38, (row * 20));
                        obj.setImage(engine.getResources().getGameImage(GameImage.Keys.SpriteSheet));
                        objects.add(obj);
                    }
                }
                else
                {
                    //if the row is an even number
                    if (row % 2 == 0)
                    {
                        obj = new LevelObject(Type.Platform);
                        obj.setLocation(col * 24, (row * 20) + 20);
                        obj.setImage(engine.getResources().getGameImage(GameImage.Keys.SpriteSheet));
                        objects.add(obj);
                    }
                    else
                    {
                    }

                    if (col % 2 != 0)
                    {
                        obj = new LevelObject(Type.Ladder);
                        obj.setLocation(col * 20, (row * 20));
                        obj.setImage(engine.getResources().getGameImage(GameImage.Keys.SpriteSheet));
                        objects.add(obj);
                    }
                    
                }
            }
        }
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
            obj.draw(graphics);
        }
    }
}