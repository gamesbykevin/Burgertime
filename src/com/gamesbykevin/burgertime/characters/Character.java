package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.base.Animation;

import com.gamesbykevin.burgertime.board.Board;
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
    //time delays
    private static final long DELAY_DEFAULT = TimerCollection.toNanoSeconds(125L);
    private static final long DELAY_DEFAULT_ENEMY = TimerCollection.toNanoSeconds(250L);
    
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
        SLOW(.05), MEDIUM(.08), FAST(.11);
        
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
    
    //the characters speed, default is slow
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
    
    protected void update(final Board board, final long time) throws Exception
    {
        //update sprite sheet
        getSpriteSheet().update(time);
        
        //if we are moving
        if (hasVelocity())
        {
            //check for collision
            checkCollision(board);
        }
        
        //now set x,y based on the column, row
        super.setX((super.getCol() * Board.WIDTH) - (getWidth()  / 2));
        super.setY((super.getRow() * Board.HEIGHT)- (getHeight() / 2));
        
        //update location
        super.update();
    }
    
    private void checkCollision(final Board board)
    {
        //get original location
        final double col = getCol();
        final double row = getRow();
        
        //update location temporary
        super.update();
        
        if (!super.hasBounds())
        {
            //stop movement if no longer in bounds
            super.resetVelocity();
            
            //reset location
            super.setCol(col);
            super.setRow(row);
            
            //exit method
            return;
        }
        
        //get the platform at the current location
        LevelObject platform = board.getObject(Type.Platform, super.getCol(), super.getRow());
        
        //if we are moving left or right
        if (super.hasVelocityX())
        {
            //if a platform exists
            if (platform != null)
            {
                //reset x coordinate
                super.setCol(col);
                
                //if we are moving horizontally on a platform correct y coordinate
                super.setRow(platform.getRow() + 1 - .4);
            }
            else
            {
                //reset location
                super.setCol(col);
                super.setRow(row);

                //reset the velocity
                super.resetVelocityX();
            }
        }
        
        //get the ladder at the current location
        LevelObject ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow());
        
        //if we are moving up or down
        if (super.hasVelocityY())
        {
            //if a ladder exists
            if (ladder != null)
            {
                //if we are moving vertically on a ladder center x coordinate
                super.setCol(ladder.getCol() + .5);

                //reset y coordinate
                super.setRow(row);
            }
            else
            {
                //are we moving south
                if (super.getVelocityY() > 0)
                {
                    //check if the ladder is below
                    ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow() + .5);

                    //ladder exists
                    if (ladder != null)
                    {
                        //if we are moving vertically on a ladder center x coordinate
                        super.setCol(ladder.getCol() + .5);

                        //reset y coordinate
                        super.setRow(row);
                    }
                    else
                    {
                        //ladder does not exist, reset location
                        super.setRow(row);
                        super.setCol(col);

                        //reset y velocity
                        super.resetVelocityY();
                    }
                }
                
                //if heading north
                if (super.getVelocityY() < 0)
                {
                    //check if ladder is above
                    ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow() - .5);
                    
                    //ladder exists
                    if (ladder != null)
                    {
                        //if we are moving vertically on a ladder center x coordinate
                        super.setCol(ladder.getCol() + .5);

                        //reset y coordinate
                        super.setRow(row);
                    }
                    else
                    {
                        //ladder does not exist, reset location
                        super.setRow(row);
                        super.setCol(col);

                        //reset y velocity
                        super.resetVelocityY();
                    }
                }
            }
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
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveSouth);

        //setup Move North
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveNorth);

        //setup Move East
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveEast);

        //setup Move West
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.MoveWest);

        //setup Death
        animation = new Animation();
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        getSpriteSheet().add(animation, State.Die);

        //setup stunned
        animation = new Animation();
        startX += 100;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_ENEMY);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.Stunned);
        
        //set the default
        getSpriteSheet().setCurrent(State.MoveSouth);
        
        //no pause animation at first also
        getSpriteSheet().setPause(false);
    }
}