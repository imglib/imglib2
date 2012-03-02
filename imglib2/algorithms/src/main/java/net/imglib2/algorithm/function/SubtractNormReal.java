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

import net.imglib2.type.numeric.RealType;

public class SubtractNormReal< A extends RealType<A>, B extends RealType<B>, C extends RealType<C> > implements Function< A, B, C >
{
	final double normalizationFactor;
	
	public SubtractNormReal( final double normalizationFactor )
	{
		this.normalizationFactor = normalizationFactor;
	}
	
	@Override
	public void compute( final A input1, final B input2, final C output )
	{
		output.setReal( ( input1.getRealDouble() - input2.getRealDouble() ) * normalizationFactor );	
	}

}
