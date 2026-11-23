package net.imglib2.view.composite;

import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.View;
import net.imglib2.view.StackView.StackAccessMode;

/**
 * A {@link RealStackCompositeView} converts an array of {@link RealRandomAccessible}s
 * of T into a RealRandomAccessible of {@link Composite} of T.
 * <p>
 * This view has the same dimensionality as each source RealRandomAccessible, and
 * at every position, the i-th value of the composite is the value for the i-th input RealRandomAccessible.
 *
 * @author John Bogovic
 */
public class RealStackCompositeView< T > implements RealRandomAccessible<Composite<T>>, View
{
	protected final int n, nd;

	protected final RealRandomAccessible< T >[] sources;

	private final StackAccessMode stackAccessMode;

	/**
	 * Creates a RealStackCompositeView. Every input {@link RealRandomAccessible} must
	 * have the same dimensionality, and be of the same type.
	 *
	 * @param sources the list of RealRandomAccessibles
	 * @param stackAccessMode the mode
	 */
	public RealStackCompositeView( final RealRandomAccessible< T >[] sources, StackAccessMode stackAccessMode )
	{
		assert( sources.length > 0 );
		this.sources = sources;
		n = sources.length;
		nd = sources[0].numDimensions();
		this.stackAccessMode = stackAccessMode;
	}

	/**
	 * Creates a RealStackCompositeView. Every input {@link RealRandomAccessible} must
	 * have the same dimensionality, and be of the same type. Uses the DEFAULT
	 * {@link StackAccessMode}.
	 *
	 * @param sources the list of RealRandomAccessibles
	 */
	public RealStackCompositeView( final RealRandomAccessible< T >[] sources )
	{
		this( sources, StackAccessMode.DEFAULT );
	}

	@Override
	public int numDimensions()
	{
		return nd;
	}

	@Override
	public RealRandomAccess< Composite<T> > realRandomAccess()
	{
		return stackAccessMode == StackAccessMode.MOVE_ALL_SLICE_ACCESSES ?
			new CompositeRealRandomAccessMoveAll() :
			new CompositeRealRandomAccessDefault();
	}

	@Override
	public RealRandomAccess< Composite<T> > realRandomAccess( RealInterval interval )
	{
		return realRandomAccess();
	}

	public class CompositeRealRandomAccessMoveAll implements RealRandomAccess< Composite<T> >, Composite<T>
	{
		final protected RealRandomAccess< T >[] sourceAccesses;

		@SuppressWarnings( "unchecked" )
		public CompositeRealRandomAccessMoveAll()
		{
			sourceAccesses = new RealRandomAccess[ n ];
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ] = sources[ i ].realRandomAccess();
		}

		@SuppressWarnings( "unchecked" )
		protected CompositeRealRandomAccessMoveAll( final CompositeRealRandomAccessMoveAll other )
		{
			sourceAccesses = new RealRandomAccess[ n ];
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ] = other.sourceAccesses[i].copy();
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public Composite<T> get()
		{
			return this;
		}

		@Override
		public CompositeRealRandomAccessMoveAll copy()
		{
			return new CompositeRealRandomAccessMoveAll( this );
		}

		@Override
		public double getDoublePosition( int d )
		{
			return sourceAccesses[0].getDoublePosition( d );
		}

		@Override
		public void move( float distance, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance, d );
		}

		@Override
		public void move( double distance, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance, d );
		}

		@Override
		public void move( RealLocalizable distance )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance );
		}

		@Override
		public void move( float[] distance )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance );
		}

		@Override
		public void move( double[] distance )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance );
		}

		@Override
		public void setPosition( RealLocalizable position )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position );
		}

		@Override
		public void setPosition( float[] position )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position );
		}

		@Override
		public void setPosition( double[] position )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position );
		}

		@Override
		public void setPosition( float position, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position, d );
		}

		@Override
		public void setPosition( double position, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position, d );
		}

		@Override
		public void fwd( int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].fwd( d );
		}

		@Override
		public void bck( int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].bck( d );
		}

		@Override
		public void move( int distance, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance, d );
		}

		@Override
		public void move( long distance, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance, d );
		}

		@Override
		public void move( Localizable distance )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance );
		}

		@Override
		public void move( int[] distance )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance );
		}

		@Override
		public void move( long[] distance )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].move( distance );
		}

		@Override
		public void setPosition( Localizable position )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position );
		}

		@Override
		public void setPosition( int[] position )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position );
		}

		@Override
		public void setPosition( long[] position )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position );
		}

		@Override
		public void setPosition( int position, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position, d );
		}

		@Override
		public void setPosition( long position, int d )
		{
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ].setPosition( position, d );
		}

		@Override
		public T get( long i )
		{
			return sourceAccesses[ (int)i ].get();
		}
	}

	public class CompositeRealRandomAccessDefault implements RealRandomAccess< Composite<T> >, Composite<T>
	{
		final protected RealRandomAccess< T >[] sourceAccesses;

		protected int currentIndex = 0;

		protected RealRandomAccess< T > currentAccess;

		@SuppressWarnings( "unchecked" )
		public CompositeRealRandomAccessDefault()
		{
			sourceAccesses = new RealRandomAccess[ n ];
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ] = sources[ i ].realRandomAccess();

			currentIndex = 0;
			currentAccess = sourceAccesses[ currentIndex ];
		}

		@SuppressWarnings( "unchecked" )
		protected CompositeRealRandomAccessDefault( final CompositeRealRandomAccessDefault other )
		{
			sourceAccesses = new RealRandomAccess[ n ];
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ] = other.sourceAccesses[i].copy();

			currentIndex = other.currentIndex;
			currentAccess = sourceAccesses[currentIndex];
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public Composite<T> get()
		{
			return this;
		}

		@Override
		public CompositeRealRandomAccessDefault copy()
		{
			return new CompositeRealRandomAccessDefault( this );
		}

		@Override
		public double getDoublePosition( int d )
		{
			return sourceAccesses[0].getDoublePosition( d );
		}

		@Override
		public void move( float distance, int d )
		{
			currentAccess.move( distance, d );
		}

		@Override
		public void move( double distance, int d )
		{
			currentAccess.move( distance, d );
		}

		@Override
		public void move( RealLocalizable distance )
		{
			currentAccess.move( distance );
		}

		@Override
		public void move( float[] distance )
		{
			for ( int i = 0; i < n; i++ )
				currentAccess.move( distance );
		}

		@Override
		public void move( double[] distance )
		{
			currentAccess.move( distance );
		}

		@Override
		public void setPosition( RealLocalizable position )
		{
			currentAccess.setPosition( position );
		}

		@Override
		public void setPosition( float[] position )
		{
			currentAccess.setPosition( position );
		}

		@Override
		public void setPosition( double[] position )
		{
			currentAccess.setPosition( position );
		}

		@Override
		public void setPosition( float position, int d )
		{
			currentAccess.setPosition( position, d );
		}

		@Override
		public void setPosition( double position, int d )
		{
			currentAccess.setPosition( position, d );
		}

		@Override
		public void fwd( int d )
		{
			currentAccess.fwd( d );
		}

		@Override
		public void bck( int d )
		{
			currentAccess.bck( d );
		}

		@Override
		public void move( int distance, int d )
		{
			currentAccess.move( distance, d );
		}

		@Override
		public void move( long distance, int d )
		{
			currentAccess.move( distance, d );
		}

		@Override
		public void move( Localizable distance )
		{
			currentAccess.move( distance );
		}

		@Override
		public void move( int[] distance )
		{
			currentAccess.move( distance );
		}

		@Override
		public void move( long[] distance )
		{
			currentAccess.move( distance );
		}

		@Override
		public void setPosition( Localizable position )
		{
			currentAccess.setPosition( position );
		}

		@Override
		public void setPosition( int[] position )
		{
			currentAccess.setPosition( position );
		}

		@Override
		public void setPosition( long[] position )
		{
			currentAccess.setPosition( position );
		}

		@Override
		public void setPosition( int position, int d )
		{
			currentAccess.setPosition( position, d );
		}

		@Override
		public void setPosition( long position, int d )
		{
			currentAccess.setPosition( position, d );
		}

		@Override
		public T get( long i )
		{
			if( i == currentIndex )
				return currentAccess.get();
			else
			{
				sourceAccesses[(int)i].setPosition(currentAccess);
				currentAccess = sourceAccesses[(int)i];
				currentIndex = (int)i;
				return currentAccess.get();
			}
		}
	}

}
