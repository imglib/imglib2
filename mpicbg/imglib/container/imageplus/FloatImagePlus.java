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
package mpicbg.imglib.container.imageplus;

import ij.IJ;
import ij.ImagePlus;


import mpicbg.imglib.container.basictypecontainer.FloatContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class FloatImagePlus<T extends Type<T>> extends ImagePlusContainer<T> implements FloatContainer<T> 
{
	final ImagePlus image;
	final float[][] mirror;
	
	public FloatImagePlus( ImagePlusContainerFactory factory, int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );
		
		image = IJ.createImage( "image", "32-Bit Black", dim[0], dim[1], dim[2]);
		mirror = new float[ dim[2] ][];
		
		for ( int i = 0; i < dim[ 2 ]; ++i )
			mirror[ i ] = (float[])image.getStack().getProcessor( i+1 ).getPixels();
	}

	public FloatImagePlus( ImagePlus image, ImagePlusContainerFactory factory ) 
	{
		this( factory, new int[]{ image.getWidth(), image.getHeight(), image.getStackSize()}, 1 );
	}
	
	@Override
	public float[] getCurrentStorageArray( Cursor<?> c ) 
	{
		return mirror[ c.getStorageIndex() ];
	}

	@Override
	public void close() { image.close(); }

	@Override
	public ImagePlus getImagePlus() { return image;	}
}

