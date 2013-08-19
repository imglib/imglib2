package net.imglib2.ui.viewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.imglib2.concatenate.Concatenable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineSet;
import net.imglib2.ui.AffineTransformType;
import net.imglib2.ui.InteractiveDisplayCanvas;
import net.imglib2.ui.PainterThread;
import net.imglib2.ui.RenderSource;
import net.imglib2.ui.Renderer;
import net.imglib2.ui.RendererFactory;
import net.imglib2.ui.TransformListener;
import net.imglib2.ui.overlay.BufferedImageOverlayRenderer;
import net.imglib2.ui.util.GuiUtil;

/**
 * TODO
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 *
 * @param <T>
 * @param <A>
 * @param <C>
 */
public class InteractiveRealViewer< T, A extends AffineSet & AffineGet & Concatenable< AffineGet >, C extends JComponent & InteractiveDisplayCanvas< A > > implements TransformListener< A >, PainterThread.Paintable
{
	final protected AffineTransformType< A > transformType;

	/**
	 * Transformation set by the interactive viewer.
	 */
	final protected A viewerTransform;

	/**
	 * Canvas used for displaying the rendered {@link #screenImages screen image}.
	 */
	final protected C display;

	/**
	 * Thread that triggers repainting of the display.
	 */
	final protected PainterThread painterThread;

	final protected Renderer< A > imageRenderer;

	final protected JFrame frame;

	final protected RenderSource< T, A > source;

	public InteractiveRealViewer( final AffineTransformType< A > transformType, final C interactiveDisplayCanvas, final RenderSource< T, A > source, final RendererFactory rendererFactory )
	{
		this.transformType = transformType;
		painterThread = new PainterThread( this );
		viewerTransform = transformType.createTransform();
		display = interactiveDisplayCanvas;
		display.addTransformListener( this );

		final BufferedImageOverlayRenderer target = new BufferedImageOverlayRenderer();
		imageRenderer = rendererFactory.create( transformType, target, painterThread );
		display.addOverlayRenderer( target );

//		final GraphicsConfiguration gc = GuiUtil.getSuitableGraphicsConfiguration( GuiUtil.ARGB_COLOR_MODEL );
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

	public C getDisplayCanvas()
	{
		return display;
	}

	public void requestRepaint()
	{
		imageRenderer.requestRepaint();
	}
}
