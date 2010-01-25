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
package mpicbg.imglib.container;

import mpicbg.imglib.Factory;
import mpicbg.imglib.container.basictypecontainer.*;
import mpicbg.imglib.type.Type;

public abstract class ContainerFactory implements Factory
{
	protected boolean useOptimizedContainers = true;
		
	public void setOptimizedContainerUse ( final boolean useOptimizedContainers ) { this.useOptimizedContainers = useOptimizedContainers; }
	public boolean useOptimizedContainers() { return useOptimizedContainers; }
	
	// All basic Type containers
	public abstract <T extends Type<T>>BitContainer<T> createBitInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>>ByteContainer<T> createByteInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>>CharContainer<T> createCharInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>>ShortContainer<T> createShortInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>>IntContainer<T> createIntInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>>LongContainer<T> createLongInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>>FloatContainer<T> createFloatInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>>DoubleContainer<T> createDoubleInstance( int[] dimensions, int entitiesPerPixel );
}
