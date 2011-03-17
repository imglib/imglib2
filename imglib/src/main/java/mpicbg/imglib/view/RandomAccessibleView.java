package mpicbg.imglib.view;

import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.transform.Transform;
import mpicbg.imglib.util.Pair;

/**
 * A view of a RandomAccessible.
 * 
 * @author Tobias Pietzsch
 */
public interface RandomAccessibleView< T > extends RandomAccessible< T >
{
	/**
	 * Get a {@link RandomAccess} covering <code>interval</code> from some
	 * underlying source in the view hierarchy. The resulting RandomAccess is in
	 * the coordinate frame of the source.
	 * 
	 * <p>
	 * A Transform is provided from the coordinate system of the view into the
	 * source coordinate system. To access position <code>p</code> in the view,
	 * you can obtain the source position <code>q</code> via
	 * <code>Transform.apply( p, q )</code>, and access the untransformed
	 * RandomAccess at <code>q</code>.
	 * </p>
	 * 
	 * @param interval
	 *            in which interval you intend to use the random access.
	 * 
	 * @return an untransformed RandomAccess on a source and the transform from
	 *         the view to the source. The Transform may be <code>null</code>
	 *         which corresponds to the identity transform.
	 */
	public Pair< RandomAccess< T >, Transform > untransformedRandomAccess( Interval interval );

	/**
	 * Get a source in the view hierarchy which can provide random access
	 * covering <code>interval</code> in the view.
	 * 
	 * <p>
	 * A Transform is provided from the coordinate system of the view into the
	 * source coordinate system. To access position <code>p</code> in the view,
	 * you can obtain the source position <code>q</code> via
	 * <code>Transform.apply( p, q )</code>, and access the source at
	 * <code>q</code>.
	 * </p>
	 * 
	 * <p>
	 * This function is similar to {@link #untransformedRandomAccess(Interval)},
	 * except that it returns the RandomAccessible source, which would provide
	 * the untransformedRandomAccess.
	 * </p>
	 * 
	 * @param interval
	 *            in which interval you intend to use the random access.
	 * 
	 * @return a source which provides untransformed RandomAccess and a
	 *         Transform to turn it into a RandomAccess on this view. The
	 *         Transform may be <code>null</code> which corresponds to the
	 *         identity transform.
	 */
	public Pair< RandomAccessible< T >, Transform > untransformedRandomAccessible( Interval interval );

	/**
	 * Get the direct source of the view. This is the next irreducible element
	 * in the view hierarchy, for example, the next
	 * ExtendedRandomAccessibleInterval or the underlying Img.
	 * 
	 * <p>
	 * For {@link TransformedRandomAccessibleView}s the target RandomAccessible
	 * is the one to which
	 * {@link TransformedRandomAccessibleView#getTransformToSource() getTransformToSource()} maps.
	 * </p>
	 * 
	 * @return the source {@link RandomAccessible}.
	 */
	public RandomAccessible< T > getSource();
}
