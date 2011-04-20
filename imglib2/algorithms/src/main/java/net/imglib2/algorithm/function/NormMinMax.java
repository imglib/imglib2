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
 * @author Stephan Preibisch
 */
package net.imglib2.algorithm.function;

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;

public class NormMinMax< A extends RealType<A> > implements Converter< A, A >
{
	final double min, tmp;
	
	public NormMinMax( final double min, final double max )
	{
		this.min = min;
		this.tmp = max - min;
	}
	
	@Override
	public void convert( final A input, final A output )
	{
		output.setReal( (input.getRealDouble() - min) / tmp );	
	}

}
