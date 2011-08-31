package net.imglib2.algorithm.mser;

import net.imglib2.Localizable;
import net.imglib2.Location;
import net.imglib2.type.Type;

public class BoundaryPixel< T extends Type< T > & Comparable< T > > extends Location implements Comparable< BoundaryPixel< T > >
{
	T value;	

	// TODO: this should be some kind of iterator over the neighborhood
	int nextNeighborIndex;

	public BoundaryPixel( final Localizable position, final T value, int nextNeighborIndex )
	{
		super( position );
		this.nextNeighborIndex = nextNeighborIndex;
		this.value = value.copy();
	}

	public int getNextNeighborIndex()
	{
		return nextNeighborIndex;
	}

	public void setNextNeighborIndex( int n )
	{
		nextNeighborIndex = n;
	}

	public T get()
	{
		return value;
	}
	
	@Override
	public int compareTo( BoundaryPixel< T > o )
	{
		return value.compareTo( o.value );
	}
}
