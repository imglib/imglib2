package tobias.views;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.ui.InteractiveViewer2D;
import net.imglib2.view.Views;

/**
 * Examples for virtual pixels access: Views, RealViews, Converters
 *
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Bee
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< UnsignedByteType > img = open( "src/test/java/resources/bee-1.tif", new UnsignedByteType() );
		ImageJFunctions.show( img );
//		show( img );
	}
/*
		// crop
		final RandomAccessibleInterval< UnsignedByteType > crop =
//				Views.interval( img, FinalInterval.createMinSize( 285, 65, 180, 200 ) );
				Views.offsetInterval( img, FinalInterval.createMinSize( 285, 65, 180, 200 ) );
//		show( crop );

		// rotate
		final RandomAccessibleInterval< UnsignedByteType > cropRotate =
				Views.zeroMin( Views.rotate( crop, 0, 1 ) );
//				Views.rotate( crop, 0, 1 );
//		show( cropRotate );

		// mirror-extension without mirroring boundary pixel
		final RandomAccessible< UnsignedByteType > cropRotateExtend =
				Views.extendMirrorSingle( cropRotate );
//		show( cropRotateExtend );

		// re-crop
		final RandomAccessibleInterval< UnsignedByteType > cropRotateExtendCrop =
//				Views.interval( cropRotateExtend, FinalInterval.createMinSize( -285, -65, img.dimension( 0 ), img.dimension( 1 ) ) );
				Views.offsetInterval( cropRotateExtend, FinalInterval.createMinSize( -285, -65, img.dimension( 0 ), img.dimension( 1 ) ) );
//		show( cropRotateExtendCrop );

		// interpolate and affine transform
		final RealRandomAccessible< UnsignedByteType > interpolant =
				Views.interpolate( cropRotateExtend, new NearestNeighborInterpolatorFactory< UnsignedByteType >() );
		final AffineTransform2D affine = new AffineTransform2D();
		affine.rotate( Math.PI / 8 );
		final RandomAccessible< UnsignedByteType > interpolantRotate =
				RealViews.affine( interpolant, affine );
//		show( interpolantRotate );

		// re-crop
		final RandomAccessibleInterval< UnsignedByteType > interpolantRotateCrop =
				Views.offsetInterval( interpolantRotate, FinalInterval.createMinSize( -285, -65, img.dimension( 0 ), img.dimension( 1 ) ) );
//		show( interpolantRotateCrop );

		// convert to ARGB
		final Converter< UnsignedByteType, ARGBType > lut = new Converter< UnsignedByteType, ARGBType >()
		{
			protected int[] rgb = new int[ 256 ];
			{
				for ( int i = 0; i < 256; ++i )
				{
					final double r = 1.0 - i / 255.0;
					final double g = Math.sin( Math.PI * r );
					final double b = 0.5 - 0.5 * Math.cos( Math.PI * g );

					final int ri = ( int ) Math.round( Math.max( 0, 255 * r ) );
					final int gi = ( int ) Math.round( Math.max( 0, 255 * g ) );
					final int bi = ( int ) Math.round( Math.max( 0, 255 * b ) );

					rgb[ i ] = ( ( ( ri << 8 ) | gi ) << 8 ) | bi | 0xff000000;
				}
			}

			@Override
			public void convert( final UnsignedByteType input, final ARGBType output )
			{
				output.set( rgb[ input.get() ] );
			}
		};

		final RandomAccessibleInterval< ARGBType > interpolantRotateCropConvert = Converters.convert( interpolantRotateCrop, lut, new ARGBType() );
//		showargb( interpolantRotateCropConvert );

//		show( cropRotateExtend );
		final SamplerConverter< UnsignedByteType, UnsignedByteType > invert = new SamplerConverter< UnsignedByteType, UnsignedByteType >()
		{
			@Override
			public UnsignedByteType convert( final Sampler< UnsignedByteType > sampler )
			{
				return new UnsignedByteType( new InvertingByteAccess( sampler ) );
			}

			final class InvertingByteAccess implements ByteAccess
			{
				private final Sampler< UnsignedByteType > sampler;

				private InvertingByteAccess( final Sampler< UnsignedByteType > sampler )
				{
					this.sampler = sampler;
				}

				@Override
				public void close() {}

				@Override
				public void setValue( final int i, final byte coded )
				{
					sampler.get().set( 255 - UnsignedByteType.getUnsignedByte( coded ) );
				}

				@Override
				public byte getValue( final int index )
				{
					return UnsignedByteType.getCodedSignedByte( 255 - sampler.get().get() );
				}
			}
		};
		final RandomAccessible< UnsignedByteType > readwriteconverted = Converters.convert( cropRotateExtend, invert );
//		show( readwriteconverted );

//		final Cursor< UnsignedByteType > c = IterableRandomAccessibleInterval.create(
//				Views.offsetInterval( readwriteconverted, FinalInterval.createMinSize( 150, 130, 20, 20 ) )
//			).cursor();
//		while( c.hasNext() )
//			c.next().set( 255 );
//		show( cropRotateExtend );
	}
*/
	public static < T extends RealType< T > & NativeType< T > >
			Img< T > open( final String filename, final T type ) throws ImgIOException
	{
		final ImgOpener opener = new ImgOpener();
		final ImgFactory< T > factory = new ArrayImgFactory< T >();
		final Img< T > img = opener.openImg( filename, factory, type );
		return img;
	}

	public static void show( RandomAccessible< UnsignedByteType > source )
	{
		final int width = 640;
		final int height = 374;
		if ( source instanceof RandomAccessibleInterval )
			source = Views.extendValue( ( RandomAccessibleInterval< UnsignedByteType > ) source, new UnsignedByteType( 0 ) );
		final RealARGBConverter< UnsignedByteType > converter = new RealARGBConverter< UnsignedByteType >( 0, 255 );
		new InteractiveViewer2D< UnsignedByteType >( width, height, source, converter );
	}

	public static void showargb( RandomAccessible< ARGBType > source )
	{
		final int width = 640;
		final int height = 374;
		if ( source instanceof RandomAccessibleInterval )
			source = Views.extendValue( ( RandomAccessibleInterval< ARGBType > ) source, new ARGBType() );
		new InteractiveViewer2D< ARGBType >( width, height, source, new TypeIdentity< ARGBType >() );
	}
}
