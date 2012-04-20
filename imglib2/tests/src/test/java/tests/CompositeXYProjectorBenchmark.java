package tests;

import ij.ImageJ;
import net.imglib2.IterableInterval;
import net.imglib2.display.ChannelARGBConverter;
import net.imglib2.display.CompositeXYProjector;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class CompositeXYProjectorBenchmark
{
	final Img< UnsignedByteType > img;

	final Img< ARGBType > argbImg;

	public CompositeXYProjectorBenchmark( final String filename ) throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		final ArrayImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		img = new ImgOpener().openImg( filename, factory, new UnsignedByteType() );
		final long[] dim = new long[ img.numDimensions() - 1 ];
		for ( int d = 0; d < dim.length; ++d )
			dim[ d ] = img.dimension( d );
		argbImg = new ArrayImgFactory< ARGBType >().create( dim, new ARGBType() );
		convert( img, argbImg );

		ImageJFunctions.show( argbImg );
	}

	public void convert( final Img< UnsignedByteType > in, final IterableInterval< ARGBType > out )
	{
		final CompositeXYProjector< UnsignedByteType > projector = new CompositeXYProjector< UnsignedByteType >( in, out, ChannelARGBConverter.converterListRGBA, 2 );
		projector.setComposite( true );
//		projector.setComposite( 0, false );
//		projector.setComposite( 1, true );
//		projector.setComposite( 2, false );
		for ( int iteration = 0; iteration < 10; ++iteration )
		{
			final long start = System.currentTimeMillis();
			for ( int i = 0; i < 10; ++i )
				projector.map();
			final long end = System.currentTimeMillis();
			System.out.println( ( end - start ) + " ms (iteration " + iteration + ")" );
		}
	}

	public static void main( final String[] args ) throws IncompatibleTypeException, ImgIOException
	{
		new ImageJ();
		new CompositeXYProjectorBenchmark( "/home/tobias/Desktop/test.png" );
	}
}
