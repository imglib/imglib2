package net.imglib2.algorithm.mser;

import net.imglib2.algorithm.mser.SimpleMserComponentHandler.SimpleMserProcessor;
import net.imglib2.type.numeric.IntegerType;

public class SimpleMserFilter< T extends IntegerType< T > > implements SimpleMserProcessor< T >
{
	final long minSize;
	final long maxSize;
	final double maxVar;
	final double minDiversity = 0.2;
	
	
	final SimpleMserProcessor< T > procNewMser;
	
	private int numDiscarded = 0;

	public SimpleMserFilter( final long minSize, final long maxSize, final double maxVar, SimpleMserProcessor< T > procNewMser )
	{
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.maxVar = maxVar;
		this.procNewMser = procNewMser;
	}

	@Override
	public void foundNewMinimum( SimpleMserEvaluationNode< T > node )
	{
		if ( node.size >= minSize && node.size <= maxSize && node.score <= maxVar )
			procNewMser.foundNewMinimum( node );
		else
			++numDiscarded;
	}
	
	@Override
	public String toString()
	{
		return "discarded " + numDiscarded + "regions" ;
	}
}
