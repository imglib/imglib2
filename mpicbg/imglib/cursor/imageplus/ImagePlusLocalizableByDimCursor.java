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
package mpicbg.imglib.cursor.imageplus;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;


public class ImagePlusLocalizableByDimCursor<T extends Type<T>> extends ImagePlusLocalizableCursor<T> implements LocalizableByDimCursor<T>
{
	final protected int[] step, tmp;
	int numNeighborhoodCursors = 0;
	
	public ImagePlusLocalizableByDimCursor( final ImagePlusContainer<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );
		
		step = Array.createAllocationSteps( container.getDimensions() );
		tmp = new int[ numDimensions ];
	}	

	@Override
	public synchronized LocalNeighborhoodCursor<T> createLocalNeighborhoodCursor()
	{
		if ( numNeighborhoodCursors == 0)
		{
			++numNeighborhoodCursors;
			return LocalNeighborhoodCursorFactory.createLocalNeighborhoodCursor( this );
		}
		else
		{
			System.out.println("ImagePlusLocalizableByDimCursor.createLocalNeighborhoodCursor(): There is only one special cursor per cursor allowed.");
			return null;
		}
	}

	@Override
	public synchronized RegionOfInterestCursor<T> createRegionOfInterestCursor( final int[] offset, final int[] size )
	{
		if ( numNeighborhoodCursors == 0)
		{
			++numNeighborhoodCursors;
			return new RegionOfInterestCursor<T>( this, offset, size );
		}
		else
		{
			System.out.println("ImagePlusLocalizableByDimCursor.createRegionOfInterestCursor(): There is only one special cursor per cursor allowed.");
			return null;
		}
	}

	@Override
	public void fwd( final int dim )
	{
		position[ dim ]++;

		if ( dim == 2 )
		{
			++slice;
			type.updateDataArray( this );
		}
		else
		{
			type.incIndex( step[ dim ] );
		}
	}

	@Override
	public void move( final int steps, final int dim )
	{
		position[ dim ] += steps;	

		if ( dim == 2 )
		{
			slice += steps;
			type.updateDataArray( this );
		}
		else
		{
			type.incIndex( step[ dim ] * steps );
		}		
	}
	
	@Override
	public void moveRel( final int[] vector )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( vector[ d ], d );
	}

	@Override
	public void moveTo( final int[] position )
	{		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int dist = position[ d ] - getPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}
	
	@Override
	public void moveTo( final LocalizableCursor<?> cursor )
	{
		cursor.getPosition( tmp );
		moveTo( tmp );
	}

	@Override
	public void setPosition( final LocalizableCursor<?> cursor )
	{
		cursor.getPosition( tmp );
		setPosition( tmp );
	}
	
	@Override
	public void bck( final int dim )
	{		
		position[ dim ]--;
		
		if ( dim == 2 )
		{
			--slice;
			type.updateDataArray( this );
		}
		else
		{
			type.decIndex( step[ dim ] );
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		type.updateIndex( container.getPos( position ) );
		
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];
		
		slice = position[ 2 ];
		type.updateDataArray( this );		
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		if ( dim == 2 )
		{
			slice = position;
			type.updateDataArray( this );
		}
		else
		{
			type.updateIndex( container.getPos( this.position ) );
		}
	}
}
