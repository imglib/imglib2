/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.imglib2.concatenate.Concatenable;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;
import net.imglib2.ui.util.GuiUtil;

/**
 * A {@link Renderer} that uses a coarse-to-fine rendering scheme. First, a
 * small {@link BufferedImage} at a fraction of the canvas resolution is
 * rendered. Then, increasingly larger images are rendered, until the full
 * canvas resolution is reached.
 * <p>
 * When drawing the low-resolution {@link BufferedImage} to the screen, they
 * will be scaled up by Java2D to the full canvas size, which is relatively
 * fast. Rendering the small, low-resolution images is usually very fast, such
 * that the display is very interactive while the user changes the viewing
 * transformation for example. When the transformation remains fixed for a
 * longer period, higher-resolution details are filled in successively.
 * <p>
 * The renderer allocates a {@link BufferedImage} for each of a predefined set
 * of <em>screen scales</em> (a screen scale of 1 means that 1 pixel in the
 * screen image is displayed as 1 pixel on the canvas, a screen scale of 0.5
 * means 1 pixel in the screen image is displayed as 2 pixel on the canvas,
 * etc.)
 * <p>
 * At any time, one of these screen scales is selected as the
 * <em>highest screen scale</em>. Rendering starts with this highest screen
 * scale and then proceeds to lower screen scales (higher resolution images).
 * Unless the highest screen scale is currently rendering,
 * {@link #requestRepaint() repaint request} will cancel rendering, such that
 * display remains interactive.
 * <p>
 * The renderer tries to maintain a per-frame rendering time close to a desired
 * number of <code>targetRenderNanos</code> nanoseconds. If the rendering time
 * (in nanoseconds) for the (currently) highest scaled screen image is above
 * this threshold, a coarser screen scale is chosen as the highest screen scale
 * to use. Similarly, if the rendering time for the (currently) second-highest
 * scaled screen image is below this threshold, this finer screen scale chosen
 * as the highest screen scale to use.
 * <p>
 * The renderer uses multiple threads (if desired) and double-buffering (if
 * desired).
 * <p>
 * Double buffering means that three {@link BufferedImage BufferedImages} are
 * created for every screen scale. After rendering the first one of them and
 * setting it to the {@link RenderTarget}, next time, rendering goes to the
 * second one, then to the third. The {@link RenderTarget} will always have a
 * complete image, which is not rendered to while it is potentially drawn to the
 * screen. When setting an image to the {@link RenderTarget}, the
 * {@link RenderTarget} will release one of the previously set images to be
 * rendered again. Thus, rendering will not interfere with painting the
 * {@link BufferedImage} to the canvas.
 * 
 * @param <A>
 *            transform type
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld
 */
abstract public class AbstractMultiResolutionRenderer< A extends AffineSet & AffineGet & Concatenable< AffineGet > > extends AbstractRenderer< A >
{
	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * {@link #source} data to {@link #screenImage}.
	 */
	protected InterruptibleProjector projector;

	/**
	 * Whether double buffering is used.
	 */
	final protected boolean doubleBuffered;

	/**
	 * Double-buffer index of next {@link #screenImages image} to render.
	 */
	final protected ArrayDeque< Integer > renderIdQueue;

	/**
	 * Maps from {@link BufferedImage} to double-buffer index. Needed for
	 * double-buffering.
	 */
	final protected HashMap< BufferedImage, Integer > bufferedImageToRenderId;

	/**
	 * Used to render the image for display. Two images per screen resolution if
	 * double buffering is enabled. First index is screen scale, second index is
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
	 * Whether the current rendering operation may be cancelled (to start a new
	 * one). Rendering may be cancelled unless we are rendering at coarsest
	 * screen scale.
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
	 *            displayed as 2 pixel on the canvas, etc. The screen scales are
	 *            assumed to be ordered finer-to-coarse, with index 0
	 *            corresponding to the full resolution usually.
	 * @param targetRenderNanos
	 *            Target rendering time in nanoseconds. The rendering time for
	 *            the coarsest rendered scale should be below this threshold.
	 * @param doubleBuffered
	 *            Whether to use double buffered rendering.
	 * @param numRenderingThreads
	 *            How many threads to use for rendering.
	 */
	public AbstractMultiResolutionRenderer(
			final AffineTransformType< A > transformType,
			final RenderTarget display,
			final PainterThread painterThread,
			final double[] screenScales,
			final long targetRenderNanos,
			final boolean doubleBuffered,
			final int numRenderingThreads )
	{
		super( transformType, display, painterThread );
		this.screenScales = screenScales.clone();
		this.doubleBuffered = doubleBuffered;
		this.numRenderingThreads = numRenderingThreads;
		renderIdQueue = new ArrayDeque< Integer >();
		bufferedImageToRenderId = new HashMap< BufferedImage, Integer >();
		screenImages = new ARGBScreenImage[ screenScales.length ][ 3 ];
		bufferedImages = new BufferedImage[ screenScales.length ][ 3 ];
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
	 * Check whether the size of the display component was changed and recreate
	 * {@link #screenImages} and {@link #screenScaleTransforms} accordingly.
	 * 
	 * @return whether the size was changed.
	 */
	protected synchronized boolean checkResize()
	{
		final int componentW = display.getWidth();
		final int componentH = display.getHeight();
		if ( screenImages[ 0 ][ 0 ] == null || screenImages[ 0 ][ 0 ].dimension( 0 ) * screenScales[ 0 ] != componentW || screenImages[ 0 ][ 0 ].dimension( 1 ) * screenScales[ 0 ] != componentH )
		{
			renderIdQueue.clear();
			renderIdQueue.addAll( Arrays.asList( 0, 1, 2 ) );
			bufferedImageToRenderId.clear();
			for ( int i = 0; i < screenScales.length; ++i )
			{
				final double screenToViewerScale = screenScales[ i ];
				final int w = ( int ) ( screenToViewerScale * componentW );
				final int h = ( int ) ( screenToViewerScale * componentH );
				if ( doubleBuffered )
				{
					for ( int b = 0; b < ( doubleBuffered ? 3 : 1 ); ++b )
					{
						// reuse storage arrays of level 0 (highest resolution)
						screenImages[ i ][ b ] = ( i == 0 ) ?
								new ARGBScreenImage( w, h ) :
								new ARGBScreenImage( w, h, screenImages[ 0 ][ b ].getData() );
						final BufferedImage bi = GuiUtil.getBufferedImage( screenImages[ i ][ b ] );
						bufferedImages[ i ][ b ] = bi;
						bufferedImageToRenderId.put( bi, b );
					}
				}
				else
				{
					screenImages[ i ][ 0 ] = new ARGBScreenImage( w, h );
					bufferedImages[ i ][ 0 ] = GuiUtil.getBufferedImage( screenImages[ i ][ 0 ] );
				}
				final A scale = screenScaleTransforms.get( i );
				final double xScale = ( double ) w / componentW;
				final double yScale = ( double ) h / componentH;
				scale.set( xScale, 0, 0 );
				scale.set( yScale, 1, 1 );
				scale.set( 0.5 * xScale - 0.5, 0, scale.numDimensions() );
				scale.set( 0.5 * yScale - 0.5, 1, scale.numDimensions() );
			}
			return true;
		}
		return false;
	}

	/**
	 * Create a {@link InterruptibleProjector} that renders to the specified
	 * target image, applying the specified transformations to some source (that
	 * is specific to the derived class).
	 * 
	 * @param viewerTransform
	 *            transforms global to screen coordinates
	 * @param screenScaleTransform
	 *            transforms screen coordinates to coordinates in specified
	 *            target image For example, if the target image has half the
	 *            screen resolution, then this will be a scaling by 0.5 in X, Y.
	 * @param target
	 *            target image
	 * @return projector that renders to the specified target image.
	 */
	abstract protected InterruptibleProjector createProjector( final A viewerTransform, final A screenScaleTransform, final ARGBScreenImage target );

	/**
	 * This is called by {@link #paint(AffineSet)} to determine whether another
	 * repaint is required after the current {@link #requestedScreenScaleIndex}
	 * has been successfully rendered. If {@link #isComplete()} returns false,
	 * painting proceeds with the next finer screen scale.
	 * <p>
	 * The default implementation checks whether the full canvas resolution has
	 * been reached. Derived classes may override this to implement different
	 * checks.
	 * 
	 * @return true, if another repaint is required.
	 */
	protected boolean isComplete()
	{
		return requestedScreenScaleIndex == 0;
	}

	@Override
	public boolean paint( final A viewerTransform )
	{
		if ( display.getWidth() <= 0 || display.getHeight() <= 0 )
			return false;

		checkResize();

		// the screen scale at which we will be rendering
		final int currentScreenScaleIndex;

		// the corresponding screen scale transform
		final A currentScreenScaleTransform;

		// the corresponding BufferedImage (to paint to the canvas)
		final BufferedImage bufferedImage;

		// the projector that paints to the screenImage.
		final InterruptibleProjector p;

		synchronized ( this )
		{
			renderingMayBeCancelled = ( requestedScreenScaleIndex < maxScreenScaleIndex );
			currentScreenScaleIndex = requestedScreenScaleIndex;
			currentScreenScaleTransform = screenScaleTransforms.get( currentScreenScaleIndex );

			final int renderId = renderIdQueue.peek();
			bufferedImage = bufferedImages[ currentScreenScaleIndex ][ renderId ];
			final ARGBScreenImage screenImage = screenImages[ currentScreenScaleIndex ][ renderId ];
			p = createProjector( viewerTransform, currentScreenScaleTransform, screenImage );
			projector = p;
		}

		// try rendering
		final boolean success = p.map();

		synchronized ( this )
		{
			// if rendering was not cancelled...
			if ( success )
			{
				final BufferedImage bi = display.setBufferedImage( bufferedImage );
				if ( doubleBuffered )
				{
					renderIdQueue.pop();
					final Integer id = bufferedImageToRenderId.get( bi );
					if ( id != null )
						renderIdQueue.add( id );
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

				if ( !isComplete() )
					requestRepaint( currentScreenScaleIndex > 0 ? currentScreenScaleIndex - 1 : 0 );
			}
		}

		return success;
	}
}
