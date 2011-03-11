package mpicbg.imglib.view;

import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.util.Pair;

public interface View< T > extends RandomAccessible< T >
{
	public Pair< RandomAccess< T >, ViewTransform > untransformedRandomAccess( Interval i );
	
	/**
	 * Get the target {@link RandomAccessible}.
	 * This is the next irreducible element in the view hierarchy, that is,
	 * the next ExtendedRandomAccessibleInterval or the underlying Img.
	 * 
	 * <p>
	 * For {@link TransformingView}s the target RandomAccessible is the one
	 * to which the {@link TransformingView#getViewTransform view transform} maps. 
	 * 
	 * @return the underlying {@link RandomAccessible}.
	 */
	public RandomAccessible< T > getTargetRandomAccessible();
}
