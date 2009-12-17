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

import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ImagePlusLocalizablePlaneCursor<T extends Type<T>> extends ImagePlusLocalizableCursor<T> implements LocalizablePlaneCursor<T>
{
	protected int planeDimA, planeDimB, planeSizeA, planeSizeB, incPlaneA, incPlaneB, maxI, pos, maxPos;
	final protected int width, height, depth;
	
	public ImagePlusLocalizablePlaneCursor( final ImagePlusContainer<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );
		
		this.width = image.getDimension( 0 );
		this.height = image.getDimension( 1 );
		this.depth = image.getDimension( 2 );
	}	
	
	@Override 
	public boolean hasNext()
	{
		if ( pos < maxPos )
			return true;
		else
			return false;
	}
	
	@Override
	public void fwd()
	{
		++pos;

		if ( position[ planeDimA ] < dimensions[ planeDimA ] - 1 )
		{
			++position[ planeDimA ];
			
			if ( incPlaneA == -1 )
			{
				++slice;
				type.updateDataArray( this );
			}
			else
			{
				type.incIndex( incPlaneA );
			}
		}		
		else if ( position[ planeDimB ] < dimensions[ planeDimB ] - 1)
		{
			position[ planeDimA ] = 0;
			++position[ planeDimB ];
			
			if ( incPlaneB == -1 )
			{
				++slice;
				type.updateDataArray( this );
			}
			else
			{
				type.incIndex( incPlaneB );
			}

			if ( incPlaneA == -1 )
			{
				slice = 0;
				type.updateDataArray( this );
			}
			else
			{
				type.decIndex( (planeSizeA - 1) * incPlaneA );	
			}						
		}
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;

		// store the current position
    	final int[] dimPos = dimensionPositions.clone();

		dimPos[ planeDimA ] = 0;
		dimPos[ planeDimB ] = 0;
		setPosition( dimPos );				
    	
    	if ( planeDimA == 0 )
    	{
    		incPlaneA = 1;
    		planeSizeA = width;
    	}
    	else if ( planeDimA == 1 )
    	{
    		incPlaneA = width;
    		planeSizeA = height;
    	}
    	else if ( planeDimA == 2 )
    	{
    		incPlaneA = -1;
    		planeSizeA = depth;
    	}
    	else
    	{
    		throw new RuntimeException("ImagePlusLocalizablePlaneCursor cannot have only 3 dimensions, cannot handle dimension index of planeDimA " + planeDimA );
    	}

    	if ( planeDimB == 0 )
    	{
    		incPlaneB = 1;
    		planeSizeB = width;
    	}
    	else if ( planeDimB == 1 )
    	{
    		incPlaneB = width;
    		planeSizeB = height;
    	}
    	else if ( planeDimB == 2 )
    	{
    		incPlaneB = -1;
    		planeSizeB = depth;
    	}
    	else
    	{
    		throw new RuntimeException("ImagePlusLocalizablePlaneCursor cannot have only 3 dimensions, cannot handle dimension index planeDimB " + planeDimB );
    	}    	
		
		maxPos = planeSizeA * planeSizeB;
		pos = 0;
		
		isClosed = false;

		if ( incPlaneA == -1 )
		{
			--slice;
		}
		else
		{
			type.decIndex( incPlaneA );
			type.updateDataArray( this );				
		}
		
		position[ planeDimA ] = -1;
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
		slice = position[ 2 ];
		
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];
	}
	
}
