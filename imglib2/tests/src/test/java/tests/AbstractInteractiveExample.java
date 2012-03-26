package tests;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.Img;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.LanczosInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractInteractiveExample< T extends RealType< T > > implements PlugIn, KeyListener, MouseWheelListener, MouseListener, MouseMotionListener
{
	final protected class GUI
	{
		final private ImageWindow window;
		final private Canvas canvas;
		
		final private ImageJ ij;
		
		/* backup */
		private KeyListener[] windowKeyListeners;
		private KeyListener[] canvasKeyListeners;
		private KeyListener[] ijKeyListeners;
		
		private MouseListener[] canvasMouseListeners;
		private MouseMotionListener[] canvasMouseMotionListeners;
		
		private MouseWheelListener[] windowMouseWheelListeners;
		
		
		GUI( final ImagePlus imp )
		{
			window = imp.getWindow();
			canvas = imp.getCanvas();
			
			ij = IJ.getInstance();
		}
		
		/**
		 * Add new event handlers.
		 */
		final void takeOverGui()
		{
			canvas.addKeyListener( AbstractInteractiveExample.this );
			window.addKeyListener( AbstractInteractiveExample.this );
			
			canvas.addMouseMotionListener( AbstractInteractiveExample.this );
			
			canvas.addMouseListener( AbstractInteractiveExample.this );
			
			ij.addKeyListener( AbstractInteractiveExample.this );
			
			window.addMouseWheelListener( AbstractInteractiveExample.this );
		}
		
		/**
		 * Backup old event handlers for restore.
		 */
		final void backupGui()
		{
			canvasKeyListeners = canvas.getKeyListeners();
			windowKeyListeners = window.getKeyListeners();
			ijKeyListeners = IJ.getInstance().getKeyListeners();
			canvasMouseListeners = canvas.getMouseListeners();
			canvasMouseMotionListeners = canvas.getMouseMotionListeners();
			windowMouseWheelListeners = window.getMouseWheelListeners();
			clearGui();	
		}
		
		/**
		 * Restore the previously active Event handlers.
		 */
		final void restoreGui()
		{
			clearGui();
			for ( final KeyListener l : canvasKeyListeners )
				canvas.addKeyListener( l );
			for ( final KeyListener l : windowKeyListeners )
				window.addKeyListener( l );
			for ( final KeyListener l : ijKeyListeners )
				ij.addKeyListener( l );
			for ( final MouseListener l : canvasMouseListeners )
				canvas.addMouseListener( l );
			for ( final MouseMotionListener l : canvasMouseMotionListeners )
				canvas.addMouseMotionListener( l );
			for ( final MouseWheelListener l : windowMouseWheelListeners )
				window.addMouseWheelListener( l );
		}
		
		/**
		 * Remove both ours and the backed up event handlers.
		 */
		final void clearGui()
		{
			for ( final KeyListener l : canvasKeyListeners )
				canvas.removeKeyListener( l );
			for ( final KeyListener l : windowKeyListeners )
				window.removeKeyListener( l );
			for ( final KeyListener l : ijKeyListeners )
				ij.removeKeyListener( l );
			for ( final MouseListener l : canvasMouseListeners )
				canvas.removeMouseListener( l );
			for ( final MouseMotionListener l : canvasMouseMotionListeners )
				canvas.removeMouseMotionListener( l );
			for ( final MouseWheelListener l : windowMouseWheelListeners )
				window.removeMouseWheelListener( l );
			
			canvas.removeKeyListener( AbstractInteractiveExample.this );
			window.removeKeyListener( AbstractInteractiveExample.this );
			ij.removeKeyListener( AbstractInteractiveExample.this );
			canvas.removeMouseListener( AbstractInteractiveExample.this );
			canvas.removeMouseMotionListener( AbstractInteractiveExample.this );
			window.removeMouseWheelListener( AbstractInteractiveExample.this );
		}
	}
	
	final public class MappingThread extends Thread
	{
		private boolean pleaseRepaint;
		
		public MappingThread()
		{
			this.setName( "MappingThread" );
		}
		
		@Override
		public void run()
		{
			while ( !isInterrupted() )
			{
				final boolean b;
				synchronized ( this )
				{
					b = pleaseRepaint;
					pleaseRepaint = false;
				}
				if ( b )
				{
					copyState();
					projector.map();
					//imp.setImage( screenImage.image() );
					visualize();
					imp.updateAndDraw();
				}
				synchronized ( this )
				{
					try
					{
						if ( !pleaseRepaint ) wait();
					}
					catch ( final InterruptedException e ){}
				}
			}
		}
		
		public void repaint()
		{
			synchronized ( this )
			{
				pleaseRepaint = true;
				notify();
			}
		}
		
		public void toggleInterpolation() {
			++interpolation;
			interpolation %= 3;
			switch ( interpolation )
			{
			case 0:
				projector = createProjector( nnFactory );
				break;
			case 1:
				projector = createProjector( nlFactory );
				break;
			case 2:
				projector = createProjector( laFactory );
				break;
			}
			
		}
	}
	
	abstract protected void copyState();
	
	abstract protected void visualize();
	
	final static protected String NL = System.getProperty( "line.separator" );
	
	protected Img< T > img;
	protected ImagePlus imp;
	protected GUI gui;
	
	final protected NearestNeighborInterpolatorFactory< T > nnFactory = new NearestNeighborInterpolatorFactory< T >();
	final protected NLinearInterpolatorFactory< T > nlFactory = new NLinearInterpolatorFactory< T >();
	final protected LanczosInterpolatorFactory< T > laFactory = new LanczosInterpolatorFactory< T >( 2, true );
	protected ARGBScreenImage screenImage;
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/* coordinates where mouse dragging started and the drag distance */
	protected double oX, oY, dX, dY;
	
	protected int interpolation = 0;
	
	protected MappingThread painter;
	
	protected Object transform;
	

	abstract protected XYRandomAccessibleProjector< T, ARGBType > createProjector(
			final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory );
	
	abstract protected void update();
	
	final protected float keyModfiedSpeed( final int modifiers )
	{
		if ( ( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
			return 10;
		else if ( ( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 )
			return 0.1f;
		else
			return 1;
	}

	@Override
	public void keyReleased( final KeyEvent e )
	{
		if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
		{
			oX += 9 * dX;
			oY += 9 * dY;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			oX -= 9 * dX / 10;
			oY -= 9 * dY / 10;
		}
	}
	
	@Override
	public void keyTyped( final KeyEvent e ){}
	
	@Override
	public void mouseMoved( final MouseEvent e ){}
	@Override
	public void mouseClicked( final MouseEvent e ){}
	@Override
	public void mouseEntered( final MouseEvent e ){}
	@Override
	public void mouseExited( final MouseEvent e ){}
	@Override
	public void mouseReleased( final MouseEvent e ){}
}
