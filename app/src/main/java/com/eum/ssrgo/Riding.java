package com.eum.ssrgo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by KHR on 2016-10-19.
 */
@IgnoreExtraProperties
public class Riding extends RidingList {

    public Double latitude;
    public Double longitude;
    public String time;


    public Riding() {
    }

    public Riding(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Riding(Double latitude, Double longitude,String time){
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }


//    @Override
//    public int size() {
//        return 0;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false;
//    }
//
//    @Override
//    public boolean contains(Object o) {
//        return false;
//    }
//
//    @NonNull
//    @Override
//    public Iterator<Riding> iterator() {
//        return null;
//    }
//
//    @NonNull
//    @Override
//    public Object[] toArray() {
//        return new Object[0];
//    }
//
//    @NonNull
//    @Override
//    public <T> T[] toArray(T[] ts) {
//        return null;
//    }
//
//    @Override
//    public boolean add(Riding riding) {
//        return false;
//    }
//
//    @Override
//    public boolean remove(Object o) {
//        return false;
//    }
//
//    @Override
//    public boolean containsAll(Collection<?> collection) {
//        return false;
//    }
//
//    @Override
//    public boolean addAll(Collection<? extends Riding> collection) {
//        return false;
//    }
//
//    @Override
//    public boolean addAll(int i, Collection<? extends Riding> collection) {
//        return false;
//    }
//
//    @Override
//    public boolean removeAll(Collection<?> collection) {
//        return false;
//    }
//
//    @Override
//    public boolean retainAll(Collection<?> collection) {
//        return false;
//    }
//
//    @Override
//    public void clear() {
//
//    }
//
//    @Override
//    public Riding get(int i) {
//        return null;
//    }
//
//    @Override
//    public Riding set(int i, Riding riding) {
//        return null;
//    }
//
//    @Override
//    public void add(int i, Riding riding) {
//
//    }
//
//    @Override
//    public Riding remove(int i) {
//        return null;
//    }
//
//    @Override
//    public int indexOf(Object o) {
//        return 0;
//    }
//
//    @Override
//    public int lastIndexOf(Object o) {
//        return 0;
//    }
//
//    @Override
//    public ListIterator<Riding> listIterator() {
//        return null;
//    }
//
//    @NonNull
//    @Override
//    public ListIterator<Riding> listIterator(int i) {
//        return null;
//    }
//
//    @NonNull
//    @Override
//    public List<Riding> subList(int i, int i1) {
//        return null;
//    }
//
//    @Override
//    public Spliterator<Riding> spliterator() {
//        return null;
//    }
}