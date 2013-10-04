package com.gamesbykevin.burgertime.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;

import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.menu.CustomMenu;

public class NewGameConfirm extends Layer implements LayerRules
{
    public NewGameConfirm(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        super.setTitle("Confirm New");
        
        //should we force the user to view this layer
        super.setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        super.setPause(true);
        
        //since there are options how big should the container be
        super.setOptionContainerRatio(RATIO);
        
        //setup options (if any)
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //setup options here
        Option tmp;
        
        tmp = new Option(CustomMenu.LayerKey.NewGameConfirmed);
        tmp.add("Yes", null);
        super.add(CustomMenu.OptionKey.NewGameConfim, tmp);
        
        tmp = new Option(CustomMenu.LayerKey.StartGame);
        tmp.add("No", null);
        super.add(CustomMenu.OptionKey.NewGameDeny, tmp);
    }
}