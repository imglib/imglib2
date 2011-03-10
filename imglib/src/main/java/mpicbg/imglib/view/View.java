package mpicbg.imglib.view;

import mpicbg.imglib.IterableInterval;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.RandomAccessibleInterval;

public interface View< T > extends IterableInterval< T >, RandomAccessible< T >, RandomAccessibleInterval< T >
{
	/** 
	 * @return transform from this {@link View} into the underlying {@link ExtendableRandomAccessibleInterval}.
	 */
	public ViewTransform getViewTransform ();

	/**
	 * @return the underlying {@link ExtendableRandomAccessibleInterval}.
	 */
	public ExtendableRandomAccessibleInterval< T > getImg();
}
