package mpicbg.imglib.view;

public interface TransformedRandomAccessibleView< T > extends RandomAccessibleView< T >
{
	/** 
	 * @return transform from this {@link RandomAccessibleView} into the underlying {@link ExtendableRandomAccessibleInterval}.
	 */
	public ViewTransform getViewTransform ();
}
