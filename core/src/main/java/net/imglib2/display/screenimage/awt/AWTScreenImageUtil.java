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
package net.imglib2.display.screenimage.awt;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Utility class to create {@link AWTScreenImage}s.
 * 
 * TODO: Add convenience methods to render {@link RandomAccessibleInterval}s.
 * 
 * @author Christian Dietz
 * 
 */
public class AWTScreenImageUtil
{

	/**
	 * Get an appropriate {@link AWTScreenImage} given a type and the
	 * dimensionality of the incoming image.
	 * 
	 * <p>
	 * Only the first two dimensions of the long[] dims are considered.
	 * </p>
	 * 
	 * TODO: review if this is really the only solution to get it running with
	 * jenkins javac.
	 * 
	 * @param type
	 *            type used to create empty {@link AWTScreenImage}
	 * @param dims
	 *            dimensions of the resulting {@link ArrayImgAWTScreenImage}
	 * @return
	 * 
	 *         // HACK: raw-cast of container to ArrayImgAWTScreenImage needed
	 *         for Sun Java 6 compiler
	 * 
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static < T extends NativeType< T >> ArrayImgAWTScreenImage< T, ? > emptyScreenImage( final T type, final long[] dims )
	{

		if ( ByteType.class.isAssignableFrom( type.getClass() ) )
		{
			final ByteArray array = new ByteArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< ByteType, ByteArray > container = new ByteAWTScreenImage( new ByteType( array ), array, dims );
			container.setLinkedType( new ByteType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		if ( UnsignedByteType.class.isAssignableFrom( type.getClass() ) )
		{
			final ByteArray array = new ByteArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< UnsignedByteType, ByteArray > container = new UnsignedByteAWTScreenImage( new UnsignedByteType( array ), array, dims );
			container.setLinkedType( new UnsignedByteType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		if ( ShortType.class.isAssignableFrom( type.getClass() ) )
		{
			final ShortArray array = new ShortArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< ShortType, ShortArray > container = new ShortAWTScreenImage( new ShortType( array ), array, dims );
			container.setLinkedType( new ShortType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		if ( UnsignedShortType.class.isAssignableFrom( type.getClass() ) )
		{
			final ShortArray array = new ShortArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< UnsignedShortType, ShortArray > container = new UnsignedShortAWTScreenImage( new UnsignedShortType( array ), array, dims );
			container.setLinkedType( new UnsignedShortType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		if ( IntType.class.isAssignableFrom( type.getClass() ) )
		{
			final IntArray array = new IntArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< IntType, IntArray > container = new IntAWTScreenImage( new IntType( array ), array, dims );
			container.setLinkedType( new IntType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		if ( UnsignedIntType.class.isAssignableFrom( type.getClass() ) )
		{
			final IntArray array = new IntArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< UnsignedIntType, IntArray > container = new UnsignedIntAWTScreenImage( new UnsignedIntType( array ), array, dims );
			container.setLinkedType( new UnsignedIntType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		if ( FloatType.class.isAssignableFrom( type.getClass() ) )
		{
			final FloatArray array = new FloatArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< FloatType, FloatArray > container = new FloatAWTScreenImage( new FloatType( array ), array, dims );
			container.setLinkedType( new FloatType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		if ( DoubleType.class.isAssignableFrom( type.getClass() ) )
		{
			final DoubleArray array = new DoubleArray( numElements( dims ) );
			final ArrayImgAWTScreenImage< DoubleType, DoubleArray > container = new DoubleAWTScreenImage( new DoubleType( array ), array, dims );
			container.setLinkedType( new DoubleType( container ) );
			return ( ArrayImgAWTScreenImage ) container;
		}

		throw new IllegalArgumentException( "Can't find AWTScreenImage for type " + type.toString() + "!" );
	}

	// only the first two dimensions are considered
	private static int numElements( final long[] dims )
	{
		return ( int ) ( dims[ 0 ] * dims[ 1 ] );
	}

}
