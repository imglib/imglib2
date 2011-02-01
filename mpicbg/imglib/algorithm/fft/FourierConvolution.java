/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * An execption is the 1D FFT implementation of Dave Hale which we use as a
 * library, wich is released under the terms of the Common Public License -
 * v1.0, which is available at http://www.eclipse.org/legal/cpl-v10.html  
 *
 * @author Stephan Preibisch
 */
package mpicbg.imglib.algorithm.fft;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.fft.FourierTransform.PreProcessing;
import mpicbg.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpicbg.imglib.algorithm.gauss.GaussianConvolution;
import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.ContainerIterator;
import mpicbg.imglib.container.PositionableContainerSampler;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValueFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexFloatType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class FourierConvolution<T extends RealType<T>, S extends RealType<S>> implements MultiThreaded, OutputAlgorithm<T>, Benchmark
{
	final int numDimensions;
	Image<T> image, convolved;
	Image<S> kernel;
	Image<ComplexFloatType> kernelFFT, imgFFT; 
	FourierTransform<T, ComplexFloatType> fftImage;
	
	final int[] kernelDim;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public FourierConvolution( final Image<T> image, final Image<S> kernel )
	{
		this.numDimensions = image.numDimensions();
				
		this.image = image;
		this.kernel = kernel;
		this.kernelDim = kernel.getDimensions();
		this.kernelFFT = null;
		this.imgFFT = null;
		
		setNumThreads();
	}
	
	public boolean replaceImage( final Image<T> image )
	{
		if ( !image.getContainer().compareStorageContainerCompatibility( this.image.getContainer() ))
		{
			errorMessage = "Image containers are not comparable, cannot exchange image";
			return false;
		}
		else
		{
			this.image = image;
			// the fft has to be recomputed
			this.imgFFT = null;
			return true;
		}
	}

	public boolean replaceKernel( final Image<S> kernel )
	{
		if ( !kernel.getContainer().compareStorageContainerCompatibility( this.kernel.getContainer() ))
		{
			errorMessage = "Kernel containers are not comparable, cannot exchange image";
			return false;
		}
		else
		{
			this.kernel = kernel;
			// the fft has to be recomputed
			this.kernelFFT = null;
			return true;
		}
	}

	
	final public static Image<FloatType> createGaussianKernel( final ContainerFactory factory, final double sigma, final int numDimensions )
	{
		final double[ ] sigmas = new double[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			sigmas[ d ] = sigma;
		
		return createGaussianKernel( factory, sigmas );
	}

	final public static Image<FloatType> createGaussianKernel( final ContainerFactory factory, final double[] sigmas )
	{
		final int numDimensions = sigmas.length;
		
		final int[] imageSize = new int[ numDimensions ];
		final double[][] kernel = new double[ numDimensions ][];
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			kernel[ d ] = MathLib.createGaussianKernel1DDouble( sigmas[ d ], true );
			imageSize[ d ] = kernel[ d ].length;
		}
		
		final Image<FloatType> kernelImg = new ImageFactory<FloatType>( new FloatType(), factory ).createImage( imageSize );
		
		final ContainerIterator<FloatType> cursor = kernelImg.createLocalizingRasterIterator();
		final int[] position = new int[ numDimensions ];
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( position );
			
			double value = 1;
			
			for ( int d = 0; d < numDimensions; ++d )
				value *= kernel[ d ][ position[ d ] ];
			
			cursor.get().set( (float)value );
		}
		
		cursor.close();
		
		return kernelImg;
	}
	
	final public static <T extends RealType<T>> Image<T> getGaussianKernel( final ImageFactory<T> imgFactory, final double sigma, final int numDimensions )
	{
		final double[ ] sigmas = new double[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			sigmas[ d ] = sigma;
		
		return getGaussianKernel( imgFactory, sigmas );
	}
	
	final public static <T extends RealType<T>> Image<T> getGaussianKernel( final ImageFactory<T> imgFactory, final double[] sigma )
	{
		final int numDimensions = sigma.length;
		final int imgSize[] = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			imgSize[ d ] = MathLib.getSuggestedKernelDiameter( sigma[ d ] );
		
		final Image<T> kernel = imgFactory.createImage( imgSize );			
		final int[] center = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			center[ d ] = kernel.getDimension( d ) / 2;
				
		final PositionableContainerSampler<T> c = kernel.createPositionableRasterSampler();
		c.setPosition( center );
		c.get().setOne();
		c.close();
		
		final GaussianConvolution<T> gauss = new GaussianConvolution<T>( kernel, new OutOfBoundsConstantValueFactory<T>(), sigma );
		
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
		if ( imgFFT == null ) //not computed in a previous step
		{
			fftImage = new FourierTransform<T, ComplexFloatType>( image, new ComplexFloatType() );
			fftImage.setNumThreads( this.getNumThreads() );
			
			// how to extend the input image out of its boundaries for computing the FFT,
			// we simply mirror the content at the borders
			fftImage.setPreProcessing( PreProcessing.EXTEND_MIRROR );		
			// we do not rearrange the fft quadrants
			fftImage.setRearrangement( Rearrangement.UNCHANGED );
			
			// the image has to be extended by the size of the kernel-1
			// as the kernel is always odd, e.g. if kernel size is 3, we need to add
			// one pixel out of bounds in each dimension (3-1=2 pixel all together) so that the
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
			// get the size of the kernel image that will be fourier transformed,
			// it has the same size as the image
			final int kernelTemplateDim[] = imgFFT.getDimensions();
			kernelTemplateDim[ 0 ] = ( imgFFT.getDimension( 0 ) - 1 ) * 2;
			
			// instaniate real valued kernel template
			// which is of the same container type as the image
			// so that the computation is easy
			final ImageFactory<S> kernelTemplateFactory = new ImageFactory<S>( kernel.createType(), image.getContainer().factory() );
			final Image<S> kernelTemplate = kernelTemplateFactory.createImage( kernelTemplateDim );
			
			// copy the kernel into the kernelTemplate,
			// the key here is that the center pixel of the kernel (e.g. 13,13,13)
			// is located at (0,0,0)
			final ContainerIterator<S> kernelCursor = kernel.createLocalizingRasterIterator();
			final PositionableContainerSampler<S> kernelTemplateCursor = kernelTemplate.createPositionableRasterSampler();
			
			final int[] position = new int[ numDimensions ];
			while ( kernelCursor.hasNext() )
			{
				kernelCursor.next();
				kernelCursor.localize( position );
				
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
				kernelTemplateCursor.get().set( kernelCursor.get() );
			}
			
			// 
			// compute FFT of kernel
			//
			final FourierTransform<S, ComplexFloatType> fftKernel = new FourierTransform<S, ComplexFloatType>( kernelTemplate, new ComplexFloatType() );
			fftKernel.setNumThreads( this.getNumThreads() );
			
			fftKernel.setPreProcessing( PreProcessing.NONE );		
			fftKernel.setRearrangement( fftImage.getRearrangement() );
			
			if ( !fftKernel.checkInput() || !fftKernel.process() )
			{
				errorMessage = "FFT of kernel failed: " + fftKernel.getErrorMessage();
				return false;			
			}		
			kernelTemplate.close();		
			kernelFFT = fftKernel.getResult();
		}
		
		//
		// Multiply in Fourier Space
		//
		final ContainerIterator<ComplexFloatType> cursorImgFFT = imgFFT.createRasterIterator();
		final ContainerIterator<ComplexFloatType> cursorKernelFFT = kernelFFT.createRasterIterator();
		
		while ( cursorImgFFT.hasNext() )
		{
			cursorImgFFT.fwd();
			cursorKernelFFT.fwd();
			
			cursorImgFFT.get().mul( cursorKernelFFT.get() );
		}
		
		cursorImgFFT.close();
		cursorKernelFFT.close();
		
		//
		// Compute inverse Fourier Transform
		//		
		final InverseFourierTransform<T, ComplexFloatType> invFFT = new InverseFourierTransform<T, ComplexFloatType>( imgFFT, fftImage );
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
	
	public void close()
	{
		kernelFFT.close(); 
		
		image = null;
		convolved = null;
		kernel = null;
		kernelFFT = null;
	}

	@Override
	public String getErrorMessage()  { return errorMessage; }

}
