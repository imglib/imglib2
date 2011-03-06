package mpicbg.imglib.view;

import mpicbg.imglib.IterableInterval;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.img.Img;

public interface View< T > extends IterableInterval< T >, RandomAccessible< T >
{
	/** 
	 * @return transform from this {@link View} into the underlying (TODO Img?).
	 */
	public ViewTransform getViewTransform ();

	/**
	 * TODO: lift this from Img to the appropriate set of Interfaces
	 * @return the underlying (TODO Img?).
	 */
	public Img< T > getImg();
}
