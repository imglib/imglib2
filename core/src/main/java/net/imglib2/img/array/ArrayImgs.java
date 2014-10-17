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

package net.imglib2.img.array;

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
import net.imglib2.util.Fraction;

/**
 * <p>
 * Convenience factory methods for creation of {@link ArrayImg} instances with
 * the most common pixel {@link Type} variants. The collection includes
 * factories to re-use existing primitive type arrays as data. This can be used
 * for in-place access to data from other libraries such as AWT or ImageJ. Keep
 * in mind that this cannot be a complete collection since the number of
 * existing pixel {@link Type}s may be extended.
 * </p>
 * 
 * <p>
 * For pixel {@link Type}s T not present in this collection, use the generic
 * {@link ArrayImgFactory#create(long[], net.imglib2.type.NativeType)}, e.g.
 * </p>
 * 
 * <pre>
 * img = new ArrayImgFactory&lt; MyType &gt;.create( new long[] { 100, 200 }, new MyType() );
 * </pre>
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class ArrayImgs
{
	private ArrayImgs()
	{}

	/**
	 * Create an {@link ArrayImg}<{@link UnsignedByteType}, {@link ByteArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedByteType, ByteArray > unsignedBytes( final long... dim )
	{
		return ( ArrayImg< UnsignedByteType, ByteArray > ) new ArrayImgFactory< UnsignedByteType >().create( dim, new UnsignedByteType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link UnsignedByteType}, {@link ByteArray}>
	 * reusing a passed byte[] array.
	 */
	final public static ArrayImg< UnsignedByteType, ByteArray > unsignedBytes( final byte[] array, final long... dim )
	{
		final ByteArray access = new ByteArray( array );
		final ArrayImg< UnsignedByteType, ByteArray > img = new ArrayImg< UnsignedByteType, ByteArray >( access, dim, new Fraction() );
		final UnsignedByteType t = new UnsignedByteType( img );		
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link ByteType}, {@link ByteArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ByteType, ByteArray > bytes( final long... dim )
	{
		return ( ArrayImg< ByteType, ByteArray > ) new ArrayImgFactory< ByteType >().create( dim, new ByteType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link ByteType}, {@link ByteArray}> reusing
	 * a passed byte[] array.
	 */
	final public static ArrayImg< ByteType, ByteArray > bytes( final byte[] array, final long... dim )
	{
		final ByteArray access = new ByteArray( array );
		final ArrayImg< ByteType, ByteArray > img = new ArrayImg< ByteType, ByteArray >( access, dim, new Fraction() );
		final ByteType t = new ByteType( img );		
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link UnsignedShortType}, {@link ShortArray}
	 * >.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedShortType, ShortArray > unsignedShorts( final long... dim )
	{
		return ( ArrayImg< UnsignedShortType, ShortArray > ) new ArrayImgFactory< UnsignedShortType >().create( dim, new UnsignedShortType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link UnsignedShortType}, {@link ShortArray}
	 * > reusing a passed short[] array.
	 */
	final public static ArrayImg< UnsignedShortType, ShortArray > unsignedShorts( final short[] array, final long... dim )
	{
		final ShortArray access = new ShortArray( array );
		final ArrayImg< UnsignedShortType, ShortArray > img = new ArrayImg< UnsignedShortType, ShortArray >( access, dim, new Fraction() );
		final UnsignedShortType t = new UnsignedShortType( img );		
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link ShortType}, {@link ShortArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ShortType, ShortArray > shorts( final long... dim )
	{
		return ( ArrayImg< ShortType, ShortArray > ) new ArrayImgFactory< ShortType >().create( dim, new ShortType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link ShortType}, {@link ShortArray}>
	 * reusing a passed short[] array.
	 */
	final public static ArrayImg< ShortType, ShortArray > shorts( final short[] array, final long... dim )
	{
		final ShortArray access = new ShortArray( array );
		final ArrayImg< ShortType, ShortArray > img = new ArrayImg< ShortType, ShortArray >( access, dim, new Fraction() );
		final ShortType t = new ShortType( img );		
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link UnsignedIntType}, {@link IntArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedIntType, IntArray > unsignedInts( final long... dim )
	{
		return ( ArrayImg< UnsignedIntType, IntArray > ) new ArrayImgFactory< UnsignedIntType >().create( dim, new UnsignedIntType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link UnsignedIntType}, {@link IntArray}>
	 * reusing a passed int[] array.
	 */
	final public static ArrayImg< UnsignedIntType, IntArray > unsignedInts( final int[] array, final long... dim )
	{
		final IntArray access = new IntArray( array );
		final ArrayImg< UnsignedIntType, IntArray > img = new ArrayImg< UnsignedIntType, IntArray >( access, dim, new Fraction() );
		final UnsignedIntType t = new UnsignedIntType( img );		
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link IntType}, {@link IntArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< IntType, IntArray > ints( final long... dim )
	{
		return ( ArrayImg< IntType, IntArray > ) new ArrayImgFactory< IntType >().create( dim, new IntType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link IntType}, {@link IntArray}> reusing a
	 * passed int[] array.
	 */
	final public static ArrayImg< IntType, IntArray > ints( final int[] array, final long... dim )
	{
		final IntArray access = new IntArray( array );
		final ArrayImg< IntType, IntArray > img = new ArrayImg< IntType, IntArray >( access, dim, new Fraction() );
		final IntType t = new IntType( img );	
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link LongType}, {@link LongArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< LongType, LongArray > longs( final long... dim )
	{
		return ( ArrayImg< LongType, LongArray > ) new ArrayImgFactory< LongType >().create( dim, new LongType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link LongType}, {@link LongArray}> reusing
	 * a passed long[] array.
	 */
	final public static ArrayImg< LongType, LongArray > longs( final long[] array, final long... dim )
	{
		final LongArray access = new LongArray( array );
		final ArrayImg< LongType, LongArray > img = new ArrayImg< LongType, LongArray >( access, dim, new Fraction() );
		final LongType t = new LongType( img );	
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link BitType}, {@link BitArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< BitType, LongArray > bits( final long... dim )
	{
		return ( ArrayImg< BitType, LongArray > ) new ArrayImgFactory< BitType >().create( dim, new BitType() );
	}

	/**
	 * Create an {@link ArrayImg}<{@link FloatType}, {@link FloatArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< FloatType, FloatArray > floats( final long... dim )
	{
		return ( ArrayImg< FloatType, FloatArray > ) new ArrayImgFactory< FloatType >().create( dim, new FloatType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link FloatType}, {@link FloatArray}>
	 * reusing a passed float[] array.
	 */
	final public static ArrayImg< FloatType, FloatArray > floats( final float[] array, final long... dim )
	{
		final FloatArray access = new FloatArray( array );
		final ArrayImg< FloatType, FloatArray > img = new ArrayImg< FloatType, FloatArray >( access, dim, new Fraction() );
		final FloatType t = new FloatType( img );	
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link DoubleType}, {@link DoubleArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< DoubleType, DoubleArray > doubles( final long... dim )
	{
		return ( ArrayImg< DoubleType, DoubleArray > ) new ArrayImgFactory< DoubleType >().create( dim, new DoubleType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link DoubleType}, {@link DoubleArray}>
	 * reusing a passed double[] array.
	 */
	final public static ArrayImg< DoubleType, DoubleArray > doubles( final double[] array, final long... dim )
	{
		final DoubleArray access = new DoubleArray( array );
		final ArrayImg< DoubleType, DoubleArray > img = new ArrayImg< DoubleType, DoubleArray >( access, dim, new Fraction() );
		final DoubleType t = new DoubleType( img );	
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link ARGBType}, {@link IntArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ARGBType, IntArray > argbs( final long... dim )
	{
		return ( ArrayImg< ARGBType, IntArray > ) new ArrayImgFactory< ARGBType >().create( dim, new ARGBType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link ARGBType}, {@link IntArray}> reusing a
	 * passed int[] array.
	 */
	final public static ArrayImg< ARGBType, IntArray > argbs( final int[] array, final long... dim )
	{
		final IntArray access = new IntArray( array );
		final ArrayImg< ARGBType, IntArray > img = new ArrayImg< ARGBType, IntArray >( access, dim, new Fraction() );
		final ARGBType t = new ARGBType( img );	
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link ComplexFloatType}, {@link FloatArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ComplexFloatType, FloatArray > complexFloats( final long... dim )
	{
		return ( ArrayImg< ComplexFloatType, FloatArray > ) new ArrayImgFactory< ComplexFloatType >().create( dim, new ComplexFloatType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link FloatType}, {@link FloatArray}>
	 * reusing a passed float[] array.
	 */
	final public static ArrayImg< ComplexFloatType, FloatArray > complexFloats( final float[] array, final long... dim )
	{
		final FloatArray access = new FloatArray( array );
		final ArrayImg< ComplexFloatType, FloatArray > img = new ArrayImg< ComplexFloatType, FloatArray >( access, dim, new Fraction( 2, 1 ) );
		final ComplexFloatType t = new ComplexFloatType( img );	
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}<{@link ComplexDoubleType}, {@link DoubleArray}
	 * >.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ComplexDoubleType, DoubleArray > complexDoubles( final long... dim )
	{
		return ( ArrayImg< ComplexDoubleType, DoubleArray > ) new ArrayImgFactory< ComplexDoubleType >().create( dim, new ComplexDoubleType() );
	}

	/**
	 * Creates an {@link ArrayImg}<{@link DoubleType}, {@link DoubleArray}>
	 * reusing a passed double[] array.
	 */
	final public static ArrayImg< ComplexDoubleType, DoubleArray > complexDoubles( final double[] array, final long... dim )
	{
		final DoubleArray access = new DoubleArray( array );
		final ArrayImg< ComplexDoubleType, DoubleArray > img = new ArrayImg< ComplexDoubleType, DoubleArray >( access, dim, new Fraction( 2, 1 ) );
		final ComplexDoubleType t = new ComplexDoubleType( img );	
		img.setLinkedType( t );
		return img;
	}
}
