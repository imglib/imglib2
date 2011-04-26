package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

public class SamplerNode< T > extends AbstractNode< T >
{
	protected final Sampler< T > sampler;
	
	public SamplerNode( Sampler< T > sampler, RealLocalizable position, int dimension, final SamplerNode< T > left, final SamplerNode< T > right ) 
	{
		super( position, dimension, left, right );
		this.sampler = sampler;
	}
	
	protected SamplerNode( final SamplerNode< T > node ) 
	{
		super( node );
		this.sampler = node.sampler.copy();
	}
	
	@Override
	public T get()
	{
		return sampler.get();
	}

	@Override
	public SamplerNode< T > copy()
	{
		return new SamplerNode< T >( this );
	}
	
	@Override
	public String toString()
	{
		return "node " + getSplitDimension() + " ? " + getSplitCoordinate() + " | " + sampler.get();
	}	
}
