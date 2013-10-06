package com.gamesbykevin.burgertime.characters;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.burgertime.engine.Engine;

/**
 *
 * @author GOD
 */
public interface ICharacter extends Disposable
{
    public void update(final Engine engine) throws Exception;
}