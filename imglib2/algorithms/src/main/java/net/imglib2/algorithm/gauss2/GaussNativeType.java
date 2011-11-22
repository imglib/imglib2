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
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;
import net.imglib2.converter.Converter;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

public abstract class GaussNativeType< T extends NumericType< T > & NativeType< T > > extends Gauss< T >
{
	public GaussNativeType( final double[] sigma, final RandomAccessible<T> input, final Interval inputInterval, 
			  final RandomAccessible<T> output, final Localizable outputOffset, 
			  final ImgFactory<T> factory )
	{
		super( sigma, input, inputInterval, output, outputOffset, factory );
	}

	/**
	 * Compute the current line. It is up to the implementation howto really do that. The idea is to only iterate
	 * over the input once (that's why it is an {@link Iterator}) as it is potentially an expensive operation 
	 * (e.g. a {@link Converter} might be involved or we are computing on a rendered input) 
	 *  
	 * @param input - the {@link Iterator}/{@link Sampler} over the current input line.
	 */
	protected void processLine( final SamplingLineIterator< T > input, final double[] kernel )
	{
		final int kernelSize = kernel.length;
		final int kernelSizeMinus1 = kernelSize - 1;
		final int kernelSizeHalf = kernelSize / 2;
		final int kernelSizeHalfMinus1 = kernelSizeHalf - 1;
		
		final ArrayRandomAccess< T > randomAccessLeft = (ArrayRandomAccess< T >)input.randomAccessLeft;
		final ArrayRandomAccess< T > randomAccessRight = (ArrayRandomAccess< T >)input.randomAccessRight;
		final T copy = input.copy; 
		final T tmp = input.tmp;

		final long imgSize = input.getProcessLine().size();

		if ( imgSize >= kernelSize )
		{
			// convolve the first pixels where the input influences less than kernel.size pixels
			for ( int i = 0; i < kernelSizeMinus1; ++i )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				randomAccessLeft.setPositionDim0( -1 );				
				
				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
				{
					randomAccessLeft.fwdDim0();
					
					tmp.set( copy );
					tmp.mul( kernel[ i - o ] );
					
					randomAccessLeft.get().add( tmp );
				}
			}
			
			// convolve all values where the input value contributes to the full kernel
			final long length = imgSize - kernelSizeMinus1;
			for ( long n = 0; n < length; ++n )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );

				// set the left and the right random access to the right coordinates
				// the left random access is always responsible for the center
				randomAccessLeft.setPositionDim0( n );
				randomAccessRight.setPositionDim0( n + kernelSizeMinus1 );
				
				// move till the last pixel before the center of the kernel
				for ( int k = 0; k < kernelSizeHalfMinus1; ++k )
				{
					tmp.set( copy );
					tmp.mul( kernel[ k ] );
					
					randomAccessLeft.get().add( tmp );
					randomAccessRight.get().add( tmp );
					
					randomAccessLeft.fwdDim0();
					randomAccessRight.bckDim0();
				}

				// do the last pixel (same as a above, but right cursor doesn't move)
				tmp.set( copy );
				tmp.mul( kernel[ kernelSizeHalfMinus1 ] );
				
				randomAccessLeft.get().add( tmp );
				randomAccessRight.get().add( tmp );
				
				randomAccessLeft.fwdDim0();

				// do the center pixel
				tmp.set( copy );
				tmp.mul( kernel[ kernelSizeHalf ] );

				randomAccessLeft.get().add( tmp );
			}
			
				
			// convolve the last pixels where the input influences less than kernel.size pixels
			final long endLength = imgSize + kernelSizeMinus1;
			for ( long i = imgSize; i < endLength; ++i )
			{
				// after the fwd() call the random access is at position imgSize as pictured above
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				randomAccessLeft.setPositionDim0( i - kernelSize );				
				
				// now add it to all output values it contributes to
				int k = 0;
				for ( long o = i - kernelSize + 1; o < imgSize; ++o )
				{
					randomAccessLeft.fwdDim0();
					
					tmp.set( copy );
					tmp.mul( kernel[ k++ ] );
					
					randomAccessLeft.get().add( tmp );
				}
			}			
		}
		else
		{
			// convolve the first pixels where the input influences less than kernel.size pixels
			for ( int i = 0; i < imgSize; ++i )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				randomAccessLeft.setPosition( -1, 0 );				
				
				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
				{
					randomAccessLeft.fwd( 0 );
					
					tmp.set( copy );
					tmp.mul( kernel[ i - o ] );
					
					randomAccessLeft.get().add( tmp );
				}				
			}
			
			// convolve the last pixels where the input influences less than kernel.size pixels
			for ( long i = imgSize; i < imgSize + kernelSizeMinus1; ++i )
			{
				// after the fwd() call the random access is at position imgSize as pictured above
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				final long position = i - kernelSize; 
				randomAccessLeft.setPosition( Math.max( -1, position ), 0 );				
				
				// now add it to all output values it contributes to
				int k = Math.max( 0, (int)position + 1 );
				for ( long o = Math.max( 0, i - kernelSize + 1); o < imgSize; ++o )
				{
					randomAccessLeft.fwd( 0 );
					
					tmp.set( copy );
					tmp.mul( kernel[ k++ ] );
					
					randomAccessLeft.get().add( tmp );
				}
			}						
		}
	}	
}
