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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.interpolation.InterpolatorImpl;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.NumericType;

public class LinearInterpolator<T extends NumericType<T>> extends InterpolatorImpl<T>
{
	final LocalizableByDimCursor<T> cursor;
	final T cursorType, tmp1, tmp2;
	
	// the offset in each dimension and a temporary array for computing the global coordinates
	final int[] baseDim, location;
	
	// the weights and inverse weights in each dimension
	final float[][] weights;
	
	// to save the temporary values in each dimension when computing the final value
	// the value in [ 0 ][ 0 ] will be the interpolated value
	final T[][] tree;
	
	// the half size of the second array in each tree step - speedup
	final int[] halfTreeLevelSizes;
		
	// the locations where to initially grab pixels from
	final boolean[][] positions;
	
	protected LinearInterpolator( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		this( img, interpolatorFactory, outsideStrategyFactory, true );
	}
	
	protected LinearInterpolator( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutsideStrategyFactory<T> outsideStrategyFactory, boolean initGenericStructures )
	{
		super(img, interpolatorFactory, outsideStrategyFactory);

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
		
		cursor = img.createLocalizableByDimCursor( outsideStrategyFactory );
		cursorType = cursor.getType();
		tmp1 = img.createType();
		tmp2 = img.createType();

		baseDim = new int[ numDimensions ];
		location = new int[ numDimensions ];
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
	
			tree = cursorType.createArray2D( numDimensions + 1, 1 );
			halfTreeLevelSizes = new int[ numDimensions + 1 ];
			
			for ( int d = 0; d < tree.length; d++ )
			{
				tree[ d ] = cursorType.createArray1D( MathLib.pow( 2, d ));
				
				for ( int i = 0; i < tree[ d ].length; i++ )
					tree[ d ][ i ] = img.createType();
	
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
			
			positions = new boolean[ MathLib.pow( 2, numDimensions ) ][ numDimensions ];
			MathLib.setCoordinateRecursive( numDimensions - 1, numDimensions, new int[ numDimensions ], positions );
						
			moveTo( position );
		}
		else
		{
			tree = null;
			positions = null;
			halfTreeLevelSizes = null;
		}
	}
	
	@Override
	public void close() { cursor.close(); }

	@Override
	public T getType() { return tree[ 0 ][ 0 ]; }

	@Override
	public void moveTo( final float[] position )
	{
        // compute the offset (Math.floor) in each dimension
		for (int d = 0; d < numDimensions; d++)
		{
			this.position[ d ] = position[ d ];
			
			baseDim[ d ] = position[ d ] > 0 ? (int)position[ d ]: (int)position[ d ]-1;			
			cursor.move( baseDim[ d ] - cursor.getPosition(d), d );
		}

        // compute the weights [0...1] in each dimension and the inverse (1-weight) [1...0]
		for (int d = 0; d < numDimensions; d++)
		{
			final float w = position[ d ] - baseDim[ d ];
			
			weights[ d ][ 1 ] = w;
			weights[ d ][ 0 ] = 1 - w;
		}
		
		//
		// compute the output value
		//
		
		// the the values from the image
		for ( int i = 0; i < positions.length; ++i )
		{
			// move to the position
			for ( int d = 0; d < numDimensions; ++d )
				if ( positions[ i ][ d ] )
					cursor.fwd(d);

			tree[ numDimensions ][ i ].set( cursorType );
			
			// move back to the offset position
			for ( int d = 0; d < numDimensions; ++d )
				if ( positions[ i ][ d ] )
					cursor.bck(d);
		}
		
		// interpolate down the tree as shown above
		for ( int d = numDimensions; d > 0; --d )
		{
			for ( int i = 0; i < halfTreeLevelSizes[ d ]; i++ )
			{
				tmp1.set( tree[ d ][ i*2 ] );
				tmp2.set( tree[ d ][ i*2+1 ] );
				
				//tmp1.mul( weights[d - 1][ 0 ] );
				//tmp2.mul( weights[d - 1][ 1 ] );
				tmp1.mul( weights[ numDimensions - d ][ 0 ] );
				tmp2.mul( weights[ numDimensions - d ][ 1 ] );
				
				tmp1.add( tmp2 );
				
				tree[ d - 1 ][ i ].set( tmp1 );
			}
		}
	}
	
	@Override
	public void setPosition( final float[] position )
	{
        // compute the offset (Math.floor) in each dimension
		for (int d = 0; d < numDimensions; d++)
		{
			this.position[ d ] = position[ d ];

			baseDim[ d ] = position[ d ] > 0 ? (int)position[ d ]: (int)position[ d ]-1;
		}
			
	    cursor.setPosition( baseDim );
		
        // compute the weights [0...1] in each dimension and the inverse (1-weight) [1...0]
		for (int d = 0; d < numDimensions; d++)
		{
			final float w = position[ d ] - baseDim[ d ];
			
			weights[ d ][ 1 ] = w;
			weights[ d ][ 0 ] = 1 - w;
		}
		
		//
		// compute the output value
		//
		
		// the the values from the image
		for ( int i = 0; i < positions.length; ++i )
		{
			// move to the position
			for ( int d = 0; d < numDimensions; ++d )
				if ( positions[ i ][ d ] )
					cursor.fwd(d);

			tree[ numDimensions ][ i ].set( cursorType );
			
			// move back to the offset position
			for ( int d = 0; d < numDimensions; ++d )
				if ( positions[ i ][ d ] )
					cursor.bck(d);
		}
		
		// interpolate down the tree as shown above
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
	}
}
