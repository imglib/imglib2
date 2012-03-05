package net.imglib2.sampler.special;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;
import net.imglib2.type.Type;

/**
 * Iterate over all pixels in an n-dimensional sphere.
 *  
 * @author Stephan Preibisch <preibisch@mpi-cbg.de> and Stephan Saalfeld <saalfeld@mpi-cbg.de>
 *
 * @param <T>
 */

public class HyperSphereCursor< T extends Type< T > > implements Cursor< T >
{
	final RandomAccessible< T > source;
	final protected Localizable center;
	final protected RandomAccess< T > randomAccess;
	
	final protected long radius;
	final int numDimensions, maxDim;
	
	// the current radius in each dimension we are at
	final long[] r;
	
	// the remaining number of steps in each dimension we still have to go
	final long[] s;
	
	public HyperSphereCursor( final RandomAccessible< T > source, final Localizable center, final long radius )
	{
		this.source = source;
		this.center = center;
		this.radius = radius;
		this.numDimensions = source.numDimensions();
		this.maxDim = numDimensions - 1;
		this.r = new long[ numDimensions ];
		this.s = new long[ numDimensions ];
		this.randomAccess = source.randomAccess();
		
		reset();
	}
	
	public HyperSphereCursor( final HyperSphereCursor< T > cursor )
	{
		this.source = cursor.source;
		this.center = cursor.center;
		this.radius = cursor.radius;
		this.numDimensions = cursor.numDimensions();
		this.maxDim = cursor.maxDim;

		this.r = cursor.r.clone();
		this.s = cursor.s.clone();
		
		this.randomAccess = source.randomAccess();
		this.randomAccess.setPosition( cursor.randomAccess );
	}

	@Override
	public boolean hasNext()
	{
		return s[ maxDim ] > 0; 
	}

	@Override
	public void fwd()
	{
		int d;
		for ( d = 0; d < numDimensions; ++d )
		{
			if ( --s[ d ] >= 0 )
			{
				randomAccess.fwd( d );
				break;
			}
			else
			{
				s[ d ] = r[ d ] = 0;
				randomAccess.setPosition( center.getLongPosition( d ), d );
			}
		}

		if ( d > 0 )
		{
			final int e = d - 1;
			final long rd = r[ d ];
			final long pd = rd - s[ d ];
			
			final long rad = (long)( Math.sqrt( rd * rd - pd * pd ) );
			s[ e ] = 2 * rad;
			r[ e ] = rad;
			
			randomAccess.setPosition( center.getLongPosition( e ) - rad, e );
		}
	}

	@Override
	public void reset()
	{		
		final int maxDim = numDimensions - 1;
		
		for ( int d = 0; d < maxDim; ++d )
		{
			r[ d ] = s[ d ] = 0;
			randomAccess.setPosition( center.getLongPosition( d ), d ); 
		}
		
		randomAccess.setPosition( center.getLongPosition( maxDim ) - radius - 1, maxDim  );
		
		r[ maxDim ] = radius;
		s[ maxDim ] = 1 + 2 * radius;			
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public void localize( final float[] position ) { randomAccess.localize( position ); }

	@Override
	public void localize( final double[] position ) { randomAccess.localize( position ); }

	@Override
	public float getFloatPosition( final int d ) { return randomAccess.getFloatPosition( d ); }

	@Override
	public double getDoublePosition( final int d ) { return randomAccess.getDoublePosition( d ); }

	@Override
	public int numDimensions() { return numDimensions; }

	@Override
	public T get() { return randomAccess.get(); }

	@Override
	public T next() 
	{
		fwd();
		return get();
	}

	@Override
	public void remove() {}

	@Override
	public void localize( final int[] position ) { randomAccess.localize( position ); }

	@Override
	public void localize( final long[] position ) { randomAccess.localize( position ); }

	@Override
	public int getIntPosition( final int d ) { return randomAccess.getIntPosition( d ); }

	@Override
	public long getLongPosition( final int d )  { return randomAccess.getLongPosition( d ); }

	@Override
	public Cursor<T> copyCursor() { return new HyperSphereCursor< T >( this ); }
	
	@Override
	public Sampler<T> copy() { return copyCursor(); }
}