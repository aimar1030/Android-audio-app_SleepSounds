package com.zenlabs.sleepsounds.plistparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by fedoro on 5/12/16.
 */
public class Array extends PListObject implements List<PListObject> {

    private ArrayList<PListObject> data;
    private static final long serialVersionUID = -2673110114913406413L;

    public Array() {
        setType(PListObjectType.ARRAY);
        data = new ArrayList<PListObject>();
    }

    public Array(Collection<? extends PListObject> collection) {
        setType(PListObjectType.ARRAY);
        data = new ArrayList<PListObject>(collection);
    }

    public Array(int capacity) {
        setType(PListObjectType.ARRAY);
        data = new ArrayList<PListObject>(capacity);
    }

    @Override
    public void add(int arg0, PListObject arg1) {
        data.add(arg0, (PListObject) arg1);
    }

    @Override
    public boolean add(PListObject arg0) {
        return data.add((PListObject) arg0);
    }

    @Override
    public boolean addAll(Collection<? extends PListObject> arg0) {
        return data.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends PListObject> arg1) {
        return data.addAll(arg0, arg1);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int lastIndexOf(Object arg0) {
        return data.indexOf(arg0);
    }

    @Override
    public ListIterator<PListObject> listIterator() {
        return data.listIterator();
    }

    @Override
    public ListIterator<PListObject> listIterator(int arg0) {
        return data.listIterator(arg0);
    }

    @Override
    public PListObject remove(int arg0) {
        return data.remove(arg0);
    }

    @Override
    public boolean remove(Object arg0) {
        return data.remove(arg0);
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        return data.remove(arg0);
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return data.retainAll(arg0);
    }

    @Override
    public PListObject set(int arg0, PListObject arg1) {
        return data.set(arg0, arg1);
    }

    @Override
    public List<PListObject> subList(int arg0, int arg1) {
        return data.subList(arg0, arg1);
    }

    @Override
    public Object[] toArray() {
        return data.toArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] toArray(Object[] array) {
        return data.toArray(array);
    }

    public void clear() {
        data.clear();
    }

    public Object clone() {
        return data.clone();
    }

    public boolean contains(Object obj) {
        return data.contains(obj);
    }

    @Override
    public boolean containsAll(@SuppressWarnings("rawtypes") Collection arg0) {
        return data.contains(arg0);
    }

    public boolean equals(Object that) {
        return data.equals(that);
    }

    public PListObject get(int index) {
        return data.get(index);
    }

    public int indexOf(Object object) {
        return data.indexOf(object);
    }

    public Iterator<PListObject> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

}