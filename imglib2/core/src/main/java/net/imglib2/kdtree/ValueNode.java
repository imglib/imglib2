package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;

public final class ValueNode< T > extends AbstractNode< T >
{
	protected final T value;
	
	public ValueNode( T value, RealLocalizable position, int dimension, final ValueNode< T > left, final ValueNode< T > right ) 
	{
		super( position, dimension, left, right );
		this.value = value;
	}
	
	protected ValueNode( final ValueNode< T > node ) 
	{
		super( node );
		this.value = node.value;
	}
	
	@Override
	public T get()
	{
		return value;
	}

	@Override
	public ValueNode< T > copy()
	{
		return new ValueNode< T >( this );
	}
	
	@Override
	public String toString()
	{
		return "node " + getSplitDimension() + " ? " + getSplitCoordinate() + " | " + value;
	}	
}
