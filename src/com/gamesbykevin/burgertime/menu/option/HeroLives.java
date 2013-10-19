package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class HeroLives extends Option
{
    private static final String TITLE = "Lives: ";
    
    public HeroLives(final Audio audio)
    {
        super(TITLE);
        
        for (int i = 0; i <= 7; i++)
        {
            if (i > 0)
                super.add("" + i, audio);
            else
                super.add("Unlimited", audio);
        }
        
        //default to 5
        super.setIndex(5);
    }
}