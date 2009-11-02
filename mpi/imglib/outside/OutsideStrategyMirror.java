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
package mpi.imglib.outside;

import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.type.Type;

public class OutsideStrategyMirror<T extends Type<T>> extends OutsideStrategy<T>
{
	final LocalizableCursor<T> parentCursor;
	final LocalizableByDimCursor<T> mirrorCursor;
	final T type;
	final int numDimensions;
	final int[] dimension, position, mirroredPosition;
	
	public OutsideStrategyMirror( final LocalizableCursor<T> parentCursor )
	{
		super( parentCursor );
		
		this.parentCursor = parentCursor;
		this.mirrorCursor = parentCursor.getImage().createLocalizableByDimCursor();
		this.type = mirrorCursor.getType();
			
		this.numDimensions = parentCursor.getImage().getNumDimensions();
		this.dimension = parentCursor.getImage().getDimensions();
		this.position = new int[ numDimensions ];
		this.mirroredPosition = new int[ numDimensions ];
	}

	@Override
	public T getType(){ return type; }

	@Override
	public void notifyOutside( final T cursorType )
	{
		parentCursor.getPosition( position );
		getMirrorCoordinate( position, mirroredPosition );		
		mirrorCursor.setPosition( mirroredPosition );

		// if the array changed we have to update it
		if ( !type.hasSameDataArray( cursorType ) )
			cursorType.updateDataArray( type );
			
		// and update the index
		cursorType.updateIndex( type.getIndex() );
	}

	/*
	 * For mirroring there is no difference between leaving the image and moving while 
	 * being outside of the image
	 * @see mpi.imglib.outside.OutsideStrategy#initOutside()
	 */
	@Override
	public void initOutside( final T cursorType ) { notifyOutside( cursorType ); }
	
	protected void getMirrorCoordinate( final int[] position, final int mirroredPosition[] )
	{
		for ( int d = 0; d < numDimensions; d++ )
		{
			mirroredPosition[ d ] = position[ d ];
			
			if ( mirroredPosition[ d ] >= dimension[ d ])
				mirroredPosition[ d ] = dimension[ d ] - (mirroredPosition[ d ] - dimension[ d ] + 2);
	
			if ( mirroredPosition[ d ] < 0 )
			{
				int tmp = 0;
				int dir = 1;
	
				while ( mirroredPosition[ d ] < 0 )
				{
					tmp += dir;
					if (tmp == dimension[ d ] - 1 || tmp == 0)
						dir *= -1;
					mirroredPosition[ d ]++;
				}
				mirroredPosition[ d ] = tmp;
			}
		}
	}

	@Override
	public void close()
	{
		mirrorCursor.close();
	}
}
