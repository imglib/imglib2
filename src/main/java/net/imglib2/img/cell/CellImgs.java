/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.cell;

import java.util.Collections;

import net.imglib2.img.basictypeaccess.array.AbstractBooleanArray;
import net.imglib2.img.basictypeaccess.array.AbstractByteArray;
import net.imglib2.img.basictypeaccess.array.AbstractDoubleArray;
import net.imglib2.img.basictypeaccess.array.AbstractFloatArray;
import net.imglib2.img.basictypeaccess.array.AbstractIntArray;
import net.imglib2.img.basictypeaccess.array.AbstractLongArray;
import net.imglib2.img.basictypeaccess.array.AbstractShortArray;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.BooleanArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.NativeType;
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

/**
 * <p>
 * Convenience factory methods for creation of {@link CellImg} instances with
 * the most common pixel {@link Type} variants. The collection includes
 * factories to re-use existing primitive type arrays as data. This can be used
 * for in-place access to data from other libraries such as AWT or ImageJ. Keep
 * in mind that this cannot be a complete collection since the number of
 * existing pixel {@link Type}s may be extended.
 * </p>
 *
 * <p>
 * For pixel {@link Type}s T not present in this collection, use the generic
 * {@link CellImgFactory#create(long[], net.imglib2.type.NativeType)}, e.g.
 * </p>
 *
 * <pre>
 * img = new CellImgFactory&lt; MyType &gt;.create( new long[] { 100, 200 }, new MyType() );
 * </pre>
 *
 * @author Mark Kittisopikul
 */
final public class CellImgs
{
	private CellImgs()
	{}
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED BYTES
	 * 
	 * 
	 * 
	 */	

	/**
	 * Create an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteArray}&gt;.
	 */
	final static public CellImg< UnsignedByteType, ByteArray > unsignedBytes( final long... dim )
	{
		return create( new UnsignedByteType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< UnsignedByteType, ByteArray > unsignedBytes( final byte[] array, final long... dim )
	{
		return create( new UnsignedByteType(), new ByteArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteArray}&gt;
	 * reusing a passed ByteArray.
	 */	
	final public static CellImg< UnsignedByteType, ByteArray > unsignedBytes( final ByteArray array, final long... dim )
	{
		return create( new UnsignedByteType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractByteArray<A>> CellImg< UnsignedByteType, A > unsignedBytes( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedByteType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType},
	 * {@link AbstractByteArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractByteArray<A> > CellImg< UnsignedByteType, A > unsignedBytes( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new UnsignedByteType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * BYTES
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link ByteType}, {@link ByteArray}&gt;.
	 */
	final static public CellImg< ByteType, ByteArray > bytes( final long... dim )
	{
		return create( new ByteType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType}, {@link ByteArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< ByteType, ByteArray > bytes( final byte[] array, final long... dim )
	{
		return create( new ByteType(), new ByteArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType}, {@link ByteArray}&gt;
	 * reusing a passed ByteArray.
	 */	
	final public static CellImg< ByteType, ByteArray > bytes( final ByteArray array, final long... dim )
	{
		return create( new ByteType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractByteArray<A>> CellImg< ByteType, A > bytes( final Cell< A > cell, final long... dim )
	{
		return create( new ByteType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType},
	 * {@link AbstractByteArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractByteArray<A> > CellImg< ByteType, A > bytes( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new ByteType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED SHORTS
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortArray}&gt;.
	 */
	final static public CellImg< UnsignedShortType, ShortArray > unsignedShorts( final long... dim )
	{
		return create( new UnsignedShortType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< UnsignedShortType, ShortArray > unsignedShorts( final short[] array, final long... dim )
	{
		return create( new UnsignedShortType(), new ShortArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortArray}&gt;
	 * reusing a passed ShortArray.
	 */	
	final public static CellImg< UnsignedShortType, ShortArray > unsignedShorts( final ShortArray array, final long... dim )
	{
		return create( new UnsignedShortType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractShortArray<A>> CellImg< UnsignedShortType, A > unsignedShorts( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedShortType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType},
	 * {@link AbstractShortArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractShortArray<A> > CellImg< UnsignedShortType, A > unsignedShorts( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new UnsignedShortType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * SHORTS
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link ShortType}, {@link ShortArray}&gt;.
	 */
	final static public CellImg< ShortType, ShortArray > shorts( final long... dim )
	{
		return create( new ShortType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType}, {@link ShortArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< ShortType, ShortArray > shorts( final short[] array, final long... dim )
	{
		return create( new ShortType(), new ShortArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType}, {@link ShortArray}&gt;
	 * reusing a passed ShortArray.
	 */	
	final public static CellImg< ShortType, ShortArray > shorts( final ShortArray array, final long... dim )
	{
		return create( new ShortType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractShortArray<A>> CellImg< ShortType, A > shorts( final Cell< A > cell, final long... dim )
	{
		return create( new ShortType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType},
	 * {@link AbstractShortArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractShortArray<A> > CellImg< ShortType, A > shorts( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new ShortType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED INTS
	 * 
	 * 
	 * 
	 */	


	/**
	 * Create an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntArray}&gt;.
	 */
	final static public CellImg< UnsignedIntType, IntArray > unsignedInts( final long... dim )
	{
		return create( new UnsignedIntType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< UnsignedIntType, IntArray > unsignedInts( final int[] array, final long... dim )
	{
		return create( new UnsignedIntType(), new IntArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntArray}&gt;
	 * reusing a passed IntArray.
	 */	
	final public static CellImg< UnsignedIntType, IntArray > unsignedInts( final IntArray array, final long... dim )
	{
		return create( new UnsignedIntType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractIntArray<A>> CellImg< UnsignedIntType, A > unsignedInts( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedIntType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType},
	 * {@link AbstractIntArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractIntArray<A> > CellImg< UnsignedIntType, A > unsignedInts( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new UnsignedIntType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * INTS
	 * 
	 * 
	 * 
	 */	
	
	
	/**
	 * Create an {@link CellImg}&lt;{@link IntType}, {@link IntArray}&gt;.
	 */
	final static public CellImg< IntType, IntArray > ints( final long... dim )
	{
		return create( new IntType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link IntType}, {@link IntArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< IntType, IntArray > ints( final int[] array, final long... dim )
	{
		return create( new IntType(), new IntArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link IntType}, {@link IntArray}&gt;
	 * reusing a passed IntArray.
	 */	
	final public static CellImg< IntType, IntArray > ints( final IntArray array, final long... dim )
	{
		return create( new IntType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link IntType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractIntArray<A>> CellImg< IntType, A > ints( final Cell< A > cell, final long... dim )
	{
		return create( new IntType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link IntType},
	 * {@link AbstractIntArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractIntArray<A> > CellImg< IntType, A > ints( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new IntType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED LONGS
	 * 
	 * 
	 * 
	 */	

	/**
	 * Create an {@link CellImg}&lt;{@link UnsignedLongType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< UnsignedLongType, LongArray > unsignedLongs( final long... dim )
	{
		return create( new UnsignedLongType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< UnsignedLongType, LongArray > unsignedLongs( final long[] array, final long... dim )
	{
		return create( new UnsignedLongType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< UnsignedLongType, LongArray > unsignedLongs( final LongArray array, final long... dim )
	{
		return create( new UnsignedLongType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< UnsignedLongType, A > unsignedLongs( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedLongType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< UnsignedLongType, A > unsignedLongs( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new UnsignedLongType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * LONGS
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link LongType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< LongType, LongArray > longs( final long... dim )
	{
		return create( new LongType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link LongType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< LongType, LongArray > longs( final long[] array, final long... dim )
	{
		return create( new LongType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link LongType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< LongType, LongArray > longs( final LongArray array, final long... dim )
	{
		return create( new LongType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link LongType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< LongType, A > longs( final Cell< A > cell, final long... dim )
	{
		return create( new LongType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link LongType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< LongType, A > longs( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new LongType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * BOOLEANS
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;.
	 */
	final static public CellImg< NativeBoolType, BooleanArray > booleans( final long... dim )
	{
		return create( new NativeBoolType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< NativeBoolType, BooleanArray > booleans( final boolean[] array, final long... dim )
	{
		return create( new NativeBoolType(), new BooleanArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;
	 * reusing a passed BooleanArray.
	 */	
	final public static CellImg< NativeBoolType, BooleanArray > booleans( final BooleanArray array, final long... dim )
	{
		return create( new NativeBoolType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractBooleanArray<A>> CellImg< NativeBoolType, A > booleans( final Cell< A > cell, final long... dim )
	{
		return create( new NativeBoolType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType},
	 * {@link AbstractBooleanArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractBooleanArray<A> > CellImg< NativeBoolType, A > booleans( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new NativeBoolType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * BITS
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link BitType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< BitType, LongArray > bits( final long... dim )
	{
		return create( new BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link BitType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< BitType, LongArray > bits( final long[] array, final long... dim )
	{
		return create( new BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link BitType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< BitType, LongArray > bits( final LongArray array, final long... dim )
	{
		return create( new BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< BitType, A > bits( final Cell< A > cell, final long... dim )
	{
		return create( new BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link BitType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< BitType, A > bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new BitType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED 2 BIT TYPE
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link Unsigned2BitType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< Unsigned2BitType, LongArray > unsigned2Bits( final long... dim )
	{
		return create( new Unsigned2BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< Unsigned2BitType, LongArray > unsigned2Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned2BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< Unsigned2BitType, LongArray > unsigned2Bits( final LongArray array, final long... dim )
	{
		return create( new Unsigned2BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< Unsigned2BitType, A > unsigned2Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned2BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< Unsigned2BitType, A > unsigned2Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new Unsigned2BitType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED 4 BIT TYPE
	 * 
	 * 
	 * 
	 */	

	/**
	 * Create an {@link CellImg}&lt;{@link Unsigned4BitType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< Unsigned4BitType, LongArray > unsigned4Bits( final long... dim )
	{
		return create( new Unsigned4BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< Unsigned4BitType, LongArray > unsigned4Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned4BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< Unsigned4BitType, LongArray > unsigned4Bits( final LongArray array, final long... dim )
	{
		return create( new Unsigned4BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< Unsigned4BitType, A > unsigned4Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned4BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< Unsigned4BitType, A > unsigned4Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new Unsigned4BitType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED 12 BIT TYPE
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link Unsigned12BitType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< Unsigned12BitType, LongArray > unsigned12Bits( final long... dim )
	{
		return create( new Unsigned12BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< Unsigned12BitType, LongArray > unsigned12Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned12BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< Unsigned12BitType, LongArray > unsigned12Bits( final LongArray array, final long... dim )
	{
		return create( new Unsigned12BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< Unsigned12BitType, A > unsigned12Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned12BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< Unsigned12BitType, A > unsigned12Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new Unsigned12BitType(), imgOfCells, dim);
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED 128 BIT TYPE
	 * 
	 * 
	 * 
	 */	
	
	
	/**
	 * Create an {@link CellImg}&lt;{@link Unsigned128BitType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< Unsigned128BitType, LongArray > unsigned128Bits( final long... dim )
	{
		return create( new Unsigned128BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< Unsigned128BitType, LongArray > unsigned128Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned128BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< Unsigned128BitType, LongArray > unsigned128Bits( final LongArray array, final long... dim )
	{
		return create( new Unsigned128BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< Unsigned128BitType, A > unsigned128Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned128BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< Unsigned128BitType, A > unsigned128Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new Unsigned128BitType(), imgOfCells, dim);
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * UNSIGNED VARIABLE BIT LENGTH TYPE
	 * 
	 * 
	 * 
	 */	
	

	/**
	 * Create an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType}, {@link LongArray}&gt;.
	 */
	final static public CellImg< UnsignedVariableBitLengthType, LongArray > unsignedVariableBitLengthType( final int nbits, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ) , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType}, {@link LongArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< UnsignedVariableBitLengthType, LongArray > unsignedVariableBitLengthType( final int nbits, final long[] array, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< UnsignedVariableBitLengthType, LongArray > unsignedVariableBitLengthType( final int nbits, final LongArray array, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractLongArray<A>> CellImg< UnsignedVariableBitLengthType, A > unsignedVariableBitLengthType( final int nbits, final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType},
	 * {@link AbstractLongArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractLongArray<A> > CellImg< UnsignedVariableBitLengthType, A > unsignedVariableBitLengthType( final int nbits, final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ), imgOfCells, dim);
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * FLOATS
	 * 
	 * 
	 * 
	 */	
	

	/**
	 * Create an {@link CellImg}&lt;{@link FloatType}, {@link FloatArray}&gt;.
	 */
	final static public CellImg< FloatType, FloatArray > floats( final long... dim )
	{
		return create( new FloatType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType}, {@link FloatArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< FloatType, FloatArray > floats( final float[] array, final long... dim )
	{
		return create( new FloatType(), new FloatArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType}, {@link FloatArray}&gt;
	 * reusing a passed FloatArray.
	 */	
	final public static CellImg< FloatType, FloatArray > floats( final FloatArray array, final long... dim )
	{
		return create( new FloatType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractFloatArray<A>> CellImg< FloatType, A > floats( final Cell< A > cell, final long... dim )
	{
		return create( new FloatType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType},
	 * {@link AbstractFloatArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractFloatArray<A> > CellImg< FloatType, A > floats( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new FloatType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * DOUBLES
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;.
	 */
	final static public CellImg< DoubleType, DoubleArray > doubles( final long... dim )
	{
		return create( new DoubleType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< DoubleType, DoubleArray > doubles( final double[] array, final long... dim )
	{
		return create( new DoubleType(), new DoubleArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;
	 * reusing a passed DoubleArray.
	 */	
	final public static CellImg< DoubleType, DoubleArray > doubles( final DoubleArray array, final long... dim )
	{
		return create( new DoubleType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractDoubleArray<A>> CellImg< DoubleType, A > doubles( final Cell< A > cell, final long... dim )
	{
		return create( new DoubleType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType},
	 * {@link AbstractDoubleArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractDoubleArray<A> > CellImg< DoubleType, A > doubles( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new DoubleType(), imgOfCells, dim);
	}
	

	/**
	 * 
	 * 
	 * 
	 * ARGBS
	 * 
	 * 
	 * 
	 */	
	

	/**
	 * Create an {@link CellImg}&lt;{@link ARGBType}, {@link IntArray}&gt;.
	 */
	final static public CellImg< ARGBType, IntArray > argbs( final long... dim )
	{
		return create( new ARGBType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType}, {@link IntArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< ARGBType, IntArray > argbs( final int[] array, final long... dim )
	{
		return create( new ARGBType(), new IntArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType}, {@link IntArray}&gt;
	 * reusing a passed IntArray.
	 */	
	final public static CellImg< ARGBType, IntArray > argbs( final IntArray array, final long... dim )
	{
		return create( new ARGBType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractIntArray<A>> CellImg< ARGBType, A > argbs( final Cell< A > cell, final long... dim )
	{
		return create( new ARGBType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType},
	 * {@link AbstractIntArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractIntArray<A> > CellImg< ARGBType, A > argbs( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new ARGBType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * COMPLEX FLOATS
	 * 
	 * 
	 * 
	 */	

	/**
	 * Create an {@link CellImg}&lt;{@link ComplexFloatType}, {@link FloatArray}&gt;.
	 */
	final static public CellImg< ComplexFloatType, FloatArray > complexFloats( final long... dim )
	{
		return create( new ComplexFloatType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType}, {@link FloatArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< ComplexFloatType, FloatArray > complexFloats( final float[] array, final long... dim )
	{
		return create( new ComplexFloatType(), new FloatArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType}, {@link FloatArray}&gt;
	 * reusing a passed FloatArray.
	 */	
	final public static CellImg< ComplexFloatType, FloatArray > complexFloats( final FloatArray array, final long... dim )
	{
		return create( new ComplexFloatType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractFloatArray<A>> CellImg< ComplexFloatType, A > complexFloats( final Cell< A > cell, final long... dim )
	{
		return create( new ComplexFloatType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType},
	 * {@link AbstractFloatArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractFloatArray<A> > CellImg< ComplexFloatType, A > complexFloats( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new ComplexFloatType(), imgOfCells, dim);
	}
	
	/**
	 * 
	 * 
	 * 
	 * COMPLEX DOUBLES
	 * 
	 * 
	 * 
	 */	
	
	/**
	 * Create an {@link CellImg}&lt;{@link ComplexDoubleType}, {@link DoubleArray}&gt;.
	 */
	final static public CellImg< ComplexDoubleType, DoubleArray > complexDoubles( final long... dim )
	{
		return create( new ComplexDoubleType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType}, {@link DoubleArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< ComplexDoubleType, DoubleArray > complexDoubles( final double[] array, final long... dim )
	{
		return create( new ComplexDoubleType(), new DoubleArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType}, {@link DoubleArray}&gt;
	 * reusing a passed DoubleArray.
	 */	
	final public static CellImg< ComplexDoubleType, DoubleArray > complexDoubles( final DoubleArray array, final long... dim )
	{
		return create( new ComplexDoubleType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends AbstractDoubleArray<A>> CellImg< ComplexDoubleType, A > complexDoubles( final Cell< A > cell, final long... dim )
	{
		return create( new ComplexDoubleType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType},
	 * {@link AbstractDoubleArray}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends AbstractDoubleArray<A> > CellImg< ComplexDoubleType, A > complexDoubles( final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		return create( new ComplexDoubleType(), imgOfCells, dim);
	}
	
	
	

	
	
	/**
	 * 
	 * 
	 * 
	 * PRIVATE METHODS
	 * 
	 * 
	 * 
	 */
	
	/**
	 * Create an {@link CellImg}&lt; T, A &gt;.
	 */
	@SuppressWarnings( "unchecked" ) //CellImgFactory.create does not specify the Access interface A
	final static private < T extends NativeType<T>, A extends ArrayDataAccess<A> > CellImg< T, A > create( T type, final long... dim )
	{
		return ( CellImg< T, A > ) new CellImgFactory<>( type ).create( dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link T}, {@link A}&gt;
	 * reusing a passed {@link ArrayDataAccess}.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A extends ArrayDataAccess<A> > CellImg< T, A > create( final T type, final A array, final long... dim )
	{
		final int[] cellDim = new int[ dim.length ];
		for(int i = 0; i < dim.length; ++i)
			cellDim[i] = (int) dim[i];
		final Cell< A > cell = new Cell< A >( cellDim, new long[ dim.length ], array );
		return create( type, cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt; T, A &gt;
	 * using a {@link Cell} passed as argument.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A extends ArrayDataAccess<A> > CellImg< T, A > create( final T type, final Cell< A > cell, final long... dim )
	{
		final ListImg< Cell< A > > imgOfCell = new ListImg< Cell< A > >( Collections.singletonList(cell), dim );
		return create( type, imgOfCell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt; T, A &gt;
	 * using a {@link ListImg} passed as argument.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A extends ArrayDataAccess<A> > CellImg< T, A > create( final T type, final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		final CellImgFactory< T > factory = new CellImgFactory< T >( type );
		return ( CellImg< T, A > ) factory.create( imgOfCells, dim );
	}

}
