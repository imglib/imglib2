package net.imglib2.img;

import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayImgBufferFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.BenchmarkHelper;

public class BufferAndUnsafeBenchmark< T extends NativeType< T > & RealType< T > >
{
	private final T type;

	private final long[] dimensions;

	public BufferAndUnsafeBenchmark( final T type, final long[] dimensions )
	{
		this.type = type;
		this.dimensions = dimensions;
	}

	public void benchmarkCopy( final ImgFactory< T > factory )
	{
		System.out.println( "copy image using " + factory.getClass() );
		final Img< T > img = factory.create( dimensions, type );
		final Img< T > img2 = factory.create( dimensions, type );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< T > out = img2.cursor();
				for ( final T t : img )
					out.next().set( t );
			}
		} );
	}

	public void benchmarkAddConstant( final ImgFactory< T > factory, final T constant )
	{
		System.out.println( "add constant to image using " + factory.getClass() );
		final Img< T > img = factory.create( dimensions, type );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( final T t : img )
					t.add( constant );
			}
		} );
	}

	public static void main( final String[] args )
	{
		final BufferAndUnsafeBenchmark< UnsignedByteType > b = new BufferAndUnsafeBenchmark< UnsignedByteType >( new UnsignedByteType( 2 ), new long[] { 300, 300, 300 } );

//		final ImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
//		final ImgFactory< UnsignedByteType > factory = new ArrayImgUnsafeFactory< UnsignedByteType >();
		final ImgFactory< UnsignedByteType > factory = new ArrayImgBufferFactory< UnsignedByteType >();


		b.benchmarkCopy( factory );

		// The following is slow for ArrayImgUnsafeFactory and
		// ArrayImgBufferFactory because it mixes UnsignedByteType backed by
		// ArrayAccess with UnsignedByteType backed by UnsafeAccess or
		// BufferAccess. This makes method calls runtime-polymorphic that would
		// be monomorphic and inlined if only a single UnsignedByteType variant
		// would be used.
		b.benchmarkAddConstant( factory, new UnsignedByteType( 1 ) );

		// Replace the above by the following crude workaround, to demonstrate
		// that runtime-polymorphism is indeed the problem here:
		/*
		final UnsignedByteType constant = factory.create( new long[] {1}, new UnsignedByteType() ).cursor().next();
//		final UnsignedByteType constant = new UnsignedByteType( new ByteUnsafeAccess( 1 ) );
		constant.set( 1 );
		b.benchmarkAddConstant( factory, constant );
		*/
	}
}
