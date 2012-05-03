/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package net.imglib2.img;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * Convenience factory methods for creation of the most common {@link Img}
 * variants.  Keep in mind that this cannot be a complete collection since
 * the number of combinations of existing pixel {@link Type}s and {@link Img}
 * implementations is already excessive---not to speak of {@link Type}s and
 * {@link Img} implementations that have not yet been introduced.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class Imgs
{
	private Imgs() {}
	
	/**
	 * Create an {@link ArrayImg}<{@link UnsignedByteType}, {@link ByteAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedByteType, ByteAccess > unsignedByteArray( final long... dim )
	{
		return ( ArrayImg< UnsignedByteType, ByteAccess > )new ArrayImgFactory< UnsignedByteType >().create( dim, new UnsignedByteType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link ByteType}, {@link ByteAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ByteType, ByteAccess > byteArray( final long... dim )
	{
		return ( ArrayImg< ByteType, ByteAccess > )new ArrayImgFactory< ByteType >().create( dim, new ByteType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link UnsignedShortType}, {@link ShortAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedShortType, ShortAccess > unsignedShortArray( final long... dim )
	{
		return ( ArrayImg< UnsignedShortType, ShortAccess > )new ArrayImgFactory< UnsignedShortType >().create( dim, new UnsignedShortType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link ShortType}, {@link ShortAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ShortType, ShortAccess > shortArray( final long... dim )
	{
		return ( ArrayImg< ShortType, ShortAccess > )new ArrayImgFactory< ShortType >().create( dim, new ShortType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link UnsignedIntType}, {@link IntAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedIntType, IntAccess > unsignedIntArray( final long... dim )
	{
		return ( ArrayImg< UnsignedIntType, IntAccess > )new ArrayImgFactory< UnsignedIntType >().create( dim, new UnsignedIntType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link IntType}, {@link IntAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< IntType, IntAccess > intArray( final long... dim )
	{
		return ( ArrayImg< IntType, IntAccess > )new ArrayImgFactory< IntType >().create( dim, new IntType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link LongType}, {@link LongAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< LongType, LongAccess > longArray( final long... dim )
	{
		return ( ArrayImg< LongType, LongAccess > )new ArrayImgFactory< LongType >().create( dim, new LongType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link BitType}, {@link ByteAccess}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< BitType, BitAccess > bitArray( final long... dim )
	{
		return ( ArrayImg< BitType, BitAccess > )new ArrayImgFactory< BitType >().create( dim, new BitType() );
	}
}
