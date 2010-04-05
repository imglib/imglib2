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
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1a
 */
public class ShapeListContainerFactory extends ContainerFactory
{
	/**
	 * This method is called by {@link Image}. The {@link ContainerFactory} can decide how to create the {@link Container},
	 * if it is for example a {@link DirectAccessContainerFactory} it will ask the {@link Type} to create a 
	 * suitable {@link Container} for the {@link Type} and the dimensionality
	 * 
	 * @return {@link Container} - the instantiated Container
	 */
	public <T extends Type<T>> ShapeList<T> createContainer( final int[] dim, final T type )
	{
		return new ShapeList<T>( this, dim, type );
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
