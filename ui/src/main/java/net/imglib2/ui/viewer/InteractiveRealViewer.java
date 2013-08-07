package net.imglib2.ui.viewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import net.imglib2.concatenate.Concatenable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;
import net.imglib2.ui.AffineTransformType;
import net.imglib2.ui.InteractiveDisplayCanvas;
import net.imglib2.ui.PainterThread;
import net.imglib2.ui.RenderSource;
import net.imglib2.ui.MultiResolutionRenderer;
import net.imglib2.ui.TransformListener;
import net.imglib2.ui.PainterThread.Paintable;
import net.imglib2.ui.util.GuiUtil;

public class InteractiveRealViewer< T, A extends AffineSet & AffineGet & Concatenable< AffineGet > > implements TransformListener< A >, PainterThread.Paintable
{
	final protected AffineTransformType< A > transformType;

	/**
	 * Transformation set by the interactive viewer.
	 */
	final protected A viewerTransform;

	/**
	 * Canvas used for displaying the rendered {@link #screenImages screen image}.
	 */
	final protected InteractiveDisplayCanvas< A > display;

	/**
	 * Thread that triggers repainting of the display.
	 */
	final protected PainterThread painterThread;

	final protected MultiResolutionRenderer< A > imageRenderer;

	final protected JFrame frame;

	final protected RenderSource< T, A > source;

	public InteractiveRealViewer( final AffineTransformType< A > transformType, final int width, final int height, final RenderSource< T, A > source, final boolean doubleBuffered, final int numRenderingThreads )
	{
		this.transformType = transformType;
		painterThread = new PainterThread( this );
		viewerTransform = transformType.createTransform();
		display = new InteractiveDisplayCanvas< A >( width, height, transformType.transformEvenHandlerFactory() );
		display.addTransformListener( this );

		final double[] screenScales = new double[] { 1, 0.5, 0.25, 0.125 };
		final long targetRenderNanos = 15 * 1000000;
		imageRenderer = new MultiResolutionRenderer< A >( transformType, display, painterThread, screenScales, targetRenderNanos, doubleBuffered, numRenderingThreads );

//		final GraphicsConfiguration gc = GuiHelpers.getSuitableGraphicsConfiguration( GuiHelpers.ARGB_COLOR_MODEL );
		final GraphicsConfiguration gc = GuiUtil.getSuitableGraphicsConfiguration( GuiUtil.RGB_COLOR_MODEL );
		frame = new JFrame( "ImgLib2", gc );
		frame.getRootPane().setDoubleBuffered( true );
		final Container content = frame.getContentPane();
		content.add( display, BorderLayout.CENTER );
		frame.pack();
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				painterThread.interrupt();
			}
		} );
		frame.setVisible( true );

		this.source = source;

		painterThread.start();

	}

	@Override
	public void paint()
	{
		imageRenderer.paint( source, viewerTransform );
		display.repaint();
	}


	@Override
	public void transformChanged( final A transform )
	{
		transformType.set( viewerTransform, transform );
		requestRepaint();
	}

	public InteractiveDisplayCanvas< A > getDisplayCanvas()
	{
		return display;
	}

	public void requestRepaint()
	{
		imageRenderer.requestRepaint();
	}
}
