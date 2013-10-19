package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class ExitGameConfirmYes extends Option
{
    private static final String TITLE = "Yes";
    
    public ExitGameConfirmYes()
    {
        super(CustomMenu.LayerKey.MainTitle);
        
        super.add(TITLE, null);
    }
}