package org.joshy.gfx.node.control;

/**
 * The base model for all list and list like controls.  Designed to be typesafe and
 * super simple.  If you want a concrete implementation look at the ArrayListModel,
 * which is an ArrayList subclass which implements ListModel.
 */
public interface ListModel<E> {
    public E get(int i);
    public int size();
}
