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
package render.volume;

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBDoubleType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.composite.RealComposite;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1a
 */
public class RealCompositeARGBDoubleConverter< T extends RealType< T > > implements Converter< RealComposite< T >, ARGBDoubleType >
{
	final protected ARGBDoubleType[] argbs;
	
	public RealCompositeARGBDoubleConverter( final int length )
	{
		argbs = new ARGBDoubleType[ length ];
		for ( int i = 0; i < length; ++i )
			argbs[ i ] = new ARGBDoubleType( 1.0, 1.0, 1.0, 1.0 );
	}
	
	public void setARGB( final ARGBDoubleType argb, final int i )
	{
		argbs[ i ].set( argb );
	}
	
	@Override
	public void convert( final RealComposite< T > input, final ARGBDoubleType output )
	{
		output.setZero();
		for ( int i = 0; i < argbs.length; ++i )
		{
			final double t = input.get( i ).getRealDouble();
			final ARGBDoubleType c = argbs[ i ];
			final double a = c.getA();
			final double r = output.getR() + a * c.getR() * t;
			final double g = output.getG() + a * c.getG() * t;
			final double b = output.getB() + a * c.getB() * t;
			
			output.setR( r );
			output.setG( g );
			output.setB( b );
		}
		
		final double r = Math.max( 0, Math.min( 1.0, output.getR() ) );
		final double g = Math.max( 0, Math.min( 1.0, output.getG() ) );
		final double b = Math.max( 0, Math.min( 1.0, output.getB() ) );
		
		//final double a = ( r + g + b ) / 3.0;
		final double a = Math.max( r, Math.max( g, b ) );
		
		output.set( a, r, g, b );
	}

}
