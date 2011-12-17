package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.transform.integer.Mixed;
import net.imglib2.transform.integer.MixedTransform;

public class MixedTransformView< T > implements TransformedRandomAccessible< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;

	protected final MixedTransform transformToSource;

	protected RandomAccessible< T > fullViewRandomAccessible;

	public MixedTransformView( RandomAccessible< T > source, final Mixed transformToSource )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();

		this.n = transformToSource.numSourceDimensions();

		while ( IntervalView.class.isInstance( source ) )
		{
			source = ( ( IntervalView< T > ) source ).getSource();
		}

		if ( MixedTransformView.class.isInstance( source ) )
		{
			final MixedTransformView< T > v = ( MixedTransformView< T > ) source;
			this.source = v.getSource();
			this.transformToSource = v.getTransformToSource().concatenate( transformToSource );
		}
		else
		{
			this.source = source;
			final int sourceDim = this.source.numDimensions();
			this.transformToSource = new MixedTransform( n, sourceDim );
			this.transformToSource.set( transformToSource );
		}

		fullViewRandomAccessible = null;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public String toString()
	{
		String className = this.getClass().getCanonicalName();
		className = className.substring( className.lastIndexOf(".") + 1, className.length());
		return className + "(" + super.toString() + ")";
	}

	@Override
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public MixedTransform getTransformToSource()
	{
		return transformToSource;
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return TransformBuilder.getEfficientRandomAccessible( interval, this ).randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		if ( fullViewRandomAccessible == null )
			fullViewRandomAccessible = TransformBuilder.getEfficientRandomAccessible( null, this );
		return fullViewRandomAccessible.randomAccess();
	}
}
