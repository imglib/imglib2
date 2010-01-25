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
package mpicbg.imglib.image;

import mpicbg.imglib.Factory;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.type.Type;

public class ImageFactory<T extends Type<T>> implements Factory
{
	protected boolean useOptimizedContainers = true;
	final ContainerFactory storageFactory;
	final T type;
	String errorMessage = "No errors.";
	
	public ImageFactory( final T type, final ContainerFactory storageFactory )
	{
		this.storageFactory = storageFactory;
		this.type = type;
	}

	public ContainerFactory getContainerFactory() { return storageFactory; }
	
	public T createType() { return type.createVariable();	}

	public Image<T> createImage( final int dim[], final String name )
	{
		return new Image<T>( this, dim, name );
	}

	public Image<T> createImage( final int dim[] ) { return createImage( dim, null ); }
	
	@Override
	public void printProperties()
	{
		System.out.println("ByteTypeImageFactory(): ");
		System.out.println("Use optimized containers if possible: " + useOptimizedContainers );
		storageFactory.printProperties();		
	}
	
	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public void setParameters(String configuration) {}	
	
	
	public void setOptimizedContainerUse ( final boolean useOptimizedContainers ) { this.useOptimizedContainers = useOptimizedContainers; }
	public boolean useOptimizedContainers() { return useOptimizedContainers; }
}
