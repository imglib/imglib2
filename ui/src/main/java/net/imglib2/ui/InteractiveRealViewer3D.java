package net.imglib2.ui;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

public class InteractiveRealViewer3D< T extends NumericType< T > > extends AbstractInteractiveViewer3D< T >
{
	/**
	 * The {@link RandomAccessible} to display
	 */
	final protected RealRandomAccessible< T > source;

	/**
	 * Converts {@link #source} type T to ARGBType for display
	 */
	final protected Converter< T, ARGBType > converter;

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final Converter< T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter );
	}

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final Converter< T, ARGBType > converter, final DisplayTypes displayType )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter, displayType );
	}

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, sourceTransform, converter, DisplayTypes.DISPLAY_SWING );
	}

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< T, ARGBType > converter, final DisplayTypes displayType )
	{
		super( width, height, sourceInterval, sourceTransform, displayType );
		this.source = source;
		this.converter = converter;
		projector = createProjector();
		display.startPainter();
	}

	@Override
	protected XYRandomAccessibleProjector< T, ARGBType > createProjector()
	{
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( source, sourceToScreen.inverse() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}
}
