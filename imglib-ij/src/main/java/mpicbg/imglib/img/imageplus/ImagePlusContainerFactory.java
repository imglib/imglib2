/**
 * Copyright (c) 2009--2010, Funke, Preibisch, Saalfeld & Schindelin
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.img.imageplus;

import mpicbg.imglib.Interval;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.CharArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.container.basictypecontainer.array.LongArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.planar.PlanarImgFactory;
import mpicbg.imglib.type.NativeType;

/**
 * Factory that creates an appropriate {@link ImagePlusContainer}.
 * 
 * @author Jan Funke, Stephan Preibisch, Stephan Saalfeld, Johannes Schindelin
 */
public class ImagePlusContainerFactory< T extends NativeType< T > > extends PlanarImgFactory< T >
{
	@Override
	public ImagePlusContainer< T, ? > create( final long[] dim, final T type )
	{
		return ( ImagePlusContainer< T, ? > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public ImagePlusContainer< T, ? > create( final Interval interval, final T type )
	{
		final long[] dim = new long[ interval.numDimensions() ];
		interval.dimensions( dim );
		
		return create( dim, type );
	}

	@Override
	public NativeImg< T, BitArray > createBitInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusContainer< T, BitArray >( new BitArray( 1 ), dimensions, entitiesPerPixel );
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

		return new ImagePlusContainer< T, CharArray >( new CharArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, DoubleArray > createDoubleInstance( long[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusContainer< T, DoubleArray >( new DoubleArray( 1 ), dimensions, entitiesPerPixel );
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

		return new ImagePlusContainer< T, LongArray >( new LongArray( 1 ), dimensions, entitiesPerPixel );
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
			return new ImagePlusContainerFactory();
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}	
}
