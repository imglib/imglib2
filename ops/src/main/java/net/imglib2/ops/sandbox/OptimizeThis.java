package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;

public class OptimizeThis
{
	public static interface Port< T >
	{
		public void set( Sampler< T > sampler );

		public void setConst( T t );

		public Sampler<T> getSampler();
	}

	public static interface Op< T > extends Sampler< T >
	{
		public Port< T > output();
	}

	public static abstract class BinaryExpression< I1, I2, O > implements Op< O >
	{
		protected Sampler< I1 > input1;

		protected final Port< I1 > input1Port = new Port< I1 >()
		{
			@Override
			public void set( final Sampler< I1 > sampler )
			{
				input1 = sampler;
			}

			@Override
			public void setConst( final I1 t )
			{
				input1 = new Const< I1 >( t );
			}

			@Override
			public Sampler<I1> getSampler() {
				return input1;
			}
		};

		protected Sampler< I2 > input2;

		protected final Port< I2 > input2Port = new Port< I2 >()
		{
			@Override
			public void set( final Sampler< I2 > sampler )
			{
				input2 = sampler;
			}

			@Override
			public void setConst( final I2 t )
			{
				input2 = new Const< I2 >( t );
			}

			@Override
			public Sampler<I2> getSampler() {
				return input2;
			}
		};

		protected Sampler< O > output;

		protected final Port< O > outputPort = new Port< O >()
		{
			@Override
			public void set( final Sampler< O > sampler )
			{
				output = sampler;
			}

			@Override
			public void setConst( final O t )
			{
				output = new Const< O >( t );
			}

			@Override
			public Sampler<O> getSampler() {
				return output;
			}
		};

		public BinaryExpression()
		{
			this( null, null, null );
		}

		public BinaryExpression( final Sampler< I1 > input1, final Sampler< I2 > input2, final Sampler< O > output )
		{
			this.input1 = input1;
			this.input2 = input2;
			this.output = output;
		}

		protected BinaryExpression( final BinaryExpression< I1, I2, O > expression )
		{
			this.input1 = expression.input1 == null ? null : expression.input1.copy();
			this.input2 = expression.input2 == null ? null : expression.input2.copy();
			this.output = expression.output == null ? null : expression.output.copy();
		}

		public Port< I1 > input1()
		{
			return input1Port;
		}

		public Port< I2 > input2()
		{
			return input2Port;
		}

		@Override
		public Port< O > output()
		{
			return outputPort;
		}
	}

	public static final class Temporary< T extends Type< T > > implements Sampler< T >
	{
		private final T t;

		public Temporary( final T t )
		{
			this.t = t;
		}

		@Override
		public T get()
		{
			return t;
		}

		@Override
		public Temporary< T > copy()
		{
			return new Temporary< T >( t.copy() );
		}

		public static < T extends Type< T > > Temporary< T > create( final T t )
		{
			return new Temporary< T >( t );
		}
	}

	public static final class Const< T > implements Sampler< T >
	{
		private final T t;

		public Const( final T t )
		{
			this.t = t;
		}

		@Override
		public T get()
		{
			return t;
		}

		@Override
		public Const< T > copy()
		{
			return this;
		}

		public static < T > Const< T > create( final T t )
		{
			return new Const< T >( t );
		}
	}

	public static final class PortForward< T > implements Port< T >, Sampler< T >
	{
		protected Sampler< T > sampler;

		public PortForward()
		{}

		protected PortForward( final PortForward< T > p )
		{
			this.sampler = p.sampler.copy();
		}

		@Override
		public T get()
		{
			return sampler.get();
		}

		@Override
		public PortForward< T > copy()
		{
			return new PortForward< T >( this );
		}

		@Override
		public void set( final Sampler< T > sampler )
		{
			this.sampler = sampler;
		}

		@Override
		public void setConst( final T t )
		{
			this.sampler = new Const< T >( t );
		}

		@Override
		public Sampler<T> getSampler() {
			return sampler;
		}
	}

	public static final class BinaryIterate< I1, I2, O > extends BinaryExpression< IterableInterval< I1 >, IterableInterval< I2 >, IterableInterval< O > >
	{
		final protected Op< O > op;

		final protected Port< I1 > ip1;

		final protected Port< I2 > ip2;

		public BinaryIterate( final Op< O > op, final Port< I1 > ip1, final Port< I2 > ip2 )
		{
			this.op = op;
			this.ip1 = ip1;
			this.ip2 = ip2;
		}

		@Override
		public IterableInterval< O > get()
		{
			final IterableInterval< O > o = output.get();
			final Cursor< O > co = o.cursor();
			final Cursor< I1 > ci1 = input1.get().cursor();
			final Cursor< I2 > ci2 = input2.get().cursor();
			op.output().set( co );
			ip1.set( ci1 );
			ip2.set( ci2 );
			while ( co.hasNext() )
			{
				co.fwd();
				ci1.fwd();
				ci2.fwd();
				op.get();
			}
			return o;
		}

		@Override
		public BinaryIterate< I1, I2, O > copy()
		{
			throw new UnsupportedOperationException();
//			return new BinaryIterate< I1, I2, O >( this );
		}
	}



	// == Ops... ===================================================== //

	public static final class Add< T extends NumericType< T > > extends BinaryExpression< T, T, T >
	{
		public Add()
		{}

		public Add( final Sampler< T > input1, final Sampler< T > input2, final Sampler< T > output )
		{
			super( input1, input2, output );
		}

		@Override
		public T get()
		{
			final T t = output.get();
			t.set( input1.get() );
			t.add( input2.get() );
			return t;
		}

		protected Add( final Add< T > expression )
		{
			super( expression );
		}

		@Override
		public Add< T > copy()
		{
			return new Add< T >( this );
		}
	}

	public static final class Sub< T extends NumericType< T > > extends BinaryExpression< T, T, T >
	{
		public Sub()
		{}

		public Sub( final Sampler< T > input1, final Sampler< T > input2, final Sampler< T > output )
		{
			super( input1, input2, output );
		}

		@Override
		public T get()
		{
			final T t = output.get();
			t.set( input1.get() );
			t.sub( input2.get() );
			return t;
		}

		protected Sub( final Sub< T > expression )
		{
			super( expression );
		}

		@Override
		public Sub< T > copy()
		{
			return new Sub< T >( this );
		}
	}


	// == Static Functions to make it look nice... =================== //

	public static < O, I1, I2 > BinaryIterate< I1, I2, O > iteration( final Op< O > op, final Port< I1 > input1, final Port< I2 > input2 )
	{
		return new BinaryIterate< I1, I2, O >( op, input1, input2 );
	}

	public static Temporary< FloatType > floatTemp()
	{
		return Temporary.create( new FloatType() );
	}

	public static Const< FloatType > floatConstant( final float f )
	{
		return Const.create( new FloatType( f ) );
	}

	public static < T extends NumericType< T > > Add< T > add( final Sampler< T > input1, final Sampler< T > input2, final Sampler< T > output )
	{
		return new Add< T >( input1, input2, output );
	}

	public static < T extends NumericType< T > > Sub< T > sub( final Sampler< T > input1, final Sampler< T > input2, final Sampler< T > output )
	{
		return new Sub< T >( input1, input2, output );
	}

	public static void main( final String[] args )
	{
		final Add< FloatType > add = new Add< FloatType >();

		final Img< FloatType > imgA = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgB = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgC = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgD = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgE = ArrayImgs.floats( 5000, 5000 );
		final Cursor< FloatType > cX = imgE.cursor();

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

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final PortForward< FloatType > i1 = new PortForward< FloatType >();
				final PortForward< FloatType > i2 = new PortForward< FloatType >();
				final BinaryIterate< FloatType, FloatType, FloatType > iterate = iteration( add( add( floatConstant( 100 ), i1, floatTemp() ), i2, null ), i1, i2 );

				iterate.input1().setConst( imgA );
				iterate.input2().setConst( imgB );
				iterate.output().setConst( imgE );

				iterate.get();
//				Opservice.run( iterate );
			}
		} );

		cX.reset();
		for ( i = 0; i < 10; ++i )
			System.out.print( cX.next().get() + "  " );
		System.out.println();

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Add< FloatType > tmp = add( floatConstant( 100 ), null, floatTemp() );
				final Add< FloatType > op = add( tmp, null, null );

				final BinaryIterate< FloatType, FloatType, FloatType > iterate = iteration( op, tmp.input2(), op.input2() );

				iterate.input1().setConst( imgA );
				iterate.input2().setConst( imgB );
				iterate.output().setConst( imgE );
				iterate.get();
			}
		} );

		cX.reset();
		for ( i = 0; i < 10; ++i )
			System.out.print( cX.next().get() + "  " );
		System.out.println();

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final Cursor< FloatType > cb = imgB.cursor();
				final Cursor< FloatType > ce = imgE.cursor();

				final Add< FloatType > tmp = add( floatConstant( 100 ), ca, floatTemp() );
				final Add< FloatType > op = add( tmp, cb, ce );

				while ( ce.hasNext() )
				{
					ca.fwd();
					cb.fwd();
					ce.fwd();
					op.get();
				}
			}
		} );

		cX.reset();
		for ( i = 0; i < 10; ++i )
			System.out.print( cX.next().get() + "  " );
		System.out.println();

		if ( ! true )
		{
			BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
			{
				@Override
				public void run()
				{
					final Cursor< FloatType > ca = imgA.cursor();
					final Cursor< FloatType > cb = imgB.cursor();
					final Cursor< FloatType > cc = imgC.cursor();
					final Cursor< FloatType > cd = imgD.cursor();
					final Cursor< FloatType > ce = imgE.cursor();

//					final Add< FloatType > e1 = new Add< FloatType >( ca, cb, Temporary.create( new FloatType() ) );
//					final Add< FloatType > e2 = new Add< FloatType >( e1, cc, Temporary.create( new FloatType() ) );
//					final Sub< FloatType > op = new Sub< FloatType >( e2, cd, ce );

					final Op< FloatType > op = sub( add( add( ca, cb, floatTemp() ), cc, floatTemp() ), cd, ce );

//					final Add< FloatType > e1 = new Add< FloatType >( null, null, Temporary.create( new FloatType() ) );
//					final Add< FloatType > e2 = new Add< FloatType >( e1, null, Temporary.create( new FloatType() ) );
//					final Sub< FloatType > op = new Sub< FloatType >( e2, null, null );
//					e1.input1().set( ca );
//					e1.input2().set( cb );
//					e2.input2().set( cc );
//					op.input2().set( cd );
//					op.output().set( ce );

					while ( cd.hasNext() )
					{
						ca.fwd();
						cb.fwd();
						cc.fwd();
						cd.fwd();
						ce.fwd();
						op.get();
					}
				}
			} );
		}

		if ( false )
		{
			BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
			{
				@Override
				public void run()
				{

					final Cursor< FloatType > ca = imgA.cursor();
					final Cursor< FloatType > cb = imgB.cursor();
					final Cursor< FloatType > cc = imgC.cursor();

					final Add< FloatType > op = new Add< FloatType >( ca, cb, cc );

					while ( cc.hasNext() )
					{
						ca.fwd();
						cb.fwd();
						cc.fwd();
						op.get();
					}
				}
			} );
		}

		cX.reset();
		for ( i = 0; i < 10; ++i )
			System.out.print( cX.next().get() + "  " );
		System.out.println();
	}
}
