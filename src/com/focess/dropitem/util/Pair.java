package com.focess.dropitem.util;

public class Pair<E,T> {
	
	private E first;
	
	private T second;
	
	private Pair(E first,T second) {
		this.first = first;
		this.second = second;
	}
	
	public static<E,T> Pair<E,T> of(E a,T b) {
		return new Pair<E,T>(a,b);
	}
	
	public E getKey() {
		return this.first;
	}
	
	public T getValue() {
		return this.second;
	}

}
