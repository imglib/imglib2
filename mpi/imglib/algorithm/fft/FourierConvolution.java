package mpi.imglib.algorithm.fft;

import mpi.imglib.algorithm.Benchmark;
import mpi.imglib.algorithm.MultiThreaded;
import mpi.imglib.algorithm.OutputAlgorithm;
import mpi.imglib.algorithm.fft.FourierTransform.PreProcessing;
import mpi.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpi.imglib.algorithm.gauss.GaussianConvolution;
import mpi.imglib.algorithm.math.MathLib;
import mpi.imglib.cursor.Cursor;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.ImageFactory;
import mpi.imglib.outside.OutsideStrategyValueFactory;
import mpi.imglib.type.NumericType;
import mpi.imglib.type.numeric.ComplexFloatType;

public class FourierConvolution<T extends NumericType<T>, S extends NumericType<S>> implements MultiThreaded, OutputAlgorithm<T>, Benchmark
{
	final int numDimensions;
	Image<T> image, convolved;
	Image<S> kernel;
	Image<ComplexFloatType> kernelFFT; 
	
	final int[] kernelDim;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public FourierConvolution( final Image<T> image, final Image<S> kernel )
	{
		this.numDimensions = image.getNumDimensions();
				
		this.image = image;
		this.kernel = kernel;
		this.kernelDim = kernel.getDimensions();
		this.kernelFFT = null;
		
		setNumThreads();
	}
	
	public boolean replaceImage( final Image<T> image )
	{
		if ( !image.getContainer().compareStorageContainerCompatibility( this.image.getContainer() ))
		{
			errorMessage = "Containers are not comparable, cannot exchange image";
			return false;
		}
		else
		{
			this.image = image;
			return true;
		}
	}
	
	final public static <T extends NumericType<T>> Image<T> getGaussianKernel( final ImageFactory<T> imgFactory, final double sigma, final int numDimensions )
	{
		final double[ ] sigmas = new double[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			sigmas[ d ] = sigma;
		
		return getGaussianKernel( imgFactory, sigmas );
	}
	
	final public static <T extends NumericType<T>> Image<T> getGaussianKernel( final ImageFactory<T> imgFactory, final double[] sigma )
	{
		final int numDimensions = sigma.length;
		final int imgSize[] = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			imgSize[ d ] = MathLib.getSuggestedKernelDiameter( sigma[ d ] );
		
		final Image<T> kernel = imgFactory.createImage( imgSize );			
		final int[] center = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			center[ d ] = kernel.getDimension( d ) / 2;
				
		final LocalizableByDimCursor<T> c = kernel.createLocalizableByDimCursor();
		c.setPosition( center );
		c.getType().setOne();
		c.close();
		
		final GaussianConvolution<T> gauss = new GaussianConvolution<T>( kernel, new OutsideStrategyValueFactory<T>(), sigma );
		
		if ( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( "Gaussian Convolution failed: " + gauss.getErrorMessage() );
			return null;
		}
		
		kernel.close();
		
		return gauss.getResult();		
	}
	
	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();

		//
		// compute fft of the input image
		//
		final FourierTransform<T> fftImage = new FourierTransform<T>( image );
		fftImage.setNumThreads( this.getNumThreads() );
		
		// we simply mirror
		fftImage.setPreProcessing( PreProcessing.ExtendMirror );		
		// we do not rearrange
		fftImage.setRearrangement( Rearrangement.Unchanged );
		
		// the image has to be extended by the size of the kernel-1
		// as the kernel is always odd, e.g. if kernel size is 3, we need to add
		// one pixel outside in each dimension (3-1=2 pixel all together) so that the
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
		
		final Image<ComplexFloatType> imgFFT = fftImage.getResult();
		
		//
		// create the kernel for fourier transform
		//
		
		// get the size of the kernel image that will be fourier transformed
		final int kernelTemplateDim[] = imgFFT.getDimensions();
		kernelTemplateDim[ 0 ] = ( imgFFT.getDimension( 0 ) - 1 ) * 2;
		
		// instaniate real valued kernel template
		// which is of the same container type as the image
		// so that the computation is easy
		final ImageFactory<S> kernelTemplateFactory = new ImageFactory<S>( kernel.createType(), image.getContainer().getFactory() );
		final Image<S> kernelTemplate = kernelTemplateFactory.createImage( kernelTemplateDim );
		//final Image<S> kernelTemplate = image.createNewImage( kernelTemplateDim );					
		
		// copy the kernel into the kernelTemplate
		final LocalizableCursor<S> kernelCursor = kernel.createLocalizableCursor();
		final LocalizableByDimCursor<S> kernelTemplateCursor = kernelTemplate.createLocalizableByDimCursor();
		
		final int[] position = new int[ numDimensions ];
		while ( kernelCursor.hasNext() )
		{
			kernelCursor.next();
			kernelCursor.getPosition( position );
			
			for ( int d = 0; d < numDimensions; ++d )
			{
				position[ d ] = ( position[ d ] - kernelDim[ d ]/2 + kernelTemplateDim[ d ] ) % kernelTemplateDim[ d ];
				/*final int tmp = ( position[ d ] - kernelDim[ d ]/2 );
				
				if ( tmp < 0 )
					position[ d ] = kernelTemplateDim[ d ] + tmp;
				else
					position[ d ] = tmp;*/
			}			
			
			kernelTemplateCursor.setPosition( position );
			kernelTemplateCursor.getType().set( kernelCursor.getType() );
		}
		
		// 
		// compute FFT of kernel
		//
		final FourierTransform<S> fftKernel = new FourierTransform<S>( kernelTemplate );
		fftKernel.setNumThreads( this.getNumThreads() );
		
		fftKernel.setPreProcessing( PreProcessing.None );		
		fftKernel.setRearrangement( fftImage.getRearrangement() );
		
		if ( !fftKernel.checkInput() || !fftKernel.process() )
		{
			errorMessage = "FFT of kernel failed: " + fftKernel.getErrorMessage();
			return false;			
		}		
		kernelTemplate.close();		
		kernelFFT = fftKernel.getResult();
		
		//
		// Multiply in Fourier Space
		//
		final Cursor<ComplexFloatType> cursorImgFFT = imgFFT.createCursor();
		final Cursor<ComplexFloatType> cursorKernelFFT = kernelFFT.createCursor();
		
		while ( cursorImgFFT.hasNext() )
		{
			cursorImgFFT.fwd();
			cursorKernelFFT.fwd();
			
			cursorImgFFT.getType().mul( cursorKernelFFT.getType() );
		}
		
		cursorImgFFT.close();
		cursorKernelFFT.close();
		
		//
		// Compute inverse Fourier Transform
		//		
		final InverseFourierTransform<T> invFFT = new InverseFourierTransform<T>( imgFFT, fftImage );
		invFFT.setInPlaceTransform( true );
		invFFT.setNumThreads( this.getNumThreads() );

		if ( !invFFT.checkInput() || !invFFT.process() )
		{
			errorMessage = "InverseFFT of image failed: " + invFFT.getErrorMessage();
			return false;			
		}
		
		imgFFT.close();
		
		convolved = invFFT.getResult();	
		
		processingTime = System.currentTimeMillis() - startTime;
        return true;
	}
	
	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	

	@Override
	public Image<T> getResult() { return convolved; }

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		
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
			if ( kernel.getDimension( d ) % 2 != 1)
			{
				errorMessage = "Kernel image has NO odd dimensionality in dim " + d + " (" + kernel.getDimension( d ) + ")";
				return false;
			}
		
		return true;
	}

	@Override
	public String getErrorMessage()  { return errorMessage; }

}
