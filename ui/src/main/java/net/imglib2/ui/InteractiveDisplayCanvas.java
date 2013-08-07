package net.imglib2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;


public class InteractiveDisplayCanvas< T > extends JComponent implements TransformListener< T >
{
	private static final long serialVersionUID = -5546719724928785878L;

	/**
	 * Mouse/Keyboard handler that manipulates the view transformation.
	 */
	protected TransformEventHandler< T > handler;

	/**
	 * Listeners that we have to notify about view transformation changes.
	 */
	final protected CopyOnWriteArrayList< TransformListener< T > > transformListeners;

	/**
	 * The {@link OverlayRenderer} that draws on top of the current {@link #bufferedImage}.
	 */
	final protected CopyOnWriteArrayList< OverlayRenderer > overlayRenderers;

	/**
	 * The {@link BufferedImage} that is actually drawn on the canvas. Depending
	 * on {@link #discardAlpha} this is either the {@link BufferedImage}
	 * obtained from {@link #screenImage}, or {@link #screenImage}s buffer
	 * re-wrapped using a RGB color model.
	 */
	protected BufferedImage bufferedImage;

	public InteractiveDisplayCanvas( final int width, final int height, final TransformEventHandlerFactory< T > factory )
	{
		super();
		setPreferredSize( new Dimension( width, height ) );
		setFocusable( true );

		this.bufferedImage = null;
		this.overlayRenderers = new CopyOnWriteArrayList< OverlayRenderer >();
		this.transformListeners = new CopyOnWriteArrayList< TransformListener< T > >();

		addComponentListener( new ComponentAdapter()
		{
			@Override
			public void componentResized( final ComponentEvent e )
			{
				final int w = getWidth();
				final int h = getHeight();
				handler.setCanvasSize( w, h, true );
				for ( final OverlayRenderer or : overlayRenderers )
					or.setCanvasSize( w, h );
//				enableEvents( AWTEvent.MOUSE_MOTION_EVENT_MASK );
			}
		} );

		addMouseListener( new MouseAdapter()
		{
			@Override
			public void mousePressed( final MouseEvent e )
			{
				requestFocusInWindow();
			}
		} );

		handler = factory.create( this );
		handler.setCanvasSize( width, height, false );
		addHandler( handler );
	}

	/**
	 * Set the {@link BufferedImage} that is to be drawn on the canvas.
	 *
	 * @param bufferedImage image to draw (may be null).
	 */
	public synchronized void setBufferedImage( final BufferedImage bufferedImage )
	{
		this.bufferedImage = bufferedImage;
	}

	/**
	 * Add an {@link OverlayRenderer} that draws on top of the current {@link #bufferedImage}.
	 *
	 * @param renderer overlay renderer to add.
	 */
	public void addOverlayRenderer( final OverlayRenderer renderer )
	{
		overlayRenderers.add( renderer );
		renderer.setCanvasSize( getWidth(), getHeight() );
	}

	/**
	 * Remove an {@link OverlayRenderer}.
	 *
	 * @param renderer overlay renderer to remove.
	 */
	public void removeOverlayRenderer( final OverlayRenderer renderer )
	{
		overlayRenderers.remove( renderer );
	}

	/**
	 * Add a {@link TransformListener} to notify about view transformation changes.
	 *
	 * @param listener the transform listener to add.
	 */
	public void addTransformListener( final TransformListener< T > listener )
	{
		transformListeners.add( listener );
	}

	/**
	 * Remove a {@link TransformListener}.
	 *
	 * @param listener the transform listener to remove.
	 */
	public void removeTransformListener( final TransformListener< T > listener )
	{
		transformListeners.remove( listener );
	}

	/**
	 * Add new event handler. Depending on the interfaces implemented by
	 * <code>handler</code> calls {@link Component#addKeyListener(KeyListener)},
	 * {@link Component#addMouseListener(MouseListener)},
	 * {@link Component#addMouseMotionListener(MouseMotionListener)},
	 * {@link Component#addMouseWheelListener(MouseWheelListener)}.
	 */
	public void addHandler( final Object handler )
	{
		if ( KeyListener.class.isInstance( handler ) )
			addKeyListener( ( KeyListener ) handler );

		if ( MouseMotionListener.class.isInstance( handler ) )
			addMouseMotionListener( ( MouseMotionListener ) handler );

		if ( MouseListener.class.isInstance( handler ) )
			addMouseListener( ( MouseListener ) handler );

		if ( MouseWheelListener.class.isInstance( handler ) )
			addMouseWheelListener( ( MouseWheelListener ) handler );
	}

	/**
	 * Remove an event handler.
	 * Add new event handler. Depending on the interfaces implemented by
	 * <code>handler</code> calls {@link Component#removeKeyListener(KeyListener)},
	 * {@link Component#removeMouseListener(MouseListener)},
	 * {@link Component#removeMouseMotionListener(MouseMotionListener)},
	 * {@link Component#removeMouseWheelListener(MouseWheelListener)}.
	 */
	public void removeHandler( final Object handler )
	{
		if ( KeyListener.class.isInstance( handler ) )
			removeKeyListener( ( KeyListener ) handler );

		if ( MouseMotionListener.class.isInstance( handler ) )
			removeMouseMotionListener( ( MouseMotionListener ) handler );

		if ( MouseListener.class.isInstance( handler ) )
			removeMouseListener( ( MouseListener ) handler );

		if ( MouseWheelListener.class.isInstance( handler ) )
			removeMouseWheelListener( ( MouseWheelListener ) handler );
	}

	/**
	 * Get the {@link TransformEventHandler} that handles mouse and key events
	 * to update our view transform.
	 *
	 * @return handles mouse and key events to update the view transform.
	 */
	public TransformEventHandler< T > getTransformEventHandler()
	{
		return handler;
	}

	/**
	 * Set the {@link TransformEventHandler} that handles mouse and key events
	 * to update our view transform.
	 *
	 * @param handles mouse and key events to update the view transform
	 */
	public synchronized void setTransformEventHandler( final TransformEventHandler< T > transformEventHandler )
	{
		removeHandler( handler );
		handler = transformEventHandler;
		handler.setCanvasSize( getWidth(), getHeight(), false );
		addHandler( handler );
	}

	@Override
	public void paintComponent( final Graphics g )
	{
		final BufferedImage bi;
		synchronized ( this )
		{
			bi = bufferedImage;
		}
		if ( bi != null )
		{
//			final StopWatch watch = new StopWatch();
//			watch.start();
//			( (Graphics2D ) g).setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			g.drawImage( bi, 0, 0, getWidth(), getHeight(), null );
//			System.out.println( String.format( "g.drawImage() :%4d ms", watch.nanoTime() / 1000000 ) );
		}
		for ( final OverlayRenderer or : overlayRenderers )
			or.drawOverlays( g );
	}

	/**
	 * This is called by our {@link #getTransformEventHandler() transform event
	 * handler} when the transform is changed. In turn, we notify all our
	 * {@link TransformListener TransformListeners} that the view transform has
	 * changed.
	 */
	@Override
	public void transformChanged( final T transform )
	{
		for ( final TransformListener< T > l : transformListeners )
			l.transformChanged( transform );
	}
}