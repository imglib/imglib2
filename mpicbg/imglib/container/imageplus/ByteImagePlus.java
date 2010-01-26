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


import mpicbg.imglib.container.basictypecontainer.ByteContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class ByteImagePlus<T extends Type<T>> extends ImagePlusContainer<T> implements ByteContainer<T> 
{
	final ImagePlus image;
	final byte[][] mirror;
	
	public ByteImagePlus( ImagePlusContainerFactory factory, int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );

		image = IJ.createImage( "image", "8-Bit Black", dim[0], dim[1], dim[2]);
		mirror = new byte[ dim[2] ][];
		
		for ( int i = 0; i < dim[ 2 ]; ++i )
			mirror[ i ] = (byte[])image.getStack().getProcessor( i+1 ).getPixels();
	}

	public ByteImagePlus( ImagePlus image, ImagePlusContainerFactory factory ) 
	{
		this( factory, new int[]{ image.getWidth(), image.getHeight(), image.getStackSize()}, 1 );
	}
	
	@Override
	public byte[] getCurrentStorageArray( Cursor<?> c ) 
	{
		return mirror[ c.getStorageIndex() ];
	}

	@Override
	public void close() { image.close(); }
	
	@Override
	public ImagePlus getImagePlus() { return image;	}
}
