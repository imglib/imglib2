package mpicbg.imglib.view;

import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.util.Pair;

public interface RandomAccessibleView< T > extends RandomAccessible< T >
{
	/**
	 * Get a RandomAccess covering <code>interval</code> from some underlying
	 * RandomAccessible in the view hierarchy. The resulting RandomAccess is in
	 * the coordinate frame of the underlying RandomAccessible. A ViewTransform
	 * is provided which will turn it into a RandomAccess on this view. If you
	 * want to access position <code>p</code> in this view, you need to get the
	 * transformed position <code>q</code> via
	 * <code>ViewTransform.transform( p, q )</code>, and access the
	 * untransformedRandomAccess at <code>q</code>.
	 * 
	 * @param i
	 *            in which interval you intend to use the random access.
	 * 
	 * @return an untransformed RandomAccess and a ViewTransform to turn it into
	 *         a RandomAccess on this view.
	 */
	public Pair< RandomAccess< T >, ViewTransform > untransformedRandomAccess( Interval i );
	
	/**
	 * Similar to {@link #untransformedRandomAccess(Interval)}, except that it returns underlying
	 * RandomAccessible, which would provide the untransformedRandomAccess.
	 * 
	 * @param i
	 *            in which interval you intend to use the random access.
	 * 
	 * @return a target which provides untransformed RandomAccess and a ViewTransform to turn it into
	 *         a RandomAccess on this view.
	 */
	public Pair< RandomAccessible< T >, ViewTransform > untransformedRandomAccessible( Interval i );
	
	/**
	 * Get the target {@link RandomAccessible}.
	 * This is the next irreducible element in the view hierarchy, that is,
	 * the next ExtendedRandomAccessibleInterval or the underlying Img.
	 * 
	 * <p>
	 * For {@link TransformedRandomAccessibleView}s the target RandomAccessible is the one
	 * to which the {@link TransformedRandomAccessibleView#getViewTransform view transform} maps. 
	 * 
	 * @return the underlying {@link RandomAccessible}.
	 */
	public RandomAccessible< T > getTarget();
}
