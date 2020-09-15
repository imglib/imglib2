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

package net.imglib2.img.cell;

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
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Convenience factory methods for creation of {@link CellImg} instances with
 * the most common pixel {@link Type} variants. Keep in mind that this cannot be
 * a complete collection since the number of existing pixel {@link Type}s may be
 * extended.
 *
 * For pixel {@link Type}s T not present in this collection, use the generic
 * {@link CellImgFactory#create(long[], net.imglib2.type.NativeType)}, e.g.
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
	 * Create an {@link CellImg}&lt;{@link UnsignedByteType}, {@link ByteArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< UnsignedByteType, ByteArray > unsignedBytes( final long... dim )
	{
		return ( CellImg< UnsignedByteType, ByteArray > ) new CellImgFactory<>( new UnsignedByteType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link ByteType}, {@link ByteArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< ByteType, ByteArray > bytes( final long... dim )
	{
		return ( CellImg< ByteType, ByteArray > ) new CellImgFactory<>( new ByteType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link UnsignedShortType}, {@link ShortArray}
	 * &gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< UnsignedShortType, ShortArray > unsignedShorts( final long... dim )
	{
		return ( CellImg< UnsignedShortType, ShortArray > ) new CellImgFactory<>( new UnsignedShortType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link ShortType}, {@link ShortArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< ShortType, ShortArray > shorts( final long... dim )
	{
		return ( CellImg< ShortType, ShortArray > ) new CellImgFactory<>( new ShortType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link UnsignedIntType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< UnsignedIntType, IntArray > unsignedInts( final long... dim )
	{
		return ( CellImg< UnsignedIntType, IntArray > ) new CellImgFactory<>( new UnsignedIntType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link IntType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< IntType, IntArray > ints( final long... dim )
	{
		return ( CellImg< IntType, IntArray > ) new CellImgFactory<>( new IntType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link LongType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< LongType, LongArray > longs( final long... dim )
	{
		return ( CellImg< LongType, LongArray > ) new CellImgFactory<>( new LongType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< NativeBoolType, BooleanArray > booleans( final long... dim )
	{
		return ( CellImg< NativeBoolType, BooleanArray > ) new CellImgFactory<>( new NativeBoolType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link BitType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< BitType, LongArray > bits( final long... dim )
	{
		return ( CellImg< BitType, LongArray > ) new CellImgFactory<>( new BitType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link FloatType}, {@link FloatArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< FloatType, FloatArray > floats( final long... dim )
	{
		return ( CellImg< FloatType, FloatArray > ) new CellImgFactory<>( new FloatType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< DoubleType, DoubleArray > doubles( final long... dim )
	{
		return ( CellImg< DoubleType, DoubleArray > ) new CellImgFactory<>( new DoubleType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link ARGBType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< ARGBType, IntArray > argbs( final long... dim )
	{
		return ( CellImg< ARGBType, IntArray > ) new CellImgFactory<>( new ARGBType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link ComplexFloatType}, {@link FloatArray}
	 * &gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< ComplexFloatType, FloatArray > complexFloats( final long... dim )
	{
		return ( CellImg< ComplexFloatType, FloatArray > ) new CellImgFactory<>( new ComplexFloatType() ).create( dim );
	}

	/**
	 * Create an {@link CellImg}&lt;{@link ComplexDoubleType},
	 * {@link DoubleArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public CellImg< ComplexDoubleType, DoubleArray > complexDoubles( final long... dim )
	{
		return ( CellImg< ComplexDoubleType, DoubleArray > ) new CellImgFactory<>( new ComplexDoubleType() ).create( dim );
	}
}