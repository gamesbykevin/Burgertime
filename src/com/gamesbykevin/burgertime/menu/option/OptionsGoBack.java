package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu.LayerKey;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class OptionsGoBack extends Option
{
    private static final String TITLE = "Go Back";
    
    public OptionsGoBack()
    {
        super(LayerKey.MainTitle);
        
        super.add(TITLE, null);
    }
}