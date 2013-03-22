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

import java.awt.Graphics;

import net.imglib2.Interval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.ui.ij.ImagePlusInteractiveDisplay3D;
import net.imglib2.ui.swing.SwingInteractiveDisplay3D;

public abstract class AbstractInteractiveViewer3D< T extends NumericType< T > > implements ScreenImageRenderer, TransformListener3D
{
	/**
	 * render target
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * A transformation to apply to {@link #source} before applying the
	 * interactive viewer {@link #viewerTransform transform}.
	 */
	final protected AffineTransform3D sourceTransform;

	/**
	 * Transformation set by the interactive viewer.
	 */
	final protected AffineTransform3D viewerTransform = new AffineTransform3D();

	/**
	 * Transformation from {@link #source} to {@link #screenImage}. This is a
	 * concatenation of {@link #sourceTransform} and the interactive
	 * viewer {@link #viewerTransform transform}.
	 */
	final protected AffineTransform3D sourceToScreen = new AffineTransform3D();

	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * {@link #source} data to {@link #screenImage}.
	 */
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/**
	 * Window used for displaying the rendered {@link #screenImage}.
	 */
	final protected AbstractInteractiveDisplay3D display;

	public AbstractInteractiveViewer3D( final int width, final int height, final Interval sourceInterval, final AffineTransform3D sourceTransform, final DisplayTypes displayType )
	{
		this.sourceTransform = sourceTransform;
		if ( displayType == DisplayTypes.DISPLAY_IMAGEPLUS )
			display = new ImagePlusInteractiveDisplay3D( width, height, sourceInterval, sourceTransform, this, this );
		else
			display = new SwingInteractiveDisplay3D( width, height, sourceInterval, sourceTransform, this, this );
	}

	public void setSourceTransform( final AffineTransform3D transform )
	{
		sourceTransform.set( transform );
		display.requestRepaint();
	}

	@Override
	public void screenImageChanged( final ARGBScreenImage img )
	{
		screenImage = img;
		projector = createProjector();
	}

	protected abstract XYRandomAccessibleProjector< T, ARGBType > createProjector();

	@Override
	public void drawScreenImage()
	{
		synchronized( viewerTransform )
		{
			sourceToScreen.set( viewerTransform );
		}
		sourceToScreen.concatenate( sourceTransform );
		projector.map();
	}

	@Override
	public void drawOverlays( final Graphics g ) {}

	@Override
	public void transformChanged( final AffineTransform3D transform )
	{
		synchronized( viewerTransform )
		{
			viewerTransform.set( transform );
		}
	}

	public AbstractInteractiveDisplay3D getDisplay()
	{
		return display;
	}
}
