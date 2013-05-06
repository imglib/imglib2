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

import ij.ImageJ;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class VolumeRenderer
{
	final static double bg = 0;
	
	static protected < T extends RealType< T > > double accumulate( final RandomAccess< T > poxel, final long min )
	{
		double a = bg;
		while ( poxel.getLongPosition( 2 ) > min )
		{
			final double b = poxel.get().getRealDouble();
			final double alpha = alpha( b );
			a *= 1.0 - alpha;
			a += b * alpha;
			poxel.bck( 2 );
		}
		return a;
	}
	
	static protected < T extends RealType< T > > void render(
			final RandomAccessible< T > volume,
			final RandomAccessibleInterval< T > canvas,
			final long minZ,
			final long maxZ )
	{
		final RandomAccess< T > pixel = canvas.randomAccess( canvas );
		final RandomAccess< T > poxel = volume.randomAccess();
		
		pixel.setPosition( canvas.min( 0 ), 0 );
		pixel.setPosition( canvas.min( 1 ), 0 );
		
		poxel.setPosition( pixel.getLongPosition( 0 ), 0 );
		poxel.setPosition( pixel.getLongPosition( 1 ), 1 );
		poxel.setPosition( maxZ, 2 );
		
		while ( pixel.getLongPosition( 1 ) <= canvas.max( 1 ) )
		{
			pixel.setPosition( canvas.min( 0 ), 0 );
			poxel.setPosition( pixel.getLongPosition( 0 ), 0 );
			while ( pixel.getLongPosition( 0 ) <= canvas.max( 0 ) )
			{
				poxel.setPosition( maxZ, 2 );
				pixel.get().setReal( accumulate( poxel , minZ ) );
				
				pixel.fwd( 0 );
				poxel.fwd( 0 );
			}
			
			pixel.fwd( 1 );
			poxel.fwd( 1 );
		}
	}
	
	final static double alpha( final double intensity )
	{
		//return Math.pow( intensity / 4095.0, 2.0 );
		return intensity / 4095.0;
	}
	
	public static void main( final String[] args ) throws ImgIOException
	{
		final double theta = -Math.PI / 4.0;
		final double cos = Math.cos( theta );
		final double sin = Math.sin( theta );
		
		final String filename = "src/main/resources/l1-cns.tif";
		final ImgPlus< FloatType > img = new ImgOpener().openImg( filename, new ArrayImgFactory< FloatType >(), new FloatType() );
		new ImageJ();
		ImageJFunctions.show( img );
		
		final AffineTransform3D centerShift = new AffineTransform3D();
		centerShift.set(
				1, 0, 0, -img.dimension( 0 ) / 2.0 - img.min( 0 ),
				0, 1, 0, -img.dimension( 1 ) / 2.0 - img.min( 1 ),
				0, 0, 1, -img.dimension( 2 ) / 2.0 - img.min( 2 ) );
		
		System.out.println( centerShift.inverse() );
		
		final AffineTransform3D affine = new AffineTransform3D();
		
		final AffineTransform3D rotation = new AffineTransform3D();
		rotation.set(
				1, 0, 0, 0,
				0, cos, -sin, 0,
				0, sin, cos, 0 );
		
//		final Scale rotation = new Scale( 1.5, 1.5, 1.5 );
		
		affine.concatenate( centerShift.inverse() );
		affine.concatenate( rotation );
		affine.concatenate( centerShift );
		
		final FinalRealInterval bounds = affine.estimateBounds( img );
		final long minZ	= ( long )Math.floor( bounds.realMin( 2 ) );
		final long maxZ	= ( long )Math.ceil( bounds.realMax( 2 ) );
		
		System.out.println( "minZ = " + minZ + "; maxZ = " + maxZ );
		
		final ExtendedRandomAccessibleInterval< FloatType, ImgPlus< FloatType > > extendedImg = Views.extendValue( img, img.firstElement().createVariable() );
		final RealRandomAccessible< FloatType > interpolant = Views.interpolate( extendedImg, new NLinearInterpolatorFactory< FloatType >() );
		final RandomAccessible< FloatType > rotated = RealViews.constantAffine( interpolant, affine );
//		final RandomAccessible< FloatType > rotated = RealViews.transform( interpolant, affine );
		final ArrayImg< FloatType, ? > canvas = ArrayImgs.floats( img.dimension( 0 ), img.dimension( 1 ) );
		
		render( rotated, canvas, minZ, maxZ );
		
		ImageJFunctions.show( canvas );
	}

}
