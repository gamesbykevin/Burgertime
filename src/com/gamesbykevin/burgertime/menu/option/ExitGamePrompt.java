/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class ExitGamePrompt extends Option
{
    private static final String TITLE = "Exit Game";
    
    public ExitGamePrompt()
    {
        super(CustomMenu.LayerKey.ExitGameConfirm);
        
        super.add(TITLE, null);
    }
}