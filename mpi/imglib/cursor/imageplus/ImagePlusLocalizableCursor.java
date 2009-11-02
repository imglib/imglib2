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
package mpi.imglib.cursor.imageplus;

import mpi.imglib.container.imageplus.ImagePlusContainer;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.type.Type;

public class ImagePlusLocalizableCursor<T extends Type<T>> extends ImagePlusCursor<T> implements LocalizableCursor<T>
{
	final protected int numDimensions; 	
	final protected int[] position, dimensions;
	
	public ImagePlusLocalizableCursor( final ImagePlusContainer<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );

		numDimensions = container.getNumDimensions(); 		
		position = new int[ numDimensions ];
		dimensions = container.getDimensions();
		
		// unluckily we have to call it twice, in the superclass position is not initialized yet
		reset();
	}	
	
	@Override
	public void fwd()
	{ 
		type.incIndex();
		
		if ( type.getIndex() > slicePixelCountMinus1 ) 
		{
			++slice;
			type.updateIndex( 0 );
			type.updateDataArray( this );
		}
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			if ( position[ d ] < dimensions[ d ] - 1 )
			{
				position[ d ]++;
				
				for ( int e = 0; e < d; e++ )
					position[ e ] = 0;
				
				return;
			}
		}
				
	}

	@Override
	public void reset()
	{
		if ( dimensions == null )
			return;
		
		isClosed = false;
		type.updateIndex( -1 );
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;
		
		slice = 0;
		
		type.updateDataArray( this );
	}


	@Override
	public void getPosition( int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public int[] getPosition(){ return position.clone(); }
	
	@Override
	public int getPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public String getPositionAsString()
	{
		String pos = "(" + position[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + position[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getPositionAsString() + " = " + type; }
}
