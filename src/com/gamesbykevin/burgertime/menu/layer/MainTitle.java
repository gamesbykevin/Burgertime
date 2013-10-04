package com.gamesbykevin.burgertime.menu.layer;

import com.gamesbykevin.burgertime.resources.MenuImage;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.burgertime.shared.Shared;

public class MainTitle extends Layer implements LayerRules
{
    public MainTitle(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        setTitle(Shared.GAME_NAME);
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.TitleBackground));
        
        //we will not force this layer to show
        setForce(false);
        
        //we do not want to pause this layer once it completes
        setPause(true);
        
        //this layer will be active for x seconds
        setTimer(new Timer(TimerCollection.toNanoSeconds(5000L)));
        
        //since there are options how big should the container be
        setOptionContainerRatio(RATIO);
        
        //setup options (if any)
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //options for this layer setup here
        Option tmp;
        
        //create start game option with next layer specified
        tmp = new Option(CustomMenu.LayerKey.StartGame);
        
        //add option selection to option
        tmp.add("Start Game", null);
        
        //add option with specified unique key to this Layer
        super.add(CustomMenu.OptionKey.StartGame, tmp);
        
        
        tmp = new Option(CustomMenu.LayerKey.Options);
        tmp.add("Options", null);
        super.add(CustomMenu.OptionKey.Options, tmp);
        
        
        tmp = new Option(CustomMenu.LayerKey.Controls1);
        tmp.add("Controls", null);
        super.add(CustomMenu.OptionKey.Controls, tmp);
        
        
        tmp = new Option(CustomMenu.LayerKey.Instructions1);
        tmp.add("Instructions", null);
        super.add(CustomMenu.OptionKey.Instructions, tmp);
        
        
        tmp = new Option(CustomMenu.LayerKey.Credits);
        tmp.add("Credits", null);
        super.add(CustomMenu.OptionKey.Credits, tmp);
    }    
}