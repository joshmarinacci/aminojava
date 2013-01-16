package org.joshy.gfx.util;

import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A ListModel implementation that is also an ArrayList. It will properly
 * fire events when updated.
 */
public class ArrayListModel<E> extends ArrayList<E> implements ListModel<E> {
    
    public ArrayListModel() {
    }

    public ArrayListModel(E ... s1) {
        super();
        for(E e : s1) {
            add(e);
        }
    }

    @Override
    public E set(int index, E element) {
        E val =super.set(index, element);
        fireUpdate();
        return val;
    }

    @Override
    public boolean add(E e) {
        boolean b = super.add(e);
        fireUpdate();
        return b;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        fireUpdate();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean b = super.addAll(c);
        fireUpdate();
        return b;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean b = super.addAll(index, c);
        fireUpdate();
        return b;
    }

    private void fireUpdate() {
        EventBus.getSystem().publish(new ListView.ListEvent(ListView.ListEvent.Updated,this));
    }


}
