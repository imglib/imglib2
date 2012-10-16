package net.imglib2.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

public class InteractiveViewer3D< T extends NumericType< T > > extends AbstractInteractiveViewer3D< T >
{
	/**
	 * The {@link RandomAccessible} to display
	 */
	final protected RandomAccessible< T > source;

	/**
	 * Converts {@link #source} type T to ARGBType for display
	 */
	final protected Converter< T, ARGBType > converter;

	public InteractiveViewer3D( final int width, final int height, final RandomAccessible< T > source, final Interval sourceInterval, final Converter< T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter );
	}

	public InteractiveViewer3D( final int width, final int height, final RandomAccessible< T > source, final Interval sourceInterval, final Converter< T, ARGBType > converter, final DisplayTypes displayType )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter, displayType );
	}

	public InteractiveViewer3D( final int width, final int height, final RandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, sourceTransform, converter, DisplayTypes.DISPLAY_SWING );
	}

	public InteractiveViewer3D( final int width, final int height, final RandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< T, ARGBType > converter, final DisplayTypes displayType )
	{
		super( width, height, sourceInterval, sourceTransform, displayType );
		this.source = source;
		this.converter = converter;
		projector = createProjector();
		display.startPainter();

		// add KeyHandler for toggling interpolation
		display.addHandler( new KeyListener() {
			@Override
			public void keyPressed( final KeyEvent e )
			{
				if ( e.getKeyCode() == KeyEvent.VK_I )
					toggleInterpolation();
			}

			@Override
			public void keyTyped( final KeyEvent e ) {}

			@Override
			public void keyReleased( final KeyEvent e ) {}
		});
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
		final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory;
		switch ( interpolation )
		{
		case 0:
			interpolatorFactory = new NearestNeighborInterpolatorFactory< T >();
			break;
		case 1:
		default:
			interpolatorFactory = new NLinearInterpolatorFactory< T >();
			break;
		}
		final Interpolant< T, RandomAccessible< T > > interpolant = new Interpolant< T, RandomAccessible< T > >( source, interpolatorFactory );
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( interpolant, sourceToScreen.inverse() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}
}
