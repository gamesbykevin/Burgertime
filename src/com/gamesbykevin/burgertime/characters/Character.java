package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.board.Board;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.levelobject.LevelObject.Type;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Rectangle;

/**
 * Enemy and Hero are Characters
 * @author GOD
 */
public abstract class Character extends LevelObject implements Disposable
{
    //time delays
    private static final long DELAY_DEFAULT = TimerCollection.toNanoSeconds(125L);
    private static final long DELAY_DEFAULT_DEATH = TimerCollection.toNanoSeconds(200L);
    private static final long DELAY_DEFAULT_ENEMY = TimerCollection.toNanoSeconds(125L);
    private static final long DELAY_DEFAULT_STUNNED = TimerCollection.toNanoSeconds(333L);
    
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
        SLOW(.02), MEDIUM(.025), FAST(.03), FASTEST(.04);
        
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
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    public void setSpeed(final Speed speed)
    {
        this.speed = speed;
    }
    
    protected double getSpeed()
    {
        return speed.getVelocity();
    }
    
    public void setState(final State state)
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
    
    public boolean isDead()
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
    
    protected void update(final long time) throws Exception
    {
        //update sprite sheet
        getSpriteSheet().update(time);
        
        //now set x,y based on the column, row
        super.setX((super.getCol() * Board.WIDTH) - (getWidth()  / 2));
        super.setY((super.getRow() * Board.HEIGHT)- (getHeight() / 2));
        
        //update location
        super.update();
    }
    
    protected void checkCollision(final Board board)
    {
        //get original location
        final double col = getCol();
        final double row = getRow();
        
        //update location temporary
        super.update();
        
        if (!super.hasBounds())
        {
            //stop movement if no longer in bounds
            resetAll(col, row);
            
            //exit method
            return;
        }
        
        //if we are moving left or right
        if (super.hasVelocityX())
        {
            //get current location by default
            LevelObject platform = board.getObject(Type.Platform, super.getCol(), super.getRow());
            
            if (super.getVelocityX() < 0)
            {
                if (super.getCol() <= ((int)super.getCol()) + .5)
                {
                    //make sure we are on a platform before we check the platform to the west
                    if (platform != null)
                    {
                        //are we moving west, if so get the platform to the west
                        platform = board.getObject(Type.Platform, super.getCol() - .5, super.getRow());
                    }
                }
            }
            else
            {
                if (super.getCol() > ((int)super.getCol()) + .5)
                {
                    //make sure we are on a platform before we check the platform to the east
                    if (platform != null)
                    {
                        //are we moving east, if so get the platform to the east
                        platform = board.getObject(Type.Platform, super.getCol() + .5, super.getRow());
                    }
                }
            }
            
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
                //reset
                resetAll(col, row);
            }
            
            return;
        }
        
        //if we are moving up or down
        if (super.hasVelocityY())
        {
            //get current location by default
            LevelObject ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow());
            
            if (getVelocityY() < 0)
            {
                if (super.getRow() <= ((int)super.getRow()) + .5)
                {
                    //make sure ladder exists at current location
                    if (ladder != null)
                    {
                        //if we are heading north
                        ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow() - .5);
                        
                        //we currently have a ladder but no ladder is above us
                        if (ladder == null)
                        {
                            //check the ladder below
                            ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow() + .5);
                        }
                    }
                }
                else
                {
                    //if the ladder does not exist at the curent position check if a ladder is below
                    if (ladder == null)
                    {
                        ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow() + .5);
                    }
                }
            }
            else
            {
                if (super.getRow() > ((int)super.getRow()) + .5)
                {
                    //if we are heading south
                    ladder = board.getObject(Type.Ladder, super.getCol(), super.getRow() + .5);
                }
            }
                
            if (ladder != null)
            {
                //if we are moving vertically on a ladder center x coordinate
                setCol(ladder.getCol() + .5);

                //reset y coordinate
                setRow(row);
            }
            else
            {
                //reset
                resetAll(col, row);
            }
            
            return;
        }
    }
    
    private void resetAll(final double col, final double row)
    {
        //reset location
        super.setCol(col);
        super.setRow(row);
        
        //stop velocity
        super.resetVelocity();
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
                animation.setLoop(false);
                animation.add(new Rectangle(260, 20, DIMENSION, DIMENSION), DELAY_DEFAULT);
                getSpriteSheet().add(animation, State.AttackWest);
                
                //remove death animation previously added
                getSpriteSheet().remove(State.Die);
                
                //setup Death since hero x coordinates are a little different
                animation = new Animation();
                
                for (int x = 280; x < 420; x += DIMENSION)
                {
                    animation.add(new Rectangle(x, 20, DIMENSION, DIMENSION), DELAY_DEFAULT_DEATH);
                }
                
                getSpriteSheet().add(animation, State.Die);
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
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_DEATH);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_DEATH);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_DEATH);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_DEATH);
        getSpriteSheet().add(animation, State.Die);

        //setup stunned
        animation = new Animation();
        startX += 100;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_STUNNED);
        startX += DIMENSION;
        animation.add(new Rectangle(startX, y, DIMENSION, DIMENSION), DELAY_DEFAULT_STUNNED);
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.Stunned);
        
        //set the default
        getSpriteSheet().setCurrent(State.MoveSouth);
        
        //no pause animation at first also
        getSpriteSheet().setPause(false);
    }
}