package net.imglib2.ui;

import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.ui.util.InterpolatingSource;

/**
 * Image data to be rendered by imglib-ui. Consists of a
 * {@link RealRandomAccessible} of arbitrary pixel type <code>T</code>, a source
 * transform, and an converter that converts type <code>T</code> to
 * {@link ARGBType} for display.
 *
 * The {@link #getInterpolatedSource() source accessible} is assumed to be extended to infinity.
 * If the underlying data is a pixel image, it is also assumed to be interpolated already
 * (see {@link InterpolatingSource}).
 *
 * @param <T>
 *            pixel type
 * @param <A>
 *            transform type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface RenderSource< T, A >
{
	/**
	 * Get the image, extended to infinity and interpolated.
	 *
	 * @return the extended and interpolated {@link RandomAccessible image}.
	 */
	public RealRandomAccessible< T > getInterpolatedSource();

	/**
	 * Get the transform from the {@link #getSource(long) source}
	 * into the global coordinate system.
	 *
	 * @return transforms source into the global coordinate system.
	 */
	public A getSourceTransform();

	/**
	 * Get the {@link Converter} (converts {@link #source} type T to ARGBType
	 * for display).
	 */
	public Converter< ? super T, ARGBType > getConverter();
}
