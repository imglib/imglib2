package net.imglib2.ui.viewer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.ui.AffineTransformType3D;
import net.imglib2.ui.overlay.BoxOverlayRenderer;
import net.imglib2.ui.util.GuiUtil;
import net.imglib2.ui.util.InterpolatingSource;

/**
 * Interactive viewer for a 3D {@link RandomAccessible}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class InteractiveViewer3D< T extends NumericType< T > > extends InteractiveRealViewer< T, AffineTransform3D >
{
	/**
	 * Create an interactive viewer for a 2D {@link RandomAccessible}.
	 *
	 * @param width
	 *            window width.
	 * @param height
	 *            window height.
	 * @param source
	 *            The source image to display. It is assumed that the source is
	 *            extended to infinity.
	 * @param sourceInterval
	 *            The size of the source in source local coordinates. This is
	 *            used for displaying a navigation wire-frame cube.
	 * @param sourceTransform
	 *            Transformation from source to global coordinates. This is
	 *            useful for pre-scaling when showing anisotropic data, for
	 *            example.
	 * @param converter
	 *            Converter from the source type to argb for rendering the
	 *            source.
	 */
	public InteractiveViewer3D( final int width, final int height, final RandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< ? super T, ARGBType > converter )
	{
		this( width, height, new InterpolatingSource< T, AffineTransform3D >( source, sourceTransform, converter ), sourceInterval );
	}

	public InteractiveViewer3D( final int width, final int height, final RandomAccessible< T > source, final Interval sourceInterval, final Converter< ? super T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter );
	}

	public InteractiveViewer3D( final int width, final int height, final InterpolatingSource< T, AffineTransform3D > interpolatingSource, final Interval sourceInterval )
	{
		super( AffineTransformType3D.instance, width, height, interpolatingSource, GuiUtil.defaultDoubleBuffered, GuiUtil.defaultNumRenderingThreads );

		final BoxOverlayRenderer box = new BoxOverlayRenderer( width, height );
		box.setSource( sourceInterval, interpolatingSource.getSourceTransform() );
		display.addTransformListener( box );
		display.addOverlayRenderer( box );

		// add KeyHandler for toggling interpolation
		display.addHandler( new KeyAdapter() {
			@Override
			public void keyPressed( final KeyEvent e )
			{
				if ( e.getKeyCode() == KeyEvent.VK_I )
				{
					interpolatingSource.switchInterpolation();
					requestRepaint();
				}
			}
		});
	}
}
