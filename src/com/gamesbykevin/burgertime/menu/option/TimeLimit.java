package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class TimeLimit extends Option
{
    private static final String TITLE = "Time Limit: ";
    
    public TimeLimit(final Audio audio)
    {
        super(TITLE);
        
        for (CustomMenu.Toggle toggle : CustomMenu.Toggle.values())
        {
            super.add(toggle.toString(), audio);
        }
        
        //default to off
        super.setIndex(0);
    }
}