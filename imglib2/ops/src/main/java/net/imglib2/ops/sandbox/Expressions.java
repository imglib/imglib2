package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;

public class Expressions
{
	public static class SumExpression< T extends NumericType< T > > implements Sampler< T >
	{
		protected Sampler< T > a = null;

		protected Sampler< T > b = null;

		protected Sampler< T > c = null;

		protected void setA( final Sampler< T > s )
		{
			a = s;
		}

		protected void setB( final Sampler< T > s )
		{
			b = s;
		}

		protected void setC( final Sampler< T > s )
		{
			c = s;
		}

		@Override
		public T get()
		{
			final T t = c.get();
			t.set( a.get() );
			t.add( b.get() );
			return t;
		}

		@Override
		public Sampler< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Port< T > implements Sampler< T >
	{
		protected T t = null;

		public void set( final T t )
		{
			this.t = t;
		}

		@Override
		public T get()
		{
			return t;
		}

		@Override
		public Sampler< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class Const< T > implements Sampler< T >
	{
		protected final T t;

		public static < T > Const< T > create( final T t )
		{
			return new Const< T >( t );
		}

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
		public Sampler< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class AddOp< T extends NumericType< T > > implements BinaryOperation< T, T, T >
	{
		@Override
		public T compute( final T input1, final T input2, final T output )
		{
			output.set( input1 );
			output.add( input2 );
			return output;
		}

		@Override
		public BinaryOperation< T, T, T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static void mainOld( final String args[] )
	{
		final FloatType a = new FloatType( 1 );
		final FloatType b = new FloatType( 2 );
		final FloatType c = new FloatType();

		// final SumExpression< FloatType > e = new SumExpression< FloatType
		// >();
		// e.setA( Const.create( a ) );
		// e.setB( Const.create( b ) );
		// e.setC( Const.create( new FloatType() ) );
		// System.out.println( e.get().get() );

		final SumExpression< FloatType > e = new SumExpression< FloatType >();
		final Port< FloatType > portA = new Port< FloatType >();
		final Port< FloatType > portB = new Port< FloatType >();
		e.setA( Const.create( a ) );
		e.setB( Const.create( b ) );
		e.setC( Const.create( c ) );

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				float sum = 0;
				for ( int i = 0; i < 5000; ++i )
				{
					a.set( i / 100.0f );
					for ( int j = 0; j < 5000; ++j )
					{
						b.set( j / 100.0f );
						// c.set( a );
						// c.add( b );
						// sum += c.get();
						sum += e.get().get();
					}
				}
				System.out.println( sum );
			}
		} );

		// using SumExpression:
		// run 0: 982 ms
		// run 1: 847 ms
		// run 2: 623 ms
		// ...
		// median: 622 ms

		// using type directly:
		// run 0: 724 ms
		// run 1: 716 ms
		// run 2: 616 ms
		// ...
		// median: 617 ms
	}

	public static void main( final String args[] )
	{
		final Img< FloatType > imgA = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgB = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgC = ArrayImgs.floats( 5000, 5000 );

		int i = 0;
		for ( final FloatType t : imgA )
			t.set( i++ );
		i = 0;
		for ( final FloatType t : imgB )
			t.set( i++ );

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final Cursor< FloatType > cb = imgB.cursor();
				final Cursor< FloatType > cc = imgC.cursor();

				final SumExpression< FloatType > e = new SumExpression< FloatType >();
				e.setA( ca );
				e.setB( cb );
				e.setC( cc );

				while ( cc.hasNext() )
				{
					ca.fwd();
					cb.fwd();
					cc.fwd();
					e.get();
				}
			}
		} );

//		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				final Cursor< FloatType > ca = imgA.cursor();
//				final Cursor< FloatType > cb = imgB.cursor();
//				final AddOp< FloatType > op = new AddOp< FloatType >();
//				for ( final FloatType t : imgC )
//					op.compute( ca.next(), cb.next(), t );
//			}
//		} );

//		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				final Cursor< FloatType > ca = imgA.cursor();
//				final Cursor< FloatType > cb = imgB.cursor();
//				for ( final FloatType t : imgC )
//				{
//					ca.fwd();
//					cb.fwd();
//					t.set( ca.get() );
//					t.add( cb.get() );
//				}
//			}
//		} );

//		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				final Cursor< FloatType > ca = imgA.cursor();
//				final Cursor< FloatType > cb = imgB.cursor();
//				final Cursor< FloatType > cc = imgC.cursor();
//				while ( cc.hasNext() )
//				{
//					ca.fwd();
//					cb.fwd();
//					cc.fwd();
//					final FloatType t = cc.get();
//					t.set( ca.get() );
//					t.add( cb.get() );
//				}
//			}
//		} );

		final Cursor< FloatType > cc = imgC.cursor();
		for ( i = 0; i < 10; ++i )
			System.out.print( cc.next().get() + "  " );
		System.out.println();

		// using SumExpression
		// ===================
		// run 0: 149 ms
		// run 1: 96 ms
		// run 2: 82 ms
		// ...
		// median: 83 ms

		// using AddOp
		// ===================
		// run 0: 111 ms
		// run 1: 82 ms
		// run 2: 80 ms
		// ...
		// median: 79 ms

		// using enhanced for
		// ===================
		// run 0: 99 ms
		// run 1: 81 ms
		// run 2: 78 ms
		// ...
		// median: 78 ms

		// using while( hasNext() )
		// ===================
		// run 0: 101 ms
		// run 1: 85 ms
		// run 2: 81 ms
		// ...
		// median: 80 ms
	}
}
