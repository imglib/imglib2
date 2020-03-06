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
package net.imglib2.interpolation.randomaccess;

import org.junit.Test;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import ij.ImageJ;
import net.imglib2.FinalInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

/**
 *
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 */
public class CardinalBSplineInterpolatorTest
{
	@Test
	public final static void main( final String... args)
	{
//		final FunctionRandomAccessible<UnsignedByteType> img = new FunctionRandomAccessible<>(
//				2,
//				( a, b ) -> {
//					final long x = a.getLongPosition( 0 );
//					final long y = a.getLongPosition( 1 );
//					final boolean xOn = (x >> 1) * 2 == x;
//					final boolean yOn = (y >> 1) * 2 == y;
//					b.setReal(xOn ^ yOn ? 255 : 0);
//				},
//				UnsignedByteType::new);

		final FunctionRandomAccessible<UnsignedByteType> img = new FunctionRandomAccessible<>(
				2,
				( a, b ) -> {
					final long x = a.getLongPosition( 0 );
					final long y = a.getLongPosition( 1 );
					b.setReal(x == 10 && y == 10 ? 255 : 0);
				},
				UnsignedByteType::new);



		final BSplineInterpolatorFactory<UnsignedByteType> factory = new BSplineInterpolatorFactory<>( 3, false );

		final FinalInterval interval = new FinalInterval( 200, 100 );

		final ArrayImg< UnsignedByteType, ByteArray > array = ArrayImgs.unsignedBytes( 200, 100 );

		LoopBuilder.setImages( Views.interval(img, interval), array ).forEachPixel( (a, b ) -> b.set( a ) );

		final RealRandomAccessible< UnsignedByteType > imgInterp = Views.interpolate( Views.extendMirrorSingle( array ), factory );

		new ImageJ();

		ImageJFunctions.show( Views.interval( Views.raster( imgInterp ), interval ) );

		// show original image
		final BdvOptions opts = BdvOptions.options().is2D();
		final BdvStackSource< UnsignedByteType > bdv = BdvFunctions.show(img, interval, "img", opts);

		// show interpolated image
		BdvFunctions.show( imgInterp, interval, "img interp", opts.addTo( bdv ));
	}

}
