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
package mpicbg.imglib.image.display;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.ByteType;

public class ByteTypeDisplay extends Display<ByteType>
{
	public ByteTypeDisplay( final Image<ByteType> img)
	{
		super(img);
		this.min = 0;
		this.max = 255;
	}	

	@Override
	public void setMinMax()
	{
		final Cursor<ByteType> c = img.createCursor();
		final ByteType t = c.getType();

		
		if ( !c.hasNext() )
		{
			min = 0;
			max = 255;
			return;
		}
		
		c.fwd();
		min = max = t.get() & 0xff;

		while ( c.hasNext() )
		{
			c.fwd();
			
			final int value = t.get() & 0xff;
			
			if ( value > max )
				max = value;			
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}
	
	@Override
	public float get32Bit( ByteType c ) { return (c.get() & 0xff); }
	@Override
	public float get32BitNormed( ByteType c ) { return normFloat( c.get() & 0xff ); }
	
	@Override
	public byte get8BitSigned( final ByteType c ) { return c.get(); }
	@Override
	public short get8BitUnsigned( final ByteType c ) { return (short)( c.get() & 0xff ); }
}

