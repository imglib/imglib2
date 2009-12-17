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
package mpicbg.imglib.outside;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.Type;

public class OutsideStrategyPeriodic<T extends Type<T>> extends OutsideStrategy<T>
{
	final LocalizableCursor<T> parentCursor;
	final LocalizableByDimCursor<T> circleCursor;
	final T type, circleType;
	final int numDimensions;
	final int[] dimension, position, circledPosition;
	
	public OutsideStrategyPeriodic( final LocalizableCursor<T> parentCursor )
	{
		super( parentCursor );
		
		this.parentCursor = parentCursor;
		this.circleCursor = parentCursor.getImage().createLocalizableByDimCursor();
		this.circleType = circleCursor.getType();
		this.type = circleType.createVariable();
			
		this.numDimensions = parentCursor.getImage().getNumDimensions();
		this.dimension = parentCursor.getImage().getDimensions();
		this.position = new int[ numDimensions ];
		this.circledPosition = new int[ numDimensions ];
	}

	@Override
	public T getType(){ return type; }
	
	@Override
	final public void notifyOutside()
	{
		parentCursor.getPosition( position );
		getCircleCoordinate( position, circledPosition, dimension, numDimensions );		
		circleCursor.setPosition( circledPosition );

		type.set( circleType );
	}

	@Override
	public void notifyOutside( final int steps, final int dim ) 
	{
		final int oldPos = circleCursor.getPosition( dim ); 
		
		circleCursor.move( getCircleCoordinateDim( oldPos + steps, dimension[ dim ] )  - oldPos, dim );
		type.set( circleType );
	}
	
	@Override
	public void notifyOutsideFwd( final int dim ) 
	{		
		final int oldPos = circleCursor.getPosition( dim ); 
		
		circleCursor.move( getCircleCoordinateDim( oldPos + 1, dimension[ dim ] )  - oldPos, dim );
		type.set( circleType );
	}

	@Override
	public void notifyOutsideBck( final int dim ) 
	{
		final int oldPos = circleCursor.getPosition( dim ); 
		
		circleCursor.move( getCircleCoordinateDim( oldPos - 1, dimension[ dim ] )  - oldPos, dim );
		type.set( circleType );
	}
	
	/*
	 * For mirroring there is no difference between leaving the image and moving while 
	 * being outside of the image
	 * @see mpi.imglib.outside.OutsideStrategy#initOutside()
	 */
	@Override
	public void initOutside() 
	{ 
		parentCursor.getPosition( position );
		getCircleCoordinate( position, circledPosition, dimension, numDimensions );		
		circleCursor.setPosition( circledPosition );

		type.set( circleType );
	}
	
	final private static int getCircleCoordinateDim( final int pos, final int dim )
	{		
		if ( pos > -1 )
			return pos % dim;
		else
			return (dim-1) + (pos+1) % dim;		
	}
	
	final private static void getCircleCoordinate( final int[] position, final int circledPosition[], final int[] dimensions, final int numDimensions )
	{
		for ( int d = 0; d < numDimensions; d++ )
			circledPosition[ d ] = getCircleCoordinateDim( position[ d ], dimensions[ d ] );
	}
	
	@Override
	public void close()
	{
		circleCursor.close();
	}
}
