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
package net.imglib2.img.array;

import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Convenience factory methods for creation of {@link ArrayImg} instances with
 * the most common pixel {@link Type} variants.  Keep in mind that this cannot
 * be a complete collection since the number of existing pixel {@link Type}s
 * may be extended.
 * 
 * For pixel {@link Type}s T not present in this collection, use the generic
 * {@link ArrayImgFactory#create(long[], net.imglib2.type.NativeType)}, e.g.
 * 
 * <pre>
 * img = new ArrayImgFactory&lt;MyType&gt;.create(new long[]{100, 200}, new MyType());
 * </pre>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class ArrayImgs
{
	private ArrayImgs() {}
	
	/**
	 * Create an {@link ArrayImg}<{@link UnsignedByteType}, {@link ByteArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedByteType, ByteArray > unsignedBytes( final long... dim )
	{
		return ( ArrayImg< UnsignedByteType, ByteArray > )new ArrayImgFactory< UnsignedByteType >().create( dim, new UnsignedByteType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link ByteType}, {@link ByteArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ByteType, ByteArray > bytes( final long... dim )
	{
		return ( ArrayImg< ByteType, ByteArray > )new ArrayImgFactory< ByteType >().create( dim, new ByteType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link UnsignedShortType}, {@link ShortArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedShortType, ShortArray > unsignedShorts( final long... dim )
	{
		return ( ArrayImg< UnsignedShortType, ShortArray > )new ArrayImgFactory< UnsignedShortType >().create( dim, new UnsignedShortType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link ShortType}, {@link ShortArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ShortType, ShortArray > shorts( final long... dim )
	{
		return ( ArrayImg< ShortType, ShortArray > )new ArrayImgFactory< ShortType >().create( dim, new ShortType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link UnsignedIntType}, {@link IntArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedIntType, IntArray > unsignedInts( final long... dim )
	{
		return ( ArrayImg< UnsignedIntType, IntArray > )new ArrayImgFactory< UnsignedIntType >().create( dim, new UnsignedIntType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link IntType}, {@link IntArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< IntType, IntArray > ints( final long... dim )
	{
		return ( ArrayImg< IntType, IntArray > )new ArrayImgFactory< IntType >().create( dim, new IntType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link LongType}, {@link LongArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< LongType, LongArray > longs( final long... dim )
	{
		return ( ArrayImg< LongType, LongArray > )new ArrayImgFactory< LongType >().create( dim, new LongType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link BitType}, {@link BitArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< BitType, BitArray > bits( final long... dim )
	{
		return ( ArrayImg< BitType, BitArray > )new ArrayImgFactory< BitType >().create( dim, new BitType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link FloatType}, {@link FloatArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< FloatType, FloatArray > floats( final long... dim )
	{
		return ( ArrayImg< FloatType, FloatArray > )new ArrayImgFactory< FloatType >().create( dim, new FloatType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link DoubleType}, {@link DoubleArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< DoubleType, DoubleArray > doubles( final long... dim )
	{
		return ( ArrayImg< DoubleType, DoubleArray > )new ArrayImgFactory< DoubleType >().create( dim, new DoubleType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link ARGBType}, {@link IntArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ARGBType, IntArray > argbs( final long... dim )
	{
		return ( ArrayImg< ARGBType, IntArray > )new ArrayImgFactory< ARGBType >().create( dim, new ARGBType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link ComplexFloatType}, {@link FloatArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ComplexFloatType, FloatArray > complexFloats( final long... dim )
	{
		return ( ArrayImg< ComplexFloatType, FloatArray > )new ArrayImgFactory< ComplexFloatType >().create( dim, new ComplexFloatType() );
	}
	
	/**
	 * Create an {@link ArrayImg}<{@link ComplexDoubleType}, {@link DoubleArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ComplexDoubleType, DoubleArray > complexDoubles( final long... dim )
	{
		return ( ArrayImg< ComplexDoubleType, DoubleArray > )new ArrayImgFactory< ComplexDoubleType >().create( dim, new ComplexDoubleType() );
	}
}
