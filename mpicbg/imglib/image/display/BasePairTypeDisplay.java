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

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.BasePairType;

public class BasePairTypeDisplay<T extends BasePairType<T>> extends Display<T>
{
	public BasePairTypeDisplay( final Image<T> img )
	{
		super(img);
		this.min = 0;
		this.max = 1;
	}	
	
	@Override
	public void setMinMax() {}
	
	@Override
	public float get32Bit( T c ) { return c.baseToValue(); }
	@Override
	public float get32BitNormed( T c ) { return c.baseToValue() / 5f; }
	
	@Override
	public byte get8BitSigned( final T c ) { return c.baseToValue(); }
	@Override
	public short get8BitUnsigned( final T c ) { return c.baseToValue(); }
}

