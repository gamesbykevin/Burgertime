package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Credits extends Option
{
    private static final String TITLE = "Credits";
    
    public Credits()
    {
        super(CustomMenu.LayerKey.Credits);
        
        super.add(TITLE, null);
    }
}