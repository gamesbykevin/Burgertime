package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Resume extends Option
{
    private static final String TITLE = "Resume";
    
    public Resume()
    {
        super(CustomMenu.LayerKey.StartGame);
        
        super.add(TITLE, null);
    }
}