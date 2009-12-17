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
package mpicbg.imglib.cursor.array;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ArrayLocalizablePlaneCursor<T extends Type<T>> extends ArrayLocalizableCursor<T> implements LocalizablePlaneCursor<T>
{
	protected int planeDimA, planeDimB, planeSizeA, planeSizeB, incPlaneA, incPlaneB, maxI;
	
	public ArrayLocalizablePlaneCursor( final Array<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );
	}	
	
	@Override 
	public boolean hasNext()
	{
		return type.getIndex() < maxI;
		
		/*if ( type.i < maxI )
			return true;
		else
			return false;*/
	}
	
	@Override
	public void fwd()
	{ 
		if ( position[ planeDimA ] < dimensions[ planeDimA ] - 1)
		{
			position[ planeDimA ]++;
			type.incIndex( incPlaneA );
		}
		else if ( position[ planeDimB ] < dimensions[ planeDimB ] - 1)
		{
			position[ planeDimA ] = 0;
			position[ planeDimB ]++;
			type.incIndex( incPlaneB );
			type.decIndex( (planeSizeA - 1) * incPlaneA );
		}
	}
	
	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
		
		this.planeSizeA = container.getDimension( planeDimA );
		this.planeSizeB = container.getDimension( planeDimB );
		
		final int[] steps = Array.createAllocationSteps( container.getDimensions() );

		// store the current position
    	final int[] dimPos = dimensionPositions.clone();
		
		incPlaneA = steps[ planeDimA ];
		dimPos[ planeDimA ] = 0;
		
		if ( planeDimB > -1 && planeDimB < steps.length )
		{
			incPlaneB = steps[ planeDimB ];
			dimPos[ planeDimB ] = 0;
		}
		else
		{
			incPlaneB = 0;
		}

		setPosition( dimPos );		
		isClosed = false;
		
		type.decIndex( incPlaneA );					
		position[ planeDimA ] = -1;
		
		dimPos[ planeDimA ] = dimensions[ planeDimA ] - 1;		
		if ( planeDimB > -1 && planeDimB < steps.length )
			dimPos[ planeDimB ] = dimensions[ planeDimB ] - 1;
		
		maxI = container.getPos( dimPos );
		
		type.updateDataArray( this );				
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB )
	{
		if ( dimensions == null )
			return;

		reset( planeDimA, planeDimB, new int[ numDimensions ] );
	}
	
	@Override
	public void reset()
	{
		if ( dimensions == null )
			return;
		
		reset( 0, 1, new int[ numDimensions ] );		
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
	
	protected void setPosition( final int[] position )
	{
		type.updateIndex( container.getPos( position ) );
		
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];
	}
	
}
