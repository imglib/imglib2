/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

import net.imglib2.img.basictypeaccess.BooleanAccess;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.img.basictypeaccess.array.BooleanArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned128BitType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.Unsigned2BitType;
import net.imglib2.type.numeric.integer.Unsigned4BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.integer.UnsignedVariableBitLengthType;
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
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
final public class ArrayImgs
{
	private ArrayImgs()
	{}

	/**
	 * Create an {@link ArrayImg}&lt;{@link UnsignedByteType}, {@link ByteArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedByteType, ByteArray > unsignedBytes( final long... dim )
	{
		return ( ArrayImg< UnsignedByteType, ByteArray > ) new ArrayImgFactory<>( new UnsignedByteType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedByteType}, {@link ByteArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static ArrayImg< UnsignedByteType, ByteArray > unsignedBytes( final byte[] array, final long... dim )
	{
		return unsignedBytes( new ByteArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedByteType},
	 * {@link ByteAccess}&gt; using a {@link ByteAccess} passed as argument.
	 */
	final public static < A extends ByteAccess > ArrayImg< UnsignedByteType, A > unsignedBytes( final A access, final long... dim )
	{
		final ArrayImg< UnsignedByteType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final UnsignedByteType t = new UnsignedByteType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link ByteType}, {@link ByteArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ByteType, ByteArray > bytes( final long... dim )
	{
		return ( ArrayImg< ByteType, ByteArray > ) new ArrayImgFactory<>( new ByteType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ByteType}, {@link ByteArray}&gt; reusing
	 * a passed byte[] array.
	 */
	final public static ArrayImg< ByteType, ByteArray > bytes( final byte[] array, final long... dim )
	{
		return bytes( new ByteArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ByteType}, {@link ByteAccess}&gt;
	 * using a {@link ByteAccess} passed as argument.
	 */
	final public static < A extends ByteAccess > ArrayImg< ByteType, A > bytes( final A access, final long... dim )
	{
		final ArrayImg< ByteType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final ByteType t = new ByteType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link UnsignedShortType},
	 * {@link ShortArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedShortType, ShortArray > unsignedShorts( final long... dim )
	{
		return ( ArrayImg< UnsignedShortType, ShortArray > ) new ArrayImgFactory<>( new UnsignedShortType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedShortType},
	 * {@link ShortArray}&gt; reusing a passed short[] array.
	 */
	final public static ArrayImg< UnsignedShortType, ShortArray > unsignedShorts( final short[] array, final long... dim )
	{
		return unsignedShorts( new ShortArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedShortType},
	 * {@link ShortAccess}&gt; using a {@link ShortAccess} passed as argument.
	 */
	final public static < A extends ShortAccess > ArrayImg< UnsignedShortType, A > unsignedShorts( final A access, final long... dim )
	{
		final ArrayImg< UnsignedShortType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final UnsignedShortType t = new UnsignedShortType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link ShortType}, {@link ShortArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ShortType, ShortArray > shorts( final long... dim )
	{
		return ( ArrayImg< ShortType, ShortArray > ) new ArrayImgFactory<>( new ShortType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ShortType}, {@link ShortArray}&gt;
	 * reusing a passed short[] array.
	 */
	final public static ArrayImg< ShortType, ShortArray > shorts( final short[] array, final long... dim )
	{
		return shorts( new ShortArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ShortType}, {@link ShortAccess}&gt;
	 * using a {@link ShortAccess} passed as argument.
	 */
	final public static < A extends ShortAccess > ArrayImg< ShortType, A > shorts( final A access, final long... dim )
	{
		final ArrayImg< ShortType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final ShortType t = new ShortType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link UnsignedIntType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedIntType, IntArray > unsignedInts( final long... dim )
	{
		return ( ArrayImg< UnsignedIntType, IntArray > ) new ArrayImgFactory<>( new UnsignedIntType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedIntType}, {@link IntArray}&gt;
	 * reusing a passed int[] array.
	 */
	final public static ArrayImg< UnsignedIntType, IntArray > unsignedInts( final int[] array, final long... dim )
	{
		return unsignedInts( new IntArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedIntType},
	 * {@link IntAccess}&gt; using a {@link IntAccess} passed as argument.
	 */
	final public static < A extends IntAccess > ArrayImg< UnsignedIntType, A > unsignedInts( final A access, final long... dim )
	{
		final ArrayImg< UnsignedIntType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final UnsignedIntType t = new UnsignedIntType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link IntType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< IntType, IntArray > ints( final long... dim )
	{
		return ( ArrayImg< IntType, IntArray > ) new ArrayImgFactory<>( new IntType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link IntType}, {@link IntArray}&gt; reusing a
	 * passed int[] array.
	 */
	final public static ArrayImg< IntType, IntArray > ints( final int[] array, final long... dim )
	{
		return ints( new IntArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link IntType}, {@link IntAccess}&gt;
	 * using a {@link IntAccess} passed as argument.
	 */
	final public static < A extends IntAccess > ArrayImg< IntType, A > ints( final A access, final long... dim )
	{
		final ArrayImg< IntType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final IntType t = new IntType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link UnsignedLongType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedLongType, LongArray > unsignedLongs( final long... dim )
	{
		return ( ArrayImg< UnsignedLongType, LongArray > ) new ArrayImgFactory<>( new UnsignedLongType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedLongType}, {@link LongArray}&gt;
	 * reusing a passed long[] array.
	 *
	 * @deprecated use {@link ArrayImgs#unsignedLongs(long[], long...)}
	 */
	@Deprecated
	final public static ArrayImg< UnsignedLongType, LongArray > unsignedLongss( final long[] array, final long... dim )
	{
		return unsignedLongs( array, dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedLongType}, {@link LongArray}&gt;
	 * reusing a passed long[] array.
	 */
	final public static ArrayImg< UnsignedLongType, LongArray > unsignedLongs( final long[] array, final long... dim )
	{
		return unsignedLongs( new LongArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedLongType},
	 * {@link LongAccess}&gt; using a {@link LongAccess} passed as argument.
	 */
	final public static < A extends LongAccess > ArrayImg< UnsignedLongType, A > unsignedLongs( final A access, final long... dim )
	{
		final ArrayImg< UnsignedLongType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final UnsignedLongType t = new UnsignedLongType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link LongType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< LongType, LongArray > longs( final long... dim )
	{
		return ( ArrayImg< LongType, LongArray > ) new ArrayImgFactory<>( new LongType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link LongType}, {@link LongArray}&gt; reusing
	 * a passed long[] array.
	 */
	final public static ArrayImg< LongType, LongArray > longs( final long[] array, final long... dim )
	{
		return longs( new LongArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link LongType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final public static < A extends LongAccess > ArrayImg< LongType, A > longs( final A access, final long... dim )
	{
		final ArrayImg< LongType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final LongType t = new LongType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< NativeBoolType, BooleanArray > booleans( final long... dim )
	{
		return ( ArrayImg< NativeBoolType, BooleanArray > ) new ArrayImgFactory<>( new NativeBoolType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static ArrayImg< NativeBoolType, BooleanArray > booleans( final boolean[] array, final long... dim )
	{
		return booleans( new BooleanArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link NativeBoolType},
	 * {@link BooleanAccess}&gt; using a {@link BooleanAccess} passed as argument.
	 */
	final public static < A extends BooleanAccess > ArrayImg< NativeBoolType, A > booleans( final A access, final long... dim )
	{
		final ArrayImg< NativeBoolType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final NativeBoolType t = new NativeBoolType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link BitType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< BitType, LongArray > bits( final long... dim )
	{
		return ( ArrayImg< BitType, LongArray > ) new ArrayImgFactory<>( new BitType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link NativeBoolType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final static public < A extends BooleanAccess > ArrayImg< NativeBoolType, A > bits( final A access, final long... dim )
	{
		final ArrayImg< NativeBoolType, A > img = new ArrayImg<>( access, dim, new Fraction( 1, 64 ) );
		final NativeBoolType t = new NativeBoolType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link BitType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final static public < A extends LongAccess > ArrayImg< BitType, A > bits( final A access, final long... dim )
	{
		final ArrayImg< BitType, A > img = new ArrayImg<>( access, dim, new Fraction( 1, 64 ) );
		final BitType t = new BitType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link Unsigned2BitType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< Unsigned2BitType, LongArray > unsigned2Bits( final long... dim )
	{
		return ( ArrayImg< Unsigned2BitType, LongArray > ) new ArrayImgFactory<>( new Unsigned2BitType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link Unsigned2BitType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final static public < A extends LongAccess > ArrayImg< Unsigned2BitType, A > unsigned2Bits( final A access, final long... dim )
	{
		final ArrayImg< Unsigned2BitType, A > img = new ArrayImg<>( access, dim, new Fraction( 2, 64 ) );
		final Unsigned2BitType t = new Unsigned2BitType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link Unsigned4BitType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< Unsigned4BitType, LongArray > unsigned4Bits( final long... dim )
	{
		return ( ArrayImg< Unsigned4BitType, LongArray > ) new ArrayImgFactory<>( new Unsigned4BitType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link Unsigned4BitType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final static public < A extends LongAccess > ArrayImg< Unsigned4BitType, A > unsigned4Bits( final A access, final long... dim )
	{
		final ArrayImg< Unsigned4BitType, A > img = new ArrayImg<>( access, dim, new Fraction( 4, 64 ) );
		final Unsigned4BitType t = new Unsigned4BitType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link Unsigned12BitType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< Unsigned12BitType, LongArray > unsigned12Bits( final long... dim )
	{
		return ( ArrayImg< Unsigned12BitType, LongArray > ) new ArrayImgFactory<>( new Unsigned12BitType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link Unsigned12BitType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final static public < A extends LongAccess > ArrayImg< Unsigned12BitType, A > unsigned12Bits( final A access, final long... dim )
	{
		final ArrayImg< Unsigned12BitType, A > img = new ArrayImg<>( access, dim, new Fraction( 12, 64 ) );
		final Unsigned12BitType t = new Unsigned12BitType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link Unsigned128BitType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< Unsigned128BitType, LongArray > unsigned128Bits( final long... dim )
	{
		return ( ArrayImg< Unsigned128BitType, LongArray > ) new ArrayImgFactory<>( new Unsigned128BitType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link Unsigned128BitType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final static public < A extends LongAccess > ArrayImg< Unsigned128BitType, A > unsigned128Bits( final A access, final long... dim )
	{
		final ArrayImg< Unsigned128BitType, A > img = new ArrayImg<>( access, dim, new Fraction( 128, 64 ) );
		final Unsigned128BitType t = new Unsigned128BitType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link UnsignedVariableBitLengthType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< UnsignedVariableBitLengthType, LongArray > unsignedVariableBitLengths( final int nbits, final long... dim )
	{
		return ( ArrayImg< UnsignedVariableBitLengthType, LongArray > ) new ArrayImgFactory<>( new UnsignedVariableBitLengthType( nbits ) ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link UnsignedVariableBitLengthType}, {@link LongAccess}&gt;
	 * using a {@link LongAccess} passed as argument.
	 */
	final static public < A extends LongAccess > ArrayImg< UnsignedVariableBitLengthType, A > unsignedVariableBitLengths( final A access, final int nbits, final long... dim )
	{
		final ArrayImg< UnsignedVariableBitLengthType, A > img = new ArrayImg<>( access, dim, new Fraction( nbits, 64 ) );
		final UnsignedVariableBitLengthType t = new UnsignedVariableBitLengthType( img, nbits );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link FloatType}, {@link FloatArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< FloatType, FloatArray > floats( final long... dim )
	{
		return ( ArrayImg< FloatType, FloatArray > ) new ArrayImgFactory<>( new FloatType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link FloatType}, {@link FloatArray}&gt;
	 * reusing a passed float[] array.
	 */
	final public static ArrayImg< FloatType, FloatArray > floats( final float[] array, final long... dim )
	{
		return floats( new FloatArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link FloatType}, {@link FloatAccess}&gt;
	 * using a {@link FloatAccess} passed as argument.
	 */
	final static public < A extends FloatAccess > ArrayImg< FloatType, A > floats( final A access, final long... dim )
	{
		final ArrayImg< FloatType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final FloatType t = new FloatType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< DoubleType, DoubleArray > doubles( final long... dim )
	{
		return ( ArrayImg< DoubleType, DoubleArray > ) new ArrayImgFactory<>( new DoubleType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;
	 * reusing a passed double[] array.
	 */
	final public static ArrayImg< DoubleType, DoubleArray > doubles( final double[] array, final long... dim )
	{
		return doubles( new DoubleArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link DoubleType},
	 * {@link DoubleAccess}&gt; using a {@link DoubleAccess} passed as argument.
	 */
	final static public < A extends DoubleAccess > ArrayImg< DoubleType, A > doubles( final A access, final long... dim )
	{
		final ArrayImg< DoubleType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final DoubleType t = new DoubleType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link ARGBType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ARGBType, IntArray > argbs( final long... dim )
	{
		return ( ArrayImg< ARGBType, IntArray > ) new ArrayImgFactory<>( new ARGBType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ARGBType}, {@link IntArray}&gt; reusing a
	 * passed int[] array.
	 */
	final public static ArrayImg< ARGBType, IntArray > argbs( final int[] array, final long... dim )
	{
		return argbs( new IntArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ARGBType}, {@link IntAccess}&gt;
	 * using a {@link IntAccess} passed as argument.
	 */
	final static public < A extends IntAccess > ArrayImg< ARGBType, A > argbs( final A access, final long... dim )
	{
		final ArrayImg< ARGBType, A > img = new ArrayImg<>( access, dim, new Fraction() );
		final ARGBType t = new ARGBType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link ComplexFloatType}, {@link FloatArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ComplexFloatType, FloatArray > complexFloats( final long... dim )
	{
		return ( ArrayImg< ComplexFloatType, FloatArray > ) new ArrayImgFactory<>( new ComplexFloatType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link FloatType}, {@link FloatArray}&gt;
	 * reusing a passed float[] array.
	 */
	final public static ArrayImg< ComplexFloatType, FloatArray > complexFloats( final float[] array, final long... dim )
	{
		return complexFloats( new FloatArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ComplexFloatType},
	 * {@link FloatAccess}&gt; using a {@link FloatAccess} passed as argument.
	 */
	final public static < A extends FloatAccess > ArrayImg< ComplexFloatType, A > complexFloats( final A access, final long... dim )
	{
		final ArrayImg< ComplexFloatType, A > img = new ArrayImg<>( access, dim, new Fraction( 2, 1 ) );
		final ComplexFloatType t = new ComplexFloatType( img );
		img.setLinkedType( t );
		return img;
	}

	/**
	 * Create an {@link ArrayImg}&lt;{@link ComplexDoubleType},
	 * {@link DoubleArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public ArrayImg< ComplexDoubleType, DoubleArray > complexDoubles( final long... dim )
	{
		return ( ArrayImg< ComplexDoubleType, DoubleArray > ) new ArrayImgFactory<>( new ComplexDoubleType() ).create( dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;
	 * reusing a passed double[] array.
	 */
	final public static ArrayImg< ComplexDoubleType, DoubleArray > complexDoubles( final double[] array, final long... dim )
	{
		return complexDoubles( new DoubleArray( array ), dim );
	}

	/**
	 * Creates an {@link ArrayImg}&lt;{@link ComplexDoubleType},
	 * {@link DoubleAccess}&gt; using a {@link DoubleAccess} passed as argument.
	 */
	final public static < A extends DoubleAccess > ArrayImg< ComplexDoubleType, A > complexDoubles( final A access, final long... dim )
	{
		final ArrayImg< ComplexDoubleType, A > img = new ArrayImg<>( access, dim, new Fraction( 2, 1 ) );
		final ComplexDoubleType t = new ComplexDoubleType( img );
		img.setLinkedType( t );
		return img;
	}

}
