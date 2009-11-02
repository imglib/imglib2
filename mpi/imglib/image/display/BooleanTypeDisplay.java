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
package mpi.imglib.image.display;

import mpi.imglib.image.Image;
import mpi.imglib.type.logic.BooleanType;

public class BooleanTypeDisplay extends Display<BooleanType>
{
	public BooleanTypeDisplay( final Image<BooleanType> img )
	{
		super(img);
		this.min = 0;
		this.max = 1;
	}	

	@Override
	public void setMinMax() {}
	
	@Override
	public float get32Bit( BooleanType c ) { return c.get() ? 1 : 0; }
	@Override
	public float get32BitNormed( BooleanType c ) { return c.get() ? 1 : 0; }
	
	@Override
	public byte get8BitSigned( final BooleanType c ) { return c.get() ? (byte)255 : (byte)0; }
	@Override
	public short get8BitUnsigned( final BooleanType c ) { return c.get() ? (short)255 : (short)0; }
}

