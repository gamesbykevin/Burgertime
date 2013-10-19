package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Controls extends Option
{
    private static final String TITLE = "Controls";
    
    public Controls()
    {
        super(CustomMenu.LayerKey.Controls1);
        
        super.add(TITLE, null);
    }
}