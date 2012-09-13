/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.img.imageplus;

import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.NativeType;

/**
 * Factory that creates an appropriate {@link ImagePlusImg}.
 * 
 * @author Funke
 * @author Preibisch
 * @author Saalfeld
 * @author Schindelin
 * @author Jan Funke
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Johannes Schindelin
 */
public class ImagePlusImgFactory< T extends NativeType< T > > extends PlanarImgFactory< T >
{
	@Override
	public ImagePlusImg< T, ? > create( final long[] dim, final T type )
	{
		return ( ImagePlusImg< T, ? > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public ImagePlusImg< T, ? > create( final Dimensions dim, final T type )
	{
		final long[] size = new long[ dim.numDimensions() ];
		dim.dimensions( size );

		return create( size, type );
	}

	@Override
	public NativeImg< T, BitArray > createBitInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusImg< T, BitArray >( new BitArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, ByteArray > createByteInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ByteImagePlus< T >( dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, CharArray > createCharInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusImg< T, CharArray >( new CharArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, DoubleArray > createDoubleInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusImg< T, DoubleArray >( new DoubleArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, FloatArray > createFloatInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new FloatImagePlus< T >( dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, IntArray > createIntInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new IntImagePlus< T >( dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, LongArray > createLongInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusImg< T, LongArray >( new LongArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, ShortArray > createShortInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ShortImagePlus< T >( dimensions, entitiesPerPixel );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <S> ImgFactory<S> imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new ImagePlusImgFactory();
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}	
}
