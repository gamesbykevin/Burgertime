package com.gamesbykevin.burgertime.menu.option;

import com.gamesbykevin.burgertime.characters.Character.Speed;

import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class CharacterSpeed extends Option
{
    private static final String TITLE = "Speed: ";
    
    public CharacterSpeed(final Audio audio)
    {
        super(TITLE);
        
        super.add("Normal", audio);
        super.add("Fast",   audio);
        
        //default to medium
        super.setIndex(0);
    }
}