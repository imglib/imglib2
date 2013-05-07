package net.imglib2.ops.expression.examples;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.ops.expression.PortRef;
import net.imglib2.ops.expression.WrapBinary;
import net.imglib2.ops.expression.ops.Add;
import net.imglib2.ops.expression.ops.BinaryIterate;
import net.imglib2.ops.expression.ops.Ops;
import net.imglib2.type.numeric.real.FloatType;

public class AddExample
{
	/**
	 * Create a pixelwise {@link Add} operation.
	 * Manually iterate it over two input and one output image by
	 * 1.) connecting cursors to the input and output ports,
	 * 2.) fwd()ing the cursors and calling get() on the operation to run it.
	 */
	public static void addExample1( final Img< FloatType > inputA, final Img< FloatType > inputB, final Img< FloatType > output )
	{
		final Cursor< FloatType > ca = inputA.cursor();
		final Cursor< FloatType > cb = inputB.cursor();
		final Cursor< FloatType > cc = output.cursor();

		final Add< FloatType > op = new Add< FloatType >();
		op.input1().set( ca );
		op.input2().set( cb );
		op.output().set( cc );

		// alternatively, connect the cursors to the Add when creating it:
//		final Add< FloatType > op = new Add< FloatType >( cc, ca, cb );

		// alternatively, use a static creator method:
//		final Add< FloatType > op = Ops.add( cc, ca, cb );

		while ( cc.hasNext() )
		{
			ca.fwd();
			cb.fwd();
			cc.fwd();
			op.get();
		}
	}

	/**
	 * DONT DO THIS! It creates lot's of temporary objects and therefore is much slower than addExample1.
	 */
	public static void addExample1b( final Img< FloatType > inputA, final Img< FloatType > inputB, final Img< FloatType > output )
	{
		final Cursor< FloatType > ca = inputA.cursor();
		final Cursor< FloatType > cb = inputB.cursor();
		final Cursor< FloatType > cc = output.cursor();

		final Add< FloatType > op = new Add< FloatType >();
		while ( cc.hasNext() )
			Ops.compute( op, cc.next(), ca.next(), cb.next() );

		// This is equivalent to the following.
//		while ( cc.hasNext() )
//		{
//			op.input1().setConst( ca.next() );
//			op.input2().setConst( cb.next() );
//			op.output().setConst( cc.next() );
//			op.get();
//		}
	}


	/**
	 * Create a pixelwise {@link Add} operation.
	 * Lift it to a binary operation on images using a {@link BinaryIterate} operation and run it on the input and output images.
	 */
	public static void addExample2( final Img< FloatType > inputA, final Img< FloatType > inputB, final Img< FloatType > output )
	{
		final Add< FloatType > pixelOp = new Add< FloatType >();
		final BinaryIterate< FloatType, FloatType, FloatType > op = new BinaryIterate<FloatType, FloatType, FloatType>( pixelOp );

		// setConst(t) creates a new Sampler<T> object whose get() returns t.
		op.input1().setConst( inputA );
		op.input2().setConst( inputB );
		op.output().setConst( output );
		op.get();

		// alternatively:
//		Ops.compute( op, output, inputA, inputB );

		// or do it in one step:
//		Ops.compute( Ops.iterate( Ops.<FloatType>add() ), output, inputA, inputB );
	}

	/**
	 * Create a pixelwise binary operation on inputs i1 and i2 that computes ( ( 100 + i1 ) + i2 ).
	 * Lift it to a binary operation on images using a {@link BinaryIterate} operation and run it on the input and output images.
	 */
	public static void addExample3( final Img< FloatType > inputA, final Img< FloatType > inputB, final Img< FloatType > output )
	{
		final Add< FloatType > innerpixelop = Ops.add( Ops.floatTemp(), Ops.floatConstant( 100 ), null );
		final Add< FloatType > outerpixelop = Ops.add( null, innerpixelop, null );
		final WrapBinary< FloatType, FloatType, FloatType > pixelop = Ops.wrap( outerpixelop, innerpixelop.input2(), outerpixelop.input2() );
		final BinaryIterate< FloatType, FloatType, FloatType > op = Ops.iterate( pixelop );

		Ops.compute( op, output, inputA, inputB );
	}

	/**
	 * Create a pixelwise binary operation on inputs i1 and i2 that computes ( ( 100 + i1 ) + i2 ).
	 * Lift it to a binary operation on images using a {@link BinaryIterate} operation and run it on the input and output images.
	 *
	 * Here, the lifted operation is constructed in one line, using {@link PortRef} objects as references to ports of the inner operations.
	 */
	public static void addExample4( final Img< FloatType > inputA, final Img< FloatType > inputB, final Img< FloatType > output )
	{
		final PortRef< FloatType > i1 = new PortRef< FloatType >();
		final PortRef< FloatType > i2 = new PortRef< FloatType >();
		final BinaryIterate< FloatType, FloatType, FloatType > op = Ops.iterate( Ops.wrap( Ops.add( null, Ops.add( Ops.floatTemp(), Ops.floatConstant( 100 ), i1 ), i2 ), i1, i2 ) );

		// This is equivalent to the following (compare to addExample3)
//		final Add< FloatType > innerpixelop = Ops.add( Ops.floatTemp(), Ops.floatConstant( 100 ), i1 );
//		final Add< FloatType > outerpixelop = Ops.add( null, innerpixelop, i2 );
//		final WrapBinary< FloatType, FloatType, FloatType > pixelop = Ops.wrap( outerpixelop, i1, i2 );
//		final BinaryIterate< FloatType, FloatType, FloatType > op = Ops.iterate( pixelop );

		Ops.compute( op, output, inputA, inputB );
	}

	public static void main( final String[] args )
	{
		final Img< FloatType > imgA = ArrayImgs.floats( 1000, 1000 );
		final Img< FloatType > imgB = ArrayImgs.floats( 1000, 1000 );
		final Img< FloatType > imgC = ArrayImgs.floats( 1000, 1000 );
		final Img< FloatType > imgD = ArrayImgs.floats( 1000, 1000 );
		final Img< FloatType > imgE = ArrayImgs.floats( 1000, 1000 );

		int i = 0;
		for ( final FloatType t : imgA )
			t.set( i++ );
		i = 0;
		for ( final FloatType t : imgB )
			t.set( i++ );
		i = 0;
		for ( final FloatType t : imgC )
			t.set( i++ );
		i = 0;
		for ( final FloatType t : imgD )
			t.set( i++ );

		final Cursor< FloatType > result = imgE.cursor();

		addExample1( imgA, imgB, imgE );

		result.reset();
		for ( i = 0; i < 10; ++i )
			System.out.print( result.next().get() + "  " );
		System.out.println( "..." );

		addExample2( imgA, imgB, imgE );

		result.reset();
		for ( i = 0; i < 10; ++i )
			System.out.print( result.next().get() + "  " );
		System.out.println( "..." );

	}
}
