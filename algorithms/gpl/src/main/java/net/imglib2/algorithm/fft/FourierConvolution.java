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

package net.imglib2.algorithm.fft;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.fft.FourierTransform.PreProcessing;
import net.imglib2.algorithm.fft.FourierTransform.Rearrangement;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Computes a convolution of an {@link Img} or {@link RandomAccessibleInterval}
 * with an kernel. The computation is based on the Fourier convolution theorem
 * and computation time is therefore independent of the size of kernel (except
 * the kernel becomes bigger than the input, which makes limited sense).
 * 
 * It is possible to exchange the kernel or the image if a series of images is
 * convolved with the same kernel - or if an image has to be convolved with
 * multiple kernels.
 * 
 * The precision of the computation is {@link ComplexFloatType}.
 * 
 * @param <T>
 *            - {@link RealType} of the image
 * @param <S>
 *            - {@link RealType} of the kernel
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 * @deprecated use {@link net.imglib2.algorithm.fft2.FFTConvolution} instead
 */
@Deprecated
public class FourierConvolution< T extends RealType< T >, S extends RealType< S >> implements MultiThreaded, OutputAlgorithm< Img< T >>, Benchmark
{
	public static < T extends RealType< T >, S extends RealType< S > > Img< T > convolve( final Img< T > img, final Img< S > kernel ) throws IncompatibleTypeException
	{
		final FourierConvolution< T, S > convolution = new FourierConvolution< T, S >( img, kernel );
		convolution.process();
		return convolution.getResult();
	}

	public static < T extends RealType< T >, S extends RealType< S > > Img< T > convolve( final RandomAccessibleInterval< T > input, final RandomAccessibleInterval< S > kernel, final ImgFactory< T > imgFactory, final ImgFactory< S > kernelImgFactory, final ImgFactory< ComplexFloatType > fftImgFactory )
	{
		final FourierConvolution< T, S > convolution = new FourierConvolution< T, S >( input, kernel, imgFactory, kernelImgFactory, fftImgFactory );
		convolution.process();
		return convolution.getResult();
	}

	final int numDimensions;

	Img< T > convolved;

	RandomAccessibleInterval< T > image;

	RandomAccessibleInterval< S > kernel;

	Img< ComplexFloatType > kernelFFT, imgFFT;

	FourierTransform< T, ComplexFloatType > fftImage;

	final ImgFactory< ComplexFloatType > fftImgFactory;

	final ImgFactory< T > imgFactory;

	final ImgFactory< S > kernelImgFactory;

	boolean keepImgFFT = true;

	final int[] kernelDim;

	String errorMessage = "";

	int numThreads;

	long processingTime;

	/**
	 * Computes a convolution in Fourier space.
	 * 
	 * @param image
	 *            - the input to be convolved
	 * @param kernel
	 *            - the kernel for the convolution operation
	 * @param fftImgFactory
	 *            - the {@link ImgFactory} that is used to create the FFT's
	 * @param imgFactory
	 *            - the {@link ImgFactory} that is used to compute the convolved
	 *            image
	 * @param kernelImgFactory
	 *            - the {@link ImgFactory} that is used to extend the kernel to
	 *            the right size
	 */
	public FourierConvolution( final RandomAccessibleInterval< T > image, final RandomAccessibleInterval< S > kernel, final ImgFactory< T > imgFactory, final ImgFactory< S > kernelImgFactory, final ImgFactory< ComplexFloatType > fftImgFactory )
	{
		this.numDimensions = image.numDimensions();

		this.image = image;
		this.kernel = kernel;
		this.fftImgFactory = fftImgFactory;
		this.imgFactory = imgFactory;
		this.kernelImgFactory = kernelImgFactory;

		this.kernelDim = new int[ numDimensions ];
		for ( int d = 0; d < numDimensions; ++d )
			kernelDim[ d ] = ( int ) kernel.dimension( d );

		this.kernelFFT = null;
		this.imgFFT = null;

		setNumThreads();
	}

	/**
	 * Computes a convolution in Fourier space.
	 * 
	 * @param image
	 *            - the input {@link Img} to be convolved
	 * @param kernel
	 *            - the kernel {@link Img} for the convolution operation
	 * @param fftImgFactory
	 *            - the {@link ImgFactory} that is used to create the FFT's
	 */
	public FourierConvolution( final Img< T > image, final Img< S > kernel, final ImgFactory< ComplexFloatType > fftImgFactory )
	{
		this( image, kernel, image.factory(), kernel.factory(), fftImgFactory );
	}

	/**
	 * Computes a convolution in Fourier space.
	 * 
	 * @param image
	 *            - the input {@link Img} to be convolved
	 * @param kernel
	 *            - the kernel {@link Img} for the convolution operation
	 * @throws IncompatibleTypeException
	 *             if the factory of the input {@link Img}<T> is not compatible
	 *             with the {@link ComplexFloatType} (it needs to be a
	 *             {@link NativeType})
	 */
	public FourierConvolution( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{
		this( image, kernel, image.factory(), kernel.factory(), image.factory().imgFactory( new ComplexFloatType() ) );
	}

	/**
	 * @return - the {@link ImgFactory} that is used to create the FFT's
	 */
	public ImgFactory< ComplexFloatType > fftImgFactory()
	{
		return fftImgFactory;
	}

	/**
	 * @return - the {@link ImgFactory} that is used to compute the convolved
	 *         image
	 */
	public ImgFactory< T > imgFactory()
	{
		return imgFactory;
	}

	public boolean replaceInput( final RandomAccessibleInterval< T > img )
	{
		this.image = img;
		// the fft has to be recomputed
		this.imgFFT = null;
		return true;
	}

	/**
	 * By default, he will not do the computation in-place and keep the imgFFT
	 * 
	 * @param keepImgFFT
	 */
	public void setKeepImgFFT( final boolean keepImgFFT )
	{
		this.keepImgFFT = keepImgFFT;
	}

	public boolean getKeepImgFFT()
	{
		return this.keepImgFFT;
	}

	public boolean replaceKernel( final RandomAccessibleInterval< S > knl )
	{
		this.kernel = knl;
		// the fft has to be recomputed
		this.kernelFFT = null;
		return true;
	}

	final public static Img< FloatType > createGaussianKernel( final ImgFactory< FloatType > factory, final double sigma, final int numDimensions )
	{
		final double[] sigmas = new double[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			sigmas[ d ] = sigma;

		return createGaussianKernel( factory, sigmas );
	}

	final public static Img< FloatType > createGaussianKernel( final ImgFactory< FloatType > factory, final double[] sigmas )
	{
		final int numDimensions = sigmas.length;

		final int[] imageSize = new int[ numDimensions ];
		final double[][] kernel = new double[ numDimensions ][];

		for ( int d = 0; d < numDimensions; ++d )
		{
			kernel[ d ] = Util.createGaussianKernel1DDouble( sigmas[ d ], true );
			imageSize[ d ] = kernel[ d ].length;
		}

		final Img< FloatType > kernelImg = factory.create( imageSize, new FloatType() );

		final Cursor< FloatType > cursor = kernelImg.localizingCursor();
		final int[] position = new int[ numDimensions ];

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( position );

			double value = 1;

			for ( int d = 0; d < numDimensions; ++d )
				value *= kernel[ d ][ position[ d ] ];

			cursor.get().set( ( float ) value );
		}

		return kernelImg;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();

		//
		// compute fft of the input image
		//
		if ( imgFFT == null ) // not computed in a previous step
		{
			fftImage = new FourierTransform< T, ComplexFloatType >( image, fftImgFactory, new ComplexFloatType() );
			fftImage.setNumThreads( this.getNumThreads() );

			// how to extend the input image out of its boundaries for computing
			// the FFT,
			// we simply mirror the content at the borders
			fftImage.setPreProcessing( PreProcessing.EXTEND_MIRROR );
			// we do not rearrange the fft quadrants
			fftImage.setRearrangement( Rearrangement.UNCHANGED );

			// the image has to be extended by the size of the kernel-1
			// as the kernel is always odd, e.g. if kernel size is 3, we need to
			// add
			// one pixel out of bounds in each dimension (3-1=2 pixel all
			// together) so that the
			// convolution works
			final int[] imageExtension = kernelDim.clone();
			for ( int d = 0; d < numDimensions; ++d )
				--imageExtension[ d ];
			fftImage.setImageExtension( imageExtension );

			if ( !fftImage.checkInput() || !fftImage.process() )
			{
				errorMessage = "FFT of image failed: " + fftImage.getErrorMessage();
				return false;
			}

			imgFFT = fftImage.getResult();
		}

		//
		// create the kernel for fourier transform
		//
		if ( kernelFFT == null )
		{
			// get the size of the kernel image that will be fourier
			// transformed,
			// it has the same size as the image
			final int kernelTemplateDim[] = new int[ numDimensions ];
			for ( int d = 0; d < numDimensions; ++d )
				kernelTemplateDim[ d ] = ( int ) imgFFT.dimension( d );

			kernelTemplateDim[ 0 ] = ( ( int ) imgFFT.dimension( 0 ) - 1 ) * 2;

			// instaniate real valued kernel template
			// which is of the same container type as the image
			// so that the computation is easy

			// HACK: Explicit assignment is needed for OpenJDK javac.
			S kernelType = Util.getTypeFromInterval( kernel );
			final Img< S > kernelTemplate = kernelImgFactory.create( kernelTemplateDim, kernelType.createVariable() );

			// copy the kernel into the kernelTemplate,
			// the key here is that the center pixel of the kernel (e.g.
			// 13,13,13)
			// is located at (0,0,0)
			final RandomAccess< S > kernelCursor = kernel.randomAccess();
			final RandomAccess< S > kernelTemplateCursor = kernelTemplate.randomAccess();

			final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( kernel );

			final int[] position = new int[ numDimensions ];
			final int[] position2 = new int[ numDimensions ];

			while ( cursorDim.hasNext() )
			{
				cursorDim.fwd();
				cursorDim.localize( position );

				for ( int d = 0; d < numDimensions; ++d )
				{
					// the kernel might not be zero-bounded
					position2[ d ] = position[ d ] + ( int ) kernel.min( d );

					position[ d ] = ( position[ d ] - kernelDim[ d ] / 2 + kernelTemplateDim[ d ] ) % kernelTemplateDim[ d ];
					/*
					 * final int tmp = ( position[ d ] - kernelDim[ d ]/2 );
					 * 
					 * if ( tmp < 0 ) position[ d ] = kernelTemplateDim[ d ] +
					 * tmp; else position[ d ] = tmp;
					 */
				}

				kernelCursor.setPosition( position2 );
				kernelTemplateCursor.setPosition( position );
				kernelTemplateCursor.get().set( kernelCursor.get() );
			}

			//
			// compute FFT of kernel
			//
			final FourierTransform< S, ComplexFloatType > fftKernel = new FourierTransform< S, ComplexFloatType >( kernelTemplate, fftImgFactory, new ComplexFloatType() );
			fftKernel.setNumThreads( this.getNumThreads() );

			fftKernel.setPreProcessing( PreProcessing.NONE );
			fftKernel.setRearrangement( fftImage.getRearrangement() );

			if ( !fftKernel.checkInput() || !fftKernel.process() )
			{
				errorMessage = "FFT of kernel failed: " + fftKernel.getErrorMessage();
				return false;
			}
			kernelFFT = fftKernel.getResult();
		}

		//
		// Multiply in Fourier Space
		//
		final Img< ComplexFloatType > copy;

		if ( keepImgFFT )
			copy = imgFFT.copy();
		else
			copy = imgFFT;

		multiply( copy, kernelFFT );

		//
		// Compute inverse Fourier Transform
		//
		final InverseFourierTransform< T, ComplexFloatType > invFFT = new InverseFourierTransform< T, ComplexFloatType >( copy, imgFactory, fftImage );
		invFFT.setNumThreads( this.getNumThreads() );

		if ( !invFFT.checkInput() || !invFFT.process() )
		{
			errorMessage = "InverseFFT of image failed: " + invFFT.getErrorMessage();
			return false;
		}

		if ( !keepImgFFT )
		{
			// the imgFFT was changed during the multiplication
			// it cannot be re-used
			imgFFT = null;
		}

		convolved = invFFT.getResult();

		processingTime = System.currentTimeMillis() - startTime;
		return true;
	}

	/**
	 * Multiply in Fourier Space
	 * 
	 * @param a
	 * @param b
	 */
	protected void multiply( final RandomAccessibleInterval< ComplexFloatType > a, final RandomAccessibleInterval< ComplexFloatType > b )
	{
		final Cursor< ComplexFloatType > cursorA = Views.iterable( a ).cursor();
		final Cursor< ComplexFloatType > cursorB = Views.iterable( b ).cursor();

		while ( cursorA.hasNext() )
		{
			cursorA.fwd();
			cursorB.fwd();

			cursorA.get().mul( cursorB.get() );
		}
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	@Override
	public Img< T > getResult()
	{
		return convolved;
	}

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 ) { return false; }

		if ( image == null )
		{
			errorMessage = "Input image is null";
			return false;
		}

		if ( kernel == null )
		{
			errorMessage = "Kernel image is null";
			return false;
		}

		for ( int d = 0; d < numDimensions; ++d )
			if ( kernel.dimension( d ) % 2 != 1 )
			{
				errorMessage = "Kernel image has NO odd dimensionality in dim " + d + " (" + kernel.dimension( d ) + ")";
				return false;
			}

		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

}
