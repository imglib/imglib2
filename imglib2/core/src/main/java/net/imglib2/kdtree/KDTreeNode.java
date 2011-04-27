package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

public abstract class KDTreeNode< T > implements RealLocalizable, Sampler< T >
{
	protected final int n;

	protected final double[] pos;

	protected final int splitDimension;
	
	protected final KDTreeNode< T > left;
	
	protected final KDTreeNode< T > right;
	
	public KDTreeNode( RealLocalizable position, int dimension, final KDTreeNode< T > left, final KDTreeNode< T > right ) 
	{
		this.n = position.numDimensions();
		this.pos = new double[n];
		position.localize( pos );
		this.splitDimension = dimension;
		this.left = left;
		this.right = right;
	}
	
	protected KDTreeNode( final KDTreeNode< T > node ) 
	{
		this.n = node.n;
		this.pos = node.pos.clone();
		this.splitDimension = node.splitDimension;
		this.left = node.left;
		this.right = node.right;
	}
	
	public final int getSplitDimension()
	{
		return splitDimension;
	}
	
	public final double getSplitCoordinate()
	{
		return pos[ splitDimension ];
	}

	@Override
	public final int numDimensions()
	{
		return n;
	}

	@Override
	public final void localize( float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float ) pos[ d ];
	}

	@Override
	public final void localize( double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public final float getFloatPosition( int d )
	{
		return ( float ) pos[ d ];
	}

	@Override
	public final double getDoublePosition( int d )
	{
		return pos[ d ];
	}

	@Override
	public abstract KDTreeNode< T > copy();
	
	public final float squDistanceTo( final float[] p )
	{
		float sum = 0;
		for ( int d = 0; d < n; ++d ) 
		{
			sum += ( pos[d] - p[d] ) * ( pos[d] - p[d] ); 
		}
		return sum;
	}

	public final double squDistanceTo( final double[] p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d ) 
		{
			sum += ( pos[d] - p[d] ) * ( pos[d] - p[d] ); 
		}
		return sum;
	}

	public final double squDistanceTo( final RealLocalizable p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d ) 
		{
			sum += ( pos[d] - p.getDoublePosition( d ) ) * ( pos[d] - p.getDoublePosition( d ) ); 
		}
		return sum;
	}
}
