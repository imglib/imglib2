package net.imglib2.algorithm.mser;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

public class SimpleMserComponentHandler< T extends RealType< T > >
		implements Component.Generator< T, SimpleMserComponent< T > >,
		Component.Handler< SimpleMserComponent< T > >
{
	public interface SimpleMserProcessor< T extends RealType< T > >
	{
		/**
		 * Called when a {@link MserEvaluationNode} is found to be a local minimum of the MSER score.
		 * @param node
		 */
		public abstract void foundNewMinimum( SimpleMserEvaluationNode< T > node );
	}

	final T maxValue;
	
	final SimpleMserProcessor< T > procNewMser;
	
	final T delta;
	
	final long[] dimensions;
	
	final Img< LongType > linkedList;

	public SimpleMserComponentHandler( final T maxValue, final RandomAccessibleInterval< T > input, final ImgFactory< LongType > imgFactory, final T delta, final SimpleMserProcessor< T > procNewMser )
	{
		this.maxValue = maxValue;
		this.delta = delta;
		this.procNewMser = procNewMser;
		dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );
		linkedList = imgFactory.create( dimensions, new LongType() );
	}

	@Override
	public SimpleMserComponent< T > createComponent( T value )
	{
		return new SimpleMserComponent< T >( value, this );
	}

	@Override
	public SimpleMserComponent< T > createMaxComponent()
	{
		return new SimpleMserComponent< T >( maxValue, this );
	}

	@Override
	public void emit( SimpleMserComponent< T > component )
	{
		new SimpleMserEvaluationNode< T >( component, delta, procNewMser );
		component.clearAncestors();
	}
}
