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
 * An execption is the 1D FFT implementation of Dave Hale which we use as a
 * library, wich is released under the terms of the Common Public License -
 * v1.0, which is available at http://www.eclipse.org/legal/cpl-v10.html
 *
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 */
import ij.ImageJ;
import mpicbg.util.RealSum;
import net.imglib2.algorithm.fft.FourierConvolution;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Perform a gaussian convolution using fourier convolution
 */
public class Example6b
{
	public Example6b() throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );
		Img< FloatType > kernel = new ImgOpener().openImg( "kernelGauss.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// normalize the kernel, otherwise we add energy to the image
		norm( kernel );

		// display image & kernel
		ImageJFunctions.show( kernel ).setTitle( "kernel" );
		ImageJFunctions.show( image ).setTitle( "drosophila wing");

		// compute & show fourier convolution
		ImageJFunctions.show( FourierConvolution.convolve( image, kernel ) )
			.setTitle( "convolution" );
	}

	/**
	 * Computes the sum of all pixels in an iterable using RealSum
	 *
	 * @param iterable - the image data
	 * @return - the sum of values
	 */
	public static < T extends RealType< T > > double sumImage( final Iterable< T > iterable )
	{
		final RealSum sum = new RealSum();

		for ( final T type : iterable )
			sum.add( type.getRealDouble() );

		return sum.getSum();
	}

	/**
	 * Norms all image values so that their sum is 1
	 *
	 * @param iterable - the image data
	 */
	public static void norm( final Iterable< FloatType > iterable )
	{
		final double sum = sumImage( iterable );

		for ( final FloatType type : iterable )
			type.setReal( type.get() / sum );
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6b();
	}
}
