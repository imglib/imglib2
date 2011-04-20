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
package mpicbg.imglib.algorithm.math;

import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.Type;

public class ImageCalculatorInPlace <S extends Type<S>, T extends Type<T>> extends ImageCalculator<S, T, S>
{
	public ImageCalculatorInPlace( final Img<S> image1, final Img<T> image2, final Function<S, T, S> function )
	{
		super( image1, image2, image1, function );
	}
}
