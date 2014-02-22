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

package net.imglib2.ops.img;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.InputIteratorFactory;
import net.imglib2.ops.input.PointInputIteratorFactory;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.operation.real.binary.RealXor;
import net.imglib2.ops.operation.real.unary.RealMaxConstant;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

// TODO
// 1) move example code out to its own test class elsewhere
// 2) since no longer Img specific move to different subpackage

/**
 * A class used to create images from input images in conjunction with unary or
 * binary operations (see {@link UnaryOperation} and {@link BinaryOperation}).
 * 
 * @author Barry DeZonia
 * 
 */
public class ImageCombiner
{
	// -- public API --

	/**
	 * Creates an output {@link Img} by applying a {@link BinaryOperation} to two
	 * input {@link RandomAccessibleInterval}s. The size of the output image is
	 * determined as the region of overlap of the input intervals.
	 * 
	 * @param op The BinaryOperation used to generate the output image from the
	 *          input intervals.
	 * @param input1 The first input RandomAccessibleInterval
	 * @param input2 The second input RandomAccessibleInterval
	 * @param imgFactory The factory used to create the output image.
	 * @param type The type of the output image.
	 * @return The combined pixel output image of specified type.
	 */
	public static < I1 extends RealType< I1 >, I2 extends RealType< I2 >,
									O extends RealType< O >>
		Img<O> applyOp(BinaryOperation<I1, I2, O> op,
			RandomAccessibleInterval<I1> input1, RandomAccessibleInterval<I2> input2,
			ImgFactory< O > imgFactory, O type )
	{
		long[] span = determineSharedExtents( input1, input2 );
		Img< O > output = imgFactory.create( span, type );
		binaryAssign( op, input1, input2, output, span );
		return output;
	}

	/**
	 * Fills an output {@link RandomAccessibleInterval} by applying a
	 * {@link BinaryOperation} to two input {@link RandomAccessibleInterval}s. The
	 * region to fill in the output interval is determined as the region of
	 * overlap of all three intervals.
	 * 
	 * @param op The BinaryOperation used to fill the output interval from the
	 *          input intervals.
	 * @param input1 The first input RandomAccessibleInterval
	 * @param input2 The second input RandomAccessibleInterval
	 * @param output The output interval to be filled
	 */
	public static < I1 extends RealType< I1 >, I2 extends RealType< I2 >, 
									O extends RealType< O >>
		void applyOp(BinaryOperation<I1, I2, O> op,
			RandomAccessibleInterval<I1> input1, RandomAccessibleInterval<I2> input2,
			RandomAccessibleInterval<O> output)
	{
		long[] span = determineSharedExtents( input1, input2, output );
		binaryAssign( op, input1, input2, output, span );
	}

	/**
	 * Creates an output {@link Img} by applying a {@link UnaryOperation} to an
	 * input {@link RandomAccessibleInterval}. The size of the output image
	 * matches the size of the input.
	 * 
	 * @param op The UnaryOperation used to generate the output image from the
	 *          input data.
	 * @param input The input RandomAccessibleInterval
	 * @param imgFactory The factory used to create the output image.
	 * @param type The type of the output image.
	 * @return The transformed pixel output image of specified type.
	 */
	public static < I extends RealType< I >, O extends RealType< O >>
 Img<O> applyOp(
		UnaryOperation<I, O> op, RandomAccessibleInterval<I> input,
		ImgFactory<O> imgFactory, O type)
	{
		long[] span = new long[ input.numDimensions() ];
		input.dimensions( span );
		Img< O > output = imgFactory.create( span, type );
		unaryAssign( op, input, output, span );
		return output;
	}

	/**
	 * Fills an output {@link RandomAccessibleInterval} by applying a
	 * {@link UnaryOperation} to an input {@link RandomAccessibleInterval}. The
	 * region to fill in the output interval is determined as the region of
	 * overlap of both intervals.
	 * 
	 * @param op The UnaryOperation used to fill the output interval from the
	 *          input interval.
	 * @param input The input RandomAccessibleInterval
	 * @param output The output interval to be filled
	 */
	public static < I extends RealType< I >, O extends RealType< O >>
	void applyOp(
		UnaryOperation<I, O> op, RandomAccessibleInterval<I> input,
		RandomAccessibleInterval<O> output)
	{
		long[] span = determineSharedExtents( input, output );
		unaryAssign( op, input, output, span );
	}

	// -- helpers --

	private static long[] determineSharedExtents(
		RandomAccessibleInterval<?>... imgs)
	{
		if ( imgs.length == 0 )
			throw new IllegalArgumentException(
				"at least one image must be provided" );
		int numDims = imgs[ 0 ].numDimensions();
		long[] commonRegion = new long[ numDims ];
		imgs[ 0 ].dimensions( commonRegion );
		for ( int i = 1; i < imgs.length; i++ )
		{
			if ( imgs[ i ].numDimensions() != numDims )
				throw new IllegalArgumentException(
					"images do not have compatible dimensions" );
			for ( int d = 0; d < numDims; d++ )
			{
				commonRegion[d] = Math.min( imgs[i].dimension(d), commonRegion[d] );
			}
		}
		return commonRegion;
	}

	private static < I1 extends RealType< I1 >, I2 extends RealType< I2 >,
										O extends RealType< O >>
		void binaryAssign(BinaryOperation<I1, I2, O> op,
			RandomAccessibleInterval<I1> input1, RandomAccessibleInterval<I2> input2,
			RandomAccessibleInterval<O> output, long[] span)
	{
		long[] origin = new long[ span.length ];
		O type = output.randomAccess().get();
		final Function< long[], I1 > f1 =
			new RealImageFunction<I1, I1>(input1, input1.randomAccess().get());
		final Function< long[], I2 > f2 =
			new RealImageFunction<I2, I2>(input2, input2.randomAccess().get());
		final Function< long[], O > binFunc =
				new GeneralBinaryFunction< long[], I1, I2, O >(f1, f2, op, type.copy());
		final InputIteratorFactory< long[] > inputFactory =
				new PointInputIteratorFactory();
		final ImageAssignment< O, O, long[] > assigner = 
				new ImageAssignment< O, O, long[] >(
						output, origin, span, binFunc, null, inputFactory );
		assigner.assign();
	}

	private static < I extends RealType< I >, O extends RealType< O >>
 void
		unaryAssign(UnaryOperation<I, O> op, RandomAccessibleInterval<I> input,
			RandomAccessibleInterval<O> output, long[] span)
	{
		final O type = output.randomAccess().get();
		final Function< long[], I > f1 =
			new RealImageFunction<I, I>(input, input.randomAccess().get());
		final Function< long[], O > unaryFunc =
				new GeneralUnaryFunction< long[], I, O >( f1, op, type.copy() );
		final InputIteratorFactory< long[] > inputFactory =
				new PointInputIteratorFactory();
		final ImageAssignment< O, O, long[] > assigner =
				new ImageAssignment< O, O, long[] >(
						output, new long[ span.length ],
						span, unaryFunc, null, inputFactory );
		assigner.assign();
	}

	// -- example code --

	public static void main( String[] args )
	{
		// unary op tests
		maxCreateExample();
		maxFillExample();

		// binary op tests
		addCreateExample();
		addFillExample();
		xorCreateExample();
		xorFillExample();
	}

	private static void addCreateExample()
	{
		Img< UnsignedByteType > img1 =
				makeTestImage( new long[] { 100, 200 }, new UnsignedByteType() );
		
		Img< FloatType > img2 =
				makeTestImage( new long[] { 75, 225 }, new FloatType() );

		ImgFactory< ShortType > imgFactory = new ArrayImgFactory< ShortType >();

		BinaryOperation< UnsignedByteType, FloatType, ShortType > addOp =
				new RealAdd< UnsignedByteType, FloatType, ShortType >();

		Img< ShortType > output =
				ImageCombiner.applyOp( addOp, img1, img2, imgFactory, new ShortType() );

		System.out.println( "--- add create test ---" );
		System.out.println( "Input image one is " +
				img1.dimension( 0 ) + " x " + img1.dimension( 1 ) );
		System.out.println( "Input image two is " +
				img2.dimension( 0 ) + " x " + img2.dimension( 1 ) );
		System.out.println( "Output image is " +
				output.dimension( 0 ) + " x " + output.dimension( 1 ) );
		System.out.println();
	}

	private static void addFillExample()
	{
		Img< UnsignedByteType > img1 =
				makeTestImage( new long[] { 100, 200 }, new UnsignedByteType() );
		
		Img< FloatType > img2 =
				makeTestImage( new long[] { 75, 225 }, new FloatType() );
		
		Img< ShortType > output =
				makeTestImage( new long[] { 75, 225 }, new ShortType() );

		BinaryOperation< UnsignedByteType, FloatType, ShortType > addOp =
				new RealAdd< UnsignedByteType, FloatType, ShortType >();

		ImageCombiner.applyOp( addOp, img1, img2, output );

		System.out.println( "--- add fill test ---" );
		System.out.println( "Input image one is " +
				img1.dimension( 0 ) + " x " + img1.dimension( 1 ) );
		System.out.println( "Input image two is " +
				img2.dimension( 0 ) + " x " + img2.dimension( 1 ) );
		System.out.println( "Output image is " +
				output.dimension( 0 ) + " x " + output.dimension( 1 ) );
		System.out.println();
	}

	private static void maxCreateExample()
	{
		Img< DoubleType > img =
				makeTestImage( new long[] { 512, 512 }, new DoubleType() );

		ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();

		UnaryOperation< DoubleType, IntType > maxOp =
				new RealMaxConstant< DoubleType, IntType >( 150.0 );

		Img< IntType > output =
				ImageCombiner.applyOp( maxOp, img, imgFactory, new IntType() );

		System.out.println( "--- max create test ---" );
		System.out.println( "Input image is " +
				img.dimension( 0 ) + " x " + img.dimension( 1 ) );
		System.out.println( "Output image is " +
				output.dimension( 0 ) + " x " + output.dimension( 1 ) );
		System.out.println();
	}

	private static void maxFillExample()
	{
		Img< DoubleType > img =
				makeTestImage( new long[] { 512, 512 }, new DoubleType() );
		
		Img< IntType > output =
				makeTestImage( new long[] { 512, 512 }, new IntType() );

		UnaryOperation< DoubleType, IntType > maxOp =
				new RealMaxConstant< DoubleType, IntType >( 150.0 );

		ImageCombiner.applyOp( maxOp, img, output );

		System.out.println( "--- max fill test ---" );
		System.out.println( "Input image is " +
				img.dimension( 0 ) + " x " + img.dimension( 1 ) );
		System.out.println( "Output image is " +
				output.dimension( 0 ) + " x " + output.dimension( 1 ) );
		System.out.println();
	}

	private static void xorCreateExample()
	{
		Img< UnsignedByteType > img1 =
				makeTestImage( new long[] { 400, 300 }, new UnsignedByteType() );
		
		Img< UnsignedByteType > img2 =
				makeTestImage( new long[] { 300, 400 }, new UnsignedByteType() );

		ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();

		BinaryOperation< UnsignedByteType, UnsignedByteType, IntType > xorOp =
				new RealXor< UnsignedByteType, UnsignedByteType, IntType >();

		Img< IntType > output =
				ImageCombiner.applyOp( xorOp, img1, img2, imgFactory, new IntType() );

		System.out.println( "--- xor create test ---" );
		System.out.println( "Input image one is " +
				img1.dimension( 0 ) + " x " + img1.dimension( 1 ) );
		System.out.println( "Input image two is " +
				img2.dimension( 0 ) + " x " + img2.dimension( 1 ) );
		System.out.println( "Output image is " +
				output.dimension( 0 ) + " x " + output.dimension( 1 ) );
		System.out.println();
	}

	private static void xorFillExample()
	{
		Img< UnsignedByteType > img1 =
				makeTestImage( new long[] { 400, 300 }, new UnsignedByteType() );
		
		Img< UnsignedByteType > img2 =
				makeTestImage( new long[] { 300, 400 }, new UnsignedByteType() );
		
		Img< IntType > output =
				makeTestImage( new long[] { 300, 400 }, new IntType() );

		BinaryOperation< UnsignedByteType, UnsignedByteType, IntType > xorOp =
				new RealXor< UnsignedByteType, UnsignedByteType, IntType >();

		ImageCombiner.applyOp( xorOp, img1, img2, output );

		System.out.println( "--- xor fill test ---" );
		System.out.println( "Input image one is " +
				img1.dimension( 0 ) + " x " + img1.dimension( 1 ) );
		System.out.println( "Input image two is " +
				img2.dimension( 0 ) + " x " + img2.dimension( 1 ) );
		System.out.println( "Output image is " +
				output.dimension( 0 ) + " x " + output.dimension( 1 ) );
		System.out.println();
	}

	// -- example support code --

	private static < O extends RealType< O > & NativeType< O >>
	Img< O > makeTestImage( long[] dims, O type )
	{
		ImgFactory< O > imgFactory = new ArrayImgFactory< O >();
		Img< O > image = imgFactory.create( dims, type );
		fillImage( image );
		return image;
	}

	private static < O extends RealType< O >> void fillImage( Img< O > img )
	{
		Random rng = new Random();
		Cursor< O > cursor = img.cursor();
		while ( cursor.hasNext() )
		{
			double value = 256 * rng.nextDouble();
			cursor.next().setReal( value );
		}
	}
}
