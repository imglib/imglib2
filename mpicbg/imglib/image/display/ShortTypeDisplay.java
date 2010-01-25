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
import mpicbg.imglib.type.numeric.ShortType;

public class ShortTypeDisplay extends Display<ShortType>
{
	public ShortTypeDisplay( final Image<ShortType> img)
	{
		super(img);
		this.min = 0;
		this.max = 65535;
	}	

	@Override
	public void setMinMax()
	{
		final Cursor<ShortType> c = img.createCursor();
		final ShortType t = c.getType();
		
		if ( !c.hasNext() )
		{
			min = 0;
			max = 65535;
			return;
		}
		
		c.fwd();
		min = max = t.get() & 0xffff;

		while ( c.hasNext() )
		{
			c.fwd();
			
			final int value = t.get() & 0xffff;

			if ( value > max )
				max = value;
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}
	
	@Override
	public float get32Bit( ShortType c ) { return c.get(); }
	@Override
	public float get32BitNormed( ShortType c ) { return normFloat( c.get() ); }
	
	@Override
	public byte get8BitSigned( final ShortType c) { return (byte)Math.round( normFloat( c.get() ) * 255 ); }
	@Override
	public short get8BitUnsigned( final ShortType c) { return (short)Math.round( normFloat( c.get() ) * 255 ); }		
}
