package net.imglib2.algorithm.region.localneighborhood;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPositionable;
import net.imglib2.util.Util;

public class LocalNeighborhood< T > implements IterableInterval< T >
{
	final int numDimensions;
	final long size;
	
	final long[] center;
	
	final RandomAccessible< T > source;
	
	public LocalNeighborhood( final RandomAccessible< T > source, final Localizable center )
	{
		this.numDimensions = source.numDimensions();
		this.center = new long[ numDimensions ];
		center.localize( this.center );
		
		this.size = Util.pow( 3, numDimensions ) - 1;
		
		this.source = source;
	}
	
	public void updateCenter( final long[] center )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.center[ d ] = center[ d ];
	}
	
	public void updateCenter( final Localizable center )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.center[ d ] = center.getLongPosition( d );
	}
	
	@Override
	public long size() { return size; }

	@Override
	public T firstElement() 
	{
		final LocalNeighborhoodCursor< T > cursor = new LocalNeighborhoodCursor< T >( source, center );
		cursor.fwd();
		return cursor.get();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval<?> f ) { return false; }

	@Override
	public double realMin( final int d ) { return center[ d ] - 1; }

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min[ d ] = center[ d ] - 1;
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min.setPosition( center[ d ] - 1, d );
	}

	@Override
	public double realMax( final int d ) { return center[ d ] + 1; }

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max[ d ] = center[ d ] + 1;
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max.setPosition( center[ d ] + 1, d );
	}

	@Override
	public int numDimensions() { return numDimensions; }

	@Override
	public Iterator<T> iterator() { return cursor(); }

	@Override
	public long min( final int d ) { return center[ d ] - 1; }

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min[ d ] = center[ d ] - 1;
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min.setPosition( center[ d ] - 1, d );
	}

	@Override
	public long max( final int d ) { return center[ d ] + 1; }

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max[ d ] = center[ d ] + 1;		
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max.setPosition( center[ d ] - 1, d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < numDimensions; ++d )
			dimensions[ d ] = 3;
	}

	@Override
	public long dimension( final int d ) { return 3; }

	@Override
	public LocalNeighborhoodCursor<T> cursor() { return new LocalNeighborhoodCursor< T >( source, center ); }

	@Override
	public LocalNeighborhoodCursor<T> localizingCursor() { return cursor(); }

}
