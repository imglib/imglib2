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
package net.imglib2.ui.overlay;

import java.awt.Graphics;
import java.awt.Graphics2D;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.TransformListener;
import net.imglib2.util.Intervals;

/**
 * {@link OverlayRenderer} showing a transformed box (interval + transform) that
 * represents the source that is shown in the viewer.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BoxOverlayRenderer implements OverlayRenderer, TransformListener< AffineTransform3D >
{
	/**
	 * Navigation wire-frame cube.
	 */
	final protected BoxOverlay box;

	/**
	 * Screen interval in which to display navigation wire-frame cube.
	 */
	protected Interval boxInterval;

	/**
	 * canvas size.
	 */
	protected Interval virtualScreenInterval;

	/**
	 * Global-to-screen transformation used by the interactive viewer.
	 */
	final protected AffineTransform3D viewerTransform;

	/**
	 * Source-to-global transformation.
	 */
	final protected AffineTransform3D sourceTransform;

	/**
	 * Source-to-screen transformation, concatenation of
	 * {@link #sourceTransform} and {@link #viewerTransform}.
	 */
	final protected AffineTransform3D sourceToScreen;

	/**
	 * The size of the source in source local coordinates.
	 */
	protected Interval sourceInterval;

	public BoxOverlayRenderer()
	{
		this( 800, 600 );
	}

	/**
	 * Create a {@link BoxOverlayRenderer} with the given initial canvas size.
	 * 
	 * @param screenWidth
	 *            initial canvas width.
	 * @param screenHeight
	 *            initial canvas height.
	 */
	public BoxOverlayRenderer( final int screenWidth, final int screenHeight )
	{
		box = new BoxOverlay();
		boxInterval = Intervals.createMinSize( 10, 10, 80, 60 );
		virtualScreenInterval = Intervals.createMinSize( 0, 0, screenWidth, screenHeight );
		viewerTransform = new AffineTransform3D();
		sourceTransform = new AffineTransform3D();
		sourceToScreen = new AffineTransform3D();
		sourceInterval = Intervals.createMinSize( 0, 0, 0, 1, 1, 1 );
	}

	/**
	 * Update the box interval. This is the screen interval in which to display
	 * navigation wire-frame cube.
	 */
	public synchronized void setBoxInterval( final Interval interval )
	{
		boxInterval = interval;
	}

	/**
	 * Update data to show in the box overlay.
	 * 
	 * @param sourceTransform
	 *            transforms source into the global coordinate system.
	 * @param sourceInterval
	 *            The size of the source in source local coordinates. This is
	 *            used for displaying the navigation wire-frame cube.
	 */
	public synchronized void setSource( final Interval sourceInterval, final AffineTransform3D sourceTransform )
	{
		this.sourceInterval = new FinalInterval( sourceInterval );
		this.sourceTransform.set( sourceTransform );
	}

	@Override
	public synchronized void transformChanged( final AffineTransform3D transform )
	{
		viewerTransform.set( transform );
	}

	@Override
	public synchronized void drawOverlays( final Graphics g )
	{
		sourceToScreen.set( viewerTransform );
		sourceToScreen.concatenate( sourceTransform );
		box.paint( ( Graphics2D ) g, sourceToScreen, sourceInterval, virtualScreenInterval, boxInterval );
	}

	/**
	 * Update the screen interval. This is the target 2D interval into which
	 * pixels are rendered. In the box overlay it is shown as a filled grey
	 * rectangle.
	 */
	@Override
	public synchronized void setCanvasSize( final int width, final int height )
	{
		virtualScreenInterval = Intervals.createMinSize( 0, 0, width, height );
	}
}
