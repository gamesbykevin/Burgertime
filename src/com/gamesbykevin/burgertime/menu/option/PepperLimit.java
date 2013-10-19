package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class PepperLimit extends Option
{
    private static final String TITLE = "Pepper: ";
    
    public PepperLimit(final Audio audio)
    {
        super(TITLE);
        
        for (int i = 0; i <= 5; i++)
        {
            if (i > 0)
            {
                super.add("" + i, audio);
            }
            else
            {
                super.add("Unlimited", audio);
            }
        }
        
        //default to 3
        super.setIndex(3);
    }
}