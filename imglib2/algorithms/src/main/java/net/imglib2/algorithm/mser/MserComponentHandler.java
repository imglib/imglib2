package net.imglib2.algorithm.mser;

import java.util.Comparator;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.LongType;

public class MserComponentHandler< T extends Type< T > >
		implements Component.Generator< T, MserComponentIntermediate< T > >,
		Component.Handler< MserComponentIntermediate< T > >
{
	public interface SimpleMserProcessor< T extends Type< T > >
	{
		/**
		 * Called when a {@link MserEvaluationNode} is found to be a local minimum of the MSER score.
		 * @param node
		 */
		public abstract void foundNewMinimum( MserEvaluationNode< T > node );
	}

	final T maxValue;
	
	final Comparator< T > comparator;
	
	final SimpleMserProcessor< T > procNewMser;
	
	final ComputeDeltaValue< T > delta;
	
	final long[] dimensions;
	
	final Img< LongType > linkedList;

	public MserComponentHandler( final T maxValue, final Comparator< T > comparator, final RandomAccessibleInterval< T > input, final ImgFactory< LongType > imgFactory, final ComputeDeltaValue< T > delta, final SimpleMserProcessor< T > procNewMser )
	{
		this.maxValue = maxValue;
		this.comparator = comparator;
		this.delta = delta;
		this.procNewMser = procNewMser;
		dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );
		linkedList = imgFactory.create( dimensions, new LongType() );
	}

	@Override
	public MserComponentIntermediate< T > createComponent( T value )
	{
		return new MserComponentIntermediate< T >( value, this );
	}

	@Override
	public MserComponentIntermediate< T > createMaxComponent()
	{
		return new MserComponentIntermediate< T >( maxValue, this );
	}

	@Override
	public void emit( MserComponentIntermediate< T > component )
	{
		new MserEvaluationNode< T >( component, comparator, delta, procNewMser );
		component.clearAncestors();
	}
}
