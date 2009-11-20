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
package mpi.imglib.cursor.array;

import mpi.imglib.container.array.Array;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.cursor.special.LocalNeighborhoodCursor;
import mpi.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpi.imglib.image.Image;
import mpi.imglib.type.Type;

public class ArrayLocalizableByDimCursor<T extends Type<T>> extends ArrayLocalizableCursor<T> implements LocalizableByDimCursor<T>
{
	final protected int[] step;
	final int tmp[];
	
	int numNeighborhoodCursors = 0;
	
	public ArrayLocalizableByDimCursor( final Array<T> container, final Image<T> image, final T type ) 
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
			System.out.println("ArrayLocalizableByDimCursor.createLocalNeighborhoodCursor(): There is only one LocalNeighborhoodCursor per cursor allowed.");
			return null;
		}
	}

	@Override
	public void fwd( final int dim )
	{
		type.incIndex( step[ dim ] );
		position[ dim ]++;	
	}

	@Override
	public void move( final int steps, final int dim )
	{
		type.incIndex( step[ dim ] * steps );
		position[ dim ] += steps;	
	}
	
	@Override
	public void bck( final int dim )
	{
		type.decIndex( step[ dim ] );
		position[ dim ]--;	
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
	public void setPosition( final int[] position )
	{
		type.updateIndex( container.getPos( position ) );
		
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;
		type.updateIndex( container.getPos( this.position ) );		
	}
}
