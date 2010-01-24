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

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.ComplexFloatType;

public class ComplexFloatTypePowerSpectrumDisplay extends Display<ComplexFloatType>
{
	public ComplexFloatTypePowerSpectrumDisplay( final Image<ComplexFloatType> img )
	{
		super(img);
	}	

	@Override
	public void setMinMax()
	{
		final Cursor<ComplexFloatType> c = img.createCursor();
		
		if ( !c.hasNext() )
		{
			min = Float.MIN_VALUE;
			max = Float.MAX_VALUE;
			return;
		}
		
		c.fwd();
		min = max = getComplexDisplayValue( c.getType() );

		while ( c.hasNext() )
		{
			c.fwd();

			final float value = getComplexDisplayValue( c.getType() );
			
			if ( value > max )
				max = value;			
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}
	
	protected float getComplexDisplayValue( final ComplexFloatType c )
	{
		final float real = c.getReal();
		final float complex = c.getComplex();

		return (float)MathLib.gLog( Math.sqrt( real * real + complex * complex ), 2 );		
	}	

	@Override
	public float get32Bit( final ComplexFloatType c ) { return getComplexDisplayValue( c ); }
	@Override
	public float get32BitNormed( final ComplexFloatType c ) { return normFloat( getComplexDisplayValue( c ) ); }
	
	@Override
	public byte get8BitSigned( final ComplexFloatType c) { return (byte) Math.round( normFloat( getComplexDisplayValue( c ) ) * 255 ); }
	@Override
	public short get8BitUnsigned( final ComplexFloatType c) { return (short)Math.round( normFloat( getComplexDisplayValue( c ) ) * 255 ); }		
	
}
