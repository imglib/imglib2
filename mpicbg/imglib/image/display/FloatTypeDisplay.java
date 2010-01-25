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
import mpicbg.imglib.type.numeric.FloatType;

public class FloatTypeDisplay extends Display<FloatType>
{
	public FloatTypeDisplay( final Image<FloatType> img )
	{
		super(img);
	}	
	
	@Override
	public void setMinMax()
	{
		final Cursor<FloatType> c = img.createCursor();
		final FloatType t = c.getType();
		
		if ( !c.hasNext() )
		{
			min = -Float.MAX_VALUE;
			max = Float.MAX_VALUE;
			return;
		}
		
		c.fwd();
		min = max = t.get();

		while ( c.hasNext() )
		{
			c.fwd();

			final float value = t.get();
			
			if ( value > max )
				max = value;			
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}

	@Override
	public float get32Bit( FloatType c ) { return c.get(); }
	@Override
	public float get32BitNormed( FloatType c ) { return normFloat( c.get() ); }
	
	@Override
	public byte get8BitSigned( final FloatType c) { return (byte) Math.round( normFloat( c.get() ) * 255 ); }
	@Override
	public short get8BitUnsigned( final FloatType c) { return (short)Math.round( normFloat( c.get() ) * 255 ); }		
}
