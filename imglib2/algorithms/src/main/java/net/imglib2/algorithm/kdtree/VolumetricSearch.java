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
 */
package net.imglib2.algorithm.kdtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.imglib2.AbstractLocalizableSampler;
import net.imglib2.AbstractRandomAccess;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.kdtree.node.Leaf;
import net.imglib2.algorithm.kdtree.node.Node;
import net.imglib2.algorithm.kdtree.node.NonLeaf;
/**
 * @author Lee Kamentsky
 *
 *The volumetric search uses a K-D tree to search for all hyper-rectangular nodes
 *that contain a given point.
 *
 *You can use this via the RandomAccessible<List<I>> interface:
 *   Get the RandomAccess<List<I>> interface
 *   Localize it to your point
 *   get() performs the search, returning the list.
 *
 */
public class VolumetricSearch<I extends RealInterval> extends KDTree<VolumetricSearch.RealIntervalLeaf<I>>
    implements RandomAccessible<List<I>> {
	final int numDimensions;
	public VolumetricSearch(List<I> intervals) {
		super(makeLeaves(intervals));
		if (intervals.size() == 0) {
			numDimensions = 0;
		} else {
			numDimensions = intervals.get(0).numDimensions();
		}
	}

	static public <K extends RealInterval> List<RealIntervalLeaf<K>> makeLeaves(List<K> intervals) {
		final ArrayList<RealIntervalLeaf<K>> leaves = new ArrayList<RealIntervalLeaf<K>>();
		for (K interval: intervals) {
			leaves.add(new RealIntervalLeaf<K>(interval));
		}
		return leaves;
	}
	/**
	 * @author Lee Kamentsky
	 * 
	 * The RealIntervalLeaf holds a RealInterval. It turns the interval's
	 * realMin and realMax into a 2N-d vector and this allows us to
	 * search for the rectangle as being on one side of a 2N-d hyperplane
	 * defined by a point in space. 
	 *
	 * @param <I> the type of RealInterval to be stored
	 */
	static public class RealIntervalLeaf<I extends RealInterval> implements Leaf<RealIntervalLeaf<I>> {
		private final I interval;
		public RealIntervalLeaf(final I interval) {
			this.interval = interval;
		}
		
		
		/**
		 * @return the interval stored within the leaf.
		 */
		I getInterval() {
			return interval;
		}
		/* (non-Javadoc)
		 * @see net.imglib2.algorithm.kdtree.node.Node#isLeaf()
		 */
		@Override
		public boolean isLeaf() {
			return true;
		}

		/* (non-Javadoc)
		 * @see net.imglib2.algorithm.kdtree.node.Leaf#get(int)
		 */
		@Override
		public float get(int k) {
			if (k < interval.numDimensions()) return (float)interval.realMin(k);
			return (float)interval.realMax(k - interval.numDimensions());
		}

		/* (non-Javadoc)
		 * @see net.imglib2.algorithm.kdtree.node.Leaf#distanceTo(net.imglib2.algorithm.kdtree.node.Leaf)
		 */
		@Override
		public float distanceTo(RealIntervalLeaf<I> other) {
			double accumulator = 0;
			for (int i=0; i<interval.numDimensions(); i++) {
				double manhattan = interval.realMin(i) - other.interval.realMin(i);
				accumulator += manhattan * manhattan;
				manhattan = interval.realMax(i) - other.interval.realMax(i);
				accumulator += manhattan * manhattan;
			}
			return (float)Math.sqrt(accumulator);
		}

		/* (non-Javadoc)
		 * @see net.imglib2.algorithm.kdtree.node.Leaf#getNumDimensions()
		 */
		@Override
		public int getNumDimensions() {
			return interval.numDimensions() * 2;
		}

		/* (non-Javadoc)
		 * @see net.imglib2.algorithm.kdtree.node.Leaf#createArray(int)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public RealIntervalLeaf<I>[] createArray(int n) {
			return new RealIntervalLeaf[n];
		}

		/* (non-Javadoc)
		 * @see net.imglib2.algorithm.kdtree.node.Leaf#getEntry()
		 */
		@Override
		public RealIntervalLeaf<I> getEntry() {
			return this;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("RealIntervalLeaf(%s)", interval.toString());
		}
	}
	
	/**
	 * Find all intervals that contain a given point
	 * 
	 * @param pt the point in question
	 * @return list of all intervals containing the point.
	 */
	public List<I> find(RealLocalizable pt) {
		final double [] position = new double[numDimensions];
		pt.localize(position);
		final LinkedList<I> list = new LinkedList<I>();
		final Stack<Node<RealIntervalLeaf<I>>> toDo = new Stack<Node<RealIntervalLeaf<I>>>();
		final Integer [] kk = new Integer [numDimensions * 2];
		for (int i=0; i<kk.length; i++) {
			kk[i] = i;
		}
		final Stack<Integer> toDoK = new Stack<Integer>();
		toDo.push(this.getRoot());
		toDoK.push(kk[0]);
		while(toDo.size() > 0) {
			final Node<RealIntervalLeaf<I>> node = toDo.pop();
			final int k = toDoK.pop();
			if (node == null) continue;
			if (node.isLeaf()) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				final RealIntervalLeaf<I> leaf = (RealIntervalLeaf) node;
				boolean good = true;
				for (int i=0; i<numDimensions; i++) {
					if ((position[i] < leaf.getInterval().realMin(i)) ||
						(position[i] > leaf.getInterval().realMax(i))) {
						good = false;
						break;
					}
				}
				if (good)
					list.add(leaf.getInterval());
			} else {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				final NonLeaf<RealIntervalLeaf<I>> branch = (NonLeaf)node;
				final Integer nextK = kk[(k+1) % (numDimensions * 2)]; 
				if (k < numDimensions) {
					// The coordinate is a minimum. 
					// If it is greater than the position, only take the left branch
					// which still could be lower.
					if (branch.coordinate > position[k]) {
						toDo.push(branch.left);
						toDoK.push(nextK);
						continue;
					}
				} else if (branch.coordinate < position[k - numDimensions]) {
					toDo.push(branch.right);
					toDoK.push(nextK);
					continue;
				}
				toDo.push(branch.left);
				toDoK.push(nextK);
				toDo.push(branch.right);
				toDoK.push(nextK);
			}
		}
		
		return list;
	}

	@Override
	public int numDimensions() {
		return numDimensions;
	}

	@Override
	public AbstractRandomAccess<List<I>> randomAccess() {
		return new AbstractRandomAccess<List<I>>(numDimensions) {

			@Override
			public void fwd(int d) {
				this.position[d]++;
			}

			@Override
			public void bck(int d) {
				this.position[d]--;
			}

			@Override
			public void move(long distance, int d) {
				this.position[d] += distance;
			}

			@Override
			public void setPosition(int[] position) {
				for (int i=0; i<numDimensions; i++) {
					this.position[i] = position[i];
				}
			}

			@Override
			public void setPosition(long[] position) {
				for (int i=0; i<numDimensions; i++) {
					this.position[i] = position[i];
				}
			}

			@Override
			public void setPosition(long position, int d) {
				this.position[d] = position;
			}

			@Override
			public List<I> get() {
				return find(this);
			}

			@Override
			public AbstractRandomAccess<List<I>> copy() {
				AbstractRandomAccess<List<I>> myCopy = randomAccess();
				myCopy.setPosition(this);
				return myCopy;
			}

			@Override
			public AbstractRandomAccess<List<I>> copyRandomAccess() {
				return copy();
			}};
	}

	@Override
	public RandomAccess<List<I>> randomAccess(Interval interval) {
		return randomAccess();
	}

}
