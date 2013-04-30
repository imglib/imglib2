/**
 * License: GPL
 *
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
 */
package interactive.remote;

import net.imglib2.display.RealARGBConverter;
import net.imglib2.type.numeric.ARGBType;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class VolatileRealTypeARGBConverter extends RealARGBConverter< VolatileRealType< ? > >
{
	final protected ARGBType background = new ARGBType( 0xff000040 );
	
	public VolatileRealTypeARGBConverter()
	{
		super();
	}
	
	public VolatileRealTypeARGBConverter( final double min, final double max )
	{
		super( min, max );
	}
	
	@Override
	public void convert( final VolatileRealType< ? > input, final ARGBType output )
	{
		if ( input.isValid() )
			super.convert( input, output );
		else
			output.set( background );
	}

}
