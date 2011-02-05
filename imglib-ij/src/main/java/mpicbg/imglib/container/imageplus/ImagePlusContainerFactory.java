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
package mpicbg.imglib.container.imageplus;

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.CharArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.container.basictypecontainer.array.LongArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.type.Type;

/**
 * Factory that creates an appropriate {@link ImagePlusContainer}.
 * 
 * @author Jan Funke, Stephan Preibisch, Stephan Saalfeld, Johannes Schindelin
 */
public class ImagePlusContainerFactory extends NativeContainerFactory
{
	@Override
	public < T extends Type< T > > NativeContainer< T, BitArray > createBitInstance( int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusContainer< T, BitArray >( this, new BitArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > NativeContainer< T, ByteArray > createByteInstance( final int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ByteImagePlus< T >( this, dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > NativeContainer< T, CharArray > createCharInstance( int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusContainer< T, CharArray >( this, new CharArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > NativeContainer< T, DoubleArray > createDoubleInstance( int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusContainer< T, DoubleArray >( this, new DoubleArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > NativeContainer< T, FloatArray > createFloatInstance( int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new FloatImagePlus< T >( this, dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > NativeContainer< T, IntArray > createIntInstance( int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new IntImagePlus< T >( this, dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > NativeContainer< T, LongArray > createLongInstance( int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ImagePlusContainer< T, LongArray >( this, new LongArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > NativeContainer< T, ShortArray > createShortInstance( int[] dimensions, final int entitiesPerPixel )
	{
		if ( dimensions.length > 5 )
			throw new RuntimeException( "Unsupported dimensionality: " + dimensions.length );

		return new ShortImagePlus< T >( this, dimensions, entitiesPerPixel );
	}

	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printProperties()
	{
	// TODO Auto-generated method stub

	}

	@Override
	public void setParameters( String configuration )
	{
	// TODO Auto-generated method stub

	}

}
