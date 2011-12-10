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
package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Location;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

final public class GaussFloat extends GaussNativeType< FloatType >
{
	public GaussFloat( final double[] sigma, final Img<FloatType> input )
	{
		this( sigma, Views.extend( input, new OutOfBoundsMirrorFactory< FloatType, Img<FloatType> >( Boundary.SINGLE ) ), input, input.factory() );
	}

	public GaussFloat( final double[] sigma, final Img<FloatType> input, final OutOfBoundsFactory< FloatType, Img<FloatType> > outOfBounds )
	{
		this( sigma, Views.extend( input, outOfBounds ), input, input.factory() );
	}
	
	/**
	 * Computes a Gaussian convolution on a {@link RandomAccessible} of {@link FloatType} in a certain {@link Interval}
	 * and returns an {@link Img} defined by the {@link ImgFactory} containing the result
	 * 
	 * @param sigma - the sigma for the convolution
	 * @param input - the {@link RandomAccessible} to work on
	 * @param interval - the area that is convolved
	 * @param factory - the {@link ImgFactory} that defines the temporary and output images to be used
	 */
	public GaussFloat( final double[] sigma, final RandomAccessible<FloatType> input, final Interval interval, final ImgFactory<FloatType> factory )
	{
		super( sigma, input, interval, factory.create( interval, new FloatType() ),  new Location( sigma.length ), factory );
	}
	
	@Override
	public Img<FloatType> getResult()
	{
		return (Img<FloatType>)output;
	}
	
	@Override
	protected FloatType getProcessingType() { return new FloatType(); }

	@Override
	protected Img<FloatType> getProcessingLine( final long sizeProcessLine )
	{
		final Img<FloatType> processLine;
		
		// try to use array if each individual line is not too long
		if ( sizeProcessLine <= Integer.MAX_VALUE )
			processLine = new ArrayImgFactory< FloatType >().create( new long[]{ sizeProcessLine }, new FloatType() );
		else
			processLine = new CellImgFactory< FloatType >( Integer.MAX_VALUE / 16 ).create( new long[]{ sizeProcessLine }, new FloatType() );
		
		return processLine;
	}	
	
	/**
	 * Compute the current line. It is up to the implementation howto really do that. The idea is to only iterate
	 * over the input once (that's why it is an {@link Iterator}) as it is potentially an expensive operation 
	 * (e.g. a {@link Converter} might be involved or we are computing on a rendered input) 
	 *  
	 * @param input - the {@link Iterator}/{@link Sampler} over the current input line.
	 */
	protected void processLine( final SamplingLineIterator< FloatType > input, final double[] kernel )
	{
		final int kernelSize = kernel.length;
		final int kernelSizeMinus1 = kernelSize - 1;
		final int kernelSizeHalf = kernelSize / 2;
		final int kernelSizeHalfMinus1 = kernelSizeHalf - 1;
		
		final float[] v = ((FloatArray)((NativeImg<?, ?>)input.getProcessLine()).update( null )).getCurrentStorageArray();
		
		final int imgSize = v.length;
		
		int indexLeft = 0;
		int indexRight = 0;
		
		if ( imgSize >= kernelSize )
		{
			// convolve the first pixels where the input influences less than kernel.size pixels
			for ( int i = 0; i < kernelSizeMinus1; ++i )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				final float copy = input.get().get();
				
				// set the random access in the processing line to the right position
				indexLeft = -1;				
				
				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
					v[ ++indexLeft ] += (float)(copy * kernel[ i - o ]);
			}
			
			// convolve all values where the input value contributes to the full kernel
			final int length = imgSize - kernelSizeMinus1;
			for ( int n = 0; n < length; ++n )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				final float copy = input.get().get();

				// set the left and the right random access to the right coordinates
				// the left random access is always responsible for the center
				indexLeft = n;
				indexRight = n + kernelSizeMinus1;
				
				// move till the last pixel before the center of the kernel
				for ( int k = 0; k < kernelSizeHalfMinus1; ++k )
				{
					final float tmp = (float)(copy * kernel[ k ]);

					v[ indexLeft++ ] += tmp;
					v[ indexRight-- ] += tmp;
				}

				// do the last pixel (same as a above, but right cursor doesn't move)
				final float tmp = (float)(copy * kernel[ kernelSizeHalfMinus1 ]);

				v[ indexLeft++ ] += tmp;
				v[ indexRight ] += tmp;

				// do the center pixel
				v[ indexLeft ] += (float)(copy * kernel[ kernelSizeHalf ]);
			}
			
				
			// convolve the last pixels where the input influences less than kernel.size pixels
			final int endLength = imgSize + kernelSizeMinus1;
			for ( int i = imgSize; i < endLength; ++i )
			{
				// after the fwd() call the random access is at position imgSize as pictured above
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				final float copy = input.get().get();
				
				// set the random access in the processing line to the right position
				indexLeft = i - kernelSize;				
				
				// now add it to all output values it contributes to
				int k = 0;
				for ( long o = i - kernelSize + 1; o < imgSize; ++o )
					v[ ++indexLeft ] += (float)(copy * kernel[ k++ ]);
			}			
		}
		else
		{
			//System.out.println( "kernel: " + kernel.length + " img: " + imgSize );
			
			// convolve the first pixels where the input influences less than kernel.size pixels
			for ( int i = 0; i < imgSize; ++i )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				final float copy = input.get().get();
				
				// set the random access in the processing line to the right position
				indexLeft = -1;				
				
				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
					v[ ++indexLeft ] += (float)(copy * kernel[ i - o ]);
			}
			
			// convolve the last pixels where the input influences less than kernel.size pixels
			for ( int i = imgSize; i < imgSize + kernelSizeMinus1; ++i )
			{
				// after the fwd() call the random access is at position imgSize as pictured above
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				final float copy = input.get().get();
				
				// set the random access in the processing line to the right position
				final int position = i - kernelSize; 
				indexLeft = Math.max( -1, position );
				
				// now add it to all output values it contributes to
				int k = Math.max( 0, (int)position + 1 );
				for ( int o = Math.max( 0, i - kernelSize + 1); o < imgSize; ++o )
					v[ ++indexLeft ] += (float)(copy * kernel[ k++ ]);
			}						
		}
	}	
}
