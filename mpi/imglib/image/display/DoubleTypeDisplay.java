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

import mpi.imglib.cursor.Cursor;
import mpi.imglib.image.Image;
import mpi.imglib.type.numeric.DoubleType;

public class DoubleTypeDisplay extends Display<DoubleType>
{
	public DoubleTypeDisplay( final Image<DoubleType> img )
	{
		super(img);
	}	
	
	@Override
	public void setMinMax()
	{
		final Cursor<DoubleType> c = img.createCursor();
		final DoubleType t = c.getType();
		
		if ( !c.hasNext() )
		{
			min = Float.MIN_VALUE;
			max = Float.MAX_VALUE;
			return;
		}
		
		c.fwd();
		min = max = t.get();

		while ( c.hasNext() )
		{
			c.fwd();

			final double value = t.get();
			
			if ( value > max )
				max = value;			
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}

	@Override
	public float get32Bit( DoubleType c ) { return (float)c.get(); }
	@Override
	public float get32BitNormed( DoubleType c ) { return (float)normDouble( c.get() ); }
	
	@Override
	public byte get8BitSigned( final DoubleType c) { return (byte) Math.round( normDouble( c.get() ) * 255 ); }
	@Override
	public short get8BitUnsigned( final DoubleType c) { return (short)Math.round( normDouble( c.get() ) * 255 ); }		
}
