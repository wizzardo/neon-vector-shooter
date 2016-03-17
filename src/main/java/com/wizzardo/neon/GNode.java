package com.wizzardo.neon;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SafeArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wizzardo on 17.03.16.
 */
public class GNode<T extends Spatial> extends Node {

    public GNode(Class<T> elementType, int initSize) {
        if (initSize > 0)
            children = new GenericSafeList(elementType, initSize);
    }

    public GNode(T t, int initSize) {
        if (initSize > 0)
            children = new GenericSafeList(t.getClass(), initSize);
    }

    public GNode(String name, Class<T> elementType, int initSize) {
        super(name);
        if (initSize > 0)
            children = new GenericSafeList(elementType, initSize);
    }

    public GNode(String name, T t, int initSize) {
        super(name);
        if (initSize > 0)
            children = new GenericSafeList(t.getClass(), initSize);
    }


    static class GenericSafeList<T> extends SafeArrayList<T> {
        public GenericSafeList(Class<T> elementType, int size) {
            super(elementType);
            ((ArrayList) getBuffer()).ensureCapacity(size);
        }
    }

    @Override
    public T getChild(int i) {
        return (T) super.getChild(i);
    }

    public List<T> getGenericChildren() {
        return (List<T>) getChildren();
    }

    public T[] getChildrenArray() {
        return (T[]) children.getArray();
    }
}
