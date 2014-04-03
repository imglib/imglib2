/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.fft2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * @author Jonathan Hale
 */
public class FFTConvolution< R extends RealType< R > >
{
	Img< ComplexFloatType > fftImg, fftKernel;

	ImgFactory< ComplexFloatType > fftFactory;

	RandomAccessible< R > img, kernel;

	Interval imgInterval, kernelInterval;

	RandomAccessibleInterval< R > output;

	// by default we use the complex conjugate of the kernel
	boolean complexConjugate = true;

	// by default we do not keep the image
	boolean keepImgFFT = false;

	private ExecutorService service;

	/**
	 * Compute a Fourier space based convolution in-place (img will be replaced
	 * by the convolved result). The image will be extended by mirroring with
	 * single boundary, the kernel will be zero-padded. The {@link ImgFactory}
	 * for creating the FFT will be identical to the one used by the 'img' if
	 * possible, otherwise an {@link ArrayImgFactory} or {@link CellImgFactory}
	 * depending on the size.
	 * 
	 * ExecutorService will be created on {@link #convolve()} with threads equal
	 * to number of processors equal to the runtime.
	 * 
	 * @param img
	 *            - the image
	 * @param kernel
	 *            - the convolution kernel
	 */
	public FFTConvolution( final Img< R > img, final Img< R > kernel )
	{
		this( img, kernel, img, (ExecutorService) null );
	}
	
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
	 * @param service
	 *            - service providing threads for multi-threading
	 */
	public FFTConvolution( final Img< R > img, final Img< R > kernel, ExecutorService service )
	{
		this( img, kernel, img, service );
	}

	/**
	 * Compute a Fourier space based convolution The image will be extended by
	 * mirroring with single boundary, the kernel will be zero-padded. The
	 * {@link ImgFactory} for creating the FFT will be identical to the one used
	 * by the 'img' if possible, otherwise an {@link ArrayImgFactory} or
	 * {@link CellImgFactory} depending on the size.
	 * 
	 * ExecutorService will be created on {@link #convolve()} with threads equal
	 * to number of processors equal to the runtime.
	 * 
	 * @param img
	 *            - the image
	 * @param kernel
	 *            - the convolution kernel
	 * @param output
	 *            - the result of the convolution
	 */
	public FFTConvolution( final Img< R > img, final Img< R > kernel, final RandomAccessibleInterval< R > output )
	{
		this( img, kernel, output, getFFTFactory( img ), null );
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
	 * @param service
	 *            - service providing threads for multi-threading
	 */
	public FFTConvolution( final Img< R > img, final Img< R > kernel, final RandomAccessibleInterval< R > output, final ExecutorService service )
	{
		this( img, kernel, output, getFFTFactory( img ), service );
	}
	
	/**
	 * Compute a Fourier space based convolution in-place (img will be replaced
	 * by the convolved result). The image will be extended by mirroring with
	 * single boundary, the kernel will be zero-padded.
	 * 
	 * ExecutorService will be created on {@link #convolve()} with threads equal
	 * to number of processors equal to the runtime.
	 * 
	 * @param img
	 *            - the image
	 * @param kernel
	 *            - the convolution kernel
	 * @param factory
	 *            - the {@link ImgFactory} to create the fourier transforms
	 */
	public FFTConvolution( final RandomAccessibleInterval< R > img, final RandomAccessibleInterval< R > kernel, final ImgFactory< ComplexFloatType > factory )
	{
		this( img, kernel, img, factory, null );
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
	 * @param service
	 *            - service providing threads for multi-threading
	 */
	public FFTConvolution( final RandomAccessibleInterval< R > img, final RandomAccessibleInterval< R > kernel, final ImgFactory< ComplexFloatType > factory, final ExecutorService service )
	{
		this( img, kernel, img, factory, service );
	}
	
	/**
	 * Compute a Fourier space based convolution The image will be extended by
	 * mirroring with single boundary, the kernel will be zero-padded.
	 * 
	 * ExecutorService will be created on {@link #convolve()} with threads equal
	 * to number of processors equal to the runtime.
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
	public FFTConvolution( final RandomAccessibleInterval< R > img, final RandomAccessibleInterval< R > kernel, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory )
	{
		this( Views.extendMirrorSingle( img ), img, Views.extendValue( kernel, Util.getTypeFromInterval( kernel ).createVariable() ), kernel, output, factory, null );
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
	 * @param service
	 *            - service providing threads for multi-threading
	 */
	public FFTConvolution( final RandomAccessibleInterval< R > img, final RandomAccessibleInterval< R > kernel, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory, final ExecutorService service )
	{
		this( Views.extendMirrorSingle( img ), img, Views.extendValue( kernel, Util.getTypeFromInterval( kernel ).createVariable() ), kernel, output, factory, service );
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
	 * ExecutorService will be created on {@link #convolve()} with threads equal
	 * to number of processors equal to the runtime.
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
	public FFTConvolution( final RandomAccessible< R > img, final Interval imgInterval, final RandomAccessible< R > kernel, final Interval kernelInterval, final ImgFactory< ComplexFloatType > factory )
	{
		this( img, imgInterval, kernel, kernelInterval, Views.interval( img, imgInterval ), factory, null );
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
	 * @param service
	 *            - service providing threads for multi-threading
	 */
	public FFTConvolution( final RandomAccessible< R > img, final Interval imgInterval, final RandomAccessible< R > kernel, final Interval kernelInterval, final ImgFactory< ComplexFloatType > factory, final ExecutorService service )
	{
		this( img, imgInterval, kernel, kernelInterval, Views.interval( img, imgInterval ), factory, service );
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
	 * ExecutorService will be created on {@link #convolve()} with threads equal
	 * to number of processors equal to the runtime.
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
	public FFTConvolution( final RandomAccessible< R > img, final Interval imgInterval, final RandomAccessible< R > kernel, final Interval kernelInterval, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory )
	{
		this(img, imgInterval, kernel, kernelInterval, output, factory, null );
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
	 * @param service
	 *            - service providing threads for multi-threading
	 * 
	 */
	public FFTConvolution( final RandomAccessible< R > img, final Interval imgInterval, final RandomAccessible< R > kernel, final Interval kernelInterval, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory, final ExecutorService service )
	{
		this.img = img;
		this.imgInterval = imgInterval;
		this.kernel = kernel;
		this.kernelInterval = kernelInterval;
		this.output = output;
		this.fftFactory = factory;

		setExecutorService( service );
	}

	public void setImg( final RandomAccessibleInterval< R > img )
	{
		this.img = Views.extendMirrorSingle( img );
		this.imgInterval = img;
		this.fftImg = null;
	}

	public void setImg( final RandomAccessible< R > img, final Interval imgInterval )
	{
		this.img = img;
		this.imgInterval = imgInterval;
		this.fftImg = null;
	}

	public void setKernel( final RandomAccessibleInterval< R > kernel )
	{
		this.kernel = Views.extendValue( kernel, Util.getTypeFromInterval( kernel ).createVariable() );
		this.kernelInterval = kernel;
		this.fftKernel = null;
	}

	public void setKernel( final RandomAccessible< R > kernel, final Interval kernelInterval )
	{
		this.kernel = kernel;
		this.kernelInterval = kernelInterval;
		this.fftKernel = null;
	}

	public void setOutput( final RandomAccessibleInterval< R > output )
	{
		this.output = output;
	}

	/**
	 * @param complexConjugate
	 *            - If the complex conjugate of the FFT of the kernel should be
	 *            used.
	 */
	public void setComputeComplexConjugate( final boolean complexConjugate )
	{
		this.complexConjugate = complexConjugate;
		this.fftKernel = null;
	}

	public boolean getComplexConjugate()
	{
		return complexConjugate;
	}

	public void setKeepImgFFT( final boolean keep )
	{
		this.keepImgFFT = keep;
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

	public void convolve()
	{
		ExecutorService s = service;
		if (service == null) {
			// create an ExecutorService
			// FIXME: Better way to define number of threads?
			s = Executors.newFixedThreadPool( Runtime.getRuntime( ).availableProcessors());
		}
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
		final RandomAccessibleInterval< R > kernelInput = Views.interval( Views.extendPeriodic( Views.interval( kernel, kernelConvolutionInterval ) ), new FinalInterval( min, max ) );
		final RandomAccessibleInterval< R > imgInput = Views.interval( img, imgConvolutionInterval );

		// compute the FFT's if they do not exist yet
		if ( fftImg == null )
			fftImg = FFT.realToComplex( imgInput, fftFactory, s );

		if ( fftKernel == null )
		{
			fftKernel = FFT.realToComplex( kernelInput, fftFactory, s );

			// compute the complex conjugate of the FFT of the kernel (same as
			// mirroring the input image)
			// otherwise it corresponds to correlation and not convolution
			if ( complexConjugate )
				FFTMethods.complexConjugate( fftKernel );
		}

		final Img< ComplexFloatType > fftconvolved;

		if ( keepImgFFT )
			fftconvolved = fftImg.copy();
		else
			fftconvolved = fftImg;

		// multiply in place
		multiplyComplex( fftconvolved, fftKernel );

		// inverse FFT in place
		FFT.complexToRealUnpad( fftconvolved, output, s );
		
		if ( service == null ) { 
			// shutdown own self created service
			s.shutdown();
		}
	}

	final public static < R extends RealType< R > > void convolve( final RandomAccessible< R > img, final Interval imgInterval, final RandomAccessible< R > kernel, final Interval kernelInterval, final RandomAccessibleInterval< R > output, final ImgFactory< ComplexFloatType > factory, final int numThreads )
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
		final RandomAccessibleInterval< R > kernelInput = Views.interval( Views.extendPeriodic( Views.interval( kernel, kernelConvolutionInterval ) ), new FinalInterval( min, max ) );
		final RandomAccessibleInterval< R > imgInput = Views.interval( img, imgConvolutionInterval );

		// compute the FFT's
		final Img< ComplexFloatType > fftImg = FFT.realToComplex( imgInput, factory, numThreads );
		final Img< ComplexFloatType > fftKernel = FFT.realToComplex( kernelInput, factory, numThreads );

		// multiply in place
		multiplyComplex( fftImg, fftKernel );

		// inverse FFT in place
		FFT.complexToRealUnpad( fftImg, output, numThreads );
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
		catch ( final IncompatibleTypeException e )
		{
			if ( img.size() > Integer.MAX_VALUE / 2 )
				return new CellImgFactory< ComplexFloatType >( 1024 );
			return new ArrayImgFactory< ComplexFloatType >();
		}
	}

	/**
	 * Set the executor service to use.
	 * 
	 * @param service
	 *            - Executor service to use.
	 */
	public void setExecutorService( final ExecutorService service )
	{
		this.service = service;
	}

	/**
	 * Utility function to create an ExecutorService
	 * 
	 * Number of threads utilized matches available processors in runtime.
	 * 
	 * @return - the new ExecutorService
	 */
	public static final ExecutorService createExecutorService()
	{
		return createExecutorService( Runtime.getRuntime().availableProcessors() );
	}

	/**
	 * Utility function to create an ExecutorService
	 * 
	 * @param nThreads
	 *            - number of threads to utilize
	 * @return - the new ExecutorService
	 */
	public static final ExecutorService createExecutorService( int nThreads )
	{
		return Executors.newFixedThreadPool( nThreads );
	}
}
