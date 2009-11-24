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
import mpi.imglib.type.numeric.ComplexFloatType;

public class ComplexFloatTypePhaseSpectrumDisplay extends ComplexFloatTypePowerSpectrumDisplay
{
	public ComplexFloatTypePhaseSpectrumDisplay( final Image<ComplexFloatType> img )
	{
		super(img);
	}	
	
	@Override
	protected float getComplexDisplayValue( final ComplexFloatType c )
	{
		final float real = c.getReal();
		final float complex = c.getComplex();

		if ( real != 0.0 || complex != 0)
			return (float)Math.atan2( complex, real );
		else
			return 0;		
	}
}
