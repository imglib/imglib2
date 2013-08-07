package net.imglib2.ui.viewer;

import net.imglib2.Interval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.ui.AffineTransformType3D;
import net.imglib2.ui.overlay.BoxOverlayRenderer;
import net.imglib2.ui.util.FinalSource;
import net.imglib2.ui.util.GuiUtil;

/**
 * Interactive viewer for a 3D {@link RealRandomAccessible}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class InteractiveRealViewer3D< T > extends InteractiveRealViewer< T, AffineTransform3D >
{
	/**
	 * Create an interactive viewer for a 3D {@link RealRandomAccessible}.
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
	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< ? super T, ARGBType > converter )
	{
		super( AffineTransformType3D.instance, width, height, new FinalSource< T, AffineTransform3D >( source, sourceTransform, converter ), GuiUtil.defaultDoubleBuffered, GuiUtil.defaultNumRenderingThreads );

		final BoxOverlayRenderer box = new BoxOverlayRenderer( width, height );
		box.setSource( sourceInterval, sourceTransform );
		display.addTransformListener( box );
		display.addOverlayRenderer( box );
	}

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final Converter< ? super T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter );
	}
}
