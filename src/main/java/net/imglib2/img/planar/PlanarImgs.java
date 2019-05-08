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

package net.imglib2.img.planar;

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
 * Convenience factory methods for creation of {@link PlanarImg} instances with
 * the most common pixel {@link Type} variants. Keep in mind that this cannot be
 * a complete collection since the number of existing pixel {@link Type}s may be
 * extended.
 *
 * For pixel {@link Type}s T not present in this collection, use the generic
 * {@link PlanarImgFactory#create(long[], net.imglib2.type.NativeType)}, e.g.
 *
 * <pre>
 * img = new PlanarImgFactory&lt; MyType &gt;.create( new long[] { 100, 200 }, new MyType() );
 * </pre>
 *
 * @author Stephan Saalfeld
 */
final public class PlanarImgs
{
	private PlanarImgs()
	{}

	/**
	 * Create an {@link PlanarImg}&lt;{@link UnsignedByteType}, {@link ByteArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< UnsignedByteType, ByteArray > unsignedBytes( final long... dim )
	{
		return ( PlanarImg< UnsignedByteType, ByteArray > ) new PlanarImgFactory<>( new UnsignedByteType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link ByteType}, {@link ByteArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ByteType, ByteArray > bytes( final long... dim )
	{
		return ( PlanarImg< ByteType, ByteArray > ) new PlanarImgFactory<>( new ByteType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link UnsignedShortType}, {@link ShortArray}
	 * &gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< UnsignedShortType, ShortArray > unsignedShorts( final long... dim )
	{
		return ( PlanarImg< UnsignedShortType, ShortArray > ) new PlanarImgFactory<>( new UnsignedShortType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link ShortType}, {@link ShortArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ShortType, ShortArray > shorts( final long... dim )
	{
		return ( PlanarImg< ShortType, ShortArray > ) new PlanarImgFactory<>( new ShortType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link UnsignedIntType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< UnsignedIntType, IntArray > unsignedInts( final long... dim )
	{
		return ( PlanarImg< UnsignedIntType, IntArray > ) new PlanarImgFactory<>( new UnsignedIntType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link IntType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< IntType, IntArray > ints( final long... dim )
	{
		return ( PlanarImg< IntType, IntArray > ) new PlanarImgFactory<>( new IntType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link LongType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< LongType, LongArray > longs( final long... dim )
	{
		return ( PlanarImg< LongType, LongArray > ) new PlanarImgFactory<>( new LongType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link NativeBoolType}, {@link BooleanArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< NativeBoolType, BooleanArray > booleans( final long... dim )
	{
		return ( PlanarImg< NativeBoolType, BooleanArray > ) new PlanarImgFactory<>( new NativeBoolType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link BitType}, {@link LongArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< BitType, LongArray > bits( final long... dim )
	{
		return ( PlanarImg< BitType, LongArray > ) new PlanarImgFactory<>( new BitType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link FloatType}, {@link FloatArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< FloatType, FloatArray > floats( final long... dim )
	{
		return ( PlanarImg< FloatType, FloatArray > ) new PlanarImgFactory<>( new FloatType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link DoubleType}, {@link DoubleArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< DoubleType, DoubleArray > doubles( final long... dim )
	{
		return ( PlanarImg< DoubleType, DoubleArray > ) new PlanarImgFactory<>( new DoubleType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link ARGBType}, {@link IntArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ARGBType, IntArray > argbs( final long... dim )
	{
		return ( PlanarImg< ARGBType, IntArray > ) new PlanarImgFactory<>( new ARGBType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link ComplexFloatType}, {@link FloatArray}
	 * &gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ComplexFloatType, FloatArray > complexFloats( final long... dim )
	{
		return ( PlanarImg< ComplexFloatType, FloatArray > ) new PlanarImgFactory<>( new ComplexFloatType() ).create( dim );
	}

	/**
	 * Create an {@link PlanarImg}&lt;{@link ComplexDoubleType},
	 * {@link DoubleArray}&gt;.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ComplexDoubleType, DoubleArray > complexDoubles( final long... dim )
	{
		return ( PlanarImg< ComplexDoubleType, DoubleArray > ) new PlanarImgFactory<>( new ComplexDoubleType() ).create( dim );
	}
}
