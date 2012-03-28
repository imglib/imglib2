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

package net.imglib2.util;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Utility methods for developers
 * 
 * Stephan Preibisch, Curtis Rueden
 *
 *
 */
final public class DevUtil
{	
	private DevUtil() {}
	
	/**
	 * Creates an {@link ArrayImg} of UnsignedByteType from a java byte array by wrapping it
	 * 
	 * @param data - the array
	 * @param dim - the dimensionality
	 * 
	 * @return the instance of {@link ArrayImg} using the given byte array
	 */
	final public static ArrayImg<UnsignedByteType, ByteAccess> createImageFromArray( final byte[] data, final long[] dim )
	{
		final ByteAccess byteAccess = new ByteArray( data );
		final ArrayImg<UnsignedByteType, ByteAccess> array = 
			new ArrayImg<UnsignedByteType, ByteAccess>( byteAccess, dim, 1 );
			
		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return array;
	}

	/**
	 * Creates an {@link ArrayImg} of FloatType from a java float array by wrapping it
	 * 
	 * @param data - the array
	 * @param dim - the dimensionality
	 * 
	 * @return the instance of {@link ArrayImg} using the given float array
	 */
	final public static ArrayImg<FloatType,FloatAccess> createImageFromArray( final float[] data, final long[] dim )
	{
		final FloatAccess floatAccess = new FloatArray( data );
		final ArrayImg<FloatType, FloatAccess> array = 
			new ArrayImg<FloatType, FloatAccess>( floatAccess, dim, 1 );
			
		// create a Type that is linked to the container
		final FloatType linkedType = new FloatType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return array;
	}
}
