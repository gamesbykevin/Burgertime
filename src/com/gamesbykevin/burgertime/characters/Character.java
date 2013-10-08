package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.base.Animation;

import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Rectangle;

/**
 *
 * @author GOD
 */
public class Character extends LevelObject
{
    private static final long DELAY_DEFAULT = TimerCollection.toNanoSeconds(125L);
    
    //all characters have the same width/height
    protected static final int DIMENSION = 20;
    
    public enum State
    {
        //the hero can attack in all 4 directions, enemies don't attack they just kill upon collision
        AttackSouth, AttackNorth, AttackWest, AttackEast,
        
        //the different animations for movement
        MoveSouth, MoveNorth, MoveWest, MoveEast, 
        
        //when the enemy has gotten hit with pepper
        Stunned,
        
        //death animation
        Die, 
    }
    
    /**
     * The different rate of speeds for the character
     */
    public enum Speed
    {
        SLOW(1), MEDIUM(1.5), FAST(2);
        
        private Speed(final double velocity)
        {
            this.velocity = velocity;
        }
        
        private final double velocity;
        
        public double getVelocity()
        {
            return this.velocity;
        }
    }
    
    //the characters speed, and the default is slow
    private Speed speed = Speed.SLOW;
    
    public Character(final Type type)
    {
        super(type);
        
        //set default width/height
        super.setDimensions(DIMENSION, DIMENSION);
    }
    
    protected void setSpeed(final Speed speed)
    {
        this.speed = speed;
    }
    
    protected double getSpeed()
    {
        return speed.getVelocity();
    }
    
    protected void setState(final State state)
    {
        super.getSpriteSheet().setCurrent(state);
        super.getSpriteSheet().reset();
    }
    
    protected State getState()
    {
        return (State)super.getSpriteSheet().getCurrent();
    }
    
    protected boolean canMove()
    {
        return (!isStunned() && !isDead() && !isAttacking());
    }
    
    protected boolean isStunned()
    {
        return (getState() == State.Stunned);
    }
    
    protected boolean isDead()
    {
        return (getState() == State.Die);
    }
    
    protected boolean isAttacking()
    {
        switch (getState())
        {
            case AttackEast:
            case AttackWest:
            case AttackNorth:
            case AttackSouth:
                return true;
                
            default:
                return false;
        }
    }
    
    @Override
    protected void setupAnimations(final Type type) throws Exception
    {
        //object used to setup all our animation
        Animation animation;
        
        switch(type)
        {
            case Hero:
                setupAnimations(40, 20);
                
                //setup Attack South
                animation = new Animation();
                animation.add(new Rectangle(200, 20, DIMENSION, DIMENSION), DELAY_DEFAULT);
                getSpriteSheet().add(animation, State.AttackSouth);
                
                //setup Attack North
                animation = new Animation();
                animation.add(new Rectangle(220, 20, DIMENSION, DIMENSION), DELAY_DEFAULT);
                getSpriteSheet().add(animation, State.AttackNorth);
                
                //setup Attack East
                animation = new Animation();
                animation.add(new Rectangle(240, 20, DIMENSION, DIMENSION), DELAY_DEFAULT);
                getSpriteSheet().add(animation, State.AttackEast);
                
                //setup Attack West
                animation = new Animation();
                animation.add(new Rectangle(260, 20, DIMENSION, DIMENSION), DELAY_DEFAULT);
                getSpriteSheet().add(animation, State.AttackWest);
                break;
                
            case EnemyHotdog:
                setupAnimations(0, 125);
                break;
                
            case EnemyEgg:
                setupAnimations(0, 145);
                break;
                
            case EnemyPickle:
                setupAnimations(0, 165);
                break;
                
            case EnemyCarrot:
                setupAnimations(0, 185);
                break;
                
            case EnemyOnion:
                setupAnimations(0, 205);
                break;
                
            default:
                throw new Exception("Type not setup here");
        }
        
        //this is the default state
        setState(State.MoveSouth);
    }
    
    /**
     * Setup all default animations which all characters should have.
     * Hero doesn't have stunned animation, so we will not use it for the hero
     * @param startX The start X location
     * @param y Y coordinate
     */
    private void setupAnimations(int startX, final int y)
    {
        Animation animation;
        
        //setup Move South
        animation = new Animation();
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveSouth);

        //setup Move North
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveNorth);

        //setup Move East
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveEast);

        //setup Move West
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveWest);

        //setup Death
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        getSpriteSheet().add(animation, State.Die);

        //setup stunned
        animation = new Animation();
        startX += 100;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.Stunned);
        
        //set the default
        getSpriteSheet().setCurrent(State.MoveSouth);
    }
}