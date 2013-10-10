package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.base.Cell;

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
    private List<Cell> steps;
    
    public Enemy(final Type type)
    {
        super(type);
        
        this.steps = new ArrayList<>();
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
        
        if (getVelocityX() > 0)
            getSpriteSheet().setCurrent(State.MoveEast);

        if (getVelocityX() < 0)
            getSpriteSheet().setCurrent(State.MoveWest);
        
        if (getVelocityY() > 0)
            getSpriteSheet().setCurrent(State.MoveSouth);

        if (getVelocityY() < 0)
            getSpriteSheet().setCurrent(State.MoveNorth);
        
        //update sprite sheet
        getSpriteSheet().update(engine.getMain().getTime());
        
        //if there are no steps calculate them
        if (steps.isEmpty())
        {
            if (super.getX() < engine.getManager().getHero().getX())
            {
                super.resetVelocity();
                super.setVelocityX(super.getSpeed());
            }
            
            if (super.getX() > engine.getManager().getHero().getX())
            {
                super.resetVelocity();
                super.setVelocityX(-super.getSpeed());
            }
            
            if (super.getY() < engine.getManager().getHero().getY())
            {
                super.resetVelocity();
                super.setVelocityY(super.getSpeed());
                
            }
            
            if (super.getY() > engine.getManager().getHero().getY())
            {
                super.resetVelocity();
                super.setVelocityY(-super.getSpeed());
            }
        }
        else
        {
            
        }
    }
}