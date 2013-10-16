package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.ai.*;
import com.gamesbykevin.framework.base.Cell;

import com.gamesbykevin.burgertime.board.Board;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;
import com.gamesbykevin.framework.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author GOD
 */
public final class Enemy extends Character implements ICharacter
{
    //the path to get to the destination
    private List<Cell> steps;
    
    //the next target
    private Cell current;
    
    //our AI object used to calculate the path
    private AStar ai;
    
    //the time to remain stunned
    private static final long DELAY_STUNNED = TimerCollection.toNanoSeconds(3000L);
    
    //timer for tracking stun time
    private Timer timer;
    
    public Enemy(final Type type)
    {
        super(type);
        
        //the enemies will move at a slower speed
        super.setSpeed(Speed.SLOW);
        
        //create a new empty list of steps representing the path
        this.steps = new ArrayList<>();
        
        //our AI object to calculate the path
        this.ai = new AStar();

        //the next target
        this.current = new Cell();
        
        //our game timer for stun time
        this.timer = new Timer(DELAY_STUNNED);
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
        super.update(engine.getMain().getTime());
        
        //are we stunned
        if (isStunned())
        {
            //update timer
            timer.update(engine.getMain().getTime());
            
            //if time has passed
            if (timer.hasTimePassed())
            {
                //reset timer
                timer.reset();
                
                //set state
                setState(State.MoveSouth);
                
                //remove all steps
                steps.clear();
                
                //exit method
                return;
            }
        }
        
        //if the enemy can't move don't continue
        if (!canMove())
            return;
        
        if (getVelocityX() > 0)
            getSpriteSheet().setCurrent(State.MoveEast);

        if (getVelocityX() < 0)
            getSpriteSheet().setCurrent(State.MoveWest);
        
        if (getVelocityY() > 0)
            getSpriteSheet().setCurrent(State.MoveSouth);

        if (getVelocityY() < 0)
            getSpriteSheet().setCurrent(State.MoveNorth);
        
        //if there are no steps calculate them
        if (steps.isEmpty())
        {
            //our goal
            final Hero hero = engine.getManager().getHero();

            //our playing board
            final Board board = engine.getManager().getBoard();
            
            //diagonal movement is not allowed
            ai.setDiagonal(false);
            
            //set start position
            ai.setStart(this);
            
            //our destination
            ai.setGoal(hero);
            
            //give the map of the area
            ai.setMap(board.getMap());
            
            //find the shortest path
            ai.calculate();
            
            //get the path to the destination
            this.steps = ai.getPath();
            
            //set the next target
            setTarget();
        }
        else
        {
            if (super.getCol() < current.getCol())
            {
                super.resetVelocity();
                super.setVelocityX(super.getSpeed());
                
                if (super.getCol() + super.getVelocityX() >= current.getCol())
                {
                    super.setCol(current);
                    super.resetVelocity();
                }
            }
            
            if (super.getCol() > current.getCol())
            {
                super.resetVelocity();
                super.setVelocityX(-super.getSpeed());
                
                if (super.getCol() + super.getVelocityX() <= current.getCol())
                {
                    super.setCol(current);
                    super.resetVelocity();
                }
            }
            
            if (super.getRow() < current.getRow())
            {
                super.resetVelocity();
                super.setVelocityY(super.getSpeed());
                
                if (super.getRow() + super.getVelocityY() >= current.getRow())
                {
                    super.setRow(current);
                    super.resetVelocity();
                }
            }
            
            if (super.getRow() > current.getRow())
            {
                super.resetVelocity();
                super.setVelocityY(-super.getSpeed());
                
                if (super.getRow() + super.getVelocityY() <= current.getRow())
                {
                    super.setRow(current);
                    super.resetVelocity();
                }
            }
            
            if (current.equals(this))
            {
                super.resetVelocity();
                
                //remove the step from the list
                steps.remove(steps.size() - 1);
                
                //set the next target
                setTarget();
            }
        }
    }
    
    private void setTarget()
    {
        if (!steps.isEmpty())
        {
            //set the current target to be in the middle of the cell
            current.setCol(steps.get(steps.size() - 1).getCol() + .5);
            current.setRow(steps.get(steps.size() - 1).getRow() + .5);
        }
    }
}