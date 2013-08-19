/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */
package net.imglib2.ui;

import java.awt.image.BufferedImage;

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
public class SimpleRenderer< A extends AffineGet & Concatenable< AffineGet > > extends AbstractRenderer< A >
{
	/**
	 * Factory for creating {@link SimpleRenderer}.
	 */
	public static class Factory< A extends AffineSet & AffineGet & Concatenable< AffineGet > > implements RendererFactory< A >
	{
		final AffineTransformType< A > transformType;

		final RenderSource< ?, A > source;

		final protected boolean doubleBuffered;

		final protected int numRenderingThreads;

		/**
		 * TODO
		 * Create a factory for {@link SimpleRenderer SimpleRenderers} with the
		 * given multi-threading and double-buffering properties.
		 *
		 * @param transformType
		 *            TODO
		 * @param source
		 *            TODO
		 * @param doubleBuffered
		 *            Whether to use double buffered rendering.
		 * @param numRenderingThreads
		 *            How many threads to use for rendering.
		 */
		public Factory( final AffineTransformType< A > transformType, final RenderSource< ?, A > source, final boolean doubleBuffered, final int numRenderingThreads )
		{
			this.transformType = transformType;
			this.source = source;
			this.doubleBuffered = doubleBuffered;
			this.numRenderingThreads = numRenderingThreads;
		}

		@Override
		public SimpleRenderer< A > create( final RenderTarget display, final PainterThread painterThread )
		{
			return new SimpleRenderer< A >( transformType, source, display, painterThread, doubleBuffered, numRenderingThreads );
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
	 * @param source
	 *            TODO
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
	public SimpleRenderer( final AffineTransformType< A > transformType, final RenderSource< ?, A > source, final RenderTarget display, final PainterThread painterThread, final boolean doubleBuffered, final int numRenderingThreads )
	{
		super( transformType, source, display, painterThread );
		this.doubleBuffered = doubleBuffered;
		this.numRenderingThreads = numRenderingThreads;
		screenImages = new ARGBScreenImage[ 2 ];
		bufferedImages = new BufferedImage[ 2 ];
		projector = null;
	}

	/**
	 * Check whether the size of the display component was changed and
	 * recreate {@link #screenImages} and {@link #screenScaleTransforms} accordingly.
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
		final InterruptibleProjector< ?, ARGBType > p;

		synchronized( this )
		{
			p = createProjector( transformType, source, viewerTransform );
			screenImage = screenImages[ 0 ];
			bufferedImage = bufferedImages[ 0 ];
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
					screenImages[ 0 ] = screenImages[ 1 ];
					screenImages[ 1 ] = screenImage;
					bufferedImages[ 0 ] = bufferedImages[ 1 ];
					bufferedImages[ 1 ] = bufferedImage;
				}
			}
		}

		return success;
	}

	protected static < T, A extends AffineGet & Concatenable< AffineGet > > InterruptibleProjector< T, ARGBType > createProjector( final AffineTransformType< A > transformType, final RenderSource< T, A > source, final A viewerTransform )
	{
		return new InterruptibleProjector< T, ARGBType >( getTransformedSource( transformType, source, viewerTransform ), source.getConverter() );
	}

	protected static < T, A extends AffineGet & Concatenable< AffineGet > > RandomAccessible< T > getTransformedSource( final AffineTransformType< A > transformType, final RenderSource< T, A > source, final A viewerTransform )
	{
		final RealRandomAccessible< T > img = source.getInterpolatedSource();

		final A sourceToScreen = transformType.createTransform();
		transformType.set( sourceToScreen, viewerTransform );
		sourceToScreen.concatenate( source.getSourceTransform() );

		return RealViews.constantAffine( img, sourceToScreen );
	}
}
