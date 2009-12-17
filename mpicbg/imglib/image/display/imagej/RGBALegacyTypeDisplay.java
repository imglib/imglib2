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

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.numeric.RGBALegacyType;

public class RGBALegacyTypeDisplay extends Display<RGBALegacyType>
{
	public RGBALegacyTypeDisplay( final Image<RGBALegacyType> img)
	{
		super(img);
		this.min = 0;
		this.max = 255;
	}	

	final protected float avg( final int col )
	{
		final int r = RGBALegacyType.red( col );
		final int g = RGBALegacyType.green( col );
		final int b = RGBALegacyType.blue( col );
		
		return ( 0.3f * r + 0.6f * g + 0.1f * b );
	}	
	
	final protected int max( final int col )
	{
		final int r = RGBALegacyType.red( col );
		final int g = RGBALegacyType.green( col );
		final int b = RGBALegacyType.blue( col );
		
		return Math.max( Math.max ( r,g ), b);
	}

	final protected int min( final int col )
	{
		final int r = RGBALegacyType.red( col );
		final int g = RGBALegacyType.green( col );
		final int b = RGBALegacyType.blue( col );

		return Math.min( Math.min( r,g ), b);
	}
	
	@Override
	public void setMinMax()
	{
		final Cursor<RGBALegacyType> c = img.createCursor();
		final RGBALegacyType t = c.getType();
		
		if ( !c.hasNext() )
		{
			min = 0;
			max = 255;
			return;
		}
		
		c.fwd();
		
		min = min( t.get() );
		max = max( t.get() );

		while ( c.hasNext() )
		{
			c.fwd();
			
			final int value = t.get();
			final int minValue = min( value );
			final int maxValue = max( value );

			if ( maxValue > max )
				max = maxValue;

			if ( value < minValue )
				min = minValue;
		}
		
		c.close();
	}
	
	@Override
	public float get32Bit( RGBALegacyType c ) { return avg( c.get() ); }
	@Override
	public float get32BitNormed( RGBALegacyType c ) { return normFloat( avg( c.get() ) ); }
	
	@Override
	public byte get8BitSigned( final RGBALegacyType c) { return (byte)Math.round( normFloat( avg( c.get() ) ) * 255 ); }
	@Override
	public short get8BitUnsigned( final RGBALegacyType c) { return (short)Math.round( normFloat( avg( c.get() ) ) * 255 ); }		
	
	@Override
	public int get8BitARGB( final RGBALegacyType c) { return c.get(); }	
}
