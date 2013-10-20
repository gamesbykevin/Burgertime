package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.levelobject.LevelObject;
import com.gamesbykevin.burgertime.resources.GameAudio;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Here is where we will manage the keyboard input among other things for our hero
 * @author GOD
 */
public final class Hero extends Character implements ICharacter
{
    //the salt the hero can dump on the enemies
    private List<LevelObject> projectiles;
    
    //the number of lives
    private int lives = -1;
    
    //the number of pepper throws
    private int pepper = -1;
    
    //sit there for 3 seconds after death
    private static final long DELAY_DEATH = Timers.toNanoSeconds(3000L);
    
    //the time to finish each board (4 minutes)
    private static final long DELAY_GAME = Timers.toNanoSeconds(60000L) * 4;
    
    //refresh the image every 1 second
    private static final long DELAY_IMAGE = Timers.toNanoSeconds(1000L);
    
    //the amount of time to wait for the intro music to finish
    private static final long DELAY_INTRO = Timers.toNanoSeconds(4000L);
    
    //where our hero will spawn
    private static final double START_COL = 8.5;
    private static final double START_ROW = 10.5;
    
    //our timer object
    private Timers timers;
    
    private enum Keys
    {
        Death, Game, ImageRefresh, Intro
    }
    
    //the score for our hero
    private int score = 0;
    
    //our buffered image for the heads-up-display
    private BufferedImage imageHud;
    
    //where to draw the hud image
    private Point hudLocation;
    
    /**
     * Create a new hero
     * @param time The time to deduct per update
     * @param timed Is our game play timed
     * @param x The location of the H.U.D. display
     * @param y The location of the H.U.D. display
     */
    public Hero(final long time, final boolean timed, final int x, final int y)
    {
        super(Type.Hero);
        
        //create new timer object
        this.timers = new Timers(time);
        
        //add the time to delay for when dead
        this.timers.add(Keys.Death, DELAY_DEATH);
        
        //add the time to create a new buffered image for our H.U.D.
        this.timers.add(Keys.ImageRefresh, DELAY_IMAGE);
        
        this.timers.add(Keys.Intro, DELAY_INTRO);
        
        if (timed)
        {
            //if timed we are counting down the time
            this.timers.add(Keys.Game, DELAY_GAME);
        }
        else
        {
            //keep track of the time passed
            this.timers.add(Keys.Game);
        }
        
        this.hudLocation = new Point(x, y);
        
        //create our list for the projectiles
        this.projectiles = new ArrayList<>();
        
        //set our start location
        resetLocation();
    }
    
    /**
     * Reset the location
     */
    public void resetLocation()
    {
        //reset start point
        super.setCol(START_COL);
        super.setRow(START_ROW);
        
        //reset animation
        setState(State.MoveSouth);
    }
    
    /**
     * Reset our timer
     */
    public void resetTimer()
    {
        this.timers.reset();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        for (LevelObject projectile : projectiles)
        {
            if (projectile != null)
                projectile.dispose();
            
            projectile = null;
        }
        
        projectiles.clear();
        projectiles = null;
    }
    
    /**
     * Add parameter to the current score
     * @param score 
     */
    public void addScore(final int score)
    {
        this.score += score;
        
        resetImage();
    }
    
    public void resetImage()
    {
        if (imageHud != null)
        {
            imageHud.flush();
            imageHud = null;
        }
    }
    
    public void addPepper()
    {
        setPepper(getPepper() + 1);
    }
    
    public void removePepper()
    {
        setPepper(getPepper() - 1);
    }
    
    public void addLife()
    {
        setLives(getLives() + 1);
    }
    
    public void removeLife()
    {
        setLives(getLives() - 1);
    }
    
    public boolean hasPepper()
    {
        return (pepper != 0);
    }
    
    public boolean hasLives()
    {
        return (lives != 0);
    }
    
    public void setLives(final int lives)
    {
        this.lives = lives;
        
        resetImage();
    }
    
    public void setPepper(final int pepper)
    {
        this.pepper = pepper;
        
        resetImage();
    }
    
    public int getPepper()
    {
        return this.pepper;
    }
    
    public int getLives()
    {
        return this.lives;
    }
    
    private int getScore()
    {
        return this.score;
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        super.update(engine.getMain().getTime());
        
        //determine if timer has started
        if (!timers.hasStarted(Keys.Intro))
        {
            getSpriteSheet().setPause(true);
            
            //stop all existing sound
            engine.getResources().stopAllSound();
            
            //play intro music
            engine.getResources().playGameAudio(GameAudio.Keys.Intro, false);
        }
        
        //has the intro time finished
        boolean finished = timers.hasTimePassed(Keys.Intro);
        
        timers.update(Keys.Intro);
        
        //if the intro time wasn't done before but it is now
        if (!finished && timers.hasTimePassed(Keys.Intro))
        {
            //stop all existing sound
            engine.getResources().stopAllSound();
            
            //play main theme
            engine.getResources().playGameAudio(GameAudio.Keys.Theme, true);
            
            getSpriteSheet().setPause(false);
        }
        
        //don't do anything until the intro has finished
        if (!timers.hasTimePassed(Keys.Intro))
            return;
        
        //if we are able to move
        if (canMove())
        {
            //get the count of projectiles before checking keyboard input
            final int count = projectiles.size();
            
            //manage the keyboard input
            manageInput(engine.getKeyboard());
            
            //a projectile was added so play sound effect
            if (count < projectiles.size())
                engine.getResources().playGameAudio(GameAudio.Keys.ThrowPepper, false);
        }
        else
        {
            //if we can't move reset the velocity
            resetVelocity();
            
            //don't continue if we are dead
            if (isDead())
            {
                if (!timers.hasStarted(Keys.Death))
                {
                    //stop all audio
                    engine.getResources().stopAllSound();
                    
                    //play death sound
                    engine.getResources().playGameAudio(GameAudio.Keys.Death, false);
                }
                
                //update our timer
                timers.update(Keys.Death);
                
                if (timers.hasTimePassed(Keys.Death))
                {
                    //reset our timer
                    timers.reset(Keys.Death);
                    
                    //lose a life
                    this.removeLife();
                    
                    //reset our location
                    this.resetLocation();
                    
                    //reset the enemies as well
                    engine.getManager().getEnemies().reset();
                    
                    //stop all existing sound
                    engine.getResources().stopAllSound();

                    //if we still have lives play main theme again
                    if (hasLives())
                    {
                        //play main theme
                        engine.getResources().playGameAudio(GameAudio.Keys.Theme, true);
                    }
                }
                
                return;
            }
        }
        
        timers.update(Keys.ImageRefresh);
        
        if (timers.hasTimePassed(Keys.ImageRefresh))
        {
            //reset the image
            resetImage();
            
            //reset timer
            timers.reset(Keys.ImageRefresh);
        }
        
        //update game time
        timers.update(Keys.Game);
        
        //make sure we are counting down
        if (timers.getTimer(Keys.Game).getReset() != 0)
        {
            //time has passed so the hero loses a life
            if (timers.hasTimePassed(Keys.Game))
            {
                //reset timer
                timers.reset(Keys.Game);

                //mark hero dead
                markDead();
            }
        }
        
        //if moving, check for collision with the board
        if (hasVelocity())
        {
            checkCollision(engine.getManager().getBoard());
        }
        
        //if the projectile exists
        if (!projectiles.isEmpty())
        {
            //update all projectiles
            for (int i=0; i < projectiles.size(); i++)
            {
                //get the current projectile
                LevelObject projectile = projectiles.get(i);
                
                //update the location and animation
                projectile.update(engine);

                //if the animation has finished we will remove
                if (projectile.getSpriteSheet().hasFinished())
                {
                    projectiles.remove(i);
                    i--;
                }
                else
                {
                    //check if the projectile has hit any of the enemies
                    engine.getManager().getEnemies().checkProjectileCollision(projectile);
                }
            }
        }
        
        //check if any enemies have hit the hero
        engine.getManager().getEnemies().checkHeroCollision(this);
        
        //manage the current animation
        checkAnimation();
    }
    
    public void markDead()
    {
        //stop moving
        resetVelocity();

        //don't pause animation
        getSpriteSheet().setPause(false);

        //set the death animation
        setState(Character.State.Die);
    }
    
    private void checkAnimation()
    {
        //if we are attacking and the animation has finished
        if (isAttacking() && getSpriteSheet().hasFinished())
        {
            switch(getState())
            {
                case AttackSouth:
                    setState(State.MoveSouth);
                    break;

                case AttackEast:
                    setState(State.MoveEast);
                    break;

                case AttackWest:
                    setState(State.MoveWest);
                    break;

                case AttackNorth:
                    setState(State.MoveNorth);
                    break;
            }
        }
    }
    
    private void manageInput(Keyboard keyboard) throws Exception
    {
        //want to move west
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            super.setVelocityX(-getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveWest);
            super.getSpriteSheet().setPause(false);
        }
        
        //want to move east
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            super.setVelocityX(getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveEast);
            super.getSpriteSheet().setPause(false);
        }
        
        //want to move north
        if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
        {
            super.setVelocityY(-getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveNorth);
            super.getSpriteSheet().setPause(false);
        }
        
        //want to move south
        if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
        {
            super.setVelocityY(getSpeed());
            super.getSpriteSheet().setCurrent(State.MoveSouth);
            super.getSpriteSheet().setPause(false);
        }
        
        //if any of the keys were released stop animation and stop movement
        if (keyboard.isKeyReleased())
        {
            //stop movement
            super.resetVelocity();
            
            //reset all keyboard events
            keyboard.reset();
            
            //pause animation since no movement
            super.getSpriteSheet().setPause(true);
        }
        
        //we want to throw pepper
        if (keyboard.hasKeyPressed(KeyEvent.VK_SPACE) && projectiles.isEmpty())
        {
            if (hasPepper())
            {
                //remove pepper from our count
                removePepper();

                switch(getState())
                {
                    case MoveSouth:
                        super.setState(State.AttackSouth);
                        addProjectile(getX(), getY() + DIMENSION, super.getCol(), super.getRow() + .5);
                        break;

                    case MoveEast:
                        super.setState(State.AttackEast);
                        addProjectile(getX() + DIMENSION, getY(), super.getCol() + .5, super.getRow());
                        break;

                    case MoveWest:
                        super.setState(State.AttackWest);
                        addProjectile(getX() - DIMENSION, getY(), super.getCol() - .5, super.getRow());
                        break;

                    case MoveNorth:
                        super.setState(State.AttackNorth);
                        addProjectile(getX(), getY() - DIMENSION, super.getCol(), super.getRow() - .5);
                        break;
                }
            }
        }
    }
    
    /**
     * Add a projectile at the specified x,y coordinates and column, row
     * @param x 
     * @param y
     * @param col
     * @param row
     */
    private void addProjectile(final double x, final double y, final double col, final double row)
    {
        //don't pause hero animation
        super.getSpriteSheet().setPause(false);
        
        //create our projectile
        LevelObject projectile = new LevelObject(Type.Salt);
        
        //set the location
        projectile.setLocation(x, y);
                
        //set the column and row
        projectile.setCol(col);
        projectile.setRow(row);
        
        //set projectile dimensions
        projectile.setDimensions(DIMENSION, DIMENSION);
        
        //set the image
        projectile.setImage(super.getImage());

        //don't pause animation
        projectile.getSpriteSheet().setPause(false);
        
        //add to list
        projectiles.add(projectile);
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
        
        //draw the projectiles
        for (LevelObject projectile : projectiles)
        {
            projectile.draw(graphics);
        }
        
        //draw our H.U.D. Display
        renderHud(graphics);
    }
    
    /**
     * Draw player information on the screen
     * @param graphics Graphics object
     */
    private void renderHud(final Graphics graphics)
    {
        if (this.imageHud == null)
        {
            this.imageHud = new BufferedImage(300, 35, BufferedImage.TYPE_INT_ARGB);
        
            final Graphics tmp = imageHud.createGraphics();

            tmp.setColor(Color.BLACK);
            tmp.fillRect(0, 0, imageHud.getWidth(), imageHud.getHeight());
            tmp.setColor(Color.WHITE);

            final int height = tmp.getFontMetrics().getHeight();

            int y = height;
            int x = 0;

            final List<String> description = new ArrayList<>();
            description.add("Lives");
            description.add("Pepper");
            description.add("Score");
            description.add("Time");

            for (String desc : description)
            {
                tmp.drawString(desc, x, y);
                x += 80;
            }

            x = 0;
            y = height * 2;

            //clear list
            description.clear();

            if (getLives() < 0)
                description.add("Unlimited");
            else
                description.add("" + getLives());

            if (getPepper() < 0)
                description.add("Unlimited");
            else
                description.add("" + getPepper());

            //add score
            description.add("" + getScore());

            //add time
            if (timers.getTimer(Keys.Game).getReset() != 0)
            {
                //if we are counting down get the time remaining
                description.add(timers.getDescRemaining(Keys.Game, Timers.FORMAT_8));
            }
            else
            {
                //if we aren't counting down get the time passed
                description.add(timers.getDescPassed(Keys.Game, Timers.FORMAT_8));
            }

            // bold/italics the info
            tmp.setFont(tmp.getFont().deriveFont(Font.BOLD).deriveFont(Font.ITALIC));

            for (String desc : description)
            {
                tmp.drawString(desc, x, y);
                x += 80;
            }
        }
        
        //draw the H.U.D. display
        graphics.drawImage(imageHud, hudLocation.x, hudLocation.y, null);
    }
    
    public void renderGameOver(final Graphics graphics)
    {
        Font tmp = graphics.getFont().deriveFont(24f).deriveFont(Font.BOLD);
        
        //white font color
        graphics.setColor(Color.WHITE);
        
        //24 point and BOLD
        graphics.setFont(tmp);
        
        int x = 55;
        int y = 75;
        final int h = graphics.getFontMetrics().getHeight();
        
        graphics.drawString("Game Over", x, y);
        
        y += h;
        
        graphics.drawString("Score: " + getScore(), x, y);
        
        y += h;
        
        graphics.drawString("Hit \"Esc\" to access menu.", x, y);
        
        y += h;
        
        graphics.drawString("Use mouse or arrows/enter to interact.", x, y);
    }
}