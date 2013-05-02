package net.imglib2.algorithm.fft2;

import net.imglib2.Cursor;
import net.imglib2.FinalDimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Computes the convolution of an image with an arbitrary kernel. Computation is
 * based on the Convolution Theorem
 * (http://en.wikipedia.org/wiki/Convolution_theorem). By default computation is
 * performed in-place, i.e. the input is replaced by the convolution. A separate
 * output can, however, be specified.
 * 
 * The class supports to sequentially convolve the same image with different
 * kernels. To achieve that, you have to call setKeepImgFFT(true) and for each
 * sequential run replace the kernel by either calling
 * setKernel(RandomAccessibleInterval< R > kernel) or
 * setKernel(RandomAccessible< R > kernel, Interval kernelInterval). The Fourier
 * convolution will keep the FFT of the image which will speed up all
 * convolutions after the initial run() call. NOTE: There is no checking if the
 * sizes are compatible. If the new kernel has smaller or larger dimensions, it
 * will simply fail. It is up to you to look for that. NOTE: This is not
 * influenced by whether the computation is performed in-place or not, just the
 * FFT of the image is kept.
 * 
 * In the same way, you can sequentially convolve varying images with the same
 * kernel. For that, you simply have to replace the image after the first run()
 * call by calling either setImg(RandomAccessibleInterval< R > img) or
 * setImg(RandomAccessible< R > img, Interval imgInterval). The Fourier
 * convolution will keep the FFT of the kernel which will speed up all
 * convolutions after the initial run() call. NOTE: There is no checking if the
 * sizes are compatible. If the new input has smaller or larger dimensions, it
 * will simply fail. It is up to you to look for that. NOTE: This is not
 * influenced by whether the computation is performed in-place or not, just the
 * FFT of the kernel is kept.
 * 
 * @author Stephan Preibisch
 */
public class FFTConvolution< T extends RealType< T >, K extends RealType< K >, R extends RealType< R > > implements Runnable
{
	Img< ComplexFloatType > fftImg, fftKernel;

	ImgFactory< ComplexFloatType > fftFactory;

	RandomAccessible< T > img;

	RandomAccessible< K > kernel;

	Interval imgInterval, kernelInterval;

	RandomAccessibleInterval< R > output;

	boolean keepImgFFT = false;

	/**
	 * Compute a Fourier space based convolution in-place (img will be replaced
	 * by the convolved result). The image will be extended by mirroring with
	 * single boundary, the kernel will be zero-padded. The {@link ImgFactory}
	 * for creating the FFT will be identical to the one used by the 'img' if
	 * possible, otherwise an {@link ArrayImgFactory} or {@link CellImgFactory}
	 * depending on the size.
	 * 
	 * @param img
	 *            - the image
	 * @param kernel
	 *            - the convolution kernel
	 */
	public static < T extends RealType< T >, K extends RealType< K >> FFTConvolution< T, K, T > create( final Img< T > img, final Img< K > kernel )
	{
		return create( img, kernel, img );
	}

	/**
	 * Compute a Fourier space based convolution The image will be extended by
	 * mirroring with single boundary, the kernel will be zero-padded. The
	 * {@link ImgFactory} for creating the FFT will be identical to the one used
	 * by the 'img' if possible, otherwise an {@link ArrayImgFactory} or
	 * {@link CellImgFactory} depending on the size.
	 * 
	 * @param img
	 *            - the image
	 * @param kernel
	 *            - the convolution kernel
	 * @param output
	 *            - the result of the convolution
	 */
	public static < T extends RealType< T >, K extends RealType< K >, R extends RealType< R >> FFTConvolution< T, K, R > create( final Img< T > img, final Img< K > kernel, final RandomAccessibleInterval< R > output )
	{
		return create( img, kernel, output, getFFTFactory( img ) );
	}

	/**
	 * Compute a Fourier space based convolution in-place (img will be replaced
	 * by the convolved result). The image will be extended by mirroring with
	 * single boundary, the kernel will be zero-padded.
	 * 
	 * @param img
	 *            - the image
	 * @param kernel
	 *            - the convolution kernel
	 * @param factory
	 *            - the {@link ImgFactory} to create the fourier transforms
	 */
	public static < T extends RealType< T >, K extends RealType< K >> FFTConvolution< T, K, T > create( final RandomAccessibleInterval< T > img, final RandomAccessibleInterval< K > kernel, final ImgFactory< ComplexFloatType > factory )
	{
		return create( img, kernel, img, factory );
	}

	/**
	 * Compute a Fourier space based convolution The image will be extended by
	 * mirroring with single boundary, the kernel will be zero-padded.
	 * 
	 * @param img
	 *            - the image
	 * @param kernel
	 *            - the convolution kernel
	 * @param output
	 *            - the output
	 * @param factory
	 *            - the {@link ImgFactory} to create the fourier transforms
	 */
	public static < T extends RealType< T >, K extends RealType< K >, R extends RealType< R >> FFTConvolution< T, K, R > create( final RandomAccessibleInterval< T > img, final RandomAccessibleInterval< K > kernel, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory )
	{
		return create( Views.extendMirrorSingle( img ), img, Views.extendValue( kernel, Util.getTypeFromInterval( kernel ).createVariable() ), kernel, output, factory );
	}

	/**
	 * Compute a Fourier space based convolution in-place (img will be replaced
	 * by the convolved result). The input as well as the kernel need to be
	 * extended or infinite already as the {@link Interval} required to perform
	 * the Fourier convolution is significantly bigger than the {@link Interval}
	 * provided here.
	 * 
	 * Interval size of img and kernel: size(img) + 2*(size(kernel)-1) + pad to
	 * fft compatible size
	 * 
	 * @param img
	 *            - the input
	 * @param imgInterval
	 *            - the input interval (i.e. the area to be convolved)
	 * @param kernel
	 *            - the kernel
	 * @param kernelInterval
	 *            - the kernel interval
	 * @param factory
	 *            - the {@link ImgFactory} to create the fourier transforms
	 */
	public static < T extends RealType< T >, K extends RealType< K >> FFTConvolution< T, K, T > create( final RandomAccessible< T > img, final Interval imgInterval, final RandomAccessible< K > kernel, final Interval kernelInterval, final ImgFactory< ComplexFloatType > factory )
	{
		return create( img, imgInterval, kernel, kernelInterval, Views.interval( img, imgInterval ), factory );
	}

	/**
	 * Compute a Fourier space based convolution. The input as well as the
	 * kernel need to be extended or infinite already as the {@link Interval}
	 * required to perform the Fourier convolution is significantly bigger than
	 * the {@link Interval} provided here.
	 * 
	 * Interval size of img and kernel: size(img) + 2*(size(kernel)-1) + pad to
	 * fft compatible size
	 * 
	 * @param img
	 *            - the input
	 * @param imgInterval
	 *            - the input interval (i.e. the area to be convolved)
	 * @param kernel
	 *            - the kernel
	 * @param kernelInterval
	 *            - the kernel interval
	 * @param output
	 *            - the output data+interval
	 * @param factory
	 *            - the {@link ImgFactory} to create the fourier transforms
	 */
	public static < T extends RealType< T >, K extends RealType< K >, R extends RealType< R >> FFTConvolution< T, K, R > create( final RandomAccessible< T > img, final Interval imgInterval, final RandomAccessible< K > kernel, final Interval kernelInterval, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory )
	{
		return new FFTConvolution< T, K, R >( img, imgInterval, kernel, kernelInterval, output, factory );
	}

	private FFTConvolution( final RandomAccessible< T > img, final Interval imgInterval, final RandomAccessible< K > kernel, final Interval kernelInterval, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory )
	{
		this.img = img;
		this.imgInterval = imgInterval;
		this.kernel = kernel;
		this.kernelInterval = kernelInterval;
		this.output = output;
		this.fftFactory = factory;
	}

	public void setImg( final RandomAccessibleInterval< T > img )
	{
		this.img = Views.extendMirrorSingle( img );
		this.imgInterval = img;
		this.fftImg = null;
	}

	public void setImg( final RandomAccessible< T > img, final Interval imgInterval )
	{
		this.img = img;
		this.imgInterval = imgInterval;
		this.fftImg = null;
	}

	public void setKernel( final RandomAccessibleInterval< K > kernel )
	{
		this.kernel = Views.extendValue( kernel, Util.getTypeFromInterval( kernel ).createVariable() );
		this.kernelInterval = kernel;
		this.fftKernel = null;
	}

	public void setKernel( final RandomAccessible< K > kernel, final Interval kernelInterval )
	{
		this.kernel = kernel;
		this.kernelInterval = kernelInterval;
		this.fftKernel = null;
	}

	public void setOutput( final RandomAccessibleInterval< R > output )
	{
		this.output = output;
	}

	public void setKeepImgFFT( final boolean keep )
	{
		this.keepImgFFT = true;
	}

	public boolean keepImgFFT()
	{
		return keepImgFFT;
	}

	public void setFFTImgFactory( final ImgFactory< ComplexFloatType > factory )
	{
		this.fftFactory = factory;
	}

	public ImgFactory< ComplexFloatType > fftImgFactory()
	{
		return fftFactory;
	}

	public Img< ComplexFloatType > imgFFT()
	{
		return fftImg;
	}

	public Img< ComplexFloatType > kernelFFT()
	{
		return fftKernel;
	}

	@Override
	public void run()
	{
		final int numDimensions = imgInterval.numDimensions();

		// the image has to be extended at least by kernelDimensions/2-1 in each
		// dimension so that
		// the pixels outside of the interval are used for the convolution.
		final long[] newDimensions = new long[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			newDimensions[ d ] = ( int ) imgInterval.dimension( d ) + ( int ) kernelInterval.dimension( d ) - 1;

		// compute the size of the complex-valued output and the required
		// padding
		// based on the prior extended input image
		final long[] paddedDimensions = new long[ numDimensions ];
		final long[] fftDimensions = new long[ numDimensions ];

		FFTMethods.dimensionsRealToComplexFast( FinalDimensions.wrap( newDimensions ), paddedDimensions, fftDimensions );

		// compute the new interval for the input image
		final Interval imgConvolutionInterval = FFTMethods.paddingIntervalCentered( imgInterval, FinalDimensions.wrap( paddedDimensions ) );

		// compute the new interval for the kernel image
		final Interval kernelConvolutionInterval = FFTMethods.paddingIntervalCentered( kernelInterval, FinalDimensions.wrap( paddedDimensions ) );

		// compute where to place the final Interval for the kernel so that the
		// coordinate in the center
		// of the kernel is at position (0,0)
		final long[] min = new long[ numDimensions ];
		final long[] max = new long[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			min[ d ] = kernelInterval.min( d ) + kernelInterval.dimension( d ) / 2;
			max[ d ] = min[ d ] + kernelConvolutionInterval.dimension( d ) - 1;
		}

		// assemble the correct kernel (size of the input + extended periodic +
		// top left at center of input kernel)
		final RandomAccessibleInterval< K > kernelInput = Views.interval( Views.extendPeriodic( Views.interval( kernel, kernelConvolutionInterval ) ), new FinalInterval( min, max ) );
		final RandomAccessibleInterval< T > imgInput = Views.interval( img, imgConvolutionInterval );

		// compute the FFT's if they do not exist yet
		if ( fftImg == null )
			fftImg = FFT.realToComplex( imgInput, fftFactory );

		if ( fftKernel == null )
			fftKernel = FFT.realToComplex( kernelInput, fftFactory );

		final Img< ComplexFloatType > fftconvolved;

		if ( keepImgFFT )
			fftconvolved = fftImg.copy();
		else
			fftconvolved = fftImg;

		// multiply in place
		multiplyComplex( fftconvolved, fftKernel );

		// inverse FFT in place
		FFT.complexToRealUnpad( fftconvolved, output );
	}

	final public static < T extends RealType< T >, K extends RealType< K >, R extends RealType< R > > void convolve( final RandomAccessible< T > img, final Interval imgInterval, final RandomAccessible< K > kernel, final Interval kernelInterval, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory )
	{
		final int numDimensions = imgInterval.numDimensions();

		// the image has to be extended at least by kernelDimensions/2-1 in each
		// dimension so that
		// the pixels outside of the interval are used for the convolution.
		final long[] newDimensions = new long[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			newDimensions[ d ] = ( int ) imgInterval.dimension( d ) + ( int ) kernelInterval.dimension( d ) - 1;

		// compute the size of the complex-valued output and the required
		// padding
		// based on the prior extended input image
		final long[] paddedDimensions = new long[ numDimensions ];
		final long[] fftDimensions = new long[ numDimensions ];

		FFTMethods.dimensionsRealToComplexFast( FinalDimensions.wrap( newDimensions ), paddedDimensions, fftDimensions );

		// compute the new interval for the input image
		final Interval imgConvolutionInterval = FFTMethods.paddingIntervalCentered( imgInterval, FinalDimensions.wrap( paddedDimensions ) );

		// compute the new interval for the kernel image
		final Interval kernelConvolutionInterval = FFTMethods.paddingIntervalCentered( kernelInterval, FinalDimensions.wrap( paddedDimensions ) );

		// compute where to place the final Interval for the kernel so that the
		// coordinate in the center
		// of the kernel is at position (0,0)
		long[] min = new long[ numDimensions ];
		long[] max = new long[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			min[ d ] = kernelInterval.min( d ) + kernelInterval.dimension( d ) / 2;
			max[ d ] = min[ d ] + kernelConvolutionInterval.dimension( d ) - 1;
		}

		// assemble the correct kernel (size of the input + extended periodic +
		// top left at center of input kernel)
		final RandomAccessibleInterval< K > kernelInput = Views.interval( Views.extendPeriodic( Views.interval( kernel, kernelConvolutionInterval ) ), new FinalInterval( min, max ) );
		final RandomAccessibleInterval< T > imgInput = Views.interval( img, imgConvolutionInterval );

		// compute the FFT's
		final Img< ComplexFloatType > fftImg = FFT.realToComplex( imgInput, factory );
		final Img< ComplexFloatType > fftKernel = FFT.realToComplex( kernelInput, factory );

		// multiply in place
		multiplyComplex( fftImg, fftKernel );

		// inverse FFT in place
		FFT.complexToRealUnpad( fftImg, output );
	}

	final public static void multiplyComplex( final Img< ComplexFloatType > img, final Img< ComplexFloatType > kernel )
	{
		final Cursor< ComplexFloatType > cursorA = img.cursor();
		final Cursor< ComplexFloatType > cursorB = kernel.cursor();

		while ( cursorA.hasNext() )
			cursorA.next().mul( cursorB.next() );
	}

	protected static ImgFactory< ComplexFloatType > getFFTFactory( final Img< ? extends RealType< ? > > img )
	{
		try
		{
			return img.factory().imgFactory( new ComplexFloatType() );
		}
		catch ( IncompatibleTypeException e )
		{
			if ( img.size() > Integer.MAX_VALUE / 2 )
				return new CellImgFactory< ComplexFloatType >( 1024 );
			else
				return new ArrayImgFactory< ComplexFloatType >();
		}
	}
}
