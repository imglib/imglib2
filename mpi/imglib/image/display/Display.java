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
import mpi.imglib.type.Type;

public abstract class Display<T extends Type<T>>
{
	final protected Image<T> img;
	
	protected double min, max;
	
	public Display( final Image<T> img )
	{
		this.img = img;
		this.min = 0;
		this.max = 1;
	}	
	
	public Image<T> getImage() { return img; }
	
	public abstract void setMinMax();
	
	public double getMin() { return min; }
	public double getMax() { return max; }
	public void setMin( final double min ) { this.min = min; }
	public void setMax( final double max ) { this.max = max; }
	public void setMinMax( final double min, final double max ) 
	{
		this.min = min;
		this.max = max; 
	}

	public double normDouble( final double c )
	{
		double value = ( c - min ) / ( max - min );
		
		if ( value < 0 )
			value = 0;
		else if ( value > 1 )
			value = 1;
		
		return value;
	}

	public float normFloat( final float c )
	{
		double value = ( c - min ) / ( max - min );
		
		if ( value < 0 )
			value = 0;
		else if ( value > 1 )
			value = 1;
		
		return (float)value;
	}
	
	public abstract float get32Bit( T c );	
	public abstract float get32BitNormed( T c );

	public abstract byte get8BitSigned( T c );
	public abstract short get8BitUnsigned( T c );
	
	public int get8BitARGB( final T c )
	{
		final int col = get8BitUnsigned( c );		
				
		return (col<<16)+(col<<8)+col;
	}	
	
}
