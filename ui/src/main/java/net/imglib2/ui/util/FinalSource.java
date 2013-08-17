package net.imglib2.ui.util;

import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.ui.RenderSource;

/**
 * A minimal implementation of {@link RenderSource}, with source
 * {@link RealRandomAccessible}, transform, and {@link Converter} provided in
 * the constructor.
 *
 * @param <T>
 *            pixel type
 * @param <A>
 *            transform type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FinalSource< T, A > implements RenderSource< T, A >
{
	protected final RealRandomAccessible< T > source;

	protected final A sourceTransform;

	protected final Converter< ? super T, ARGBType > converter;

	/**
	 * Create a {@link RenderSource}.
	 *
	 * @param source
	 *            a source image, extending to infinity and interpolated if
	 *            necessary.
	 * @param sourceTransform
	 *            The transformation from the source image coordinates into the
	 *            global coordinate system.
	 * @param converter
	 *            A converter from the {@link #source} type T to
	 *            {@link ARGBType}.
	 */
	public FinalSource( final RealRandomAccessible< T > source, final A sourceTransform, final Converter< ? super T, ARGBType > converter )
	{
		this.source = source;
		this.sourceTransform = sourceTransform;
		this.converter = converter;
	}

	@Override
	public RealRandomAccessible< T > getInterpolatedSource()
	{
		return source;
	}

	@Override
	public A getSourceTransform()
	{
		return sourceTransform;
	}

	@Override
	public Converter< ? super T, ARGBType > getConverter()
	{
		return converter;
	}
}
