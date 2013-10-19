package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class EnemyRespawn extends Option
{
    private static final String TITLE = "Enemy Respawn: ";
    
    public EnemyRespawn(final Audio audio)
    {
        super(TITLE);
        
        for (CustomMenu.Toggle toggle : CustomMenu.Toggle.values())
        {
            super.add(toggle.toString(), audio);
        }
        
        //default to on
        super.setIndex(1);
    }
}