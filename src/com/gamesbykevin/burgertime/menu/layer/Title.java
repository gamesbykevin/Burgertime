package com.gamesbykevin.burgertime.menu.layer;

import com.gamesbykevin.burgertime.resources.MenuImage;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.menu.CustomMenu;

public class Title extends Layer implements LayerRules
{
    public Title(final Engine engine)
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //set the background image of the Layer
        super.setImage(engine.getResources().getMenuImage(MenuImage.Keys.TitleScreen));

        //we will force this layer to show
        setForce(true);
        
        //we do not want to pause this layer once it completes
        setPause(false);
        
        //the next layer to go to
        setNextLayer(CustomMenu.LayerKey.Credits);
        
        //this layer will be active for 2 seconds
        setTimer(new Timer(Timers.toNanoSeconds(2000L)));
        
        //no options here to setup
    }
}
