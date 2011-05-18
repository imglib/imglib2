package net.imglib2.shape;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

/**
 * A ShapeListSeries is sequence of 2D planes
 * without bounds and with double floating-point precision in its XY planes
 * and only integer precision in all other dimensions.
 * The data for each plane is nothing else than an ordered {@link List} of {@link Shape} instances,
 * where the latest {@link #add(Shape, Object)}ed {@link Shape} is queried first,
 * which means that for an image, the latest added Shape paints on top.
 * Each {@link Shape} has an associated T instance, which is returned when requesting
 * a value for a position that intersects that {@link Shape}.
 * 
 * @author Albert Cardona and Stephan Saalfeld
 */
public class ShapeListSeries<T> implements RandomAccessibleInterval<T>
{

	protected final List<List<Shape>> shapeLists = new ArrayList<List<Shape>>();
	protected final List<List<T>> typeLists = new ArrayList<List<T>>();

	final protected long[] dim;
	final protected T background;
	
	public ShapeListSeries(final long[] dim, final T background) {
		assert dim.length > 2 : "Creating ShapeListSeries with less than 3 dimensions.";
		this.dim = dim;
		this.background = background;
		int n = 1;
		for (int i=2; i<dim.length; ++i) {
			n *= dim[i];
		}
		for (int i=0; i<n; ++i) {
			shapeLists.add(new ArrayList<Shape>());
			typeLists.add(new ArrayList<T>());
		}
	}
	
	/** @param position The location. Leave x and y empty, which are defined by the {@link Shape} itself.
	 *  @param s The {@link Shape} to add.
	 *  @param t The value to add for {@param s}. */
	public void add(final Shape s, final T t, final long[] position) {
		final int p = getListIndex(position);
		shapeLists.get(p).add(s);
		typeLists.get(p).add(t);
	}
	
	/** @return a shallow copy of the lists of {@link Shape} instances.
	 *  That is, the {@link Shape} instances themselves are the originals. */
	public synchronized List< List< Shape > > getShapeLists() {
		final List< List< Shape > > sl = new ArrayList< List< Shape > >();
		for (final List< Shape > a : shapeLists)
		{
			sl.add( new ArrayList< Shape >( a ) );
		}

		return sl;
	}

	/** @return a shallow copy of the lists of {@link Type} instances.
	 *  That is, the {@link Type} instances themselves are the originals. */
	public synchronized List< List< T > > getTypeLists() {
		final ArrayList< List< T > > tl = new ArrayList< List< T > >();
		for (final List< T > a : typeLists)
		{
			tl.add( new ArrayList< T >( a ) );
		}

		return tl;
	}

	/** Given a {@param position} array, return the index of the {@link List} of {@link Shape}
	 * instances that corresponds to the X,Y plane of that position. */
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
		final List< Shape > shapeList = shapeLists.get( listIndex );
		for ( int i = shapeList.size() - 1; i > -1; --i )
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
			listIndex = p;
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