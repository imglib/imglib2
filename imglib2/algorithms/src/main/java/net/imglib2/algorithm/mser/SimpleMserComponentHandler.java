package net.imglib2.algorithm.mser;

import net.imglib2.type.numeric.IntegerType;

public class SimpleMserComponentHandler< T extends IntegerType< T > >
		implements Component.Generator< T, SimpleMserComponent< T > >,
		Component.Handler< SimpleMserComponent< T > >
{
	public interface SimpleMserProcessor< T extends IntegerType< T > >
	{
		/**
		 * Called when a {@link MserEvaluationNode} is found to be a local minimum of the MSER score.
		 * @param node
		 */
		public abstract void foundNewMinimum( SimpleMserEvaluationNode< T > node );
	}

	final T maxValue;
	
	final SimpleMserProcessor< T > procNewMser;
	
	final long delta;
	
	final int n;
	
	public SimpleMserComponentHandler( final int numDimensions, final T maxValue, final long delta, final SimpleMserProcessor< T > procNewMser )
	{
		this.maxValue = maxValue;
		this.delta = delta;
		this.n = numDimensions;
		this.procNewMser = procNewMser;
	}

	@Override
	public SimpleMserComponent< T > createComponent( T value )
	{
		return new SimpleMserComponent< T >( value, n );
	}

	@Override
	public SimpleMserComponent< T > createMaxComponent()
	{
		return new SimpleMserComponent< T >( maxValue, n );
	}

	@Override
	public void emit( SimpleMserComponent< T > component )
	{
		//SimpleMserEvaluationNode< T > evalNode = 
		new SimpleMserEvaluationNode< T >( component, delta, procNewMser );
		component.clearAncestors();
	}
}
