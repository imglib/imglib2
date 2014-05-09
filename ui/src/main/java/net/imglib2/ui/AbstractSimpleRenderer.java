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

import net.imglib2.concatenate.Concatenable;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.ui.util.GuiUtil;

/**
 * A {@link Renderer} that uses multiple threads (if desired) and
 * double-buffering (if desired).
 * <p>
 * Double buffering means that two {@link BufferedImage BufferedImages} are
 * created. After rendering the first one of them and setting it to the
 * {@link RenderTarget}, next time, rendering goes to the second one. Thus, the
 * {@link RenderTarget} will always have a complete image. Rendering will not
 * interfere with painting the {@link BufferedImage} to the canvas.
 * 
 * @param <A>
 *            transform type
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractSimpleRenderer< A extends AffineGet & Concatenable< AffineGet > > extends AbstractRenderer< A >
{
	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * source data to {@link #screenImage}.
	 */
	protected InterruptibleProjector projector;

	/**
	 * Whether double buffering is used.
	 */
	final protected boolean doubleBuffered;

	/**
	 * Used to render the image for display. Two images per screen resolution if
	 * double buffering is enabled. (Index is double-buffer.)
	 */
	protected ARGBScreenImage[] screenImages;

	/**
	 * {@link BufferedImage}s wrapping the data in the {@link #screenImages}.
	 * (Index is double-buffer.)
	 */
	protected BufferedImage[] bufferedImages;

	/**
	 * How many threads to use for rendering.
	 */
	final protected int numRenderingThreads;

	/**
	 * @param transformType
	 *            which transformation type (e.g. {@link AffineTransformType2D
	 *            affine 2d} or {@link AffineTransformType3D affine 3d}) is used
	 *            for the source and viewer transforms.
	 * @param source
	 *            source data to be rendered.
	 * @param display
	 *            The canvas that will display the images we render.
	 * @param painterThread
	 *            Thread that triggers repainting of the display. Requests for
	 *            repainting are send there.
	 * @param doubleBuffered
	 *            Whether to use double buffered rendering.
	 * @param numRenderingThreads
	 *            How many threads to use for rendering.
	 */
	public AbstractSimpleRenderer(
			final AffineTransformType< A > transformType,
			final RenderTarget display,
			final PainterThread painterThread,
			final boolean doubleBuffered,
			final int numRenderingThreads )
	{
		super( transformType, display, painterThread );
		this.doubleBuffered = doubleBuffered;
		this.numRenderingThreads = numRenderingThreads;
		screenImages = new ARGBScreenImage[ 2 ];
		bufferedImages = new BufferedImage[ 2 ];
		projector = null;
	}

	/**
	 * Check whether the size of the display component was changed and recreate
	 * {@link #screenImages} and {@link #screenScaleTransforms} accordingly.
	 */
	protected synchronized boolean checkResize()
	{
		final int componentW = display.getWidth();
		final int componentH = display.getHeight();
		if ( componentW <= 0 || componentH <= 0 )
			return false;
		if ( screenImages[ 0 ] == null || screenImages[ 0 ].dimension( 0 ) != componentW || screenImages[ 0 ].dimension( 1 ) != componentH )
		{
			for ( int b = 0; b < ( doubleBuffered ? 2 : 1 ); ++b )
			{
				screenImages[ b ] = new ARGBScreenImage( componentW, componentH );
				bufferedImages[ b ] = GuiUtil.getBufferedImage( screenImages[ b ] );
			}
		}
		return true;
	}

	@Override
	public boolean paint( final A viewerTransform )
	{
		if ( !checkResize() )
			return false;

		// the corresponding ARGBScreenImage (to render to)
		final ARGBScreenImage screenImage;

		// the corresponding BufferedImage (to paint to the canvas)
		final BufferedImage bufferedImage;

		// the projector that paints to the screenImage.
		final InterruptibleProjector p;

		synchronized ( this )
		{
			screenImage = screenImages[ 0 ];
			bufferedImage = bufferedImages[ 0 ];
			p = createProjector( viewerTransform, screenImage );
			projector = p;
		}

		// try rendering
		final boolean success = p.map();

		synchronized ( this )
		{
			// if rendering was not cancelled...
			if ( success )
			{
				display.setBufferedImage( bufferedImage );

				if ( doubleBuffered )
				{
					screenImages[ 0 ] = screenImages[ 1 ];
					screenImages[ 1 ] = screenImage;
					bufferedImages[ 0 ] = bufferedImages[ 1 ];
					bufferedImages[ 1 ] = bufferedImage;
				}
			}
		}

		return success;
	}

	/**
	 * Create a {@link InterruptibleProjector} that renders to the specified
	 * target image, applying the specified transformation to some source (that
	 * is specific to the derived class).
	 * 
	 * @param viewerTransform
	 *            transforms global to screen coordinates
	 * @param target
	 *            target image
	 * @return projector that renders to the specified target image.
	 */
	abstract protected InterruptibleProjector createProjector( final A viewerTransform, final ARGBScreenImage target );
}
