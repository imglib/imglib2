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
import mpicbg.imglib.transform.integer.Mixed;
import mpicbg.imglib.transform.integer.MixedTransform;
import mpicbg.imglib.transform.integer.TranslationTransform;
import mpicbg.imglib.util.Util;

public class TransformBuilder< T >
{
	/**
	 * Get a RandomAccessible which provides RandomAccess to the specified
	 * {@code interval} of {@code randomAccessible}.
	 * 
	 * <p>
	 * Create a new TransformBuilder that traverses the view hierarchy starting
	 * from {@code randomAccessible}. {@link #build()} an efficient
	 * RandomAccessible by joining and simplifying the collected
	 * transformations.
	 * </p>
	 * 
	 * @param interval
	 *            The interval in which access is needed.
	 * @param randomAccessible
	 */
	public static < S > RandomAccessible< S > getEfficientRandomAccessible( Interval interval, RandomAccessible< S > randomAccessible )
	{
		return new TransformBuilder< S >( interval, randomAccessible ).build();
	}

	/**
	 * Provides the untransformed random access.
	 */
	RandomAccessible< T > source;

	/**
	 * Interval transformed to the currently visited view. null means that the
	 * interval is infinite.
	 */
	BoundingBox boundingBox;

	/**
	 * List of transforms that have to be applied when wrapping the
	 * {@link #source} RandomAccess to obtain a RandomAccess in the target
	 * coordinate system.
	 */
	List< Transform > transforms;

	/**
	 * Create a new TransformBuilder. Starting from {@code randomAccessible}, go
	 * down the view hierarchy to the RandomAccessible that will provide the
	 * source RandomAccess into the specified {@code interval}. While traversing
	 * the view hierarchy transforms are collected into the {@link #transforms}
	 * list. These transforms have to be applied when wrapping the source
	 * RandomAccess to obtain a RandomAccess in the coordinate system of
	 * {@code randomAccessible}.
	 * 
	 * @param interval
	 *            The interval in which access is needed. This is converted to a
	 *            bounding box which is propagated through the transforms down
	 *            the view hierarchy.
	 * @param randomAccessible
	 */
	TransformBuilder( Interval interval, RandomAccessible< T > randomAccessible )
	{
		transforms = new ArrayList< Transform >();
		boundingBox = ( interval == null) ? null : new BoundingBox( interval );
		System.out.println( randomAccessible );
		visit( randomAccessible );
	}

	/**
	 * Append a transform to the {@link #transforms} list. Also apply the
	 * transform to {@link #boundingBox}, which This is called while traversing
	 * the view hierarchy.
	 * 
	 * @param t
	 *            the transform to add.
	 */
	protected void appendTransform( Transform t )
	{
		if ( BoundingBoxTransform.class.isInstance( t ) )
			boundingBox = ( ( BoundingBoxTransform ) t ).transform( boundingBox );
		else
			boundingBox = null;
		transforms.add( t );
	}

	/**
	 * Visit a RandomAccessible (while traversing the view hierarchy). The
	 * {@code randomAccessible} is handled by
	 * {@link #visitTransformed(TransformedRandomAccessible)} or
	 * {@link #visitExtended(ExtendedRandomAccessibleInterval)} when it has the
	 * appropriate type. Otherwise, the traversal stops and
	 * {@code randomAccessible} is set as the {@link #source}.
	 * 
	 * @param randomAccessible
	 */
	@SuppressWarnings( "unchecked" )
	protected void visit( RandomAccessible< T > randomAccessible )
	{
		if ( TransformedRandomAccessible.class.isInstance( randomAccessible ) )
		{
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

	/**
	 * Visit a TransformedRandomAccessible (while traversing the view
	 * hierarchy). Append the view's transform to the list and
	 * {@link #visit(RandomAccessible)} the view's source.
	 * 
	 * @param randomAccessible
	 */
	protected void visitTransformed( TransformedRandomAccessible< T > randomAccessible )
	{
		appendTransform( randomAccessible.getTransformToSource() );
		visit( randomAccessible.getSource() );
	}

	/**
	 * Visit a ExtendedRandomAccessibleInterval (while traversing the view
	 * hierarchy). If the no out-of-bounds extension is needed for the current
	 * bounding box, {@link #visit(RandomAccessible)} the view's source.
	 * Otherwise, the traversal stops and {@code randomAccessible} is set as the
	 * {@link #source}.
	 * 
	 * @param randomAccessible
	 */
	protected void visitExtended( ExtendedRandomAccessibleInterval< T, ? > randomAccessible )
	{
		RandomAccessibleInterval< T > sourceInterval = randomAccessible.getSource();
		if ( Util.contains( sourceInterval, boundingBox.getInterval() ) )
			visit( sourceInterval );
		else
			source = randomAccessible;
	}
	
	public static boolean isIdentity( Mixed t )
	{
		final int n = t.numSourceDimensions();
		final int m = t.numTargetDimensions();
		if ( n != m )
			return false;

		for ( int d = 0; d < m; ++d )
		{
			if ( t.getTranslation( d ) != 0 )
				return false;
			if ( t.getComponentZero( d ) )
				return false;
			if ( t.getComponentInversion( d ) )
				return false;
			if ( t.getComponentMapping( d ) != d )
				return false;
		}
		return true;
	}

	public static boolean isTranslation( Mixed t )
	{
		final int n = t.numSourceDimensions();
		final int m = t.numTargetDimensions();
		if ( n != m )
			return false;

		for ( int d = 0; d < m; ++d )
		{
			if ( t.getComponentZero( d ) )
				return false;
			if ( t.getComponentInversion( d ) )
				return false;
			if ( t.getComponentMapping( d ) != d )
				return false;
		}
		return true;
	}

	/**
	 * Simplify the transforms list and create a sequence of wrapped
	 * RandomAccessibles.
	 * 
	 * @return RandomAccessible on the interval specified in the constructor.
	 */
	protected RandomAccessible< T > build()
	{
		mpicbg.imglib.concatenate.Util.join( transforms );

		// TODO: simplify transform list
		for ( ListIterator< Transform > i = transforms.listIterator( transforms.size() ); i.hasPrevious(); )
		{
			Transform t = i.previous();
			if ( Mixed.class.isInstance( t ) )
			{
				Mixed mixed = ( Mixed ) t;
				if ( isIdentity( mixed ) )
				{
					// found identity
					// remove from transforms list
					i.remove();
				}
				else if ( isTranslation( mixed ) )
				{
					// found pure translation
					// replace by a TranslationTransform
					final long[] translation = new long[ mixed.numTargetDimensions() ]; 
					mixed.getTranslation( translation );
					i.set( new TranslationTransform( translation ) );
				}
			}
		}
		
		// build RandomAccessibles
		RandomAccessible< T > result = source;
		for ( ListIterator< Transform > i = transforms.listIterator( transforms.size() ); i.hasPrevious(); )
		{
			Transform t = i.previous();
			if ( MixedTransform.class.isInstance( t ) )
				result = wrapMixedTransform( result, ( MixedTransform ) t );
			else if ( TranslationTransform.class.isInstance( t ) )
				result = wrapTranslationTransform( result, ( TranslationTransform ) t );
			else
				result = wrapGenericTransform( result, t );
		}
		return result;
	}

	protected RandomAccessible< T > wrapGenericTransform( final RandomAccessible< T > s, final Transform t )
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
				return new TransformRandomAccess< T >( s.randomAccess(), t );
			}

			@Override
			public RandomAccess< T > randomAccess( Interval interval )
			{
				return new TransformRandomAccess< T >( s.randomAccess(), t );
			}
		};
	}

	protected RandomAccessible< T > wrapMixedTransform( final RandomAccessible< T > s, final MixedTransform t )
	{
		final boolean full = t.hasFullSourceMapping();
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
				if ( full )
					return new FullSourceMapMixedRandomAccess< T >( s.randomAccess(), t );
				else
					return new MixedRandomAccess< T >( s.randomAccess(), t );
			}

			@Override
			public RandomAccess< T > randomAccess( Interval interval )
			{
				if ( full )
					return new FullSourceMapMixedRandomAccess< T >( s.randomAccess(), t );
				else
					return new MixedRandomAccess< T >( s.randomAccess(), t );
			}
		};
	}

	protected RandomAccessible< T > wrapTranslationTransform( final RandomAccessible< T > s, final TranslationTransform t )
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
				return new TranslationRandomAccess< T >( s.randomAccess(), t );
			}

			@Override
			public RandomAccess< T > randomAccess( Interval interval )
			{
				return new TranslationRandomAccess< T >( s.randomAccess(), t );
			}
		};
	}
}
