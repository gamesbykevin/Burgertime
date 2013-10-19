package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class NewGameConfirmYes extends Option
{
    private static final String TITLE = "Yes";
    
    public NewGameConfirmYes()
    {
        super(CustomMenu.LayerKey.NewGameConfirmed);
        
        super.add(TITLE, null);
    }
}