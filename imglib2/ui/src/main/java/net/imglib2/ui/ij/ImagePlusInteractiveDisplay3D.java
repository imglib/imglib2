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

package net.imglib2.ui.ij;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import net.imglib2.Interval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.AbstractInteractiveDisplay3D;
import net.imglib2.ui.BoxOverlay;
import net.imglib2.ui.ScreenImageRenderer;
import net.imglib2.ui.TransformListener3D;
import net.imglib2.util.Intervals;

public class ImagePlusInteractiveDisplay3D extends AbstractInteractiveDisplay3D implements TransformListener3D
{
	/**
	 * The size of the {@link #source}. This is used for displaying the
	 * navigation wire-frame cube.
	 */
	final protected Interval sourceInterval;

	/**
	 * Additional transformation to apply to {@link #sourceInterval}
	 * when displaying navigation wire-frame cube. This is useful
	 * for pre-scaling when showing anisotropic data, for example.
	 */
	final protected AffineTransform3D sourceTransform;

	/**
	 * Used to render into {@link #imp}.
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * The transformation interactively set by the user.
	 */
	final protected AffineTransform3D viewerTransform;

	/**
	 * Transformation from {@link #source} to {@link #screenImage}. This is a
	 * concatenation of {@link #sourceTransform} and the interactive
	 * {@link #viewerTransform transform} set by the user.
	 */
	final protected AffineTransform3D sourceToScreen;

	/**
	 * Navigation wire-frame cube.
	 */
	final protected BoxOverlay box;

	/**
	 * Screen interval in which to display navigation wire-frame cube.
	 */
	final protected Interval boxInterval;

	/**
	 * Mouse/Keyboard handler to manipulate {@link #viewerTransform} transformation.
	 */
	final protected TransformEventHandler3Dij handler;

	/**
	 * Display.
	 */
	final protected ImagePlus imp;

	/**
	 * Register and restore key and mouse handlers.
	 */
	final protected GUI gui;

	final protected ScreenImageRenderer renderer;

	final protected TransformListener3D renderTransformListener;

	/**
	 *
	 * @param width
	 *            window width.
	 * @param height
	 *            window height.
	 * @param sourceInterval
	 *            The size of the source. This is used for displaying the
	 *            navigation wire-frame cube.
	 * @param sourceTransform
	 *            Additional transformation to apply to {@link #sourceInterval}
	 *            when displaying navigation wire-frame cube. This is useful for
	 *            pre-scaling when showing anisotropic data, for example.
	 * @param renderer
	 *            is called to render into a {@link ARGBScreenImage}.
	 * @param renderTransformListener
	 *            is notified when the viewer transformation is changed.
	 */
	public ImagePlusInteractiveDisplay3D( final int width, final int height, final Interval sourceInterval, final AffineTransform3D sourceTransform, final ScreenImageRenderer renderer, final TransformListener3D renderTransformListener )
	{
		this.sourceInterval = sourceInterval;
		this.sourceTransform = sourceTransform;
		viewerTransform = new AffineTransform3D();
		sourceToScreen = new AffineTransform3D();

		boxInterval = Intervals.createMinSize( 10, 10, 80, 60 );
		box = new BoxOverlay();

		final ColorProcessor cp = new ColorProcessor( width, height );
		screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] ) cp.getPixels() );

		this.renderer = renderer;
		renderer.screenImageChanged( screenImage );
		this.renderTransformListener = renderTransformListener;

		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.getCanvas().setMagnification( 1.0 );
		imp.draw();

		gui = new GUI( imp );

		handler = new TransformEventHandler3Dij( imp, this );
		handler.setWindowCenter( width / 2, height / 2 );
		addHandler( handler );

		// additional keyboard mappings
		addHandler( new KeyListener() {
			@Override
			public void keyPressed( final KeyEvent e )
			{
				if ( e.getKeyCode() == KeyEvent.VK_E )
				{
					IJ.log( viewerTransform.toString() );
				}
				else if ( e.getKeyCode() == KeyEvent.VK_F1 )
				{
					IJ.showMessage( handler.getHelpString() );
				}
				else if ( e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS )
				{
					IJ.run("In [+]");
				}
				else if ( e.getKeyCode() == KeyEvent.VK_MINUS )
				{
					IJ.run("Out [-]");
				}
			}

			@Override
			public void keyTyped( final KeyEvent e ) {}

			@Override
			public void keyReleased( final KeyEvent e ) {}
		} );
	}

	/**
	 * Add new event handler.
	 */
	@Override
	public void addHandler( final Object handler )
	{
		gui.addHandler( handler );
	}

	@Override
	public void paint()
	{
		renderer.drawScreenImage();
		synchronized( viewerTransform )
		{
			sourceToScreen.set( viewerTransform );
		}
		sourceToScreen.concatenate( sourceTransform );
		box.paint( ( Graphics2D ) screenImage.image().getGraphics(), sourceInterval, screenImage, sourceToScreen, boxInterval );
		renderer.drawOverlays( screenImage.image().getGraphics() );
		imp.draw();
	}

	@Override
	public void transformChanged( final AffineTransform3D transform )
	{
		synchronized( viewerTransform )
		{
			viewerTransform.set( transform );
		}
		renderTransformListener.transformChanged( transform );
		requestRepaint();
	}

	@Override
	public void setViewerTransform( final AffineTransform3D transform )
	{
		handler.setTransform( transform );
		transformChanged( transform );
	}

	@Override
	public AffineTransform3D getViewerTransform()
	{
		return viewerTransform;
	}
}
