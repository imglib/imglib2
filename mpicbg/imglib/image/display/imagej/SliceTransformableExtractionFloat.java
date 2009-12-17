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
package mpicbg.imglib.image.display.imagej;

import ij.ImagePlus;
import mpicbg.imglib.type.Type;

public class SliceTransformableExtractionFloat<T extends Type<T>> extends SliceTransformableExtraction<T>
{
	final float[] sliceImg;

	public SliceTransformableExtractionFloat( final int numImages, final InverseTransformDescription<T> it, final float[] sliceImg, 
											  final ImagePlus parent, final int[] dimensionPositions, 
											  final int dimX, final int dimY, final int dimZ, 
											  final int sizeX, final int sizeY, final int slice)
	{
		super( numImages, it, parent, dimensionPositions, dimX, dimY, dimZ, sizeX, sizeY, slice );

		this.sliceImg = sliceImg;
	}
	
	@Override
	final protected void setIntensity( final int index )
	{
		sliceImg[ index ] += display.get32Bit(type);		
	}
}
