package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Instructions extends Option
{
    private static final String TITLE = "Instructions";
    
    public Instructions()
    {
        super(CustomMenu.LayerKey.Instructions1);
        
        super.add(TITLE, null);
    }
}