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
package mpicbg.imglib.function;

import mpicbg.imglib.type.numeric.IntegerType;

/**
 * Converts an {@link IntegerType} into another one without doing any range checking
 *
 * @author Stephan Preibisch
 *
 * @param <A> - the input {@link IntegerType}
 * @param <B> - the output {@link IntegerType}
 */
public class IntegerTypeConverter< A extends IntegerType<A>, B extends IntegerType<B> > implements Converter<A, B>
{
	@Override
	public void convert( final A input, final B output )
	{
		output.setInteger( input.getIntegerLong() );
	}
}
