package com.focess.dropitem.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class Array<E> implements Iterable<E>, Cloneable {

	private enum ArrayList {
		Array(2), ArrayList(1), Element(0),;

		private final int id;

		private ArrayList(final int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}
	}

	private class Itr implements Iterator<E> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			return this.index != Array.this.size;
		}

		@Override
		public E next() {
			if (!this.hasNext())
				throw new NullPointerException();
			final E temp = (E) Array.this.value[this.index];
			this.index++;
			return temp;
		}

	}

	private final ArrayList arrayList;
	private Object befor;
	private Class<?> Classz;
	private boolean isMap = false;
	private boolean isMore = false;
	private Object[] map = new Object[0];

	private int size = 0;

	private Object[] value;

	public Array() {
		this.arrayList = ArrayList.Element;
	}

	public Array(final Array<E> array) {
		this.value = array.value;
		this.size = array.size;
		this.arrayList = array.arrayList;
		this.befor = array.befor;
	}

	public Array(final boolean isMap) {
		this.arrayList = ArrayList.Element;
		this.setMap(isMap);
	}

	public Array(final Collection<E> array) {
		final Array<E> temp = new Array<>();
		for (final E e : array)
			temp.add(e);
		this.value = temp.value;
		this.size = temp.size;
		this.arrayList = temp.arrayList;
		this.befor = temp.befor;
	}

	public Array(final E[] array) {
		this.value = array;
		if (array != null)
			this.size = array.length;
		else
			this.size = 0;
		this.arrayList = ArrayList.Array;
		this.befor = array;
	}

	public Array(final java.util.ArrayList<E> array) {
		final Object[] temp = new Object[array.size()];
		for (int i = 0; i < array.size(); i++)
			temp[i] = array.get(i);
		this.value = temp;
		this.size = array.size();
		this.arrayList = ArrayList.ArrayList;
		this.befor = array;
	}

	public int add(final E e) {
		this.check();
		if (this.size > 0) {
			final Object[] temp = this.value;
			this.value = new Object[this.size + 1];
			for (int i = 0; i < temp.length; i++)
				this.value[i] = temp[i];
			this.value[this.size] = e;
			this.size++;
		} else {
			this.value = new Object[this.size + 1];
			this.value[this.size] = e;
			this.size++;
		}
		return this.size - 1;
	}

	public void add(final int index, final E e) {
		this.check();
		if (index >= this.size) {
			final Object[] temp = new Object[index + 1];
			for (int i = 0; i < this.size; i++)
				temp[i] = this.value[i];
			temp[index] = e;
			this.value = temp;
			this.size = index + 1;
		} else
			this.value[index] = e;
	}

	public void addAll(final int index, final Collection<E> collection) {
		if (index >= this.size) {
			int number = index - this.size;
			final Array<E> array = new Array<>(this.toArray());
			for (; number > 0; number--)
				array.add(null);
			final Object[] temp = new Array<>(collection).toArray();
			for (final Object obj : temp)
				array.add((E) obj);
			this.value = array.value;
			this.size = array.size;
		} else {
			final Array<E> array = new Array<>(this.toArray());
			final Object[] temp = new Array<>(collection).toArray();
			int number = 0;
			for (int i = index; i < (index + collection.size()); i++) {
				array.add(i, (E) temp[number]);
				number++;
			}
			this.value = array.value;
			this.size = array.size;
		}

	}

	private void check() {
		/*
		 * if (arrayList.getId() == 0) {
		 *
		 * }
		 */
		if (this.value != null) {
			if (this.value.length != this.size)
				throw new IndexOutOfBoundsException(this.value.length + " is not " + this.size + ".");
		} else if (this.size != 0)
			throw new IndexOutOfBoundsException(this.size + " > 0.");
	}

	public void clear() {
		this.size = 0;
		this.value = null;
	}

	@Override
	public Array<E> clone() {
		return new Array<>(this);
	}

	public boolean contains(final Object e) {
		return this.indexOf(e) != -1;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final Array<?> other = (Array<?>) obj;
		if (this.Classz == null) {
			if (other.Classz != null)
				return false;
		} else if (this.Classz != other.Classz)
			return false;
		if (this.arrayList != other.arrayList)
			return false;
		if (this.befor == null) {
			if (other.befor != null)
				return false;
		} else if (!this.befor.equals(other.befor))
			return false;
		if (!Arrays.equals(this.map, other.map))
			return false;
		if (this.size != other.size)
			return false;
		if (!Arrays.equals(this.value, other.value))
			return false;
		return true;
	}

	private boolean fsort(final E e, final E e2) {
		return e2.hashCode() < e.hashCode();
	}

	public E get(final int index) {
		this.check();
		if (!(index < this.size))
			throw new IndexOutOfBoundsException(index + " > " + (this.size - 1));
		return (E) this.value[index];
	}

	public java.util.ArrayList<E> getArrayList() {
		final java.util.ArrayList<E> array = new java.util.ArrayList<>();
		for (final E e : this)
			array.add(e);
		return array;
	}

	public E[] getBeforArray() {
		if (this.arrayList.getId() != 2)
			throw new ClassCastException("It's not an array.");
		return (E[]) this.befor;
	}

	public java.util.ArrayList<E> getBeforArrayList() {
		if (this.arrayList.getId() != 1)
			throw new ClassCastException("It's not ArrayList.");
		return (java.util.ArrayList<E>) this.befor;
	}

	public Object[] getValue(final E e) {
		final Array<Object> temp = new Array<>();
		for (int i = 0; i < this.size; i++)
			if (((E) this.value[i]).equals(e))
				temp.add(this.map[i]);
		return temp.toArray();
	}

	public Object getValue(final int index) {
		this.Mapcheck(index);
		return new Array<>(this.map).get(index);
	}

	public Class<?> getValueType() {
		this.MapTypecheck(false);
		this.Mapcheck(-1);
		return this.Classz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (this.Classz == null ? 0 : this.Classz.hashCode());
		result = (prime * result) + (this.arrayList == null ? 0 : this.arrayList.hashCode());
		result = (prime * result) + (this.befor == null ? 0 : this.befor.hashCode());
		result = (prime * result) + Arrays.hashCode(this.map);
		result = (prime * result) + this.size;
		result = (prime * result) + Arrays.hashCode(this.value);
		return result;
	}

	public int indexOf(final E e, final int index) {
		if (!(index < this.size))
			throw new IndexOutOfBoundsException(index + " >= " + this.size);
		for (int i = index; i < this.size; i++)
			if (((E) this.value[i]).equals(e))
				return i;
		return -1;
	}

	public int indexOf(final Object e) {
		for (int i = 0; i < this.size; i++)
			if (((E) this.value[i]).equals(e))
				return i;
		return -1;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public boolean isMap() {
		return this.isMap;
	}

	public boolean isMapType() {
		return this.isMore;
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}

	public <V> Array<V> Map(final java.util.Map<E, V> map) {
		if (map.size() != this.size)
			throw new RuntimeException("Map " + map.size() + " != " + this.size);
		if (!this.isMap)
			return null;
		final Object[] temp = new Array<>(map.values()).toArray();
		if (temp == null)
			throw new NullPointerException("Values are null.");
		this.Classz = temp[0].getClass();
		this.map = new Array<>(map.values()).toArray();
		return new Array<>(map.values());
	}

	private void Mapcheck(final int index) {
		this.check();
		if (!(index < this.size))
			throw new IndexOutOfBoundsException(index + " > " + (this.size - 1));
		if (!this.isMap)
			throw new NullPointerException("Map is not found.");
		if (this.map == null)
			throw new NullPointerException("Map is null.");
	}

	private void MapTypecheck(final boolean is) {
		if (is != this.isMore)
			throw new NullPointerException("Map Type Error.");
	}

	public boolean remove(final E e) {
		this.check();
		int number = 0;
		for (int i = 0; i < this.value.length; i++)
			if (this.value[i] == e) {
				number++;
				this.value[i] = null;
			}
		if (number > 0) {
			final Object[] temp = new Object[this.size - number];
			int index = 0;
			for (final Object element : this.value) {
				if (element != null)
					temp[index] = element;
				else
					index--;
				index++;
			}
			this.value = temp;
			this.size = this.value.length;
			return true;
		} else
			return false;
	}

	public boolean remove(final int index) {
		this.check();
		if (index > this.size)
			return false;
		else {
			final Array<E> temp = new Array<>();
			for (int i = 0; i < this.value.length; i++)
				if (i == index)
					continue;
				else
					temp.add((E) this.value[i]);
			this.value = temp.toArray();
			this.size = this.value.length;
			return true;
		}
	}

	public boolean replace(final E e, final E e2) {
		boolean isreplace = false;
		for (int i = 0; i < this.size; i++)
			if (((E) this.value[i]).equals(e)) {
				this.value[i] = e2;
				isreplace = true;
			}
		return isreplace;
	}

	public boolean replace(final int index, final E e, final E e2) {
		if (!(index < this.size))
			throw new IndexOutOfBoundsException(index + " >= " + this.size);
		boolean isreplace = false;
		for (int i = index; i < this.size; i++)
			if (((E) this.value[i]).equals(e)) {
				this.value[i] = e2;
				isreplace = true;
			}
		return isreplace;
	}

	public boolean setMap(final boolean setMap) {
		if (!this.isMap)
			this.map = new Object[0];
		return this.isMap = setMap;
	}

	public boolean setMapType(final boolean is) {
		this.Mapcheck(-1);
		if (is)
			this.Classz = null;
		return this.isMore = is;
	}

	public <V> V setValue(final int index, final V v) {
		this.Mapcheck(index);
		if (this.isMore) {
			final Array<Object> array = new Array<>(this.map);
			array.add(index, v);
			this.map = array.toArray();
		}
		if (this.Classz != null)
			if (v.getClass().getName().equals(this.Classz.getName())) {
				final Array<Object> array = new Array<>(this.map);
				array.add(index, v);
				this.map = array.toArray();
			} else
				throw new ClassCastException(
						"Class " + v.getClass().getName() + " is not " + this.Classz.getName() + " Class.");
		else {
			this.Classz = v.getClass();
			final Array<Object> array = new Array<>(this.map);
			array.add(index, v);
			this.map = array.toArray();
		}
		return v;
	}

	public void setValueType(final Class<?> classz) {
		this.MapTypecheck(false);
		this.Mapcheck(-1);
		if (this.Classz != null) {
			if (classz.equals(this.Classz))
				return;
			if (this.Classz.isAssignableFrom(classz))
				this.Classz = classz;
			if (new Array<>(this.map).size() == 0)
				this.Classz = classz;
			else
				throw new ClassCastException("Class " + this.Classz + " cannot turn to " + classz + " Class.");
		} else
			this.Classz = classz;

	}

	public int size() {
		return this.size;
	}

	public void sort(final boolean deep) {
		for (int j = 0; j < this.size; j++)
			for (int k = 0; k < (this.size - 1); k++)
				if (deep) {
					if (!this.zsort((E) this.value[k], (E) this.value[k + 1])) {
						final E temp = (E) this.value[k];
						this.value[k] = this.value[k + 1];
						this.value[k + 1] = temp;
					}
				} else if (!this.fsort((E) this.value[k], (E) this.value[k + 1])) {
					final E temp = (E) this.value[k];
					this.value[k] = this.value[k + 1];
					this.value[k + 1] = temp;
				}
	}

	public E[] toArray() {
		return (E[]) this.value;
	}

	public E[] toArray(final E[] e) {
		final E[] temp = (E[]) java.lang.reflect.Array.newInstance(e.getClass().getComponentType(), e.length);
		if (e.length < this.size)
			for (int i = 0; i < e.length; i++)
				temp[i] = (E) this.value[i];
		else
			for (int i = 0; i < this.size; i++)
				temp[i] = (E) this.value[i];
		return temp;
	}

	public List<E> toArrayList() {
		final java.util.ArrayList<E> arrayList = new java.util.ArrayList<>();
		if (this.value == null)
			return arrayList;
		for (final Object e : this.value)
			arrayList.add((E) e);
		return arrayList;
	}

	@Override
	public String toString() {
		String context = "";
		boolean isf = true;
		for (final Object e : this.value)
			if (isf) {
				context = e.toString();
				isf = false;
			} else
				context = context + "," + e.toString();
		if (this.isMap) {
			String map = "";
			boolean isff = true;
			for (final Object o : this.map)
				if (isff) {
					map = o.toString();
					isff = false;
				} else
					map = map + "," + o.toString();
			if (!this.isMore)
				return "[ { value: " + context + " } ," + " { type: " + this.arrayList.toString() + " } ,"
						+ "{ maptype: " + this.Classz.getName() + " , mapvalue: " + map + " } " + "]";
			else
				return "[ { value: " + context + " } ," + " { type: " + this.arrayList.toString() + " } ,"
						+ "{ mapvalue: " + map + " } " + "]";
		} else
			return "[ { value: " + context + " } ," + " { type: " + this.arrayList.toString() + " } , { other: "
					+ this.befor.toString() + " } ]";
	}

	private boolean zsort(final E e, final E e2) {
		return e.hashCode() < e2.hashCode();
	}
}
