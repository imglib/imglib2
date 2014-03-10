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

import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.concatenate.Concatenable;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.numeric.ARGBType;

/**
 * A {@link AbstractSimpleRenderer} for a single {@link RenderSource}.
 * 
 * @param <A>
 *            transform type
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class SimpleRenderer< A extends AffineGet & Concatenable< AffineGet > > extends AbstractSimpleRenderer< A >
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
		 * Create a factory for {@link SimpleRenderer SimpleRenderer} of the
		 * given source, with the specified multi-threading and double-buffering
		 * properties.
		 * 
		 * @param transformType
		 *            which transformation type (e.g.
		 *            {@link AffineTransformType2D affine 2d} or
		 *            {@link AffineTransformType3D affine 3d}) is used for the
		 *            source and viewer transforms.
		 * @param source
		 *            source data to be rendered.
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
	 * source data to be rendered.
	 */
	final protected RenderSource< ?, A > source;

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
	public SimpleRenderer(
			final AffineTransformType< A > transformType,
			final RenderSource< ?, A > source,
			final RenderTarget display,
			final PainterThread painterThread,
			final boolean doubleBuffered,
			final int numRenderingThreads )
	{
		super( transformType, display, painterThread, doubleBuffered, numRenderingThreads );
		this.source = source;
	}

	@Override
	protected SimpleInterruptibleProjector< ?, ARGBType > createProjector( final A viewerTransform, final ARGBScreenImage target )
	{
		return createProjector( transformType, source, viewerTransform, target, numRenderingThreads );
	}

	protected static < T, A extends AffineGet & Concatenable< AffineGet > > SimpleInterruptibleProjector< T, ARGBType > createProjector(
			final AffineTransformType< A > transformType,
			final RenderSource< T, A > source,
			final A viewerTransform,
			final ARGBScreenImage screenImage,
			final int numRenderingThreads )
	{
		return new SimpleInterruptibleProjector< T, ARGBType >( getTransformedSource( transformType, source, viewerTransform ), source.getConverter(), screenImage, numRenderingThreads );
	}

	protected static < T, A extends AffineGet & Concatenable< AffineGet > > RandomAccessible< T > getTransformedSource(
			final AffineTransformType< A > transformType,
			final RenderSource< T, A > source,
			final A viewerTransform )
	{
		final RealRandomAccessible< T > img = source.getInterpolatedSource();

		final A sourceToScreen = transformType.createTransform();
		transformType.set( sourceToScreen, viewerTransform );
		sourceToScreen.concatenate( source.getSourceTransform() );

		return RealViews.constantAffine( img, sourceToScreen );
	}
}
