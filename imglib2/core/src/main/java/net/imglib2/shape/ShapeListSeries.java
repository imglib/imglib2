package net.imglib2.shape;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

public class ShapeListSeries<T> implements RandomAccessibleInterval<T>
{

	protected final ArrayList<ArrayList<Shape>> shapeLists = new ArrayList<ArrayList<Shape>>();
	protected final ArrayList<ArrayList<T>> typeLists = new ArrayList<ArrayList<T>>();

	final protected long[] dim;
	final protected T background;
	
	public ShapeListSeries(final long[] dim, final T background) {
		assert dim.length > 2 : "Creating ShapeListSeries with less than 3 dimensions.";
		this.dim = dim;
		this.background = background;
	}
	
	/** @return a shallow copy of the lists of {@link Shape} instances.
	 *  That is, the {@link Shape} instances themselves are the originals. */
	public synchronized ArrayList< ArrayList< Shape > > getShapeLists() {
		final ArrayList< ArrayList< Shape > > sl = new ArrayList< ArrayList< Shape > >();
		for (final ArrayList< Shape > a : shapeLists)
		{
			sl.add( new ArrayList< Shape >( a ) );
		}

		return sl;
	}

	/** @return a shallow copy of the lists of {@link Type} instances.
	 *  That is, the {@link Type} instances themselves are the originals. */
	public synchronized ArrayList< ArrayList< T > > getTypeLists() {
		final ArrayList< ArrayList< T > > tl = new ArrayList< ArrayList< T > >();
		for (final ArrayList< T > a : typeLists)
		{
			tl.add( new ArrayList< T >( a ) );
		}

		return tl;
	}

	/** */
	public final int getListIndex(final long[] position) {
		long p = 0;
		long f = 1;
		for ( int d = 2; d < position.length; ++d )
		{
			p += f * position[ d ];
			f *= dim[ d ];
		}
		return (int)p;
	}

	@Override
	public ShapeListSeriesRandomAccess randomAccess() {
		return new ShapeListSeriesRandomAccess();
	}

	@Override
	public RandomAccess<T> randomAccess(Interval interval) {
		return randomAccess();
	}

	@Override
	public int numDimensions() {
		return dim.length;
	}

	@Override
	public long min(int d) {
		return 0;
	}

	@Override
	public void min(long[] min) {
		Arrays.fill(min, 0);
	}

	/** Returns the maximum possible index, which is dimension[d] - 1. */
	@Override
	public long max(int d) {
		return dim[d] -1;
	}

	@Override
	public void max(long[] max) {
		for (int i=0; i<dim.length; ++i) {
			max[i] = dim[i] -1;
		}
	}

	@Override
	public void dimensions(long[] dimensions) {
		System.arraycopy(dim, 0, dimensions, 0, dim.length);
	}

	@Override
	public long dimension(int d) {
		return dim[d];
	}

	@Override
	public double realMin(int d) {
		return 0;
	}

	@Override
	public void realMin(double[] min) {
		Arrays.fill(min, 0);
	}

	@Override
	public double realMax(int d) {
		return max(d);
	}

	@Override
	public void realMax(double[] max) {
		for (int i=0; i<dim.length; ++i) {
			max[i] = dim[i] -1;
		}
	}

	public T getShapeType( final double x, final double y, final int listIndex ) {
		final ArrayList< Shape > shapeList = shapeLists.get( listIndex );
		for ( int i = shapeList.size() - 1; i >= 0; --i )
		{
			if ( shapeList.get( i ).contains( x, y ) )
				return typeLists.get( listIndex ).get( i );
		}
		return background;
	}
	
	protected class ShapeListSeriesRandomAccess extends AbstractRandomAccess<T>
	{
		protected int listIndex = 0;
		
		public ShapeListSeriesRandomAccess() {
			super(dim.length);
		}

		private final void updateListIndex() {
			int p = 0;
			int f = 1;
			for ( int d = 2; d < position.length; ++d )
			{
				p += f * position[ d ];
				f *= dim[ d ];
			}
		}
		
		@Override
		public T get() {
			return getShapeType( position[0], position[1], listIndex );
		}

		@Override
		public ShapeListSeriesRandomAccess copy() {
			final ShapeListSeriesRandomAccess s = new ShapeListSeriesRandomAccess();
			System.arraycopy(this.position, 0, s.position, 0, s.position.length);
			return s;
		}

		@Override
		public void fwd( int d) {
			++position[d];
			if (d > 1) updateListIndex();
		}

		@Override
		public void bck(int d) {
			--position[d];
			if (d > 1) updateListIndex();
		}

		@Override
		public void move(long distance, int d) {
			position[d] += distance;
			if (d > 1) updateListIndex();
		}

		@Override
		public void setPosition(int[] position) {
			for (int i=0; i<this.position.length; ++i)
				this.position[i] = position[i];

			updateListIndex();
		}

		@Override
		public void setPosition(long[] position) {
			for (int i=0; i<this.position.length; ++i)
				this.position[i] = position[i];
			
			updateListIndex();
		}

		@Override
		public void setPosition(long position, int d) {
			this.position[d] = position;
			if (d > 1) updateListIndex();
		}

		@Override
		public ShapeListSeriesRandomAccess copyRandomAccess() {
			return copy();
		}
	}
}