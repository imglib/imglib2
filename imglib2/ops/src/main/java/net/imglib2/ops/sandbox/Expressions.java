package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;
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
		public SumExpression< T > copy()
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
		public AddOp< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static void mainBinary( final String args[] )
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

				ca.reset();
				cb.reset();
				cc.reset();

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

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final Cursor< FloatType > cb = imgB.cursor();
				final AddOp< FloatType > op = new AddOp< FloatType >();
				for ( final FloatType t : imgC )
					op.compute( ca.next(), cb.next(), t );
			}
		} );

		final Cursor< FloatType > cc = imgC.cursor();
		for ( i = 0; i < 10; ++i )
			System.out.print( cc.next().get() + "  " );
		System.out.println();
	}

// ==================================================================================

	public static class CopyExpression< T extends NumericType< T > > implements Sampler< T >
	{
		protected Sampler< T > a = null;

		protected Sampler< T > b = null;

		public CopyExpression()
		{
		}

		protected void setA( final Sampler< T > s )
		{
			a = s;
		}

		protected void setB( final Sampler< T > s )
		{
			b = s;
		}

		@Override
		public T get()
		{
			final T t = b.get();
			t.set( a.get() );
			return t;
		}

		@Override
		public CopyExpression< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class CopyOp< T extends NumericType< T > > implements UnaryOperation< T, T >
	{
		@Override
		public T compute( final T input1, final T output )
		{
			output.set( input1 );
			return output;
		}

		@Override
		public CopyOp< T >copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static void mainUnary( final String args[] )
	{
		final Img< FloatType > imgA = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgB = ArrayImgs.floats( 5000, 5000 );

		int i = 0;
		for ( final FloatType t : imgA )
			t.set( i++ );

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final Cursor< FloatType > cb = imgB.cursor();

				ca.reset();
				cb.reset();

				final CopyExpression< FloatType > e = new CopyExpression< FloatType >();
				e.setA( ca );
				e.setB( cb );

				while ( cb.hasNext() )
				{
					ca.fwd();
					cb.fwd();
					e.get();
				}
			}
		} );

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final CopyOp< FloatType > op = new CopyOp< FloatType >();
				for ( final FloatType t : imgB )
					op.compute( ca.next(), t );
			}
		} );

		final Cursor< FloatType > cb = imgB.cursor();
		for ( i = 0; i < 10; ++i )
			System.out.print( cb.next().get() + "  " );
		System.out.println();
	}

	public static void main( final String args[] )
	{
		mainBinary( args );
		mainUnary( args );
	}

}
