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

package net.imglib2.img.planar;

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
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class PlanarImgs
{
	private PlanarImgs()
	{}

	/**
	 * Create an {@link PlanarImg}<{@link UnsignedByteType}, {@link ByteArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< UnsignedByteType, ByteArray > unsignedBytes( final long... dim )
	{
		return ( PlanarImg< UnsignedByteType, ByteArray > ) new PlanarImgFactory< UnsignedByteType >().create( dim, new UnsignedByteType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link ByteType}, {@link ByteArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ByteType, ByteArray > bytes( final long... dim )
	{
		return ( PlanarImg< ByteType, ByteArray > ) new PlanarImgFactory< ByteType >().create( dim, new ByteType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link UnsignedShortType}, {@link ShortArray}
	 * >.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< UnsignedShortType, ShortArray > unsignedShorts( final long... dim )
	{
		return ( PlanarImg< UnsignedShortType, ShortArray > ) new PlanarImgFactory< UnsignedShortType >().create( dim, new UnsignedShortType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link ShortType}, {@link ShortArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ShortType, ShortArray > shorts( final long... dim )
	{
		return ( PlanarImg< ShortType, ShortArray > ) new PlanarImgFactory< ShortType >().create( dim, new ShortType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link UnsignedIntType}, {@link IntArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< UnsignedIntType, IntArray > unsignedInts( final long... dim )
	{
		return ( PlanarImg< UnsignedIntType, IntArray > ) new PlanarImgFactory< UnsignedIntType >().create( dim, new UnsignedIntType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link IntType}, {@link IntArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< IntType, IntArray > ints( final long... dim )
	{
		return ( PlanarImg< IntType, IntArray > ) new PlanarImgFactory< IntType >().create( dim, new IntType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link LongType}, {@link LongArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< LongType, LongArray > longs( final long... dim )
	{
		return ( PlanarImg< LongType, LongArray > ) new PlanarImgFactory< LongType >().create( dim, new LongType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link BitType}, {@link BitArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< BitType, BitArray > bits( final long... dim )
	{
		return ( PlanarImg< BitType, BitArray > ) new PlanarImgFactory< BitType >().create( dim, new BitType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link FloatType}, {@link FloatArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< FloatType, FloatArray > floats( final long... dim )
	{
		return ( PlanarImg< FloatType, FloatArray > ) new PlanarImgFactory< FloatType >().create( dim, new FloatType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link DoubleType}, {@link DoubleArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< DoubleType, DoubleArray > doubles( final long... dim )
	{
		return ( PlanarImg< DoubleType, DoubleArray > ) new PlanarImgFactory< DoubleType >().create( dim, new DoubleType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link ARGBType}, {@link IntArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ARGBType, IntArray > argbs( final long... dim )
	{
		return ( PlanarImg< ARGBType, IntArray > ) new PlanarImgFactory< ARGBType >().create( dim, new ARGBType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link ComplexFloatType}, {@link FloatArray}
	 * >.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ComplexFloatType, FloatArray > complexFloats( final long... dim )
	{
		return ( PlanarImg< ComplexFloatType, FloatArray > ) new PlanarImgFactory< ComplexFloatType >().create( dim, new ComplexFloatType() );
	}

	/**
	 * Create an {@link PlanarImg}<{@link ComplexDoubleType},
	 * {@link DoubleArray}>.
	 */
	@SuppressWarnings( "unchecked" )
	final static public PlanarImg< ComplexDoubleType, DoubleArray > complexDoubles( final long... dim )
	{
		return ( PlanarImg< ComplexDoubleType, DoubleArray > ) new PlanarImgFactory< ComplexDoubleType >().create( dim, new ComplexDoubleType() );
	}
}
