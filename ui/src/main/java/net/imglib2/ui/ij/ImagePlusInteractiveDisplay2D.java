package net.imglib2.ui.ij;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import net.imglib2.display.ARGBScreenImage;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.ui.AbstractInteractiveDisplay2D;
import net.imglib2.ui.ScreenImageRenderer;
import net.imglib2.ui.TransformListener2D;

public class ImagePlusInteractiveDisplay2D extends AbstractInteractiveDisplay2D implements TransformListener2D
{
	/**
	 * Used to render into {@link #imp}.
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * Transformation from {@link #sourceInterval} to {@link #screenImage}.
	 */
	final protected AffineTransform2D sourceToScreen;

	/**
	 * Mouse/Keyboard handler to manipulate {@link #sourceToScreen} transformation.
	 */
	final protected TransformEventHandler2Dij handler;

	/**
	 * Display.
	 */
	final protected ImagePlus imp;

	/**
	 * Register and restore key and mouse handlers.
	 */
	final protected GUI gui;

	final protected ScreenImageRenderer renderer;

	final protected TransformListener2D renderTransformListener;

	public ImagePlusInteractiveDisplay2D( final int width, final int height, final ScreenImageRenderer renderer, final TransformListener2D renderTransformListener )
	{
		sourceToScreen = new AffineTransform2D();

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

		handler = new TransformEventHandler2Dij( imp, this );
		handler.setWindowCenter( width / 2, height / 2 );
		addHandler( handler );

		// additional keyboard mappings
		addHandler( new KeyListener() {
			@Override
			public void keyPressed( final KeyEvent e )
			{
				if ( e.getKeyCode() == KeyEvent.VK_E )
				{
					IJ.log( sourceToScreen.toString() );
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
		renderer.drawOverlays( screenImage.image().getGraphics() );
		imp.draw();
	}

	@Override
	public void transformChanged( final AffineTransform2D transform )
	{
		synchronized( sourceToScreen )
		{
			sourceToScreen.set( transform );
		}
		renderTransformListener.transformChanged( transform );
		requestRepaint();
	}

	@Override
	public void setViewerTransform( final AffineTransform2D transform )
	{
		handler.setTransform( transform );
		transformChanged( transform );
	}

	@Override
	public AffineTransform2D getViewerTransform()
	{
		return sourceToScreen;
	}
}
