package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class ExitGameConfirmNo extends Option
{
    private static final String TITLE = "No";
    
    public ExitGameConfirmNo()
    {
        super(CustomMenu.LayerKey.StartGame);
        
        super.add(TITLE, null);
    }
}