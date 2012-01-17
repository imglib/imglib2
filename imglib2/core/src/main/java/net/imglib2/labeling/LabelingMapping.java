/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Lee Kamentsky
 *
 */
package net.imglib2.labeling;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * The LabelingMapping maps a set of labelings of a pixel to an index
 * value which can be more compactly stored than the set of labelings.
 * The service it provides is an "intern" function that supplies a
 * canonical object for each set of labelings in a container.
 * 
 * For example, say pixels are labeled with strings and a particular
 * pixel is labeled as belonging to both "Foo" and "Bar" and this
 * is the first label assigned to the container. The caller will ask
 * for the index of { "Foo", "Bar" } and get back the number, "1".
 * LabelingMapping will work faster if the caller first interns
 * { "Foo", "Bar" } and then requests the mapping of the returned object.
 *  
 * @param <T>
 * @param <N>
 */
public class LabelingMapping<T extends Comparable<T>, N extends Number> {
	Constructor<N> constructor;
	N instance;
	final List<T> theEmptyList;
	
	@SuppressWarnings("unchecked")
	public LabelingMapping(N srcInstance) {
		instance = srcInstance;
		Class<? extends Number> c = srcInstance.getClass();
		try {
			constructor = (Constructor<N>) c.getConstructor(new Class [] { String.class });
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new AssertionError(e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new AssertionError("Number class cannot be constructed from a string");
		}
		List<T> background = Collections.emptyList(); 
		theEmptyList = intern(background);
	}
	private static class InternedList<T1 extends Comparable<T1>, N extends Number> implements List<T1>
	{
		private final List<T1> value;
		final N index;
		final LabelingMapping<T1,N> owner;
		public InternedList(List<T1> src, N index, LabelingMapping<T1,N> owner) {
			value = Collections.unmodifiableList(src);
			this.index = index;
			this.owner = owner;
		}

		@Override
		public int size() {
			return value.size();
		}

		@Override
		public boolean isEmpty() {
			return value.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return value.contains(o);
		}

		@Override
		public Iterator<T1> iterator() {
			return value.iterator();
		}

		@Override
		public Object[] toArray() {
			return value.toArray();
		}

		@Override
		public boolean add(T1 e) {
			return value.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return value.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return value.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends T1> c) {
			return value.addAll(c);
		}

		@Override
		public boolean addAll(int i, Collection<? extends T1> c) {
			return value.addAll(i, c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return value.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return value.retainAll(c);
		}

		@Override
		public void clear() {
			value.clear();
		}

		@Override
		public T1 get(int i) {
			return value.get(i);
		}

		@Override
		public T1 set(int index, T1 element) {
			return value.set(index, element);
		}

		@Override
		public void add(int i, T1 element) {
			value.add(i, element);
		}

		@Override
		public T1 remove(int i) {
			return value.remove(i);
		}

		@Override
		public int indexOf(Object o) {
			return value.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return value.lastIndexOf(o);
		}

		@Override
		public ListIterator<T1> listIterator() {
			return value.listIterator();
		}

		@Override
		public ListIterator<T1> listIterator(int i) {
			return value.listIterator(i);
		}

		@Override
		public List<T1> subList(int fromIndex, int toIndex) {
			return value.subList(fromIndex, toIndex);
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return value.toArray(a);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return value.hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof InternedList) {
				@SuppressWarnings("rawtypes")
				InternedList iobj = (InternedList)obj;
				return value.equals(iobj.value);
			}
			return value.equals(obj);
		}
	}

	protected Map<List<T>, InternedList<T, N>> internedLists = 
		new HashMap<List<T>, InternedList<T, N>>();
	protected ArrayList<InternedList<T,N>> listsByIndex = 
		new ArrayList<InternedList<T, N>>();
	
	public List<T> emptyList() {
		return theEmptyList;
	}
	/**
	 * Return the canonical list for the given list
	 * 
	 * @param src
	 * @return
	 */
	public List<T> intern(List<T> src) {
		return internImpl(src);
	}
	
	@SuppressWarnings("unchecked")
	private InternedList<T,N> internImpl(List<T> src) {
		InternedList<T,N> interned;
		if (src instanceof InternedList) {
			interned = (InternedList<T,N>)src;
			if (interned.owner == this)
				return interned;
		}
		List<T> copy = new ArrayList<T>(src);
		Collections.sort(copy);
		interned = internedLists.get(copy);
		if (interned == null) {
			int intIndex = listsByIndex.size();
			N index;
			try {
				index = constructor.newInstance(Integer.toString(intIndex));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new AssertionError(e.getMessage());
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new AssertionError(e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new AssertionError(e.getMessage());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				if (e.getTargetException() instanceof NumberFormatException) {
					throw new AssertionError(String.format("Too many labels (or types of multiply-labeled pixels): %d maximum", intIndex));
				}
				throw new AssertionError(e.getMessage());
			}
			interned = new InternedList<T, N>(copy, index, this);
			listsByIndex.add(interned);
			internedLists.put(interned, interned);
		}
		return interned;
	}

	public List<T> intern(T [] src) {
		return intern(Arrays.asList(src));
	}
	public N indexOf(List<T> key) {
		InternedList<T,N> interned = internImpl(key); 
		return interned.index;
	}
	public N indexOf(T [] key) {
		return indexOf(intern(key));
	}
	
	public List<T> listAtIndex(int index) {
		return listsByIndex.get(index);
	}
	/**
	 * @return the labels defined in the mapping.
	 */
	public List<T> getLabels() {
		HashSet<T> result = new HashSet<T>();
		for (InternedList<T,N> inst: listsByIndex) {
			for (T label: inst) {
				result.add(label);
			}
		}
		return new ArrayList<T>(result);
	}
}
