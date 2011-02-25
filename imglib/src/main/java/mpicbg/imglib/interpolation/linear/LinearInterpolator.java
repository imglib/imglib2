/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgRandomAccess;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.location.transform.Floor;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.util.Util;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class LinearInterpolator< T extends NumericType< T > > extends Floor< ImgRandomAccess< T > > implements Interpolator< T >
{
	final protected OutOfBoundsFactory< T > outOfBoundsStrategyFactory;
	final protected Img< T > image;
	final protected int numDimensions;
	
	final protected T tmp1, tmp2;
	
	// the weights and inverse weights in each dimension
	final float[][] weights;
	
	// to save the temporary values in each dimension when computing the final value
	// the value in [ 0 ][ 0 ] will be the interpolated value
	final T[][] tree;
	
	// the half size of the second array in each tree step - speedup
	final int[] halfTreeLevelSizes;
		
	// the locations where to initially grab pixels from
	final boolean[][] positions;
	
	final static private < T extends Type< T > > ImgRandomAccess< T > createSampler( final Img< T > image, final RasterOutOfBoundsFactory<T> outOfBoundsStrategyFactory )
	{
		return image.createPositionableRasterSampler( outOfBoundsStrategyFactory );
	}
	
	protected LinearInterpolator( final Img<T> image, final RasterOutOfBoundsFactory<T> outOfBoundsStrategyFactory )
	{
		this( image, outOfBoundsStrategyFactory, true );
	}
	
	protected LinearInterpolator( final Img<T> image, final RasterOutOfBoundsFactory<T> outOfBoundsStrategyFactory, boolean initGenericStructures )
	{
		super( createSampler( image, outOfBoundsStrategyFactory ) );
		
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.image = image;
		
		numDimensions = image.numDimensions();
		
		// Principle of interpolation used
		//
		// example: 3d
		//
		// STEP 1 - Interpolate in dimension 0 (x)
		//
		//   ^
		// y |
		//   |        _
		//   |        /|     [6]     [7]
		//   |     z /        *<----->*       
  		//   |      /        /       /|
		//   |     /    [2] /   [3] / |    
		//   |    /        *<----->*  * [5]
		//   |   /         |       | /
		//   |  /          |       |/
		//   | /           *<----->*
		//   |/           [0]     [1]
		//   *--------------------------> 
		//                             x
		//
		// STEP 2 - Interpolate in dimension 1 (y)
		//
		//   [2][3]   [6][7]
		//      *-------*
		//      |       |
		//      |       |       
		//      |       |
		//      *-------*
		//   [0][1]   [4][5]
		//
		//     [2]     [3]
		//      *<----->*
		//      |       |
		//      |       |       
		//      |       |
		//      *<----->*
		//     [0]     [1]
		//
		// STEP 3 - Interpolate in dimension 1 (z)
		//
		//   [2][3]  
		//      *    
		//      |     
		//      |      
		//      |    
		//      *    
		//   [0][1]  
		//
		//     [0]     [1]
		//      *<----->*
		//
		// yiels the interpolated value in 3 dimensions
		
		tmp1 = image.createType();
		tmp2 = image.createType();

		weights = new float[ numDimensions ][ 2 ];

		if ( initGenericStructures )
		{		
			// create the temporary datastructure for computing the actual interpolation
			//
			// example: 3d-image
			//
			// 3d: get values from image and interpolate in dimension 0
			//     see above and below which coordinates are [0]...[7]
			//
			//              [0] [1] [2] [3] [4] [5] [6] [7]
			// interp in 3d  |   |   |   |   |   |   |   |
			// store in 2d:  \   /   \   /   \   /   \   /
			//                [0]     [1]     [2]     [3] 
			// interp in 2d    \       /       \       /
			// and store in     \     /         \     /
			// 1d                \   /           \   /
			//                    [0]             [1]
			// interpolate in 1d   \               /       
			// and store            \             / 
			// the final             \           /
			// result                 \         /
			//                         \       / 
			//                          \     / 
			//                           \   /
			//  final interpolated value  [0]
	
			tree = tmp1.createArray2D( numDimensions + 1, 1 );
			halfTreeLevelSizes = new int[ numDimensions + 1 ];
			
			for ( int d = 0; d < tree.length; d++ )
			{
				tree[ d ] = tmp1.createArray1D( Util.pow( 2, d ));
				
				for ( int i = 0; i < tree[ d ].length; i++ )
					tree[ d ][ i ] = image.createType();
	
				halfTreeLevelSizes[ d ] = tree[ d ].length / 2;
			}
						
			// recursively get the coordinates we need for interpolation
			// ( relative location to the offset in each dimension )
			//
			// example for 3d:
			//
			//  x y z index
			//  0 0 0 [0]
			//  1 0 0 [1] 
			//  0 1 0 [2]
			//  1 1 0 [3] 
			// 	0 0 1 [4] 
			// 	1 0 1 [5] 
			// 	0 1 1 [6] 
			// 	1 1 1 [7] 
			
			positions = new boolean[ Util.pow( 2, numDimensions ) ][ numDimensions ];
			Util.setCoordinateRecursive( numDimensions() - 1, numDimensions, new int[ numDimensions ], positions );
		}
		else
		{
			tree = null;
			positions = null;
			halfTreeLevelSizes = null;
		}
	}
	

	/* Dimensionality */
	
	@Override
	final public int numDimensions()
	{
		return numDimensions;
	}

	/**
	 * Returns the {@link RasterOutOfBoundsFactory} used for interpolation
	 * 
	 * @return - the {@link RasterOutOfBoundsFactory}
	 */
	@Override
	public RasterOutOfBoundsFactory< T > getOutOfBoundsStrategyFactory()
	{
		return outOfBoundsStrategyFactory;
	}

	/**
	 * Returns the typed image the interpolator is working on
	 * 
	 * @return - the image
	 */
	@Override
	public Img< T > getImage()
	{
		return image;
	}
	
	
	@Override
	public void close() { target.close(); }

	@Override
	public T get()
	{
		/* calculate weights [0...1] and their inverse (1-weight) [1...0] in each dimension */
		for (int d = 0; d < numDimensions; d++)
		{
			final float w = position[ d ] - target.getIntPosition( d );
			weights[ d ][ 1 ] = w;
			weights[ d ][ 0 ] = 1 - w;
		}
		
		/* the values from the image */
		
		for ( int i = 0; i < positions.length; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				if ( positions[ i ][ d ] )
					target.fwd( d );

			tree[ numDimensions ][ i ].set( target.get() );
			
			// move back to the offset position
			for ( int d = 0; d < numDimensions; ++d )
				if ( positions[ i ][ d ] )
					target.bck(d);
		}
		
		/* interpolate down the tree as shown above */
		for ( int d = numDimensions; d > 0; --d )
		{
			for ( int i = 0; i < halfTreeLevelSizes[ d ]; i++ )
			{
				tmp1.set( tree[ d ][ i*2 ] );
				tmp2.set( tree[ d ][ i*2+1 ] );
				
				tmp1.mul( weights[ numDimensions - d ][ 0 ] );
				tmp2.mul( weights[ numDimensions - d ][ 1 ] );
				
				tmp1.add( tmp2 );
				
				tree[ d - 1 ][ i ].set( tmp1 );
			}
		}
		
		return tree[ 0 ][ 0 ];
	}
	
	@Override
	@Deprecated
	final public T getType()
	{
		return get();
	}
}
