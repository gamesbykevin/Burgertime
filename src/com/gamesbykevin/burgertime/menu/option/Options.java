package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Options extends Option
{
    private static final String TITLE = "Options";
    
    public Options()
    {
        super(CustomMenu.LayerKey.Options);
        
        super.add(TITLE, null);
    }
}