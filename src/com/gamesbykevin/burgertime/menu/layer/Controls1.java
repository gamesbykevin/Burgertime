package com.gamesbykevin.burgertime.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.menu.CustomMenu;
import com.gamesbykevin.burgertime.resources.MenuImage;

public class Controls1 extends Layer implements LayerRules
{
    public Controls1(final Engine engine)
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.Controls1));
        
        //what is the next layer
        setNextLayer(CustomMenu.LayerKey.MainTitle);
        
        //should we force the user to view this layer
        setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        setPause(true);
        
        //what is the duration of the current layer
        setTimer(null);
        
        //setup options (if any)
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine)
    {
        //no options here to setup
    }
}