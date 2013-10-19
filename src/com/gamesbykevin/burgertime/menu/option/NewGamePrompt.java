package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class NewGamePrompt extends Option
{
    private static final String TITLE = "New Game";
    
    public NewGamePrompt()
    {
        super(CustomMenu.LayerKey.NewGameConfirm);
        
        super.add(TITLE, null);
    }
}