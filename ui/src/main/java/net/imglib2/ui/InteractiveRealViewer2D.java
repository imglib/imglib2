package net.imglib2.ui;

import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

public class InteractiveRealViewer2D< T extends NumericType< T > > extends AbstractInteractiveViewer2D< T >
{
	/**
	 * The {@link RandomAccessible} to display
	 */
	final protected RealRandomAccessible< T > source;

	/**
	 * Converts {@link #source} type T to ARGBType for display
	 */
	final protected Converter< T, ARGBType > converter;

	public InteractiveRealViewer2D( final int width, final int height, final RealRandomAccessible< T > source, final Converter< T, ARGBType > converter )
	{
		this( width, height, source, new AffineTransform2D(), converter );
	}

	public InteractiveRealViewer2D( final int width, final int height, final RealRandomAccessible< T > source, final Converter< T, ARGBType > converter, final DisplayTypes displayType )
	{
		this( width, height, source, new AffineTransform2D(), converter, displayType );
	}

	public InteractiveRealViewer2D( final int width, final int height, final RealRandomAccessible< T > source, final AffineTransform2D sourceTransform, final Converter< T, ARGBType > converter )
	{
		this( width, height, source, sourceTransform, converter, DisplayTypes.DISPLAY_SWING );
	}

	public InteractiveRealViewer2D( final int width, final int height, final RealRandomAccessible< T > source, final AffineTransform2D sourceTransform, final Converter< T, ARGBType > converter, final DisplayTypes displayType )
	{
		super( width, height, sourceTransform, displayType );
		this.source = source;
		this.converter = converter;
		projector = createProjector();
		display.startPainter();
	}

	protected int interpolation = 0;

	protected void toggleInterpolation()
	{
		++interpolation;
		interpolation %= 2;
		projector = createProjector();
		display.requestRepaint();
	}

	@Override
	protected XYRandomAccessibleProjector< T, ARGBType > createProjector()
	{
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( source, sourceToScreen.inverse() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}
}
