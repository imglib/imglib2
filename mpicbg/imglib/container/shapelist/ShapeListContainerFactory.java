/**
 * License: GPL
 *
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
 */
package mpicbg.imglib.container.shapelist;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.CharAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.LongAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.type.Type;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1a
 */
public class ShapeListContainerFactory extends ContainerFactory
{

	@Override
	public < T extends Type< T >> Container< T, ? extends BitAccess > createBitInstance( int[] dimensions, int entitiesPerPixel )
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * A pointless hack to get a container instance somehow.
	 * 
	 * TODO Check if that factory system is really necessary and if so, how it
	 * can be made flexible enough to prevent these stupid re-implementations.  
	 */
	@Override
	public ByteShapeList< ByteAccess > createByteInstance( int[] dimensions, int entitiesPerPixel )
	{
		return new ByteShapeList< ByteAccess >( this, dimensions );
	}

	@Override
	public < T extends Type< T >> Container< T, ? extends CharAccess > createCharInstance( int[] dimensions, int entitiesPerPixel )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public < T extends Type< T >> Container< T, ? extends DoubleAccess > createDoubleInstance( int[] dimensions, int entitiesPerPixel )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public < T extends Type< T >> Container< T, ? extends FloatAccess > createFloatInstance( int[] dimensions, int entitiesPerPixel )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public < T extends Type< T >> Container< T, ? extends IntAccess > createIntInstance( int[] dimensions, int entitiesPerPixel )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public < T extends Type< T >> Container< T, ? extends LongAccess > createLongInstance( int[] dimensions, int entitiesPerPixel )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public < T extends Type< T >> Container< T, ? extends ShortAccess > createShortInstance( int[] dimensions, int entitiesPerPixel )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mpicbg.imglib.Factory#printProperties()
	 */
	@Override
	public void printProperties()
	{
	// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see mpicbg.imglib.Factory#setParameters(java.lang.String)
	 */
	@Override
	public void setParameters( String configuration )
	{
	// TODO Auto-generated method stub

	}

}
