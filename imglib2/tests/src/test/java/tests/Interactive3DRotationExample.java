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

package tests;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class Interactive3DRotationExample< T extends RealType< T > & NativeType< T > > extends AbstractInteractiveExample< T >
{
	@Override
	final protected synchronized void copyState()
	{
		reducedAffineCopy.set( reducedAffine );
	}
		
	private double perspectiveX( final double[] p, final double d, final double w2 )
	{
		return ( p[ 0 ] - w2 ) / 10 / ( p[ 2 ] / 10 + d ) * d + w2 / 5;
	}
		
	private double perspectiveY( final double[] p, final double d, final double h2 )
	{
		return ( p[ 1 ] - h2 ) / 10 / ( p[ 2 ] / 10 + d ) * d + h2 / 5;
	}
		
	private void splitEdge(
			final double[] a,
			final double[] b,
			final GeneralPath before,
			final GeneralPath behind,
			final double d2,
			final double w2,
			final double h2 )
	{
		final double[] t = new double[ 3 ];
		if ( a[ 2 ] <= 0 )
		{
			before.moveTo( perspectiveX( a, d2, w2 ), perspectiveY( a, d2, h2 ) );
			if ( b[ 2 ] <= 0 )
				before.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );					
			else
			{
				final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
				t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
				t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
				before.lineTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				behind.moveTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				behind.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
			}
		}
		else
		{
			behind.moveTo( perspectiveX( a, d2, w2 ), perspectiveY( a, d2, h2 ) );
			if ( b[ 2 ] > 0 )
				behind.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );					
			else
			{
				final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
				t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
				t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
				behind.lineTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				before.moveTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				before.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
			}
		}
		
	}
		
	@Override
	final protected void visualize()
	{
		final double w = img.dimension( 0 ) - 1;
		final double h = img.dimension( 1 ) - 1;
		final double d = img.dimension( 2 ) - 1;
		final double w2 = screenImage.dimension( 0 ) / 2.0;
		final double h2 = screenImage.dimension( 0 ) / 2.0;
		final double d2 = d ;
		
		final double[] p000 = new double[]{ 0, 0, 0 };
		final double[] p100 = new double[]{ w, 0, 0 };
		final double[] p010 = new double[]{ 0, h, 0 };
		final double[] p110 = new double[]{ w, h, 0 };
		final double[] p001 = new double[]{ 0, 0, d };
		final double[] p101 = new double[]{ w, 0, d };
		final double[] p011 = new double[]{ 0, h, d };
		final double[] p111 = new double[]{ w, h, d };
		
		final double[] q000 = new double[ 3 ];
		final double[] q100 = new double[ 3 ];
		final double[] q010 = new double[ 3 ];
		final double[] q110 = new double[ 3 ];
		final double[] q001 = new double[ 3 ];
		final double[] q101 = new double[ 3 ];
		final double[] q011 = new double[ 3 ];
		final double[] q111 = new double[ 3 ];
		
		final double[] px = new double[]{ w / 2, 0, 0 };
		final double[] py = new double[]{ 0, h / 2, 0 };
		final double[] pz = new double[]{ 0, 0, d / 2 };
		
		final double[] qx = new double[ 3 ];
		final double[] qy = new double[ 3 ];
		final double[] qz = new double[ 3 ];
		
		final double[] c000 = new double[]{ 0, 0, 0 };
		final double[] c100 = new double[]{ screenImage.dimension( 0 ), 0, 0 };
		final double[] c010 = new double[]{ 0, screenImage.dimension( 1 ), 0 };
		final double[] c110 = new double[]{ screenImage.dimension( 0 ), screenImage.dimension( 1 ), 0 };
		
		reducedAffineCopy.apply( p000, q000 );
		reducedAffineCopy.apply( p100, q100 );
		reducedAffineCopy.apply( p010, q010 );
		reducedAffineCopy.apply( p110, q110 );
		reducedAffineCopy.apply( p001, q001 );
		reducedAffineCopy.apply( p101, q101 );
		reducedAffineCopy.apply( p011, q011 );
		reducedAffineCopy.apply( p111, q111 );
		
		reducedAffineCopy.apply( px, qx );
		reducedAffineCopy.apply( py, qy );
		reducedAffineCopy.apply( pz, qz );
		
		final GeneralPath box = new GeneralPath();
		final GeneralPath boxBehind = new GeneralPath();
		
		splitEdge( q000, q100, box, boxBehind, d2, w2, h2 );
		splitEdge( q100, q110, box, boxBehind, d2, w2, h2 );
		splitEdge( q110, q010, box, boxBehind, d2, w2, h2 );
		splitEdge( q010, q000, box, boxBehind, d2, w2, h2 );
		
		splitEdge( q001, q101, box, boxBehind, d2, w2, h2 );
		splitEdge( q101, q111, box, boxBehind, d2, w2, h2 );
		splitEdge( q111, q011, box, boxBehind, d2, w2, h2 );
		splitEdge( q011, q001, box, boxBehind, d2, w2, h2 );
		
		splitEdge( q000, q001, box, boxBehind, d2, w2, h2 );
		splitEdge( q100, q101, box, boxBehind, d2, w2, h2 );
		splitEdge( q110, q111, box, boxBehind, d2, w2, h2 );
		splitEdge( q010, q011, box, boxBehind, d2, w2, h2 );
		
		/* virtual slice canvas */
		final GeneralPath canvas = new GeneralPath();
		canvas.moveTo( perspectiveX( c000, d2, w2 ), perspectiveY( c000, d2, h2 ) );
		canvas.lineTo( perspectiveX( c100, d2, w2 ), perspectiveY( c100, d2, h2 ) );
		canvas.lineTo( perspectiveX( c110, d2, w2 ), perspectiveY( c110, d2, h2 ) );
		canvas.lineTo( perspectiveX( c010, d2, w2 ), perspectiveY( c010, d2, h2 ) );
		canvas.closePath();
		
		final Image image = imp.getImage();
		final Graphics2D graphics = ( Graphics2D )image.getGraphics();
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setPaint( Color.MAGENTA );
		graphics.draw( boxBehind );
		graphics.setPaint( new Color( 0x80ffffff, true ) );
		graphics.fill( canvas );
		graphics.setPaint( Color.GREEN );
		graphics.draw( box );
		graphics.setPaint( Color.WHITE );
		graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
		graphics.drawString("x", ( float )perspectiveX( qx, d2, w2 ), ( float )perspectiveY( qx, d2, h2 ) - 2 );
		graphics.drawString("y", ( float )perspectiveX( qy, d2, w2 ), ( float )perspectiveY( qy, d2, h2 ) - 2 );
		graphics.drawString("z", ( float )perspectiveX( qz, d2, w2 ), ( float )perspectiveY( qz, d2, h2 ) - 2 );
		
	}
	
	final private ImgPlus< T > imgPlus;
	final private ColorProcessor cp;
	
	final private ArrayList< AffineTransform3D > list = new ArrayList< AffineTransform3D >();
	final private AffineTransform3D affine = new AffineTransform3D();
	final private AffineTransform3D mouseRotation = new AffineTransform3D();
	final private AffineTransform3D sliceShift = new AffineTransform3D();
	final private AffineTransform3D reducedAffine = new AffineTransform3D();
	final private AffineTransform3D reducedAffineCopy = new AffineTransform3D();
	
	final private RealARGBConverter< T > converter;
	
	final static private float step = ( float )Math.PI / 180;

	private double yScale, zScale;
	
	/* the current rotation axis, indexed x->0, y->1, z->2 */
	private int axis = 0;
	
	/* the current slice index (rotated z) in isotropic x,y,z space */
	private double currentSlice = 0;
	
	public Interactive3DRotationExample( final ImgPlus< T > imgPlus, final RealARGBConverter< T > converter )
	{
		this.imgPlus = imgPlus;
		this.converter = converter;
		cp = new ColorProcessor( 400, 300 );
		img = imgPlus.getImg();
	}
	
	@Override
	protected XYRandomAccessibleProjector< T, ARGBType > createProjector(
			final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final T template = img.randomAccess().get().copy();
		final RandomAccessible< T > extendedImg = Views.extendValue( img, template );
		final Interpolant< T, RandomAccessible< T > > interpolant = new Interpolant< T, RandomAccessible< T > >( extendedImg, interpolatorFactory );
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( interpolant, reducedAffineCopy.inverse() );
		screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] )cp.getPixels() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}
	
	@Override
	public void run( final String arg )
    {	
		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		
		//imp.getWindow().setLocation( 0, 0 );
		//IJ.run("In [+]");
		//IJ.run("In [+]");

		list.clear();
		
		gui = new GUI( imp );
		
		
		if ( Double.isNaN( imgPlus.calibration( 0 ) ) )
			yScale = zScale = 1.0;
		else
		{
			if ( Double.isNaN( imgPlus.calibration( 1 ) ) )
				yScale = 1.0;
			else
				yScale = imgPlus.calibration( 1 ) / imgPlus.calibration( 0 );
			if ( Double.isNaN( imgPlus.calibration( 2 ) ) )
				zScale = 1.0;
			else
				zScale = imgPlus.calibration( 2 ) / imgPlus.calibration( 0 );
		}
		
		final int d = ( int )img.dimension( 2 );
		
		currentSlice = ( d / 2.0 - 0.5 ) * zScale;
		
		/* un-scale */
		final AffineTransform3D unScale = new AffineTransform3D();
		unScale.set(
			1.0, 0.0, 0.0, ( cp.getWidth() - img.dimension( 0 ) ) / 2.0,
			0.0, yScale, 0.0, ( cp.getHeight() - img.dimension( 1 ) * yScale ) / 2.0,
			0.0, 0.0, zScale, 0.0 );

		/* initialize affine */
		affine.set(
			1.0, 0.0, 0.0, 0.0,
			0.0, 1.0, 0.0, 0.0,
			0.0, 0.0, 1.0, 0.0 );
		
		/* slice shift */
		sliceShift.set(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, -currentSlice );

		list.add( unScale );
		list.add( affine );
		list.add( sliceShift );
		
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
		}

		gui.backupGui();
		gui.takeOverGui();
		
		projector = createProjector( nnFactory );
		
		painter = new MappingThread();
		
		painter.start();
		
		update();
    }
	
	@Override
	final protected void update()
	{
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
		}
		painter.repaint();
	}
	
	
	private void rotate( final int a, final double d )
	{
		final double width = screenImage.dimension( 0 );
		final double height = screenImage.dimension( 1 );
		
		final AffineTransform3D t = new AffineTransform3D();
		
		/* center shift */
		t.set(
				1, 0, 0, -width / 2.0,
				0, 1, 0, -height / 2.0 * yScale,
				0, 0, 1, -currentSlice );
		
		affine.preConcatenate( t );
		affine.rotate( a, d * step );
		
		/* center un-shift */
		t.set(
				1, 0, 0, width / 2.0,
				0, 1, 0, height / 2.0 * yScale,
				0, 0, 1, currentSlice );
		
		affine.preConcatenate( t );
	}
	
	private void scale( final double dScale )
	{
		final double width = screenImage.dimension( 0 );
		final double height = screenImage.dimension( 1 );
		
		final AffineTransform3D t = new AffineTransform3D();
		
		/* center shift */
		t.set(
				1, 0, 0, -width / 2.0,
				0, 1, 0, -height / 2.0 * yScale,
				0, 0, 1, -currentSlice );
		
		affine.preConcatenate( t );
		affine.scale( dScale );
		
		/* center un-shift */
		t.set(
				1, 0, 0, width / 2.0,
				0, 1, 0, height / 2.0 * yScale,
				0, 0, 1, currentSlice );
		
		affine.preConcatenate( t );
	}
	
	private void translate( final double dX, final double dY )
	{
		final AffineTransform3D t = new AffineTransform3D();
		
		t.set(
				1, 0, 0, dX,
				0, 1, 0, dY,
				0, 0, 1, 0 );
		
		affine.preConcatenate( t );
	}
	
	final private void shift( final double d )
	{
		currentSlice += d;
		sliceShift.set(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, -currentSlice );
	}
	
	/**
	 * Concatenate a list of {@link AffineTransform3D}
	 * 
	 * @param list
	 * @param affine
	 */
	final private static void reduceAffineTransformList( final Iterable< AffineTransform3D > list, final AffineTransform3D affine )
	{
		final AffineTransform3D a = new AffineTransform3D();
		for ( final AffineTransform3D t : list )
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
			oX -= 9 * dX / 10;
			oY -= 9 * dY / 10;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			oX += 9 * dX;
			oY += 9 * dY;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_X )
		{
			axis = 0;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_Y )
		{
			axis = 1;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_Z )
		{
			axis = 2;
		}
		else
		{
			final float v = keyModfiedSpeed( e.getModifiersEx() );
			if ( e.getKeyCode() == KeyEvent.VK_LEFT )
			{
				rotate( axis, v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
			{
				rotate( axis, -v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_COMMA )
			{
				shift( v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_PERIOD )
			{
				shift( -v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_I )
			{
				painter.toggleInterpolation();
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_E )
			{
				IJ.log( affine.toString() );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_F1 )
			{
				IJ.showMessage(
						"Interactive Stack Rotation",
						"Mouse control:" + NL + " " + NL +
						"Pan and tilt the volume by left-click and dragging the image in the canvas, " + NL +
						"move the volume by middle-click and dragging the image in the canvas, " + NL +
						"browse alongside the z-axis using the mouse-wheel, and" + NL +
						"zoom in and out using the mouse-wheel holding CTRL+SHIFT." + NL + " " + NL +
						"Key control:" + NL + " " + NL +
						"X - Select x-axis as rotation axis." + NL +
						"Y - Select y-axis as rotation axis." + NL +
						"Z - Select z-axis as rotation axis." + NL +
						"CURSOR LEFT - Rotate clockwise around the choosen rotation axis." + NL +
						"CURSOR RIGHT - Rotate counter-clockwise around the choosen rotation axis." + NL +
						"./> - Forward alongside z-axis." + NL +
						",/< - Backwardi alongside z-axis." + NL +
						"SHIFT - Rotate and browse 10x faster." + NL +
						"CTRL - Rotate and browse 10x slower." + NL +
						"ENTER/ESC - Return." + NL +
						"I - Toggle interpolation." + NL +
						"E - Export the current rotation to the log window." );
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
	public void mouseWheelMoved( final MouseWheelEvent e )
	{
		final int modifiers = e.getModifiersEx();
		final float v = keyModfiedSpeed( modifiers );
		final int s = e.getWheelRotation();
		if (
				( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 &&
				( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
		{
			final double dScale = 1.0 + 0.1;
			if ( s > 0 )
				scale( 1.0 / dScale );
			else
				scale( dScale );
		}
		else
			shift( v * -s );
		
		update();	
	}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		final int modifiers = e.getModifiersEx();
		if ( ( modifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 )
		{
			final float v = step * keyModfiedSpeed( e.getModifiersEx() );
			dX = oX - e.getX() / imp.getCanvas().getMagnification();
			dY = oY - e.getY() / imp.getCanvas().getMagnification();
			
			affine.set( mouseRotation );
			
			final AffineTransform3D t = new AffineTransform3D();
			
			/* center shift */
			t.set(
					1, 0, 0, -oX,
					0, 1, 0, -oY,
					0, 0, 1, -currentSlice );
			
			affine.preConcatenate( t );
			affine.rotate( 0, -dY * v );
			affine.rotate( 1, dX * v );
			
			/* center un-shift */
			t.set(
					1, 0, 0, oX,
					0, 1, 0, oY,
					0, 0, 1, currentSlice );
			
			affine.preConcatenate( t );
		}
		else if ( ( modifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 )
		{
			dX = e.getX() / imp.getCanvas().getMagnification() - oX;
			dY = e.getY() / imp.getCanvas().getMagnification() - oY;
			oX += dX;
			oY += dY;
			translate( dX, dY );
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
		oX = e.getX() / imp.getCanvas().getMagnification();
		oY = e.getY() / imp.getCanvas().getMagnification();
		mouseRotation.set( affine );
	}
	
	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		final ImgOpener io = new ImgOpener();
		final ImgPlus< UnsignedShortType > imgPlus;
		try
		{
			imgPlus = io.openImg( "/home/saalfeld/Desktop/l1-cns.tif", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType() );
		}
		catch ( final ImgIOException e )
		{
			IJ.log( "Problems opening the image, check the error msg." );
			e.printStackTrace();
			return;
		}
		new Interactive3DRotationExample< UnsignedShortType >( imgPlus, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) ).run( "" );
	}
}
