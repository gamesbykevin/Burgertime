package com.gamesbykevin.burgertime.menu.layer;

import com.gamesbykevin.burgertime.resources.MenuAudio;
import com.gamesbykevin.burgertime.resources.MenuImage;
import com.gamesbykevin.framework.menu.*;
import com.gamesbykevin.framework.resources.Audio;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.characters.Character.*;
import com.gamesbykevin.burgertime.manager.Manager.*;
import com.gamesbykevin.burgertime.menu.CustomMenu.*;
import com.gamesbykevin.burgertime.menu.option.*;

public class Options extends Layer implements LayerRules
{
    public Options(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        setTitle("Options");
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.TitleBackground));
        
        //what is the duration of the current layer
        setTimer(new Timer(TimerCollection.toNanoSeconds(5000L)));
        
        //should we force the user to view this layer
        setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        setPause(true);
        
        //since there are options how big should the container be
        setOptionContainerRatio(RATIO);
        
        Audio audio = engine.getResources().getMenuAudio(MenuAudio.Keys.OptionChange);
        
        //add options
        super.add(OptionKey.Sound,          new Sound(audio));
        super.add(OptionKey.FullScreen,     new FullScreen(audio));
        super.add(OptionKey.TimeLimit,      new TimeLimit(audio));
        super.add(OptionKey.Speed,          new CharacterSpeed(audio));
        super.add(OptionKey.EnemyRespawn,   new EnemyRespawn(audio));
        super.add(OptionKey.EnemyLimit,     new EnemyCount(audio));
        super.add(OptionKey.HeroLives,      new HeroLives(audio));
        super.add(OptionKey.HeroPepper,     new PepperLimit(audio));
        super.add(OptionKey.GoBack,         new OptionsGoBack());
    }
}