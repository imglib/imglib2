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
package mpicbg.imglib.algorithm.math.function;

import mpicbg.imglib.type.numeric.RealType;

public class SubtractNormalized<S extends RealType<S>, T extends RealType<T>, U extends RealType<U>> implements Function<S,T,U>
{
	final double normalizationFactor;
	
	public SubtractNormalized( final double normalizationFactor )
	{
		this.normalizationFactor = normalizationFactor;
	}
	
	@Override
	public void compute( final S input1, final T input2, final U output )
	{
		output.setReal( ( input1.getRealDouble() - input2.getRealDouble() ) * normalizationFactor );	
	}

}
