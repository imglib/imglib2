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
package net.imglib2.img.imageplus;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Convenience factory methods for creation of {@link ImagePlusImg} instances
 * with the most common pixel {@link Type} variants.  Keep in mind that this
 * cannot be a complete collection since the number of existing pixel
 * {@link Type}s may be extended.
 * 
 * For pixel {@link Type}s T not present in this collection, use the generic
 * {@link ImagePlusImgFactory#create(long[], net.imglib2.type.NativeType)},
 * e.g.
 * 
 * <pre>
 * img = new ImagePlusImgFactory&lt;MyType&gt;.create(new long[]{100, 200}, new MyType());
 * </pre>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class ImagePlusImgs
{
	private ImagePlusImgs() {}
	
	/**
	 * Create a {@link ByteImagePlus}<{@link UnsignedByteType}>.
	 * 
	 * <p>(in ImageJ that would be a hyperstack of {@link ByteProcessor}s)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ByteImagePlus< UnsignedByteType > unsignedBytes( final long... dim )
	{
		return ( ByteImagePlus< UnsignedByteType > )new ImagePlusImgFactory< UnsignedByteType >().create( dim, new UnsignedByteType() );
	}
	
	/**
	 * Create a {@link ByteImagePlus}<{@link ByteType}>.
	 * 
	 * <p>(in ImageJ that would be a hyperstack of {@link ByteProcessor}s)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ByteImagePlus< ByteType > bytes( final long... dim )
	{
		return ( ByteImagePlus< ByteType > )new ImagePlusImgFactory< ByteType >().create( dim, new ByteType() );
	}
	
	/**
	 * Create a {@link ShortImagePlus}<{@link UnsignedShortType}>.
	 * 
	 * <p>(in ImageJ that would be a hyperstack of {@link ShortProcessor}s)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ShortImagePlus< UnsignedShortType > unsignedShorts( final long... dim )
	{
		return ( ShortImagePlus< UnsignedShortType > )new ImagePlusImgFactory< UnsignedShortType >().create( dim, new UnsignedShortType() );
	}
	
	/**
	 * Create a {@link ShortImagePlus}<{@link ShortType}>.
	 * 
	 * <p>(in ImageJ that would be a hyperstack of {@link ShortProcessor}s)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public ShortImagePlus< ShortType > shorts( final long... dim )
	{
		return ( ShortImagePlus< ShortType > )new ImagePlusImgFactory< ShortType >().create( dim, new ShortType() );
	}
	
	/**
	 * Create a {@link IntImagePlus}<{@link UnsignedIntType}>.
	 * 
	 * <p>(In ImageJ that would be a hyperstack of {@link ColorProcessor}s.
	 * The integers, however, would be displayed as ARGB unsigned byte channels
	 * and thus look weird.)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public IntImagePlus< UnsignedIntType > unsignedInts( final long... dim )
	{
		return ( IntImagePlus< UnsignedIntType > )new ImagePlusImgFactory< UnsignedIntType >().create( dim, new UnsignedIntType() );
	}
	
	/**
	 * Create a {@link IntImagePlus}<{@link IntType}>.
	 * 
	 * <p>(In ImageJ that would be a hyperstack of {@link ColorProcessor}s.
	 * The integers, however, would be displayed as ARGB unsigned byte channels
	 * and thus look weird.)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public IntImagePlus< IntType > ints( final long... dim )
	{
		return ( IntImagePlus< IntType > )new ImagePlusImgFactory< IntType >().create( dim, new IntType() );
	}
	
	/**
	 * Create an {@link FloatImagePlusImg}<{@link FloatType}>.
	 * 
	 * <p>(in ImageJ that would be a hyperstack of {@link FloatProcessor}s)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public FloatImagePlus< FloatType > floats( final long... dim )
	{
		return ( FloatImagePlus< FloatType > )new ImagePlusImgFactory< FloatType >().create( dim, new FloatType() );
	}
	
	/**
	 * Create an {@link IntImagePlus}<{@link ARGBType}>.
	 * 
	 * <p>(in ImageJ that would be a hyperstack of {@link ColorProcessor}s)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public IntImagePlus< ARGBType > argbs( final long... dim )
	{
		return ( IntImagePlus< ARGBType > )new ImagePlusImgFactory< ARGBType >().create( dim, new ARGBType() );
	}
	
	/**
	 * Create a {@link FloatImagePlus}<{@link ComplexFloatType}>.
	 * 
	 * <p>(In ImageJ that would be a hyperstack of {@link FloatProcessor}s
	 * with real and imaginary numbers interleaved in the plane.  That means it
	 * would look weird.)</p>
	 * 
	 * @param dim
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	final static public FloatImagePlus< ComplexFloatType > complexFloats( final long... dim )
	{
		return ( FloatImagePlus< ComplexFloatType > )new ImagePlusImgFactory< ComplexFloatType >().create( dim, new ComplexFloatType() );
	}
	
	/**
	 * Create an {@link ImagePlusImg}<{@link ComplexDoubleType}, {@link DoubleArray}>.
	 * 
	 * @param dim
	 * @return
	 */
	final static public < T extends NumericType< T > & NativeType< T > > ImagePlusImg< T, ? > from( final ImagePlus imp )
	{
		return ( ImagePlusImg< T, ? > )ImagePlusAdapter.< T >wrap( imp );
	}
}
