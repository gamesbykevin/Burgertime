package com.gamesbykevin.burgertime.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.burgertime.engine.Engine;
import com.gamesbykevin.burgertime.menu.CustomMenu;

public class ExitGameConfirm extends Layer implements LayerRules
{
    public ExitGameConfirm(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        super.setTitle("Confirm Exit");
        
        //we will not force this layer to show
        super.setForce(false);
        
        //we do not want to pause this layer once it completes
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
        
        tmp = new Option(CustomMenu.LayerKey.MainTitle);
        tmp.add("Yes", null);
        super.add(CustomMenu.OptionKey.ExitGameConfirm, tmp);
        
        tmp = new Option(CustomMenu.LayerKey.StartGame);
        tmp.add("No", null);
        super.add(CustomMenu.OptionKey.ExitGameDeny, tmp);
    }
}