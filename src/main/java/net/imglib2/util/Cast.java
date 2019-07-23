package net.imglib2.util;

/**
 * Utility class for type casts.
 *
 * @author Matthais Arzt
 */
public final class Cast
{

	private Cast()
	{
		// prevent from instantiation
	}

	/**
	 * Performs an unchecked cast.
	 * <p>
	 * For example this code snipped:
	 * <pre>
	 * {@code @SuppressWarnings("unchecked") }
	 * {@code Img<T> image = (Img<T>) ArrayImgs.ints(100, 100); }
	 * </pre>
	 * Can be rewritten as:
	 * <pre>
	 * {@code Img<T> image = Casts.unchecked( ArrayImgs.ints(100, 100) );}
	 * </pre>
	 * It's possible to explicitly specify the return type:
	 * <pre>
	 * {@code Img<T> image = Casts.<Img<T>>unchecked( ArrayImgs.ints(100, 100) );}
	 * </pre>
	 * @throws ClassCastException during runtime, if the cast is not possible.
	 */
	public static < T > T unchecked( final Object value )
	{
		@SuppressWarnings( "unchecked" )
		T t = ( T ) value;
		return t;
	}
}
