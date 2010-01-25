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
package mpicbg.imglib.container.cube;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.type.Type;

public abstract class CubeElement<C extends CubeElement<C,D,T>, D extends Cube<C,D,T>, T extends Type<T>> extends Array<T>
{
	final protected int[] offset, step;	
	final protected D parent;
	final protected int cubeId;
	
	public CubeElement( final D parent, final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel)
	{
		super( null, dim, entitiesPerPixel );
		this.offset = offset;		
		this.parent = parent;
		this.cubeId = cubeId;
		
		step = new int[ parent.getNumDimensions() ];
		
		// the steps when moving inside a cube
		Array.createAllocationSteps( dim, step );		
	}	
	
	// done by Array
	/*protected final int getPosLocal( final int[] l ) 
	{ 
		int i = l[ 0 ];
		for ( int d = 1; d < dim.length; ++d )
			i += l[ d ] * step[ d ];
		
		return i;
	}*/
	
	public void getSteps( final int[] step )
	{
		for ( int d = 0; d < numDimensions; d++ )
			step[ d ] = this.step[ d ];
	}
	
	public int getCubeId() { return cubeId; }
	
	public void getOffset( final int[] offset )
	{
		for ( int i = 0; i < numDimensions; i++ )
			offset[ i ] = this.offset[ i ];
	}
	
	public final int getPosGlobal( final int[] l ) 
	{ 
		int i = l[ 0 ] - offset[ 0 ];
		for ( int d = 1; d < dim.length; ++d )
			i += (l[ d ] - offset[ d ]) * step[ d ];
		
		return i;
	}
	
}
