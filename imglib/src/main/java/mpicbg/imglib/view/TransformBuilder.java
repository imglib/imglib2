package mpicbg.imglib.view;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import mpicbg.imglib.ExtendedRandomAccessibleInterval;
import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.transform.Transform;
import mpicbg.imglib.transform.integer.BoundingBox;
import mpicbg.imglib.transform.integer.BoundingBoxTransform;
import mpicbg.imglib.transform.integer.MixedTransform;
import mpicbg.imglib.util.Util;

public class TransformBuilder< T >
{
	/**
	 * Provides the untransformed random access.
	 */
	RandomAccessible< T > source;
	
	/**
	 * Interval transformed to the currently visited view.
	 * null means that the interval is infinite.
	 */
	BoundingBox boundingBox;
	
	List< Transform > transforms;
	
	public static < S > RandomAccessible< S > getEfficientRandomAccessible( Interval interval, RandomAccessible< S > randomAccessible )
	{
		return new TransformBuilder< S >( interval, randomAccessible ).build();
	}
	
	TransformBuilder( Interval interval, RandomAccessible< T > randomAccessible )
	{
		transforms = new ArrayList< Transform >();
		boundingBox = new BoundingBox( interval );
		System.out.println( randomAccessible );
		visit( randomAccessible );
	}
	
	protected void appendTransform( Transform t )
	{
		if ( BoundingBoxTransform.class.isInstance( t ) )
			boundingBox = ( (BoundingBoxTransform) t ).transform( boundingBox );
		else
			boundingBox = null;
		transforms.add( t );
	}

	@SuppressWarnings( "unchecked" )
	protected void visit( RandomAccessible< T > randomAccessible )
	{
		if ( TransformedRandomAccessible.class.isInstance( randomAccessible ) ) {
			visitTransformed( ( TransformedRandomAccessible< T > ) randomAccessible );
		}
		else if ( ExtendedRandomAccessibleInterval.class.isInstance( randomAccessible ) )
		{
			visitExtended( ( ExtendedRandomAccessibleInterval< T, ? > ) randomAccessible );			
		}
		else
		{
			source = randomAccessible;
		}
	}

	protected void visitTransformed( TransformedRandomAccessible< T > randomAccessible )
	{
		appendTransform( randomAccessible.getTransformToSource() );
		visit( randomAccessible.getSource() );
	}

	protected void visitExtended( ExtendedRandomAccessibleInterval< T, ? > randomAccessible )
	{
		RandomAccessibleInterval< T > sourceInterval = randomAccessible.getSource();
		if ( Util.contains( sourceInterval, boundingBox.getInterval() ) )
			visit( sourceInterval );
		else
			source = randomAccessible;
	}

	protected RandomAccessible< T > build()
	{
		// TODO: simplify transform list
		RandomAccessible< T > result = source;
		for ( ListIterator< Transform > i = transforms.listIterator( transforms.size() ); i.hasPrevious(); )
		{
			Transform t = i.previous();
			if ( MixedTransform.class.isInstance( t ) )
				result = wrapMixedTransform( result, ( MixedTransform ) t );
			else
				result = wrapGenericTransform( result, t );
		}
		return result;
	}
	
	protected RandomAccessible< T > wrapGenericTransform( final RandomAccessible< T > s, final Transform t )
	{
		throw new RuntimeException("RandomAccessible for general Transforms is not implemented yet");
	}

	protected RandomAccessible< T > wrapMixedTransform( final RandomAccessible< T > s, final MixedTransform t )
	{
		return new RandomAccessible< T >()
		{
			@Override
			public int numDimensions()
			{
				return t.numSourceDimensions();
			}

			@Override
			public RandomAccess< T > randomAccess()
			{
				return new MixedRandomAccess< T >( s.randomAccess(), t );
			}

			@Override
			public RandomAccess< T > randomAccess( Interval interval )
			{
				return new MixedRandomAccess< T >( s.randomAccess(), t );
			}
		};		
	}
}
