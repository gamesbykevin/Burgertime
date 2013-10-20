package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class EnemyCount extends Option
{
    private static final String TITLE = "# Enemies: ";
    
    public EnemyCount(final Audio audio)
    {
        super(TITLE);
        
        for (int i=1; i <= 8; i++)
        {
            super.add("" + i, audio);
        }
        
        //default 5 enemies
        super.setIndex(4);
    }
}