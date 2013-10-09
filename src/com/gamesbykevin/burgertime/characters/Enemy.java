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
    
    public static Type getRandom(final Random random)
    {
        List<Type> types = new ArrayList<>();
        
        for (Type type : Type.values())
        {
            if (isType(type))
                types.add(type);
        }
        
        return types.get(random.nextInt(types.size()));
    }
    
    public static boolean isType(final Type type)
    {
        switch(type)
        {
            case EnemyHotdog:
            case EnemyEgg:
            case EnemyPickle:
            case EnemyCarrot:
            case EnemyOnion:
                return true;
            
            default:
                return false;
        }
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        super.update();
        
        //update sprite sheet
        getSpriteSheet().update(engine.getMain().getTime());
    }
}