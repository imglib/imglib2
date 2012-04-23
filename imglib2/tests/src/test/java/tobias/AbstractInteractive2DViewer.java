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
import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

public class AbstractInteractive2DViewer< T extends RealType< T > & NativeType< T > > extends AbstractInteractiveExample< T > implements TransformEventHandler2D.TransformListener
{
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
		graphics.drawString("theta = " + String.format( "%.3f", ( transformEventHandler.getTheta() / Math.PI * 180 ) ), 10, 10 );
		graphics.drawString("scale = " + String.format( "%.3f", ( transformEventHandler.getScale() ) ), 10, 20 );
	}

	final protected ArrayList< AffineTransform2D > list = new ArrayList< AffineTransform2D >();
	final protected AffineTransform2D affine = new AffineTransform2D();
	final protected AffineTransform2D reducedAffine = new AffineTransform2D();
	final protected AffineTransform2D reducedAffineCopy = new AffineTransform2D();

	final protected Converter< T, ARGBType > converter;

	@Override
	public void setTransform( final AffineTransform2D transform )
	{
		synchronized ( reducedAffine )
		{
			affine.set( transform );
		}
		update();
	}

	@Override
	public void quit()
	{
		painter.interrupt();
		if ( imp != null )
		{
			gui.restoreGui();
		}
	}

	final protected ColorProcessor cp;

	final protected RandomAccessible< T > source;

	public AbstractInteractive2DViewer( final int width, final int height, final RandomAccessible< T > source, final Converter< T, ARGBType > converter, final AffineTransform2D initialTransform )
	{
		this.converter = converter;
		this.source = source;
		cp = new ColorProcessor( width, height );
		projector = createProjector( nnFactory );
		list.add( initialTransform );
		list.add( affine );
	}

	final protected void update()
	{
		synchronized ( reducedAffine )
		{
			TransformEventHandler2D.reduceAffineTransformList( list, reducedAffine );
		}

		painter.repaint();
	}

	protected TransformEventHandler2D transformEventHandler;

	protected GUI< TransformEventHandler2D > gui;

	final protected NearestNeighborInterpolatorFactory< T > nnFactory = new NearestNeighborInterpolatorFactory< T >();

	final protected NLinearInterpolatorFactory< T > nlFactory = new NLinearInterpolatorFactory< T >();

	protected int interpolation = 0;

	protected void toggleInterpolation()
	{
		++interpolation;
		interpolation %= 2;
		switch ( interpolation )
		{
		case 0:
			projector = createProjector( nnFactory );
			break;
		case 1:
			projector = createProjector( nlFactory );
			break;
		}
	}

	protected XYRandomAccessibleProjector< T, ARGBType > createProjector(
			final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final Interpolant< T, RandomAccessible< T > > interpolant = new Interpolant< T, RandomAccessible< T > >( source, interpolatorFactory );
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( interpolant, reducedAffineCopy.inverse() );
		screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] )cp.getPixels() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}

	public void run()
	{
		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.getCanvas().setMagnification( 1.0 );
		imp.updateAndDraw();

		transformEventHandler = new TransformEventHandler2D( new FinalInterval( new long[] { imp.getWidth(), imp.getHeight() } ), this );
		gui = new GUI< TransformEventHandler2D >( imp );
		gui.takeOverGui( transformEventHandler );

		painter = new MappingThread();
		painter.start();

		update();
	}
}
