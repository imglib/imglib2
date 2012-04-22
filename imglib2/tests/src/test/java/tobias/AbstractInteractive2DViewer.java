/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package tobias;
import ij.IJ;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import net.imglib2.converter.Converter;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

abstract public class AbstractInteractive2DViewer< T extends RealType< T > & NativeType< T > > extends AbstractInteractiveExample< T >
{
	final static protected double step = Math.PI /180;
	protected double theta = 0.0;
	protected double scale = 1.0;
	protected double oTheta = 0;


	@Override
	final protected synchronized void copyState()
	{
		reducedAffineCopy.set( reducedAffine );
	}

	@Override
	final protected void visualize()
	{
		final Image image = imp.getImage();
		final Graphics2D graphics = ( Graphics2D )image.getGraphics();
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setPaint( Color.WHITE );
		graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
		graphics.drawString("theta = " + String.format( "%.3f", ( theta / Math.PI * 180 ) ), 10, 10 );
		graphics.drawString("scale = " + String.format( "%.3f", ( scale ) ), 10, 20 );
	}


	final protected ArrayList< AffineTransform2D > list = new ArrayList< AffineTransform2D >();
	final protected ArrayList< AffineTransform2D > rotationList = new ArrayList< AffineTransform2D >();
	final protected AffineTransform2D affine = new AffineTransform2D();
	final protected AffineTransform2D centerShift = new AffineTransform2D();
	final protected AffineTransform2D centerUnShift = new AffineTransform2D();
	final protected AffineTransform2D rotation = new AffineTransform2D();
	final protected AffineTransform2D reducedAffine = new AffineTransform2D();
	final protected AffineTransform2D reducedAffineCopy = new AffineTransform2D();
	final protected AffineTransform2D reducedRotation = new AffineTransform2D();

	final protected Converter< T, ARGBType > converter;

	public AbstractInteractive2DViewer( final Converter< T, ARGBType > converter )
	{
		this.converter = converter;

		rotationList.add( centerShift );
		rotationList.add( rotation );
		rotationList.add( centerUnShift );
	}

	@Override
	final protected void update()
	{
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( rotationList, reducedRotation );
			centerShift.set(
					1, 0, 0,
					0, 1, 0 );
			centerUnShift.set(
					1, 0, 0,
					0, 1, 0 );
			rotation.set(
					1, 0, 0,
					0, 1, 0 );
			affine.preConcatenate( reducedRotation );
			reduceAffineTransformList( list, reducedAffine );
		}

		painter.repaint();
	}


	protected void rotate( double dTheta )
	{
		while ( dTheta > Math.PI )
			dTheta -= Math.PI + Math.PI;
		while ( dTheta < -Math.PI )
			dTheta += Math.PI + Math.PI;

		theta += dTheta;

		while ( theta > Math.PI )
			theta -= Math.PI + Math.PI;
		while ( theta < -Math.PI )
			theta += Math.PI + Math.PI;

		synchronized ( reducedAffine )
		{
			centerShift.set(
					1, 0, -screenImage.dimension( 0 ) / 2.0,
					0, 1, -screenImage.dimension( 1 ) / 2.0 );
			centerUnShift.set(
					1, 0, screenImage.dimension( 0 ) / 2.0,
					0, 1, screenImage.dimension( 1 ) / 2.0 );
			rotation.rotate( dTheta );
		}
	}

	protected void scale( final double dScale )
	{
		scale *= dScale;

		synchronized ( reducedAffine )
		{
			centerShift.set(
					1, 0, -screenImage.dimension( 0 ) / 2.0,
					0, 1, -screenImage.dimension( 1 ) / 2.0 );
			centerUnShift.set(
					1, 0, screenImage.dimension( 0 ) / 2.0,
					0, 1, screenImage.dimension( 1 ) / 2.0 );
			rotation.scale( dScale );
		}
	}

	protected void translate( final double x, final double y )
	{
		synchronized ( reducedAffine )
		{
			rotation.set(
				1, 0, x,
				0, 1, y );
		}
	}

	/* coordinates where mouse dragging started and the drag distance */
	protected double oX, oY, dX, dY;

	/**
	 * Concatenate a list of {@link AffineTransform3D}
	 *
	 * @param list
	 * @param affine
	 */
	final protected static void reduceAffineTransformList( final Iterable< AffineTransform2D > list, final AffineTransform2D affine )
	{
		final AffineTransform2D a = new AffineTransform2D();
		for ( final AffineTransform2D t : list )
			a.preConcatenate( t );
		affine.set( a );
	}

	@Override
	public void keyPressed( final KeyEvent e )
	{
		if ( e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER )
		{
			painter.interrupt();

			if ( imp != null )
			{
				if ( e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER )
				{
					gui.restoreGui();
				}
			}
		}
		else if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
		{
			// TODO
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			// TODO
		}
		else
		{
			final float v = keyModfiedSpeed( e.getModifiersEx() );
			if ( e.getKeyCode() == KeyEvent.VK_LEFT )
			{
				rotate( -step * v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
			{
				rotate( step * v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_I )
			{
				toggleInterpolation();
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_E )
			{
				IJ.log( rotation.toString() );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_F1 )
			{
				IJ.showMessage(
						"Interactive Rotation",
						"Mouse control:" + NL + " " + NL +
						"Rotate the image by dragging the image in the canvas." + NL + " " + NL +
						"Key control:" + NL + " " + NL +
						"CURSOR LEFT - Rotate clockwise around the choosen rotation axis." + NL +
						"CURSOR RIGHT - Rotate counter-clockwise around the choosen rotation axis." + NL +
						"SHIFT - Rotate 10x faster." + NL +
						"CTRL - Rotate 10x slower." + NL +
						"ENTER/ESC - Return." + NL +
						"I - Toggle interpolation." + NL +
						"E - Export the current rotation to the log window." );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS )
			{
				scale( 1.0 + 0.1 * v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_MINUS )
			{
				scale( 1.0 / ( 1.0 + 0.1 * v ) );
				update();
			}
		}
	}

	@Override
	public void mouseWheelMoved( final MouseWheelEvent e )
	{
		final float v = keyModfiedSpeed( e.getModifiersEx() );
		final int s = e.getWheelRotation();
		final double dScale = 1.0 + 0.1 * v;
		if ( s > 0 )
			scale( 1.0 / dScale );
		else
			scale( dScale );
		update();
	}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		final int modifiers = e.getModifiersEx();
		if ( ( modifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 )
		{
			dX = e.getX() - oX;
			dY = e.getY() - oY;
			oX += dX;
			oY += dY;
			translate( dX, dY );
		}
		else
		{
			dX = screenImage.dimension( 0 ) / 2 - e.getX();
			dY = screenImage.dimension( 1 ) / 2 - e.getY();
			final double a = Math.sqrt( dX * dX + dY * dY );
			if ( a == 0 )
				return;
			final double dTheta = Math.atan2( dY / a, dX / a );

			rotate( dTheta - oTheta );

			oTheta = dTheta;
		}
		update();
	}

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
	@Override
	public void mousePressed( final MouseEvent e )
	{
		oX = e.getX();
		oY = e.getY();

		dX = screenImage.dimension( 0 ) / 2 - oX;
		dY = screenImage.dimension( 1 ) / 2 - oY;
		final double a = Math.sqrt( dX * dX + dY * dY );
		if ( a == 0 )
			return;

		oTheta = Math.atan2( dY / a , dX / a );
	}
}
