package org.joshy.gfx.event;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 20, 2010
* Time: 10:16:39 AM
* To change this template use File | Settings | File Templates.
*/
public interface Callback<T> {
    public void call(T event) throws Exception;
}
