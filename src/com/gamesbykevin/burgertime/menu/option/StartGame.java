package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class StartGame extends Option
{
    private static final String TITLE = "Start Game";
    
    public StartGame()
    {
        super(CustomMenu.LayerKey.StartGame);
        
        super.add(TITLE, null);
    }
}