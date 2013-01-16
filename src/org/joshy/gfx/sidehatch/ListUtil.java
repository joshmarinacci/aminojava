package org.joshy.gfx.sidehatch;

import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.util.ArrayListModel;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 29, 2010
 * Time: 8:49:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListUtil {
    public static <T extends Comparable<? super T>> ListModel<T> toAlphaListModel(ListModel<T> model) {
        ArrayListModel<T> m2 = new ArrayListModel<T>();
        for(int i = 0; i<model.size(); i++) {
            m2.add(model.get(i));
        }
        Collections.sort(m2);
        return m2;
    }

    public static <U extends Comparable<? super U>> Iterable<U> toAlphaCollection(Collection<U> collection) {
        Iterator<U> it = collection.iterator();
        List<U> list = new ArrayList<U>();
        while(it.hasNext()) {
            list.add(it.next());
        }
        Collections.sort(list);
        return list;
    }
}
