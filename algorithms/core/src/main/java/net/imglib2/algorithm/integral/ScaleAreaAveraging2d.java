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

package net.imglib2.algorithm.integral;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.converter.Converter;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * TODO
 * 
 */
public class ScaleAreaAveraging2d< T extends RealType< T >, R extends RealType< R >> implements OutputAlgorithm< Img< R > >
{
	protected ImgFactory< R > imgFactory;

	protected Img< R > scaled;

	protected RandomAccessibleInterval< T > integralImg;

	protected String error;

	protected final long[] size;

	final R targetType;

	final Converter< T, R > converter;

	/**
	 * @param integralImg
	 *            The instance of {@link IntegralImg} or equivalent.
	 * @param targetType
	 *            The desired type of the scaled image.
	 * @param size
	 *            The target dimensions of the desired scaled image.
	 */
	@Deprecated
	public ScaleAreaAveraging2d( final Img< T > integralImg, final R targetType, final long[] size )
	{
		this( integralImg, targetType, size, null );

		try
		{
			this.imgFactory = integralImg.factory().imgFactory( targetType );
		}
		catch ( final IncompatibleTypeException e )
		{
			this.imgFactory = new ListImgFactory< R >();
		}
	}

	/**
	 * @param integralImg
	 *            The instance of {@link IntegralImg} or equivalent.
	 * @param targetType
	 *            The desired type of the scaled image.
	 * @param size
	 *            The target dimensions of the desired scaled image.
	 */
	@SuppressWarnings( "unchecked" )
	public ScaleAreaAveraging2d( final RandomAccessibleInterval< T > integralImg, final R targetType, final long[] size, final ImgFactory< R > imgFactory )
	{
		this.size = size;
		this.targetType = targetType;
		this.integralImg = integralImg;
		this.imgFactory = imgFactory;

		if ( targetType.getClass().isInstance( Views.iterable( integralImg ).firstElement().createVariable() ) )
		{
			// double cast to workaround javac error
			converter = ( Converter< T, R > ) ( Converter< ?, ? > ) new TypeIdentity< T >();
		}
		else
		{
			converter = new Converter< T, R >()
			{
				@Override
				public void convert( final T input, final R output )
				{
					output.setReal( input.getRealDouble() );
				}
			};
		}
	}

	@Deprecated
	public ScaleAreaAveraging2d( final Img< T > integralImg, final R targetType, final Converter< T, R > converter, final long[] size )
	{
		this( integralImg, targetType, converter, size, null );

		try
		{
			this.imgFactory = integralImg.factory().imgFactory( targetType );
		}
		catch ( final IncompatibleTypeException e )
		{
			this.imgFactory = new ListImgFactory< R >();
		}
	}

	public ScaleAreaAveraging2d( final RandomAccessibleInterval< T > integralImg, final R targetType, final Converter< T, R > converter, final long[] size, final ImgFactory< R > imgFactory )
	{
		this.size = size;
		this.targetType = targetType;
		this.integralImg = integralImg;
		this.converter = converter;
		this.imgFactory = imgFactory;
	}

	/**
	 * Set the desired dimensions of the scaled image obtainable after invoking
	 * {@link #process()} via {@link #getResult()}.
	 * 
	 * @param width
	 * @param height
	 */
	public void setOutputDimensions( final long width, final long height )
	{
		size[ 0 ] = width;
		size[ 1 ] = height;
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public boolean process()
	{
		scaled = imgFactory.create( size, targetType );

		final Cursor< R > cursor = scaled.cursor();
		final RandomAccess< T > c2 = integralImg.randomAccess();

		final T sum = Views.iterable( integralImg ).firstElement().createVariable();
		final T area = sum.createVariable();

		if ( isIntegerDivision( integralImg, scaled ) )
		{
			final long stepSizeX = ( integralImg.dimension( 0 ) - 1 ) / size[ 0 ];
			final long stepSizeY = ( integralImg.dimension( 1 ) - 1 ) / size[ 1 ];
			area.setReal( stepSizeX * stepSizeY );

			// final int vX = stepSizeX;
			// final int vY = stepSizeY;

			while ( cursor.hasNext() )
			{
				cursor.fwd();

//				final int px = cursor.getPosition( 0 );
//				final int py = cursor.getPosition( 1 );
//
//				final int startX = px * stepSizeX;
//				final int startY = py *	stepSizeY;
//
//				computeAverage( startX, startY, vX, vY, c2, sum );				

				// Same as above, without intermediary variables:
				computeSum(
						cursor.getLongPosition( 0 ) * stepSizeX,
						cursor.getLongPosition( 1 ) * stepSizeY,
						stepSizeX, stepSizeY, // vX, vY,
						c2, sum );

				sum.div( area );

//				System.out.println( sum );
//				System.exit( 0 );

				converter.convert( sum, cursor.get() );
			}
		}
		else
		{
			final double stepSizeX = ( ( double ) integralImg.dimension( 0 ) - 1 ) / size[ 0 ];
			final double stepSizeY = ( ( double ) integralImg.dimension( 1 ) - 1 ) / size[ 1 ];

			while ( cursor.hasNext() )
			{
				cursor.fwd();

				final long px = cursor.getLongPosition( 0 );
				final long py = cursor.getLongPosition( 1 );

				final double tmp1 = px * stepSizeX + 0.5;
				final long startX = ( long ) ( tmp1 );
				final long vX = ( long ) ( tmp1 + stepSizeX ) - startX;

				final double tmp2 = py * stepSizeY + 0.5;
				final long startY = ( long ) ( tmp2 );
				final long vY = ( long ) ( tmp2 + stepSizeY ) - startY;

				area.setReal( vX * vY );

				computeSum( startX, startY, vX, vY, c2, sum );

				sum.div( area );

				converter.convert( sum, cursor.get() );
			}
		}

		return true;
	}

	final private static < T extends RealType< T >> void computeSum( final long startX, final long startY, final long vX, final long vY,
			final RandomAccess< T > c2, final T sum )
	{
		c2.setPosition( startX, 0 );
		c2.setPosition( startY, 1 );
		sum.set( c2.get() );

		c2.move( vX, 0 );
		sum.sub( c2.get() );

		c2.move( vY, 1 );
		sum.add( c2.get() );

		c2.move( -vX, 0 );
		sum.sub( c2.get() );
	}

	/**
	 * The dimensions of the integral image are always +1 from the integrated
	 * image.
	 */
	protected static final boolean isIntegerDivision( final RandomAccessibleInterval< ? > integralImg, final RandomAccessibleInterval< ? > scaled )
	{
		for ( int d = 0; d < scaled.numDimensions(); ++d )
			if ( 0 != ( integralImg.dimension( d ) - 1 ) % scaled.dimension( d ) )
				return false;

		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return error;
	}

	@Override
	public Img< R > getResult()
	{
		return scaled;
	}

}
