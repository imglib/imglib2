
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.imglib2.BenchmarkHelper;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.localneighborhood.LocalNeighborhood2;
import net.imglib2.algorithm.region.localneighborhood.LocalNeighborhoodCursor2;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class TV_Restoration_Imglib implements PlugInFilter {

	public static final float a = 1e-4f;

	protected ImagePlus image;

	@Override
	public int setup(final String arg, final ImagePlus imp) {
		this.image = imp;
		return DOES_8G | DOES_16 | DOES_32;
	}

	@Override
	public void run(final ImageProcessor ip) {
	}

	public static double step(final RandomAccessibleInterval<FloatType> original, final RandomAccessibleInterval<FloatType> current, final IterableInterval<FloatType> next, final double lambda) {
		final RandomAccessibleInterval<FloatType> lv = calculateLocalVariation(current);
		final double w_ag = 0;

		final Cursor<FloatType> cursor = next.localizingCursor();
//		final RandomAccessible<FloatType> lv_extended = Views.extend
//		final LocalNeighborhood<FloatType> l = new LocalNeighborhood<FloatType>()

		return 0;
	}

	public float estimateTolerance(final IterableInterval<FloatType> input) {
		final FloatType min = input.firstElement().copy();
		final FloatType max = min.copy();
		for(final FloatType v : input) {
			if(v.compareTo(min) < 0)
				min.set(v);
			if(v.compareTo(max) > 0)
				max.set(v);
		}
		max.sub(min);
		max.div(new FloatType(256));
		return max.get();
	}

	private static < T extends RealType< T >> void calculateLocalVariation( final RandomAccessible< T > ip, final RandomAccessibleInterval< FloatType > op )
	{
		final Cursor< FloatType > output = Views.iterable( op ).localizingCursor();

		final int n = op.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		op.min( min );
		op.max( max );
		for ( int d = 0; d < n; ++d )
		{
			--min[d];
			++max[d];
		}
		final RandomAccessibleInterval< T > ipInterval = Views.interval( ip, min, max );

		final LocalNeighborhood2< T > ln = new LocalNeighborhood2< T >( ipInterval, output );
		final LocalNeighborhoodCursor2< T > lnc = ln.cursor();
		final IterableInterval< T > iterableIp = Views.iterable( Views.interval( ip, op ) );
		final Cursor< T > input = iterableIp.cursor();

		// iterate over the image
		final FloatType init = new FloatType( a * a );
		final FloatType sum = new FloatType();
		final FloatType tmp = new FloatType();
		final T d = iterableIp.firstElement().createVariable();
		while ( output.hasNext() )
		{
			output.fwd();

			sum.set( init );
			final T ua = input.next();

			ln.updateCenter( output );
			lnc.reset();
			while ( lnc.hasNext() )
			{
				d.set( lnc.next() );
				d.sub( ua );
				d.mul( d );
				tmp.set( d.getRealFloat() );
				sum.add( tmp );
			}
			output.get().set( sum );
		}
	}

	private static <T extends RealType<T>> RandomAccessibleInterval<FloatType> calculateLocalVariation(final RandomAccessibleInterval<T> ip) {

	        final Img<FloatType> uip = new ArrayImgFactory<FloatType>().create(ip, new FloatType());
	        final RandomAccessible<T> extended = Views.extendMirrorDouble(ip);

	        BenchmarkHelper.benchmarkAndPrint( 3, true, new Runnable()
	        {
				@Override
				public void run()
				{
			        calculateLocalVariation( extended, uip );
				}
			} );

	        return uip;
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = new ImgOpener().openImg( "/home/tobias/flybrain-32bit.tif", new ArrayImgFactory< FloatType >(), new FloatType() );
		ImageJFunctions.show( img );
		calculateLocalVariation( img );
	}
}
