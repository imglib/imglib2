/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;

import org.junit.Test;

/**
 * @author Johannes Schindelin
 */

public class GeneralPathRegionOfInterestTest
{

	@Test
	public void testIsMemberInCircle()
	{
		/*
		 * Testing with non-integral center because otherwise the right- and the
		 * bottom-most pixel would be triggering an assertion error.
		 */
		final double x0 = 100.5, y0 = 120.5, radius = 50;
		final AbstractRegionOfInterest roi = makeCircle( x0, y0, radius );

		final double[] coords = new double[ 2 ];
		for ( coords[ 0 ] = 0; coords[ 0 ] < x0 + radius + 10; coords[ 0 ] += 1 )
		{
			for ( coords[ 1 ] = 0; coords[ 1 ] < y0 + radius + 10; coords[ 1 ] += 1 )
			{
				final double distance = getDistance( x0, y0, coords[ 0 ], coords[ 1 ] );
				if ( distance <= radius )
					assertTrue( "(" + coords[ 0 ] + ", " + coords[ 1 ] +
							") is inside", roi.contains( coords ) );
				else
					assertFalse( "(" + coords[ 0 ] + ", " + coords[ 1 ] + ") is outside",
							roi.contains( coords ) );
			}
		}
	}

	@Test
	public void testCircleAsIterator()
	{
		/*
		 * Testing with non-integral center because otherwise the right- and the
		 * bottom-most pixel would be triggering an assertion error.
		 */
		final double x0 = 10, y0 = 12, radius = 5;
		final IterableRegionOfInterest roi = makeCircle( x0, y0, radius );

		final long width = ( int ) Math.ceil( x0 + radius + 10 );
		final long height = ( int ) Math.ceil( y0 + radius + 10 );

		final RandomAccessible< BitType > randomAccessible = new ArrayImgFactory< BitType >().create( new long[] { width, height }, new BitType() );
		final IterableInterval< BitType > interval = roi.getIterableIntervalOverROI( randomAccessible );
		final Cursor< BitType > cursor = interval.localizingCursor();

		int y = ( int ) Math.ceil( y0 - radius );
		int x = ( int ) Math.ceil( x0 - Math.sqrt( radius * radius - ( y - y0 ) * ( y - y0 ) ) );

		// For AWT, we are pretty lenient about boundary pixels...
		final double[] coords = new double[ 2 ];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( coords );
			if ( Math.abs( x - coords[ 0 ] ) > 1e-10 || Math.abs( y - coords[ 1 ] ) > 1e-10 )
			{
				assertTrue( "< 1", Math.abs( y - coords[ 1 ] ) <= 1 + 1e-10 );
				y++;
				x = ( int ) Math.ceil( x0 - Math.sqrt( radius * radius - ( y - y0 ) * ( y - y0 ) ) );
				if ( Math.abs( x - coords[ 0 ] ) <= 1 + 1e-10 )
				{
					x = ( int ) Math.round( coords[ 0 ] );
				}
			}
			assertEquals( "x", x, coords[ 0 ], 1e-10 );
			assertEquals( "y", y, coords[ 1 ], 1e-10 );
			x++;
		}
	}

	@Test
	public void testOnePixelWideRoiIteration()
	{
		final double x0 = 6, y0 = 7, height = 5;
		final IterableRegionOfInterest roi = makeOnePixelWideRoi( x0, y0, height );

		final long imgHeight = ( int ) Math.ceil( x0 + 1 + 5 );
		final long imgWidth = ( int ) Math.ceil( y0 + height + 5 );

		final RandomAccessible< BitType > randomAccessible = new ArrayImgFactory< BitType >().create( new long[] { imgWidth, imgHeight }, new BitType() );
		final IterableInterval< BitType > interval = roi.getIterableIntervalOverROI( randomAccessible );
		final Cursor< BitType > cursor = interval.localizingCursor();

		/*
		 * This iteration can cause an ArrayIndexOutOfBoundsException if {@link
		 * GeneralPathRegionOfInterest#ensureStripes()} allocates less space
		 * than needed for saving states.
		 */
		while ( cursor.hasNext() )
		{
			cursor.fwd();
		}
	}

	private double getDistance( final double x0, final double y0,
			final double x1, final double y1 )
	{
		final double x = x1 - x0;
		final double y = y1 - y0;
		final double distance = Math.sqrt( x * x + y * y );
		return distance;
	}

	/**
	 * Approximate a circle.
	 * 
	 * For a subdivision into n segments, the inner control points of each cubic
	 * Bézier segment should be tangential at a distance of 4/3 * tan(2*PI/n) to
	 * the closest end point.
	 */
	private GeneralPathRegionOfInterest makeCircle( final double x0,
			final double y0, final double radius )
	{
		final GeneralPath path = new GeneralPath();
		final double controlDistance =
				4.0 / 3.0 * Math.tan( 1.0 / 4.0 * ( Math.PI * 2 / 4 ) ) * radius;

		path.moveTo( x0 + radius, y0 );
		path.curveTo( x0 + radius, y0 - controlDistance, x0 + controlDistance, y0 -
				radius, x0, y0 - radius );
		path.curveTo( x0 - controlDistance, y0 - radius, x0 - radius, y0 -
				controlDistance, x0 - radius, y0 );
		path.curveTo( x0 - radius, y0 + controlDistance, x0 - controlDistance, y0 +
				radius, x0, y0 + radius );
		path.curveTo( x0 + controlDistance, y0 + radius, x0 + radius, y0 +
				controlDistance, x0 + radius, y0 );
		path.closePath();

		final GeneralPathRegionOfInterest roi = new GeneralPathRegionOfInterest();
		roi.setGeneralPath( path );

		return roi;
	}

	/**
	 * Generate a one pixel wide GeneralPathRegionOfInterest.
	 */
	private GeneralPathRegionOfInterest makeOnePixelWideRoi( final double x0,
			final double y0, final double height )
	{
		final GeneralPath path = new GeneralPath();

		path.moveTo( x0, y0 );
		path.lineTo( x0 + 1d, y0 );
		path.lineTo( x0 + 1d, y0 + height );
		path.lineTo( x0, y0 + height );
		path.closePath();

		final GeneralPathRegionOfInterest roi = new GeneralPathRegionOfInterest();
		roi.setGeneralPath( path );

		return roi;
	}

	@SuppressWarnings( "unused" )
	// for debugging
	private void showPath( final GeneralPath path )
	{
		final JFrame frame = new JFrame( "Path " + path );
		final JPanel panel = new JPanel()
		{

			private static final long serialVersionUID = 5294182503921406779L;

			@Override
			public void paint( final Graphics g )
			{
				final Graphics2D g2d = ( Graphics2D ) g;
				g2d.setColor( Color.BLACK );
				g2d.fill( path );
			}

			@Override
			public Dimension getPreferredSize()
			{
				final Rectangle bounds = path.getBounds();
				return new Dimension( bounds.width + bounds.x, bounds.height + bounds.y );
			}
		};
		frame.getContentPane().add( panel );
		frame.pack();
		frame.setVisible( true );
	}
}
