package net.imglib2.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.ColorModel;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.imglib2.display.ARGBScreenImage;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.ui.AbstractInteractiveDisplay2D;
import net.imglib2.ui.ScreenImageRenderer;
import net.imglib2.ui.TransformEventHandler2D;
import net.imglib2.ui.TransformListener2D;

public class SwingInteractiveDisplay2D extends AbstractInteractiveDisplay2D implements TransformListener2D
{
	/**
	 * Used to render the image for on-screen display.
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * Transformation from {@link #sourceInterval} to {@link #screenImage}.
	 */
	final protected AffineTransform2D sourceToScreen;

	/**
	 * Mouse/Keyboard handler to manipulate {@link #sourceToScreen} transformation.
	 */
	final protected TransformEventHandler2D handler;

	final protected JFrame frame;

	final protected Viewer2DCanvas canvas;

	final protected ScreenImageRenderer renderer;

	final protected TransformListener2D renderTransformListener;

	public SwingInteractiveDisplay2D( final int width, final int height, final ScreenImageRenderer renderer, final TransformListener2D renderTransformListener )
	{
		this.screenImage = new ARGBScreenImage( width, height );
		sourceToScreen = new AffineTransform2D();
		this.renderer = renderer;
		renderer.screenImageChanged( screenImage );
		this.renderTransformListener = renderTransformListener;

		handler = new TransformEventHandler2D( this );
		handler.setWindowCenter( width / 2, height / 2 );

		canvas = new Viewer2DCanvas( width, height );
		canvas.addComponentListener( new ComponentListener()
		{
			@Override
			public void componentShown( final ComponentEvent e ) {}

			@Override
			public void componentMoved( final ComponentEvent e ) {}

			@Override
			public void componentHidden( final ComponentEvent e ) {}

			@Override
			public void componentResized( final ComponentEvent e )
			{
				final int oldW = ( int ) screenImage.dimension( 0 );
				final int oldH = ( int ) screenImage.dimension( 1 );
				final int w = canvas.getWidth();
				final int h = canvas.getHeight();
				synchronized( sourceToScreen )
				{
					sourceToScreen.set( handler.getTransform() );
					sourceToScreen.set( sourceToScreen.get( 0, 2 ) - oldW/2, 0, 2 );
					sourceToScreen.set( sourceToScreen.get( 1, 2 ) - oldH/2, 1, 2 );
					sourceToScreen.scale( ( double ) w / oldW );
					sourceToScreen.set( sourceToScreen.get( 0, 2 ) + w/2, 0, 2 );
					sourceToScreen.set( sourceToScreen.get( 1, 2 ) + h/2, 1, 2 );
					handler.setTransform( sourceToScreen );
					handler.setWindowCenter( w / 2, h / 2 );
					renderTransformListener.transformChanged( sourceToScreen );
				}
				requestRepaint();
			}
		} );

		final GraphicsConfiguration gc = getSuitableGraphicsConfiguration( ARGBScreenImage.ARGB_COLOR_MODEL );
		frame = new JFrame( "ImgLib2", gc );
		frame.getRootPane().setDoubleBuffered( true );
		frame.getContentPane().add( canvas );
		frame.pack();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );

		addHandler( handler );
	}

	/**
	 * Add new event handler.
	 */
	@Override
	public void addHandler( final Object handler )
	{
		if ( KeyListener.class.isInstance( handler ) )
			frame.addKeyListener( ( KeyListener ) handler );

		if ( MouseMotionListener.class.isInstance( handler ) )
			canvas.addMouseMotionListener( ( MouseMotionListener ) handler );

		if ( MouseListener.class.isInstance( handler ) )
			canvas.addMouseListener( ( MouseListener ) handler );

		if ( MouseWheelListener.class.isInstance( handler ) )
			canvas.addMouseWheelListener( ( MouseWheelListener ) handler );
	}

	final protected class Viewer2DCanvas extends JComponent
	{
		public Viewer2DCanvas( final int w, final int h )
		{
			super();
			setPreferredSize( new Dimension( w, h ) );
		}

		@Override
		public void paintComponent( final Graphics g )
		{
			g.drawImage( screenImage.image(), 0, 0, getWidth(), getHeight(), null );
			renderer.drawOverlays( g );
		}
	}

	protected static GraphicsConfiguration getSuitableGraphicsConfiguration( final ColorModel colorModel )
	{
		final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		final GraphicsConfiguration defaultGc = device.getDefaultConfiguration();
		if ( defaultGc.getColorModel( Transparency.TRANSLUCENT ).equals( colorModel ) )
			return defaultGc;

		for ( final GraphicsConfiguration gc : device.getConfigurations() )
			if ( gc.getColorModel( Transparency.TRANSLUCENT ).equals( colorModel ) )
				return gc;

		return defaultGc;
	}

	@Override
	public void paint()
	{
		final int w = canvas.getWidth();
		final int h = canvas.getHeight();
		if ( screenImage.dimension( 0 ) != w || screenImage.dimension( 1 ) != h )
		{
			screenImage = new ARGBScreenImage( w, h );
			renderer.screenImageChanged( screenImage );
		}
		renderer.drawScreenImage();
		canvas.repaint();
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
