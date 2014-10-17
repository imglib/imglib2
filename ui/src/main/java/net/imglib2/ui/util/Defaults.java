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
package net.imglib2.ui.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.imglib2.concatenate.Concatenable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;
import net.imglib2.ui.AffineTransformType;
import net.imglib2.ui.MultiResolutionRenderer;
import net.imglib2.ui.RenderSource;
import net.imglib2.ui.RendererFactory;
import net.imglib2.ui.viewer.InteractiveRealViewer2D;
import net.imglib2.ui.viewer.InteractiveRealViewer3D;
import net.imglib2.ui.viewer.InteractiveViewer2D;
import net.imglib2.ui.viewer.InteractiveViewer3D;

/**
 * Default rendering settings used by the convenience viewer classes
 * {@link InteractiveViewer2D}, {@link InteractiveRealViewer2D},
 * {@link InteractiveViewer3D}, and {@link InteractiveRealViewer3D}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Defaults
{
	/**
	 * Whether to discard the alpha components when drawing
	 * {@link BufferedImage} to {@link Graphics}.
	 */
	public static final boolean discardAlpha = true;

	/**
	 * Whether to use double buffered rendering.
	 */
	public static final boolean doubleBuffered = true;

	/**
	 * How many threads to use for rendering.
	 */
	public static final int numRenderingThreads = 3;

	/**
	 * For the {@link MultiResolutionRenderer}: Scale factors from the viewer
	 * canvas to screen images of different resolutions. A scale factor of 1
	 * means 1 pixel in the screen image is displayed as 1 pixel on the canvas,
	 * a scale factor of 0.5 means 1 pixel in the screen image is displayed as 2
	 * pixel on the canvas, etc.
	 */
	public static final double[] screenScales = new double[] { 1, 0.5, 0.25, 0.125 };

	/**
	 * Target rendering time in nanoseconds. The rendering time for the coarsest
	 * rendered scale in a {@link MultiResolutionRenderer} should be below this
	 * threshold.
	 */
	public static final long targetRenderNanos = 15 * 1000000;

	/**
	 * Create a factory to construct the default {@link Renderer} type with
	 * default settings for a single {@link RenderSource}.
	 * 
	 * @param transformType
	 * @param source
	 *            the source data that will be rendered by the {@link Renderer}
	 *            that is created by the returned factory.
	 * @return a factory to construct the default {@link Renderer} for the given
	 *         {@link RenderSource source data}.
	 */
	public static < A extends AffineSet & AffineGet & Concatenable< AffineGet > > RendererFactory< A > rendererFactory( final AffineTransformType< A > transformType, final RenderSource< ?, A > source )
	{
		return new MultiResolutionRenderer.Factory< A >( transformType, source, screenScales, targetRenderNanos, doubleBuffered, numRenderingThreads );
	}
}
