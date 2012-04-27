package net.imglib2.algorithm.region.localneighborhood;

import java.util.Iterator;

import net.imglib2.AbstractCursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;

/**
 * A cursor that iterates over an arbitrary nD rectangle. For instance, 
 * allow to define neighborhoods such as 3x5x1, resulting in iterating only over X & Y.
 * <p>
 * This class is both an {@link IterableInterval} defined by the neighborhood, and
 * a cursor that iterates over it in raster order.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Mar, 2012
 *
 * @param <T>
 */
public class DomainCursor<T>  extends AbstractCursor<T> implements IterableInterval< T > {
	
	private final int numDimensions;

	private final long[] position, parentPosition;

	private long count = 0;
	
	private long size;
	private final RandomAccess< T > parent;
	/** The extend over which to iterate. Element <i>i</i> of the array refers to the 
	 * <i>i</i>th dimension of the {@link #parent} access. To forbid iterating 
	 * over a dimension, simply enters a span of 0 for this dimension. 
	 * Iteration is done for ± span with respect to the center. */
	private long[] span;
	
	/*
	 * CONSTRUCTOR
	 */
	
	/**
	 * Instantiate a new domain cursor, centered on the given {@link RandomAccess} current location,
	 * and spanning <code>±span[d]</code> for dimension d.
	 * 
	 * @param parent  a {@link RandomAccess} to specify the neighborhood center. Will not be
	 * modified during iteration. 
	 * 
	 * @param span  the extend over which to iterate. Element <i>i</i> of the array refers to the 
	 * <i>i</i>th dimension of the {@link #parent} access. To forbid iterating 
	 * over a dimension, simply enters a span of 0 for this dimension. 
	 * Iteration is done for ± span with respect to the center.
	 */
	public DomainCursor( final RandomAccess<T> parent, final long[] span ) {
		super(parent.numDimensions());
		this.numDimensions = parent.numDimensions();
		this.span = span;
		
		this.position = new long[ parent.numDimensions() ];
		this.parentPosition = new long[ parent.numDimensions() ];
		parent.localize(parentPosition);
		this.size = 1;
		for (int i = 0; i < numDimensions; i++) {
			size *= (2 * span[i] + 1);
		}
		this.parent = parent.copyRandomAccess();
		reset();
	}
	
	/*
	 * METHODS
	 */
	
	@Override
	public long size() { return size; }

	@Override
	public T firstElement() {
		RandomAccess<T> ra = parent.copyRandomAccess();
		ra.setPosition(parentPosition);
		return ra.get();
	}

	@Override
	public Object iterationOrder() {
		return this;
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f ) {
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public double realMin( final int d ) { return parentPosition [ d ] - span[ d ]; }

	@Override
	public void realMin( final double[] min ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			min[ d ] = parentPosition [ d ] - span [ d ];
		}
	}

	@Override
	public void realMin( final RealPositionable min ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			min.setPosition( parentPosition [ d ] - span[ d ], d );
		}
	}

	@Override
	public double realMax( final int d ) { return parentPosition [ d ] + span[ d ]; }
	
	@Override
	public void realMax( final double[] max ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			max[ d ] = parentPosition [ d ] + span[ d ];
		}
	}

	@Override
	public void realMax( final RealPositionable max ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			max.setPosition( parentPosition [ d ] + span[ d ], d );
		}
	}

	@Override
	public int numDimensions() { return numDimensions; }

	@Override
	public Iterator<T> iterator() { return cursor(); }

	@Override
	public long min( final int d ) { return parentPosition [ d ] - span[ d ]; }

	@Override
	public void min( final long[] min ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			min[ d ] = parentPosition [ d ] - span[ d ];
		}
	}

	@Override
	public void min( final Positionable min ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			min.setPosition( parentPosition [ d ] - span[ d ], d );
		}
	}

	@Override
	public long max( final int d ) { return parentPosition [ d ] + span[ d ]; }

	@Override
	public void max( final long[] max ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			max[ d ] = parentPosition [ d ] + span[ d ];
		}
	}

	@Override
	public void max( final Positionable max ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			max.setPosition( parentPosition [ d ] - span[ d ], d );
		}
	}

	@Override
	public void dimensions( final long[] dimensions ) {
		for ( int d = 0; d < numDimensions; ++d ) {
			dimensions[ d ] = 2 * span[ d ] + 1;
		}
	}

	@Override
	public long dimension( final int d ) { return 2 * span[ d ] + 1; }

	@Override
	public DomainCursor<T> cursor() { 
		RandomAccess<T> ra = parent.copyRandomAccess();
		ra.setPosition(parentPosition);
		return new DomainCursor< T >( ra, span ); 
	}

	

	@Override
	public T get() {
		return parent.get();
	}

	@Override
	public void fwd() {
		for (int i=0; i<position.length; ++i) {
			++position[i];
			if (position[i] > parentPosition[i] + span[i]) {
				position[i] = parentPosition[i] - span[i];
				// Continue to advance next dimension
			} else {
				break;
			}
		}
		parent.setPosition(position);
		++count;
	}

	@Override
	public void reset() {
		parent.setPosition(parentPosition);
		for (int i=0; i<position.length; ++i) {
			position[i] = parentPosition[i] -span[i];
		}
		count = 0;
		// Set ready for starting, which needs a call to fwd() which adds one:
		--position[0];
	}

	public void reset(final long[] currentParentPosition) {
		for (int i=0; i<this.parentPosition.length; ++i) {
			this.parentPosition[i] = currentParentPosition[i];
		}
		reset();
	}

	@Override
	public boolean hasNext() {
		return count < size;
	}

	@Override
	public void localize(long[] pos) {
		for (int i=0; i<position.length; ++i) {
			pos[i] = position[i];
		}
	}

	@Override
	public long getLongPosition(int d) {
		return position[ d ];
	}

	@Override
	public DomainCursor<T> copy() {
		return new DomainCursor<T>(parent, span);
	}

	@Override
	public DomainCursor<T> copyCursor() {
		return copy();
	}
	
	
	@Override
	public DomainCursor<T> localizingCursor() { return cursor(); }


}
