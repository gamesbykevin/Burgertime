package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class NewGameConfirmNo extends Option
{
    private static final String TITLE = "No";
    
    public NewGameConfirmNo()
    {
        super(CustomMenu.LayerKey.StartGame);
        
        super.add(TITLE, null);
    }
}