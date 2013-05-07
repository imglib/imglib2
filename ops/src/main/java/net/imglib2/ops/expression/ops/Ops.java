package net.imglib2.ops.expression.ops;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;
import net.imglib2.ops.expression.BinaryOp;
import net.imglib2.ops.expression.Const;
import net.imglib2.ops.expression.Op;
import net.imglib2.ops.expression.Port;
import net.imglib2.ops.expression.Temporary;
import net.imglib2.ops.expression.UnaryOp;
import net.imglib2.ops.expression.WrapBinary;
import net.imglib2.ops.expression.WrapUnary;
import net.imglib2.type.BooleanType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;

public class Ops
{
	public static < O, I1, I2 > BinaryIterate< O, I1, I2 > iterate( final BinaryOp< O, I1, I2 > op )
	{
		return new BinaryIterate< O, I1, I2 >( op );
	}

	public static < O, I1, I2 > BinaryIterate< O, I1, I2 > iterate( final BinaryOp< O, I1, I2 > op, final Sampler< IterableInterval< O > > output, final Sampler< IterableInterval< I1 > > input1, final Sampler< IterableInterval< I2 > > input2 )
	{
		return new BinaryIterate< O, I1, I2 >( op, output, input1, input2 );
	}

	public static < O, I1 > UnaryIterate< O, I1 > iterate( final UnaryOp< O, I1 > op )
	{
		return new UnaryIterate< O, I1 >( op );
	}

	public static < O, I1 > UnaryIterate< O, I1 > iterate( final UnaryOp< O, I1 > op, final Sampler< IterableInterval< O > > output, final Sampler< IterableInterval< I1 > > input1 )
	{
		return new UnaryIterate< O, I1 >( op, output, input1 );
	}

	public static < T extends Type< T > > IntervalReduce< T > reduce( final BinaryOp< T, T, T > op )
	{
		return new IntervalReduce< T >( op );
	}

	public static < T extends Type< T > > IntervalReduce< T > reduce( final BinaryOp< T, T, T > op, final Sampler< T > output, final Sampler< IterableInterval< T > > input1 )
	{
		return new IntervalReduce< T >( op, output, input1 );
	}

	public static < T extends Comparable< T > & Type< T >, B extends BooleanType< B >, I extends RandomAccessibleInterval< B > & Positionable & Localizable >  RegionFilter< T, B, I >
		regionfilter( final I region, final UnaryOp< T, IterableInterval< T > > op )
	{
		return new RegionFilter< T, B, I >( region, op );
	}

	public static < T extends Comparable< T > & Type< T >, B extends BooleanType< B >, I extends RandomAccessibleInterval< B > & Positionable & Localizable >  RegionFilter< T, B, I >
		regionfilter( final I region, final UnaryOp< T, IterableInterval< T > > op, final Sampler< RandomAccessibleInterval< T > > output, final Sampler< RandomAccessible< T > > input1 )
	{
		return new RegionFilter< T, B, I >( region, op, output, input1 );
	}

	public static < O, I1, I2 > WrapBinary< O, I1, I2 > wrap( final Op< O > op, final Port< I1 > input1Port, final Port< I2 > input2Port )
	{
		return new WrapBinary< O, I1, I2 >( op, input1Port, input2Port );
	}

	public static < O, I1 > WrapUnary< O, I1 > wrap( final Op< O > op, final Port< I1 > input1Port )
	{
		return new WrapUnary< O, I1 >( op, input1Port );
	}

	public static < O, I1, I2 > O compute( final BinaryOp< O, I1, I2 > op, final O output, final I1 input1, final I2 input2 )
	{
		op.output().setConst( output );
		op.input1().setConst( input1 );
		op.input2().setConst( input2 );
		return op.get();
	}

	public static < O, I1 > O compute( final UnaryOp< O, I1 > op, final O output, final I1 input1 )
	{
		op.output().setConst( output );
		op.input1().setConst( input1 );
		return op.get();
	}

	public static Temporary< FloatType > floatTemp()
	{
		return Temporary.create( new FloatType() );
	}

	public static Const< FloatType > floatConstant( final float f )
	{
		return Const.create( new FloatType( f ) );
	}

	public static < T extends NumericType< T > > Add< T > add( final Sampler< T > output, final Sampler< T > input1, final Sampler< T > input2 )
	{
		return new Add< T >( output, input1, input2 );
	}

	public static < T extends NumericType< T > > Add< T > add()
	{
		return new Add< T >();
	}

	public static < T extends NumericType< T > > Sub< T > sub( final Sampler< T > output, final Sampler< T > input1, final Sampler< T > input2 )
	{
		return new Sub< T >( output, input1, input2 );
	}

	public static < T extends NumericType< T > > Sub< T > sub()
	{
		return new Sub< T >();
	}

	public static < T extends Comparable< T > & Type< T > > Min< T > min( final Sampler< T > output, final Sampler< T > input1, final Sampler< T > input2 )
	{
		return new Min< T >( output, input1, input2 );
	}

	public static < T extends Comparable< T > & Type< T > > Min< T > min()
	{
		return new Min< T >();
	}

	public static < T extends Comparable< T > & Type< T > > Max< T > max( final Sampler< T > output, final Sampler< T > input1, final Sampler< T > input2 )
	{
		return new Max< T >( output, input1, input2 );
	}

	public static < T extends Comparable< T > & Type< T > > Max< T > max()
	{
		return new Max< T >();
	}
}
