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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/**
 * A region of interest that is defined as a list of closed Beziér curves,
 * combined via the even/odd winding rule. TODO: re-implement it without using
 * AWT, to allow use of ImgLib, say, on Android
 * 
 * @author Johannes Schindelin
 */
public class GeneralPathRegionOfInterest extends
		AbstractIterableRegionOfInterest implements GeneralPathSegmentHandler
{

	private GeneralPath path;

	private long[] stripes; // one-dimensional array for efficiency; these are
							// really { xStart, xEnd, y } triplets

	private int index;

	public GeneralPathRegionOfInterest()
	{
		super( 2 );
		path = new GeneralPath();
	}

	@Override
	public void moveTo( final double x, final double y )
	{
		path.moveTo( x, y );
	}

	@Override
	public void lineTo( final double x, final double y )
	{
		path.lineTo( x, y );
	}

	@Override
	public void quadTo( final double x1, final double y1, final double x, final double y )
	{
		path.quadTo( x1, y1, x, y );
	}

	@Override
	public void cubicTo( final double x1, final double y1, final double x2, final double y2, final double x, final double y )
	{
		path.curveTo( x1, y1, x2, y2, x, y );
	}

	@Override
	public void close()
	{
		path.closePath();
	}

	public void reset()
	{
		path.reset();
	}

	// TODO: remove
	public void setGeneralPath( final GeneralPath path )
	{
		this.path = path;
		this.stripes = null;
	}

	// TODO: remove
	public GeneralPath getGeneralPath()
	{
		return path;
	}

	// TODO: use an Interval
	public void iteratePath( final GeneralPathSegmentHandler handler )
	{
		final double[] coords = new double[ 6 ];
		for ( final PathIterator iterator = path.getPathIterator( null ); !iterator.isDone(); iterator.next() )
		{
			final int type = iterator.currentSegment( coords );
			switch ( type )
			{
			case PathIterator.SEG_MOVETO:
				handler.moveTo( coords[ 0 ], coords[ 1 ] );
				break;
			case PathIterator.SEG_LINETO:
				handler.lineTo( coords[ 0 ], coords[ 1 ] );
				break;
			case PathIterator.SEG_QUADTO:
				handler.quadTo( coords[ 0 ], coords[ 1 ], coords[ 2 ], coords[ 3 ] );
				break;
			case PathIterator.SEG_CUBICTO:
				handler.cubicTo( coords[ 0 ], coords[ 1 ], coords[ 2 ], coords[ 3 ], coords[ 4 ], coords[ 5 ] );
				break;
			case PathIterator.SEG_CLOSE:
				handler.close();
				break;
			default:
				throw new RuntimeException( "Unsupported segment type: " + type );
			}
		}
	}

	@Override
	protected boolean nextRaster( final long[] position, final long[] end )
	{
		ensureStripes();
		if ( index >= stripes.length )
		{
			index = 0;
			return false;
		}
		position[ 0 ] = stripes[ index ];
		end[ 0 ] = stripes[ index + 1 ];
		position[ 1 ] = end[ 1 ] = stripes[ index + 2 ];
		index += 3;
		return true;
	}

	@Override
	public boolean contains( final double[] position )
	{
		return path.contains( position[ 0 ], position[ 1 ] );
	}

	private void ensureStripes()
	{
		if ( stripes != null )
			return;

		// handle degenerate case gracefully
		if ( path.getPathIterator( null ).isDone() )
		{
			stripes = new long[ 0 ];
			return;
		}

		final Rectangle2D bounds = path.getBounds2D();
		final int left = ( int ) Math.floor( bounds.getMinX() );
		final int top = ( int ) Math.floor( bounds.getMinY() );
		final int width = ( int ) ( Math.ceil( bounds.getMaxX() ) - left );
		final int height = ( int ) ( Math.ceil( bounds.getMaxY() ) - top );

		final byte[] pixels = new byte[ width * height ];
		final ColorModel colorModel = new IndexColorModel( 1, 2, new byte[] { 0, 1 }, new byte[] { 0, 1 }, new byte[] { 0, 1 } );
		final SampleModel sampleModel = new MultiPixelPackedSampleModel( DataBuffer.TYPE_BYTE, width, height, 8 );
		final DataBuffer dataBuffer = new DataBufferByte( pixels, width * height );
		final WritableRaster raster = Raster.createWritableRaster( sampleModel, dataBuffer, null );
		final BufferedImage image = new BufferedImage( colorModel, raster, false, null );
		final GeneralPath transformed = new GeneralPath( path );
		transformed.transform( AffineTransform.getTranslateInstance( -bounds.getMinX(), -bounds.getMinY() ) );
		final Graphics2D g2d = ( Graphics2D ) image.getGraphics();
		g2d.setColor( Color.WHITE );
		g2d.setStroke( new BasicStroke( 0 ) );
		g2d.fill( transformed );

		final long[] strps = new long[ 3 * (width==1?2:width) * height / 2 ]; // avoid
																				// re-allocation
		int i = 0;
		for ( int y = 0; y < height; y++ )
		{
			long start = -1;
			for ( int x = 0; x < width; x++ )
			{
				final boolean inside = pixels[ x + width * y ] != 0;
				if ( start < 0 )
				{
					if ( inside )
					{
						start = x;
						strps[ i ] = x + left;
						strps[ i + 2 ] = y + top;
					}
				}
				else if ( !inside )
				{
					start = -1;
					strps[ i + 1 ] = x + left;
					i += 3;
				}
			}
			if ( start >= 0 )
			{
				start = -1;
				strps[ i + 1 ] = width + left;
				i += 3;
			}
		}

		this.stripes = new long[ i ];
		System.arraycopy( strps, 0, this.stripes, 0, i );
		this.index = 0;
	}

	@Override
	public void move( final double displacement, final int d )
	{
		if ( d != 0 && d != 1 )
			throw new IllegalArgumentException( "Cannot move 2D ROI in dimension " + d );
		final AffineTransform transform = AffineTransform.getTranslateInstance( d == 0 ? displacement : 0, d == 1 ? displacement : 0 );
		path.transform( transform );
	}

	@Override
	public void move( final double[] displacement )
	{
		if ( displacement.length != 2 )
			throw new IllegalArgumentException( "Cannot move 2D ROI in " + displacement.length + " dimensions" );
		final AffineTransform transform = AffineTransform.getTranslateInstance( displacement[ 0 ], displacement[ 1 ] );
		path.transform( transform );
	}

}
