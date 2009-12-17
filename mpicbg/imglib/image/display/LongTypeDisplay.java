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
import mpicbg.imglib.type.numeric.LongType;

public class LongTypeDisplay extends Display<LongType>
{
	public LongTypeDisplay( final Image<LongType> img)
	{
		super(img);
		min = Long.MIN_VALUE;
		max = Long.MAX_VALUE;
	}	

	@Override
	public void setMinMax()
	{
		final Cursor<LongType> c = img.createCursor();
		final LongType t = c.getType();
		
		if ( !c.hasNext() )
		{
			min = Long.MIN_VALUE;
			max = Long.MAX_VALUE;
			return;
		}
		
		c.fwd();
		min = max = t.get();

		while ( c.hasNext() )
		{
			c.fwd();
			
			final long value = t.get();

			if ( value > max )
				max = value;
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}
	
	@Override
	public float get32Bit( LongType c ) { return c.get(); }
	@Override
	public float get32BitNormed( LongType c ) { return normFloat( c.get() ); }
	
	@Override
	public byte get8BitSigned( final LongType c) { return (byte)Math.round( normFloat( c.get() ) * 255 ); }
	@Override
	public short get8BitUnsigned( final LongType c) { return (short)Math.round( normFloat( c.get() ) * 255 ); }			
}
