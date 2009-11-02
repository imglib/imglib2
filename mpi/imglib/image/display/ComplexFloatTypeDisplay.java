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
import mpi.imglib.type.numeric.ComplexFloatType;

public class ComplexFloatTypeDisplay extends Display<ComplexFloatType>
{
	public ComplexFloatTypeDisplay( final Image<ComplexFloatType> img )
	{
		super(img);
	}	
	
	@Override
	public void setMinMax()
	{
		final Cursor<ComplexFloatType> c = img.createCursor();
		final ComplexFloatType t = c.getType();
		
		if ( !c.hasNext() )
		{
			min = Float.MIN_VALUE;
			max = Float.MAX_VALUE;
			return;
		}
		
		c.fwd();
		min = max = t.getReal();

		while ( c.hasNext() )
		{
			c.fwd();

			final float value = t.getReal();
			
			if ( value > max )
				max = value;			
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}

	@Override
	public float get32Bit( ComplexFloatType c ) { return c.getReal(); }
	@Override
	public float get32BitNormed( ComplexFloatType c ) { return normFloat( c.getReal() ); }
	
	@Override
	public byte get8BitSigned( final ComplexFloatType c) { return (byte) Math.round( normFloat( c.getReal() ) * 255 ); }
	@Override
	public short get8BitUnsigned( final ComplexFloatType c) { return (short)Math.round( normFloat( c.getReal() ) * 255 ); }		
}
