package mpicbg.imglib.view;

import mpicbg.imglib.IterableInterval;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.RandomAccessibleInterval;

public interface View< T > extends IterableInterval< T >, RandomAccessible< T >, RandomAccessibleInterval< T, View< T > >
{
	/** 
	 * @return transform from this {@link View} into the underlying (TODO Img?).
	 */
	public ViewTransform getViewTransform ();

	/**
	 * TODO: lift this from Img to the appropriate set of Interfaces
	 * @return the underlying (TODO Img?).
	 */
	public ExtendableRandomAccessibleInterval< T > getImg();
}
