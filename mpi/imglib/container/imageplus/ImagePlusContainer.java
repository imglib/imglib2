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
 * @author Johannes Schindelin
 */
package mpi.imglib.container.imageplus;

import ij.ImagePlus;

import mpi.imglib.container.Container3D;
import mpi.imglib.container.ContainerImpl;

import mpi.imglib.cursor.Cursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizablePlaneCursor;

import mpi.imglib.cursor.imageplus.ImagePlusCursor;
import mpi.imglib.cursor.imageplus.ImagePlusLocalizableByDimCursor;
import mpi.imglib.cursor.imageplus.ImagePlusLocalizableByDimOutsideCursor;
import mpi.imglib.cursor.imageplus.ImagePlusLocalizableCursor;
import mpi.imglib.cursor.imageplus.ImagePlusLocalizablePlaneCursor;

import mpi.imglib.image.Image;

import mpi.imglib.outside.OutsideStrategyFactory;

import mpi.imglib.type.Type;

public abstract class ImagePlusContainer<T extends Type<T>> extends ContainerImpl<T> implements Container3D<T>
{
	final ImagePlusContainerFactory factory;
	final int width, height, depth;

	ImagePlusContainer( final ImagePlusContainerFactory factory, final int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );		
		
		this.factory = factory;
		this.width = dim[ 0 ];
		this.height = dim[ 1 ];
		this.depth = dim[ 2 ];

/*		int i = 0;
		width = image.getWidth();
		height = image.getHeight();
		channels = image.getNChannels();
		slices = image.getNSlices();
		frames = image.getNFrames();

		dimensions = new int[2
			+ (channels > 1 ? 1 : 0)
			+ (slices > 1 ? 1 : 0)
			+ (frames > 1 ? 1 : 0)];
		dimensions[i++] = width;
		dimensions[i++] = height;
		if (channels > 1) dimensions[i++] = channels;
		if (slices > 1) dimensions[i++] = slices;
		if (frames > 1) dimensions[i++] = frames;
*/
	}

	@Override
	public int getWidth() { return width; }
	@Override
	public int getHeight() { return height; }
	@Override
	public int getDepth() { return depth; }

	public final int getPos( final int[] l ) { return l[1] * width + l[0];	}	
	
	public abstract ImagePlus getImagePlus();

	public Cursor<T> createCursor( T type, Image<T> image ) 
	{
		return new ImagePlusCursor<T>( this, image, type );
	}

	public LocalizableCursor<T> createLocalizableCursor( T type, Image<T> image ) 
	{
		return new ImagePlusLocalizableCursor<T>( this, image, type );
	}
;
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor( T type, Image<T> image ) 
	{
		return new ImagePlusLocalizablePlaneCursor<T>( this, image, type );
	}
;
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( T type, Image<T> image ) 
	{
		return new ImagePlusLocalizableByDimCursor<T>( this, image, type );
	}
;
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( T type, Image<T> image, OutsideStrategyFactory<T> outsideFactory ) 
	{
		return new ImagePlusLocalizableByDimOutsideCursor<T>( this, image, type, outsideFactory );
	}
	
	public ImagePlusContainerFactory getFactory() { return factory; }
}
