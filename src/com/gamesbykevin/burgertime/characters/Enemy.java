package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author GOD
 */
public final class Enemy extends Character implements ICharacter
{
    public Enemy(final Type type)
    {
        super(type);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        
    }
}