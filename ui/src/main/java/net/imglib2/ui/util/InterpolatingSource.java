package net.imglib2.ui.util;

import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorARGBFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.ui.RenderSource;
import net.imglib2.view.Views;

/**
 * A {@link RenderSource}, that provides an interpolated source
 * {@link RandomAccessible} and is able to switch between nearest-neighbor and
 * n-linear interpolation.
 *
 * The (discrete) source {@link RandomAccessible}, transform, and
 * {@link Converter} provided in the constructor.
 *
 * @param <T>
 *            pixel type
 * @param <A>
 *            transform type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class InterpolatingSource< T extends NumericType< T >, A > implements RenderSource< T, A >
{
	protected final A sourceTransform;

	protected final Converter< ? super T, ARGBType > converter;

	protected final RealRandomAccessible< T >[] sourceInterpolants;

	private int interpolation;

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public InterpolatingSource( final RandomAccessible< T > source, final A sourceTransform, final Converter< ? super T, ARGBType > converter )
	{
		final InterpolatorFactory< T, RandomAccessible< T > > nLinearInterpolatorFactory;
		if ( ARGBType.class.isInstance( source.randomAccess().get() ) )
			nLinearInterpolatorFactory = ( InterpolatorFactory )new NLinearInterpolatorARGBFactory();
		else
			nLinearInterpolatorFactory = new NLinearInterpolatorFactory< T >();

		this.sourceTransform = sourceTransform;
		this.converter = converter;
		sourceInterpolants = new RealRandomAccessible[] {
				Views.interpolate( source, new NearestNeighborInterpolatorFactory< T >() ),
				Views.interpolate( source, nLinearInterpolatorFactory ) };
		interpolation = 0;
	}

	@Override
	public RealRandomAccessible< T > getInterpolatedSource()
	{
		return sourceInterpolants[ interpolation ];
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

	/**
	 * Switch to the other interpolation method (nearest-neighbor or n-linear)
	 */
	public void switchInterpolation()
	{
		interpolation = ( interpolation + 1 ) % sourceInterpolants.length;
	}

	/**
	 * Get current interpolation method.
	 *
	 * @return interpolation method: 0 is nearest-neighbor, 1 is n-linear
	 */
	public int getInterpolation()
	{
		return interpolation;
	}

	/**
	 * Set current interpolation method.
	 *
	 * @param interpolation
	 *            0 is nearest-neighbor, 1 is n-linear
	 */
	public void setInterpolation( final int interpolation )
	{
		this.interpolation = interpolation % sourceInterpolants.length;
	}
}