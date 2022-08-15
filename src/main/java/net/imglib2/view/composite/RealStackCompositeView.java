package net.imglib2.view.composite;

import java.util.Arrays;

import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.View;

/**
 * A {@link RealStackCompositeView} converts an array of {@link RealRandomAccessible}s
 * of T into a RealRandomAccessible of {@link Composite} of T.
 * <p>
 * This view has the same dimensionality as each source RealRandomAccessible, and
 * at every position, the i-th value of the composite is the value for the i-th input source.
 *
 * @author John Bogovic
 */
public class RealStackCompositeView< T > implements RealRandomAccessible<Composite<T>>, View
{
	protected final int n;

	protected final int nd;

	protected final RealRandomAccessible< T >[] sources;

	protected final RealRandomAccess< Composite< T > > access;

	/**
	 * Creates a RealStackCompositeView. Every input {@link RealRandomAccessible} must
	 * have the same dimensionality, and be of the same type.
	 * 
	 * @param sources the list of RealRandomAccessibles
	 */
	@SuppressWarnings( "unchecked" )
	public RealStackCompositeView( final RealRandomAccessible< T >... sources )
	{
		assert( sources.length > 0 );
		this.sources = sources;
		n = sources.length;
		nd = sources[0].numDimensions();
		access = new CompositeRealRandomAccess();
	}

	public class CompositeRealRandomAccess implements RealRandomAccess< Composite< T > >, Composite< T >
	{
		final protected RealRandomAccess< T >[] sourceAccesses;

		@SuppressWarnings( "unchecked" )
		public CompositeRealRandomAccess()
		{
			sourceAccesses = new RealRandomAccess[ n ];
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ] = sources[ i ].realRandomAccess();
		}

		@SuppressWarnings( "unchecked" )
		protected CompositeRealRandomAccess( final CompositeRealRandomAccess other )
		{
			sourceAccesses = new RealRandomAccess[ n ];
			for ( int i = 0; i < n; i++ )
				sourceAccesses[ i ] = other.sourceAccesses[i].copy();
		}

		@Override
		public int numDimensions()
		{
			return nd;
		}

		@Override
		public Composite< T > get()
		{
			return this;
		}

		@Override
		public CompositeRealRandomAccess copy()
		{
			return new CompositeRealRandomAccess( this );
		}

		@Override
		public double getDoublePosition( int d )
		{
			return sourceAccesses[0].getDoublePosition( d );
		}

		@Override
		public void move( float distance, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance, d ) );
		}

		@Override
		public void move( double distance, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance, d ) );
		}

		@Override
		public void move( RealLocalizable distance )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance ) );
		}

		@Override
		public void move( float[] distance )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance ) );
		}

		@Override
		public void move( double[] distance )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance ) );
		}

		@Override
		public void setPosition( RealLocalizable position )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position ) );
		}

		@Override
		public void setPosition( float[] position )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position ) );
		}

		@Override
		public void setPosition( double[] position )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position ) );
		}

		@Override
		public void setPosition( float position, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position, d ) );
		}

		@Override
		public void setPosition( double position, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position, d ) );
		}

		@Override
		public void fwd( int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.fwd( d ) );
		}

		@Override
		public void bck( int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.bck( d ) );
		}

		@Override
		public void move( int distance, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance, d ) );
		}

		@Override
		public void move( long distance, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance, d ) );
		}

		@Override
		public void move( Localizable distance )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance ) );
		}

		@Override
		public void move( int[] distance )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance ) );
		}

		@Override
		public void move( long[] distance )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.move( distance ) );
		}

		@Override
		public void setPosition( Localizable position )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position ) );
		}

		@Override
		public void setPosition( int[] position )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position ) );
		}

		@Override
		public void setPosition( long[] position )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position ) );
		}

		@Override
		public void setPosition( int position, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position, d ) );
		}

		@Override
		public void setPosition( long position, int d )
		{
			Arrays.stream( sourceAccesses ).forEach( x -> x.setPosition( position, d ) );
		}

		@Override
		public T get( long i )
		{
			return sourceAccesses[ (int)i ].get();
		}
	}

	@Override
	public int numDimensions()
	{
		return nd;
	}

	@Override
	public RealRandomAccess< Composite< T > > realRandomAccess()
	{
		return access;
	}

	@Override
	public RealRandomAccess< Composite< T > > realRandomAccess( RealInterval interval )
	{
		return realRandomAccess();
	}

}
