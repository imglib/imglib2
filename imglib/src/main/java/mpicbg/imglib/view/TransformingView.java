package mpicbg.imglib.view;

public interface TransformingView< T > extends View< T >
{
	/** 
	 * @return transform from this {@link View} into the underlying {@link ExtendableRandomAccessibleInterval}.
	 */
	public ViewTransform getViewTransform ();
}
