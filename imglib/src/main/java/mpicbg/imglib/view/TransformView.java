package mpicbg.imglib.view;

import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.transform.Transform;

/**
 * Wrap a {@code source} RandomAccessible which is related to this by a generic
 * {@link Transform} {@code transformToSource}.
 *  
 * @author Tobias Pietzsch
 */
public class TransformView< T > implements TransformedRandomAccessible< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;
	
	protected final Transform transformToSource;
	
	protected RandomAccessible< T > fullViewRandomAccessible;
	
	public TransformView( RandomAccessible< T > source, final Transform transformToSource )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();

		this.n = transformToSource.numSourceDimensions();

		this.source = source;
		this.transformToSource = transformToSource;
		
		fullViewRandomAccessible = null;		
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public Transform getTransformToSource()
	{
		return transformToSource;
	}

	@Override
	public RandomAccess< T > randomAccess( Interval interval )
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
