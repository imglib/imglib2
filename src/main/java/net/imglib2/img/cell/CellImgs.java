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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
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
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListLocalizingCursor;
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
	 * Create an {@link CellImg}&lt;{@link UnsignedByteType}, ?&gt;.
	 */
	final static public CellImg< UnsignedByteType, ? > unsignedBytes( final long... dim )
	{
		return create( new UnsignedByteType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteAccess}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< UnsignedByteType, ByteAccess > unsignedBytes( final byte[] array, final long... dim )
	{
		return create( new UnsignedByteType(), new ByteArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteAccess}&gt;
	 * reusing an array of byte arrays.
	 */
	final public static CellImg< UnsignedByteType, ByteAccess > unsignedBytes( final byte[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< ByteAccess > list = new ArrayList<>(arrays.length);
		for(byte[] array: arrays) {
			list.add(new ByteArray(array));
		}
		return create( new UnsignedByteType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteAccess}&gt;
	 * reusing a passed ByteAccess.
	 */	
	final public static CellImg< UnsignedByteType, ByteAccess > unsignedBytes( final ByteAccess access, final long... dim )
	{
		return create( new UnsignedByteType(), access, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteAccess}&gt;
	 * using an array of ByteAccess
	 */
	final public static CellImg< UnsignedByteType, ByteAccess > unsignedBytes( final ByteAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new UnsignedByteType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends ByteAccess > CellImg< UnsignedByteType, A > unsignedBytes( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedByteType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends ByteAccess > CellImg< UnsignedByteType, A > unsignedBytes( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new UnsignedByteType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType},
	 * {@link ByteAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends ByteAccess > CellImg< UnsignedByteType, A > unsignedBytes( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link ByteType}, ?&gt;.
	 */
	final static public CellImg< ByteType, ? > bytes( final long... dim )
	{
		return create( new ByteType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType}, {@link ByteArray}&gt;
	 * reusing a passed byte[] array.
	 */
	final public static CellImg< ByteType, ByteAccess > bytes( final byte[] array, final long... dim )
	{
		return create( new ByteType(), new ByteArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType}, {@link ByteAccess}&gt;
	 * reusing an array of byte arrays.
	 */
	final public static CellImg< ByteType, ByteAccess > bytes( final byte[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< ByteAccess > list = new ArrayList<>(arrays.length);
		for(byte[] array: arrays) {
			list.add(new ByteArray(array));
		}
		return create( new ByteType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType}, {@link ByteArray}&gt;
	 * reusing a passed ByteArray.
	 */	
	final public static CellImg< ByteType, ByteAccess > bytes( final ByteAccess array, final long... dim )
	{
		return create( new ByteType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteAccess}&gt;
	 * using an array of ByteAccess
	 */
	final public static CellImg< ByteType, ByteAccess > bytes( final ByteAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new ByteType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends ByteAccess > CellImg< ByteType, A > bytes( final Cell< A > cell, final long... dim )
	{
		return create( new ByteType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedByteType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends ByteAccess > CellImg< ByteType, A > bytes( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new ByteType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ByteType},
	 * {@link ByteAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends ByteAccess > CellImg< ByteType, A > bytes( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< UnsignedShortType, ? > unsignedShorts( final long... dim )
	{
		return create( new UnsignedShortType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortAccess}&gt;
	 * reusing a passed short[] array.
	 */
	final public static CellImg< UnsignedShortType, ShortAccess > unsignedShorts( final short[] array, final long... dim )
	{
		return create( new UnsignedShortType(), new ShortArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortAccess}&gt;
	 * reusing an array of short arrays.
	 */
	final public static CellImg< UnsignedShortType, ShortAccess > unsignedShorts( final short[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< ShortAccess > list = new ArrayList<>(arrays.length);
		for(short[] array: arrays) {
			list.add(new ShortArray(array));
		}
		return create( new UnsignedShortType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortArray}&gt;
	 * reusing a passed ShortArray.
	 */	
	final public static CellImg< UnsignedShortType, ShortAccess > unsignedShorts( final ShortAccess access, final long... dim )
	{
		return create( new UnsignedShortType(), access, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortArray}&gt;
	 * using an array of ShortArrays
	 */
	final public static CellImg< UnsignedShortType, ShortAccess > unsignedShorts( final ShortAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new UnsignedShortType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A> CellImg< UnsignedShortType, A > unsignedShorts( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedShortType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends ShortAccess > CellImg< UnsignedShortType, A > unsignedShorts( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new UnsignedShortType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedShortType},
	 * {@link ShortAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends ShortAccess  > CellImg< UnsignedShortType, A > unsignedShorts( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link ShortType}, ?&gt;.
	 */
	final static public CellImg< ShortType, ? > shorts( final long... dim )
	{
		return create( new ShortType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType}, {@link ShortArray}&gt;
	 * reusing a passed short[] array.
	 */
	final public static CellImg< ShortType, ShortAccess > shorts( final short[] array, final long... dim )
	{
		return create( new ShortType(), new ShortArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType}, {@link ShortAccess}&gt;
	 * reusing an array of short arrays.
	 */
	final public static CellImg< ShortType, ShortAccess > shorts( final short[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< ShortAccess > list = new ArrayList<>(arrays.length);
		for(short[] array: arrays) {
			list.add(new ShortArray(array));
		}
		return create( new ShortType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType}, {@link ShortArray}&gt;
	 * reusing a passed ShortArray.
	 */	
	final public static CellImg< ShortType, ShortAccess > shorts( final ShortArray array, final long... dim )
	{
		return create( new ShortType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType}, {@link ShortArray}&gt;
	 * using an array of ShortArrays
	 */
	final public static CellImg< ShortType, ShortAccess > shorts( final ShortAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new ShortType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends ShortAccess > CellImg< ShortType, A > shorts( final Cell< A > cell, final long... dim )
	{
		return create( new ShortType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends ShortAccess > CellImg< ShortType, A > shorts( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new ShortType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ShortType},
	 * {@link ShortAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends ShortAccess > CellImg< ShortType, A > shorts( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link UnsignedIntType}, ?&gt;.
	 */
	final static public CellImg< UnsignedIntType, ? > unsignedInts( final long... dim )
	{
		return create( new UnsignedIntType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntAccess}&gt;
	 * reusing a passed int[] array.
	 */
	final public static CellImg< UnsignedIntType, IntAccess > unsignedInts( final int[] array, final long... dim )
	{
		return create( new UnsignedIntType(), new IntArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link IntType}, {@link IntAccess}&gt;
	 * reusing an array of int arrays.
	 */
	final public static CellImg< UnsignedIntType, IntAccess > unsignedInts( final int[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< IntAccess > list = new ArrayList<>(arrays.length);
		for(int[] array: arrays) {
			list.add(new IntArray(array));
		}
		return create( new UnsignedIntType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntAccess}&gt;
	 * reusing a passed IntAccess.
	 */	
	final public static CellImg< UnsignedIntType, IntAccess > unsignedInts( final IntAccess access, final long... dim )
	{
		return create( new UnsignedIntType(), access, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntAccess}&gt;
	 * using an array of IntAccess
	 */
	final public static CellImg< UnsignedIntType, IntAccess > unsignedInts( final IntAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new UnsignedIntType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static <A extends IntAccess> CellImg< UnsignedIntType, A > unsignedInts( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedIntType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends IntAccess > CellImg< UnsignedIntType, A > unsignedInts( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new UnsignedIntType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType},
	 * {@link IntAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends IntAccess > CellImg< UnsignedIntType, A > unsignedInts( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< IntType, ? > ints( final long... dim )
	{
		return create( new IntType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link IntType}, {@link IntAccess}&gt;
	 * reusing a passed int[] array.
	 */
	final public static CellImg< IntType, IntAccess > ints( final int[] array, final long... dim )
	{
		return create( new IntType(), new IntArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link IntType}, {@link IntAccess}&gt;
	 * reusing an array of int arrays.
	 */
	final public static CellImg< IntType, IntAccess > ints( final int[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< IntAccess > list = new ArrayList<>(arrays.length);
		for(int[] array: arrays) {
			list.add(new IntArray(array));
		}
		return create( new IntType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link IntType}, {@link IntAccess}&gt;
	 * reusing a passed IntAccess.
	 */	
	final public static CellImg< IntType, IntAccess > ints( final IntAccess array, final long... dim )
	{
		return create( new IntType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntAccess}&gt;
	 * using an array of IntAccess
	 */
	final public static CellImg< IntType, IntAccess > ints( final IntAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new IntType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link IntType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends IntAccess > CellImg< IntType, A > ints( final Cell< A > cell, final long... dim )
	{
		return create( new IntType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link IntType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends IntAccess > CellImg< IntType, A > ints( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new IntType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link IntType},
	 * {@link IntAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends IntAccess > CellImg< IntType, A > ints( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link UnsignedLongType}, ?&gt;.
	 */
	final static public CellImg< UnsignedLongType, ? > unsignedLongs( final long... dim )
	{
		return create( new UnsignedLongType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType}, {@link LongAccess}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< UnsignedLongType, LongAccess > unsignedLongs( final long[] array, final long... dim )
	{
		return create( new UnsignedLongType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType}, {@link LongAccess}&gt;
	 * reusing an array of long arrays.
	 */
	final public static CellImg< UnsignedLongType, LongAccess > unsignedLongs( final long[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< LongAccess > list = new ArrayList<>(arrays.length);
		for(long[] array: arrays) {
			list.add(new LongArray(array));
		}
		return create( new UnsignedLongType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType}, {@link LongAccess}&gt;
	 * reusing a passed LongAccess.
	 */	
	final public static CellImg< UnsignedLongType, LongAccess > unsignedLongs( final LongAccess array, final long... dim )
	{
		return create( new UnsignedLongType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType}, {@link LongAccess}&gt;
	 * using an array of LongAccess
	 */
	final public static CellImg< UnsignedLongType, LongAccess > unsignedLongs( final LongAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new UnsignedLongType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< UnsignedLongType, A > unsignedLongs( final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedLongType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< UnsignedLongType, A > unsignedLongs( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new UnsignedLongType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedLongType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< UnsignedLongType, A > unsignedLongs( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< LongType, ? > longs( final long... dim )
	{
		return create( new LongType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link LongType}, {@link LongArray}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< LongType, LongAccess > longs( final long[] array, final long... dim )
	{
		return create( new LongType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link LongType}, {@link LongAccess}&gt;
	 * reusing an array of long arrays.
	 */
	final public static CellImg< LongType, LongAccess > longs( final long[][] arrays, final int[] cellDim, final long... dim )
	{
		ArrayList< LongAccess > list = new ArrayList<>(arrays.length);
		for(long[] array: arrays) {
			list.add(new LongArray(array));
		}
		return create( new LongType(), list, cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link LongType}, {@link LongArray}&gt;
	 * reusing a passed LongAccess.
	 */	
	final public static CellImg< LongType, LongAccess > longs( final LongAccess array, final long... dim )
	{
		return create( new LongType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link LongType}, {@link LongAccess}&gt;
	 * using an array of LongAccess
	 */
	final public static CellImg< LongType, LongAccess > longs( final LongAccess[] accesses, final int[] cellDim, final long... dim )
	{
		return create( new LongType(), Arrays.asList(accesses), cellDim, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link LongType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< LongType, A > longs( final Cell< A > cell, final long... dim )
	{
		return create( new LongType(), cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link LongType},
	 * {@link Cell}&gt; using a {@link Collection} of {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< LongType, A > longs( final Collection< Cell< A > > cells, final long... dim )
	{
		return create( new LongType(), cells, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link LongType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< LongType, A > longs( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< NativeBoolType, ? > booleans( final long... dim )
	{
		return create( new NativeBoolType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType}, {@link BooleanAccess}&gt;
	 * reusing a passed boolean[] array.
	 */
	final public static CellImg< NativeBoolType, BooleanAccess > booleans( final boolean[] array, final long... dim )
	{
		return create( new NativeBoolType(), new BooleanArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;
	 * reusing a passed BooleanArray.
	 */	
	final public static CellImg< NativeBoolType, BooleanAccess > booleans( final BooleanArray array, final long... dim )
	{
		return create( new NativeBoolType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends BooleanAccess > CellImg< NativeBoolType, A > booleans( final Cell< A > cell, final long... dim )
	{
		return create( new NativeBoolType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link NativeBoolType},
	 * {@link BooleanAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends BooleanAccess > CellImg< NativeBoolType, A > booleans( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< BitType, ? > bits( final long... dim )
	{
		return create( new BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link BitType}, {@link LongArray}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< BitType, LongAccess > bits( final long[] array, final long... dim )
	{
		return create( new BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link BitType}, {@link LongArray}&gt;
	 * reusing a passed LongAccess.
	 */	
	final public static CellImg< BitType, LongAccess > bits( final LongAccess array, final long... dim )
	{
		return create( new BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< BitType, A > bits( final Cell< A > cell, final long... dim )
	{
		return create( new BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link BitType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< BitType, A > bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link Unsigned2BitType}, ?&gt;.
	 */
	final static public CellImg< Unsigned2BitType, ? > unsigned2Bits( final long... dim )
	{
		return create( new Unsigned2BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType}, {@link LongAccess}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< Unsigned2BitType, LongAccess > unsigned2Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned2BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType}, {@link LongAccess}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< Unsigned2BitType, LongAccess > unsigned2Bits( final LongAccess array, final long... dim )
	{
		return create( new Unsigned2BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned2BitType, A > unsigned2Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned2BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned2BitType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned2BitType, A > unsigned2Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< Unsigned4BitType, ? > unsigned4Bits( final long... dim )
	{
		return create( new Unsigned4BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType}, {@link LongArray}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< Unsigned4BitType, LongAccess > unsigned4Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned4BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType}, {@link LongArray}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< Unsigned4BitType, LongAccess > unsigned4Bits( final LongAccess array, final long... dim )
	{
		return create( new Unsigned4BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned4BitType, A > unsigned4Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned4BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned4BitType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned4BitType, A > unsigned4Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link Unsigned12BitType}, ?&gt;.
	 */
	final static public CellImg< Unsigned12BitType, ? > unsigned12Bits( final long... dim )
	{
		return create( new Unsigned12BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType}, {@link LongAccess}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< Unsigned12BitType, LongAccess > unsigned12Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned12BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType}, {@link LongAccess}&gt;
	 * reusing a passed LongAccess.
	 */	
	final public static CellImg< Unsigned12BitType, LongAccess > unsigned12Bits( final LongAccess array, final long... dim )
	{
		return create( new Unsigned12BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned12BitType, A > unsigned12Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned12BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned12BitType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned12BitType, A > unsigned12Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link Unsigned128BitType}, {@link LongAccess}&gt;.
	 */
	final static public CellImg< Unsigned128BitType, ? > unsigned128Bits( final long... dim )
	{
		return create( new Unsigned128BitType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType}, {@link LongAccess}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< Unsigned128BitType, LongAccess > unsigned128Bits( final long[] array, final long... dim )
	{
		return create( new Unsigned128BitType(), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType}, {@link LongAccess}&gt;
	 * reusing a passed LongArray.
	 */	
	final public static CellImg< Unsigned128BitType, LongAccess > unsigned128Bits( final LongAccess array, final long... dim )
	{
		return create( new Unsigned128BitType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned128BitType, A > unsigned128Bits( final Cell< A > cell, final long... dim )
	{
		return create( new Unsigned128BitType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link Unsigned128BitType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< Unsigned128BitType, A > unsigned128Bits( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType}, ?&gt;.
	 */
	final static public CellImg< UnsignedVariableBitLengthType, ? > unsignedVariableBitLengthType( final int nbits, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ) , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType}, {@link LongAccess}&gt;
	 * reusing a passed long[] array.
	 */
	final public static CellImg< UnsignedVariableBitLengthType, LongAccess > unsignedVariableBitLengthType( final int nbits, final long[] array, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ), new LongArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType}, {@link LongArray}&gt;
	 * reusing a passed LongAccess.
	 */	
	final public static CellImg< UnsignedVariableBitLengthType, LongAccess > unsignedVariableBitLengthType( final int nbits, final LongAccess array, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< UnsignedVariableBitLengthType, A > unsignedVariableBitLengthType( final int nbits, final Cell< A > cell, final long... dim )
	{
		return create( new UnsignedVariableBitLengthType( nbits ), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link UnsignedVariableBitLengthType},
	 * {@link LongAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends LongAccess > CellImg< UnsignedVariableBitLengthType, A > unsignedVariableBitLengthType( final int nbits, final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link FloatType}, ?&gt;.
	 */
	final static public CellImg< FloatType, ? > floats( final long... dim )
	{
		return create( new FloatType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType}, {@link FloatAccess}&gt;
	 * reusing a passed float[] array.
	 */
	final public static CellImg< FloatType, FloatAccess > floats( final float[] array, final long... dim )
	{
		return create( new FloatType(), new FloatArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType}, {@link FloatAccess}&gt;
	 * reusing a passed FloatArray.
	 */	
	final public static CellImg< FloatType, FloatAccess > floats( final FloatAccess array, final long... dim )
	{
		return create( new FloatType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends FloatAccess > CellImg< FloatType, A > floats( final Cell< A > cell, final long... dim )
	{
		return create( new FloatType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link FloatType},
	 * {@link FloatAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends FloatAccess > CellImg< FloatType, A > floats( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link DoubleType}, ?&gt;.
	 */
	final static public CellImg< DoubleType, ? > doubles( final long... dim )
	{
		return create( new DoubleType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType}, {@link DoubleAccess}&gt;
	 * reusing a passed double[] array.
	 */
	final public static CellImg< DoubleType, DoubleAccess > doubles( final double[] array, final long... dim )
	{
		return create( new DoubleType(), new DoubleArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType}, {@link DoubleAccess}&gt;
	 * reusing a passed DoubleArray.
	 */	
	final public static CellImg< DoubleType, DoubleAccess > doubles( final DoubleAccess array, final long... dim )
	{
		return create( new DoubleType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends DoubleAccess > CellImg< DoubleType, A > doubles( final Cell< A > cell, final long... dim )
	{
		return create( new DoubleType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link DoubleType},
	 * {@link DoubleAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends DoubleAccess > CellImg< DoubleType, A > doubles( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt;{@link ARGBType}, ?&gt;.
	 */
	final static public CellImg< ARGBType, ? > argbs( final long... dim )
	{
		return create( new ARGBType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType}, {@link IntAccess}&gt;
	 * reusing a passed int[] array.
	 */
	final public static CellImg< ARGBType, IntAccess > argbs( final int[] array, final long... dim )
	{
		return create( new ARGBType(), new IntArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType}, {@link IntAccess}&gt;
	 * reusing a passed IntAccess.
	 */	
	final public static CellImg< ARGBType, IntAccess > argbs( final IntAccess array, final long... dim )
	{
		return create( new ARGBType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends IntAccess > CellImg< ARGBType, A > argbs( final Cell< A > cell, final long... dim )
	{
		return create( new ARGBType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ARGBType},
	 * {@link IntAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends IntAccess > CellImg< ARGBType, A > argbs( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< ComplexFloatType, ? > complexFloats( final long... dim )
	{
		return create( new ComplexFloatType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType}, {@link FloatAccess}&gt;
	 * reusing a passed float[] array.
	 */
	final public static CellImg< ComplexFloatType, FloatAccess > complexFloats( final float[] array, final long... dim )
	{
		return create( new ComplexFloatType(), new FloatArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType}, {@link FloatAccess}&gt;
	 * reusing a passed FloatArray.
	 */	
	final public static CellImg< ComplexFloatType, FloatAccess > complexFloats( final FloatAccess array, final long... dim )
	{
		return create( new ComplexFloatType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends FloatAccess > CellImg< ComplexFloatType, A > complexFloats( final Cell< A > cell, final long... dim )
	{
		return create( new ComplexFloatType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexFloatType},
	 * {@link FloatAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends FloatAccess > CellImg< ComplexFloatType, A > complexFloats( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	final static public CellImg< ComplexDoubleType, ? > complexDoubles( final long... dim )
	{
		return create( new ComplexDoubleType() , dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType}, {@link DoubleAccess}&gt;
	 * reusing a passed double[] array.
	 */
	final public static CellImg< ComplexDoubleType, DoubleAccess > complexDoubles( final double[] array, final long... dim )
	{
		return create( new ComplexDoubleType(), new DoubleArray( array ), dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType}, {@link DoubleAccess}&gt;
	 * reusing a passed DoubleAccess.
	 */	
	final public static CellImg< ComplexDoubleType, DoubleAccess > complexDoubles( final DoubleAccess array, final long... dim )
	{
		return create( new ComplexDoubleType(), array, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType},
	 * {@link Cell}&gt; using a {@link Cell} passed as argument.
	 */
	final public static < A extends DoubleAccess > CellImg< ComplexDoubleType, A > complexDoubles( final Cell< A > cell, final long... dim )
	{
		return create( new ComplexDoubleType(), cell, dim );
	}

	/**
	 * Creates an {@link CellImg}&lt;{@link ComplexDoubleType},
	 * {@link DoubleAccess}&gt; using a {@link ListImg} passed as argument.
	 */
	final public static < A extends DoubleAccess > CellImg< ComplexDoubleType, A > complexDoubles( final ListImg< Cell< A > > imgOfCells, final long... dim )
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
	 * Create an {@link CellImg}&lt; T, ? &gt;.
	 */
	final static private < T extends NativeType<T> > CellImg< T, ? > create( T type, final long... dim )
	{
		return new CellImgFactory<>( type ).create( dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link T}, {@link A}&gt;
	 * reusing a passed Access interface.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A > CellImg< T, A > create( final T type, final A access, final long... dim )
	{
		final int[] cellDim = new int[ dim.length ];
		for(int i = 0; i < dim.length; ++i)
			cellDim[i] = (int) dim[i];
		final Cell< A > cell = new Cell< A >( cellDim, new long[ dim.length ], access );
		return create( type, cell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt;{@link T}, {@link A}&gt;
	 * reusing a passed {@link Collection} of Access.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A > CellImg< T, A > create( final T type, final Collection< A > accesses, final int[] cellDim, final long... dim )
	{
		// Adapted from CellImgFactory
		Dimensions.verify( dim );

		final int n = dim.length;

		final CellGrid grid = new CellGrid( dim, cellDim );
		final long[] gridDimensions = new long[ grid.numDimensions() ];
		grid.gridDimensions( gridDimensions );

		final Cell< A > cellType = new Cell<>( new int[] { 1 }, new long[] { 1 }, null );
		final ListImg< Cell< A > > cells = new ListImg<>( gridDimensions, cellType );

		final long[] cellGridPosition = new long[ n ];
		final long[] cellMin = new long[ n ];
		final int[] cellDims = new int[ n ];
		final ListLocalizingCursor< Cell< A > > cellCursor = cells.localizingCursor();
		Iterator<A> arrayIterator = accesses.iterator();
		while ( cellCursor.hasNext() && arrayIterator.hasNext())
		{
			cellCursor.fwd();
			cellCursor.localize( cellGridPosition );
			grid.getCellDimensions( cellGridPosition, cellMin, cellDims );
			cellCursor.set( new Cell<>( cellDims, cellMin, arrayIterator.next() ) );
		}
		
		return create( type, cells, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt; T, A &gt;
	 * using a {@link Cell} passed as argument.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A > CellImg< T, A > create( final T type, final Cell< A > cell, final long... dim )
	{
		long[] gridDimensions = new long[ dim.length ];
		Arrays.fill(gridDimensions, 1);
		final ListImg< Cell< A > > imgOfCell = new ListImg< Cell< A > >( Collections.singletonList(cell), gridDimensions );
		return create( type, imgOfCell, dim );
	}
	
	/**
	 * Creates an {@link CellImg}&lt; T, A &gt;
	 * using a {@link Collection} passed as argument.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A > CellImg< T, A > create( final T type, final Collection< Cell< A > > cells, final long... dim )
	{
		Cell< A> cell = cells.iterator().next();
		final int[] cellDimensions = new int[ dim.length ];
		cell.dimensions(cellDimensions);
		final CellGrid grid = new CellGrid( dim, cellDimensions );
		
		final ListImg< Cell< A > > imgOfCells = new ListImg< Cell< A > >(cells, grid.getGridDimensions());
		return create(type, imgOfCells, dim);
	}
	
	/**
	 * Creates an {@link CellImg}&lt; T, A &gt;
	 * using a {@link ListImg} passed as argument.
	 * 
	 * Private because < T, A > pairs are not constrained generically.
	 * 
	 */
	final private static < T extends NativeType<T>, A > CellImg< T, A > create( final T type, final ListImg< Cell< A > > imgOfCells, final long... dim )
	{
		final CellImgFactory< T > factory = new CellImgFactory< T >( type );
		if( ! verifyCellDimensionsAreIdenticalAndDivisible( imgOfCells , dim ) ) {
			//Alternatively, should we throw an exception?
			return null;
		}
		return factory.create( imgOfCells, dim );
	}
	
	/**
	 * Verify that all cells in ListImg have the same dimensions that evenly divide the entire image
	 *   
	 * @param <A>
	 * @param imgOfCells
	 * @return
	 */
	final private static <A> boolean verifyCellDimensionsAreIdenticalAndDivisible(final ListImg< Cell< A > > imgOfCells, final long[] dim) {
		Cursor< Cell<A> > c = imgOfCells.cursor();
		
		long[] firstCellDim = new long[ c.numDimensions() ];
		long[] cellDim = new long[ c.numDimensions() ];
		
		// Empty cursor
		if ( ! c.hasNext() )
			return false;
		
		// Get first cell
		c.fwd();
		c.get().dimensions( firstCellDim );
		
		// Make sure the cell dimensions evenly divide the image dimensions
		for(int d = 0; d < dim.length; d++) {
			System.out.println("d: " + d + ", dim:" + dim[d] + ", cellDim: " + cellDim[d]);
			if( dim[d] % firstCellDim[d] != 0 ) {
				return false;
			}
		}
		
		// Check that the cell dimensions are all the same
		while ( c.hasNext() ) {
			c.fwd();
			c.get().dimensions( cellDim );
			if( !Arrays.equals( firstCellDim, cellDim ) ) {
				return false;
			}
		}
		
		return true;
	}
}
