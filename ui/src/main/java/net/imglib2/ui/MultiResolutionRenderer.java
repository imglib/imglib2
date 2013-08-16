package net.imglib2.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.concatenate.Concatenable;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.ui.util.GuiUtil;

/**
 * TODO
 *
 * @param <A>
 *            transform type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class MultiResolutionRenderer< A extends AffineSet & AffineGet & Concatenable< AffineGet > > extends Renderer< A >
{
	public static class Factory implements RendererFactory
	{
		final protected double[] screenScales;

		final protected long targetRenderNanos;

		final protected boolean doubleBuffered;

		final protected int numRenderingThreads;

		public Factory( final double[] screenScales, final long targetRenderNanos, final boolean doubleBuffered, final int numRenderingThreads )
		{
			this.screenScales = screenScales;
			this.targetRenderNanos = targetRenderNanos;
			this.doubleBuffered = doubleBuffered;
			this.numRenderingThreads = numRenderingThreads;
		}

		@Override
		public < A extends AffineSet & AffineGet & Concatenable< AffineGet > > MultiResolutionRenderer< A > create( final AffineTransformType< A > transformType, final RenderTarget display, final PainterThread painterThread )
		{
			return new MultiResolutionRenderer< A >( transformType, display, painterThread, screenScales, targetRenderNanos, doubleBuffered, numRenderingThreads );
		}
	}

	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * {@link #source} data to {@link #screenImage}.
	 */
	protected InterruptibleProjector< ?, ARGBType > projector;

	/**
	 * Whether double buffering is used.
	 */
	final protected boolean doubleBuffered;

	/**
	 * Used to render the image for display. Two images per screen resolution
	 * if double buffering is enabled. First index is screen scale, second index is
	 * double-buffer.
	 */
	protected ARGBScreenImage[][] screenImages;

	/**
	 * {@link BufferedImage}s wrapping the data in the {@link #screenImages}.
	 * First index is screen scale, second index is double-buffer.
	 */
	protected BufferedImage[][] bufferedImages;

	/**
	 * Scale factors from the {@link #display viewer canvas} to the
	 * {@link #screenImages}.
	 *
	 * A scale factor of 1 means 1 pixel in the screen image is displayed as 1
	 * pixel on the canvas, a scale factor of 0.5 means 1 pixel in the screen
	 * image is displayed as 2 pixel on the canvas, etc.
	 */
	final protected double[] screenScales;

	/**
	 * The scale transformation from viewer to {@link #screenImages screen
	 * image}. Each transformations corresponds to a {@link #screenScales screen
	 * scale}.
	 */
	protected final ArrayList< A > screenScaleTransforms;

	/**
	 * Try to maintain a per-frame rendering time at around
	 * <code>targetRenderNanos</code> nanoseconds.
	 * <p>
	 * If the rendering time (in nanoseconds) for the (currently) highest scaled
	 * screen image is above this threshold, increase the
	 * {@link #maxScreenScaleIndex index} of the highest screen scale to use.
	 * Similarly, if the rendering time for the (currently) second-highest
	 * scaled screen image is below this threshold, decrease the
	 * {@link #maxScreenScaleIndex index} of the highest screen scale to use.
	 */
	final protected long targetRenderNanos;

	/**
	 * The index of the (coarsest) screen scale with which to start rendering.
	 * Once this level is painted, rendering proceeds to lower screen scales
	 * until index 0 (full resolution) has been reached. While rendering, the
	 * maxScreenScaleIndex is adapted such that it is the highest index for
	 * which rendering in {@link #targetRenderNanos} nanoseconds is still
	 * possible.
	 */
	protected int maxScreenScaleIndex;

	/**
	 * The index of the screen scale which should be rendered next.
	 */
	protected int requestedScreenScaleIndex;

	/**
	 * Whether the current rendering operation may be cancelled (to start a
	 * new one). Rendering may be cancelled unless we are rendering at
	 * coarsest screen scale.
	 */
	protected volatile boolean renderingMayBeCancelled;

	/**
	 * How many threads to use for rendering.
	 */
	final protected int numRenderingThreads;

	/**
	 * @param transformType
	 * @param display
	 *            The canvas that will display the images we render.
	 * @param painterThread
	 *            Thread that triggers repainting of the display. Requests for
	 *            repainting are send there.
	 * @param screenScales
	 *            Scale factors from the viewer canvas to screen images of
	 *            different resolutions. A scale factor of 1 means 1 pixel in
	 *            the screen image is displayed as 1 pixel on the canvas, a
	 *            scale factor of 0.5 means 1 pixel in the screen image is
	 *            displayed as 2 pixel on the canvas, etc.
	 * @param targetRenderNanos
	 *            Target rendering time in nanoseconds. The rendering time for
	 *            the coarsest rendered scale should be below this threshold.
	 * @param doubleBuffered
	 *            Whether to use double buffered rendering.
	 * @param numRenderingThreads
	 *            How many threads to use for rendering.
	 */
	public MultiResolutionRenderer( final AffineTransformType< A > transformType, final RenderTarget display, final PainterThread painterThread, final double[] screenScales, final long targetRenderNanos, final boolean doubleBuffered, final int numRenderingThreads )
	{
		super( transformType, display, painterThread );
		this.screenScales = screenScales.clone();
		this.doubleBuffered = doubleBuffered;
		this.numRenderingThreads = numRenderingThreads;
		screenImages = new ARGBScreenImage[ screenScales.length ][ 2 ];
		bufferedImages = new BufferedImage[ screenScales.length ][ 2 ];
		screenScaleTransforms = new ArrayList< A >();
		for ( int i = 0; i < screenScales.length; ++i )
			screenScaleTransforms.add( transformType.createTransform() );
		projector = null;

		this.targetRenderNanos = targetRenderNanos;
		maxScreenScaleIndex = screenScales.length - 1;
		requestedScreenScaleIndex = maxScreenScaleIndex;
		renderingMayBeCancelled = true;
	}

	/**
	 * Request a repaint of the display at the coarsest screen scale.
	 */
	@Override
	public synchronized void requestRepaint()
	{
		requestRepaint( maxScreenScaleIndex );
	}

	/**
	 * Request a repaint of the display from the painter thread. The painter
	 * thread will trigger a {@link #paint()} as soon as possible (that is,
	 * immediately or after the currently running {@link #paint()} has
	 * completed).
	 */
	public synchronized void requestRepaint( final int screenScaleIndex )
	{
		if ( renderingMayBeCancelled && projector != null )
			projector.cancel();
		requestedScreenScaleIndex = screenScaleIndex;
		painterThread.requestRepaint();
	}

	/**
	 * Check whether the size of the display component was changed and
	 * recreate {@link #screenImages} and {@link #screenScaleTransforms} accordingly.
	 */
	protected synchronized void checkResize()
	{
		final int componentW = display.getWidth();
		final int componentH = display.getHeight();
		if ( screenImages[ 0 ][ 0 ] == null || screenImages[ 0 ][ 0 ].dimension( 0 ) * screenScales[ 0 ] != componentW || screenImages[ 0 ][ 0 ].dimension( 1 )  * screenScales[ 0 ] != componentH )
		{
			for ( int i = 0; i < screenScales.length; ++i )
			{
				final double screenToViewerScale = screenScales[ i ];
				final int w = ( int ) ( screenToViewerScale * componentW );
				final int h = ( int ) ( screenToViewerScale * componentH );
				for ( int b = 0; b < ( doubleBuffered ? 2 : 1 ); ++b )
				{
					screenImages[ i ][ b ] = new ARGBScreenImage( w, h );
					bufferedImages[ i ][ b ] = GuiUtil.getBufferedImage( screenImages[ i ][ b ] );
				}
				final A scale = screenScaleTransforms.get( i );
				final double xScale = ( double ) w / componentW;
				final double yScale = ( double ) h / componentH;
				scale.set( xScale, 0, 0 );
				scale.set( yScale, 1, 1 );
				scale.set( 0.5 * xScale - 0.5, 0, scale.numDimensions() );
				scale.set( 0.5 * yScale - 0.5, 1, scale.numDimensions() );
			}
		}
	}

	@Override
	public boolean paint( final RenderSource< ?, A > source, final A viewerTransform )
	{
		checkResize();

		// the screen scale at which we will be rendering
		final int currentScreenScaleIndex;

		// the corresponding screen scale transform
		final A currentScreenScaleTransform;

		// the corresponding ARGBScreenImage (to render to)
		final ARGBScreenImage screenImage;

		// the corresponding BufferedImage (to paint to the canvas)
		final BufferedImage bufferedImage;

		// the projector that paints to the screenImage.
		final InterruptibleProjector< ?, ARGBType > p;

		synchronized( this )
		{
			renderingMayBeCancelled = ( requestedScreenScaleIndex < maxScreenScaleIndex );
			currentScreenScaleIndex = requestedScreenScaleIndex;
			currentScreenScaleTransform = screenScaleTransforms.get( currentScreenScaleIndex );

			p = createProjector( transformType, source, viewerTransform, currentScreenScaleTransform );
			screenImage = screenImages[ currentScreenScaleIndex ][ 0 ];
			bufferedImage = bufferedImages[ currentScreenScaleIndex ][ 0 ];
			projector = p;
		}

		// try rendering
		final boolean success = p.map( screenImage, numRenderingThreads );

		synchronized ( this )
		{
			// if rendering was not cancelled...
			if ( success )
			{
				display.setBufferedImage( bufferedImage );

				if ( doubleBuffered )
				{
					screenImages[ currentScreenScaleIndex ][ 0 ] = screenImages[ currentScreenScaleIndex ][ 1 ];
					screenImages[ currentScreenScaleIndex ][ 1 ] = screenImage;
					bufferedImages[ currentScreenScaleIndex ][ 0 ] = bufferedImages[ currentScreenScaleIndex ][ 1 ];
					bufferedImages[ currentScreenScaleIndex ][ 1 ] = bufferedImage;
				}

				final long rendertime = p.getLastFrameRenderNanoTime();
				if ( currentScreenScaleIndex == maxScreenScaleIndex )
				{
					if ( rendertime > targetRenderNanos && maxScreenScaleIndex < screenScales.length - 1 )
						maxScreenScaleIndex++;
				}
				else if ( currentScreenScaleIndex == maxScreenScaleIndex - 1 )
				{
					if ( rendertime < targetRenderNanos && maxScreenScaleIndex > 0 )
						maxScreenScaleIndex--;
				}
//				System.out.println( "scale = " + currentScreenScaleIndex );
//				System.out.println( String.format( "rendering:%4d ms", rendertime / 1000000 ) );
//				System.out.println( "maxScreenScaleIndex = " + maxScreenScaleIndex + "  (" + screenImages[ maxScreenScaleIndex ][ 0 ].dimension( 0 ) + " x " + screenImages[ maxScreenScaleIndex ][ 0 ].dimension( 1 ) + ")" );

				if ( currentScreenScaleIndex > 0 )
					requestRepaint( currentScreenScaleIndex - 1 );
			}
		}

		return success;
	}

	protected static < T, A extends AffineGet & Concatenable< AffineGet > > InterruptibleProjector< T, ARGBType > createProjector( final AffineTransformType< A > transformType, final RenderSource< T, A > source, final A viewerTransform, final A screenScaleTransform )
	{
		return new InterruptibleProjector< T, ARGBType >( getTransformedSource( transformType, source, viewerTransform, screenScaleTransform ), source.getConverter() );
	}

	protected static < T, A extends AffineGet & Concatenable< AffineGet > > RandomAccessible< T > getTransformedSource( final AffineTransformType< A > transformType, final RenderSource< T, A > source, final A viewerTransform, final A screenScaleTransform )
	{
		final RealRandomAccessible< T > img = source.getInterpolatedSource();

		final A sourceToScreen = transformType.createTransform();
		transformType.set( sourceToScreen, screenScaleTransform );
		sourceToScreen.concatenate( viewerTransform );
		sourceToScreen.concatenate( source.getSourceTransform() );

		return RealViews.constantAffine( img, sourceToScreen );
	}
}
