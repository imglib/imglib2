package net.imglib2.converter;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.BenchmarkHelper;

public class RealARGBConverterBenchmark
{
	final Img< UnsignedByteType > img;

	final Img< ARGBType > argbImg;

	public RealARGBConverterBenchmark( final String filename ) throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		final ArrayImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		img = new ImgOpener().openImg( filename, factory, new UnsignedByteType() );
		argbImg = new ArrayImgFactory< ARGBType >().create( img, new ARGBType() );

		BenchmarkHelper.benchmarkAndPrint( 15, true, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int i = 0; i < 10; ++i )
					convert( img, argbImg );
			}
		} );

		ImageJFunctions.show( argbImg );
	}

	public < T extends RealType< T > > void convert( final Img< T > in,  final Img< ARGBType > out )
	{
		final Cursor< T > cin = in.cursor();
		final Cursor< ARGBType > cout = out.cursor();
		final RealARGBConverter< T > converter = new RealARGBConverter< T >( 0, 1000 );
		while( cin.hasNext() )
			converter.convert( cin.next(), cout.next() );
	}

	public static void main( final String[] args ) throws IncompatibleTypeException, ImgIOException
	{
		new ImageJ();
		new RealARGBConverterBenchmark( "/Users/pietzsch/workspace/data/DrosophilaWing.tif" );
	}

}
