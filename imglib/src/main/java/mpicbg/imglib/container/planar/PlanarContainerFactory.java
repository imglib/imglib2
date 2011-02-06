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
package mpicbg.imglib.container.planar;

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
import mpicbg.imglib.type.NativeType;

/**
 * Factory that creates an appropriate {@link PlanarContainer}.
 * 
 * @author Jan Funke, Stephan Preibisch, Stephan Saalfeld, Johannes Schindelin
 */
public class PlanarContainerFactory< T extends NativeType<T> > extends NativeContainerFactory< T >
{
	@Override
	public NativeContainer< T, BitArray > createBitInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T, BitArray >( new BitArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ByteArray > createByteInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T , ByteArray >( new ByteArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, CharArray > createCharInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T, CharArray >( new CharArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, DoubleArray > createDoubleInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T, DoubleArray >( new DoubleArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, FloatArray > createFloatInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T, FloatArray >( new FloatArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, IntArray > createIntInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T, IntArray >( new IntArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, LongArray > createLongInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T, LongArray >( new LongArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeContainer< T, ShortArray > createShortInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarContainer< T, ShortArray >( new ShortArray( 1 ), dimensions, entitiesPerPixel );
	}
}
