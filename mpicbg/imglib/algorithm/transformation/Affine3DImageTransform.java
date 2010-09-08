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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.algorithm.transformation;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.type.Type;
import mpicbg.models.AffineModel3D;

public class Affine3DImageTransform<T extends Type<T>> implements OutputAlgorithm<T>
{
	final Image<T> img;
	final int numDimensions;
	final Transform3D transform;
	final InterpolatorFactory<T> interpolatorFactory;
	final float[] location;
	final int[] offset;
	
	Image<T> transformed;
	String errorMessage = "";
	
	public Affine3DImageTransform( final Image<T> img, final Transform3D transform, final InterpolatorFactory<T> interpolatorFactory )
	{
		this.img = img;
		this.interpolatorFactory = interpolatorFactory;
		this.numDimensions = img.numDimensions();
		this.location = new float[ numDimensions ];
		this.offset = new int[ numDimensions ];
		
		if ( numDimensions != 3 )
		{
			errorMessage = "A Transform3D is not suitable for a " + img.numDimensions() + "-dimensional image.";
			this.transform = null;
		}
		else
		{
			this.transform = transform;			
		}
	}

	public Affine3DImageTransform( final Image<T> img, final AffineModel3D transform, final InterpolatorFactory<T> interpolatorFactory )
	{
		this.img = img;
		this.interpolatorFactory = interpolatorFactory;
		this.numDimensions = img.numDimensions();
		this.location = new float[ numDimensions ];
		this.offset = new int[ numDimensions ];
		
		if ( numDimensions != 3 )
		{
			errorMessage = "A Transform3D is not suitable for a " + img.numDimensions() + "-dimensional image.";
			this.transform = null;
		}
		else
		{
			this.transform = MathLib.getTransform3D( transform );			
		}
	}

	public Affine3DImageTransform( final Image<T> img, final float[] transform, final InterpolatorFactory<T> interpolatorFactory )
	{
		this.img = img;
		this.interpolatorFactory = interpolatorFactory;
		this.numDimensions = img.numDimensions();
		this.location = new float[ numDimensions ];
		this.offset = new int[ numDimensions ];
		
		if ( transform == null )
		{
			this.transform = null;
		}
		else if ( transform.length != (img.numDimensions()+1) * (img.numDimensions()+1) )
		{
			this.transform = null;
			errorMessage = "AffineTransform: [float[] transform] has a length of " + transform.length + ", should be " + 
						   ((img.numDimensions()+1) * (img.numDimensions()+1)) +" (" + (img.numDimensions()+1) + "x" + (img.numDimensions()+1) + ")" +
						   " for a " + img.numDimensions() + "-dimensional image.";
		}
		else if ( numDimensions == 3)
		{
			this.transform = new Transform3D( transform );
		}
		else
		{
			errorMessage = "Only 3D-Transforms are supported yet but this is a " + img.numDimensions() + "-dimensional image.";
			this.transform = null;
		}
	}
	
	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( img == null )
		{
			errorMessage = "AffineTransform: [Image<T> img] is null.";
			return false;
		}
		else if ( interpolatorFactory.getOutOfBoundsStrategyFactory() == null )
		{
			errorMessage = "AffineTransform: [OutOfBoundsStrategyFactory<T> of interpolatorFactory] is null.";
			return false;
		}
		else if ( interpolatorFactory == null )
		{
			errorMessage = "AffineTransform: [InterpolatorFactory<T> interpolatorFactory] is null.";
			return false;
		}
		else if ( transform == null )
		{
			errorMessage = "AffineTransform: [Transform3D transform] or [float[] transform] is null.";
			return false;
		}
		else
			return true;
	}

	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public Image<T> getResult() { return transformed; }
	
	/**
	 * Returns the translational offset applied so that the image data starts at (0,0,0)
	 * @return - int[] offset
	 */
	public int[] getOffset() { return offset; }

	@Override
	public boolean process()
	{
		if ( !checkInput() )
			return false;
		
		// we do not want to change the original affine transformation matrix
		final Transform3D trans = new Transform3D();
		trans.set(transform);

		// get image dimensions
		final int width = img.getDimension( 0 );
		final int height = img.getDimension( 1 );
		final int depth = img.getDimension( 2 );

		//
		// first determine new min-max in x,y,z of the image
		// by transforming all the corner-points
		//
		final int maxDimX = width;// - 1;
		final int maxDimY = height;// - 1;
		final int maxDimZ = depth;// - 1;

		final Point3d points[] = new Point3d[8];
		points[0] = new Point3d(0, 0, 0);
		points[1] = new Point3d(maxDimX, 0, 0);
		points[2] = new Point3d(maxDimX, maxDimY, 0);
		points[3] = new Point3d(0, maxDimY, 0);

		points[4] = new Point3d(0, 0, maxDimZ);
		points[5] = new Point3d(maxDimX, 0, maxDimZ);
		points[6] = new Point3d(maxDimX, maxDimY, maxDimZ);
		points[7] = new Point3d(0, maxDimY, maxDimZ);

		//adaptPoints(points);

		// transform the points
		for (final Point3d point : points)
			trans.transform(point);

		//adaptPoints(points);

		// get min and max
		int minX = Integer.MAX_VALUE;
		int maxX = -Integer.MAX_VALUE;

		int minY = Integer.MAX_VALUE;
		int maxY = -Integer.MAX_VALUE;

		int minZ = Integer.MAX_VALUE;
		int maxZ = -Integer.MAX_VALUE;

		for (final Point3d point : points)
		{
			if (Math.round(point.x) < minX) minX = (int)Math.round(point.x);
			if (Math.round(point.y) < minY) minY = (int)Math.round(point.y);
			if (Math.round(point.z) < minZ) minZ = (int)Math.round(point.z);

			if (Math.round(point.x) > maxX) maxX = (int)Math.round(point.x);
			if (Math.round(point.y) > maxY) maxY = (int)Math.round(point.y);
			if (Math.round(point.z) > maxZ) maxZ = (int)Math.round(point.z);
		}

		final int dimX = maxX - minX;
		final int dimY = maxY - minY;
		final int dimZ = maxZ - minZ;
		
		offset[ 0 ] = minX;
		offset[ 1 ] = minY;
		offset[ 2 ] = minZ;

		transformed = img.createNewImage( new int[] {dimX, dimY, dimZ} );

		// in order to compute the voxels in the new object we have to apply
		// the inverse transform to all voxels of the new array and interpolate
		// the position in the original image
		trans.invert();

		/*
		if ( Array.class.isInstance( img.getContainer() ))
		{
			final Point3f loc1 = new Point3f(0,0,0);
			final Point3f loc2 = new Point3f(0,0,0);
			final Point3f tmpX = new Point3f(0,0,0);
			final Point3f tmpY = new Point3f(0,0,0);
			
			final Point3f vectorX = new Point3f();
			final Point3f vectorY = new Point3f();
			final Point3f vectorZ = new Point3f();
			
			loc1.x = 0;
			loc1.y = 0;
			loc1.z = 0;
			
			trans.transform( loc1 );			

			loc2.x = dimX;
			loc2.y = 0;
			loc2.z = 0;
			
			trans.transform( loc2 );
			
			vectorX.sub( loc2, loc1 );

			loc1.x = 0;
			loc1.y = 0;
			loc1.z = 0;
			
			trans.transform( loc1 );

			loc2.x = 0;
			loc2.y = dimY;
			loc2.z = 0;
			
			trans.transform( loc2 );
			
			vectorY.sub( loc2, loc1 );

			loc1.x = 0;
			loc1.y = 0;
			loc1.z = 0;
			
			trans.transform( loc1 );

			loc2.x = 0;
			loc2.y = 0;
			loc2.z = dimZ;
			
			trans.transform( loc2 );
			
			vectorZ.sub( loc2, loc1 );
						
			vectorX.scale( 1.0f / dimX );
			vectorY.scale( 1.0f / dimY );
			vectorZ.scale( 1.0f / dimZ );
			
			final Interpolator<T> interpolator = img.createInterpolator( interpolatorFactory );
			final Cursor<T> transformedIterator = transformed.createCursor();
			final T transformedValue = transformedIterator.getType();
			
			loc1.x = minX;
			loc1.y = minY;
			loc1.z = minZ;
			
			trans.transform( loc1 );
			
			for ( int z = 0; z < dimZ; ++z )
			{
				tmpY.x = loc1.x;
				tmpY.y = loc1.y;
				tmpY.z = loc1.z;
				
				for ( int y = 0; y < dimY; ++y )
				{
					tmpX.x = loc1.x;
					tmpX.y = loc1.y;
					tmpX.z = loc1.z;
					
					for ( int x = 0; x < dimX; ++x )
					{
						transformedIterator.fwd();
						
						loc1.add( vectorX );
						
						location[ 0 ] = loc1.x;
						location[ 1 ] = loc1.y;
						location[ 2 ] = loc1.z;
			
						interpolator.moveTo( location );
				
						transformedValue.set( interpolator.getType() );					
					}
					
					loc1.x = tmpX.x;
					loc1.y = tmpX.y;
					loc1.z = tmpX.z;
					
					loc1.add( vectorY );
				}
				
				loc1.x = tmpY.x;
				loc1.y = tmpY.y;
				loc1.z = tmpY.z;		
				
				loc1.add( vectorZ );
			}
			
		}
		else*/
		{	
			final Point3f loc = new Point3f(0,0,0);
			
			final RasterIterator<T> transformedIterator = transformed.createLocalizingRasterIterator();
			final Interpolator<T> interpolator = img.createInterpolator( interpolatorFactory );
	
			//final T transformedValue = transformedIterator.getType();		
			//final T interpolatedValue = interpolator.getType();
				
			int c = 0;
			
			while (transformedIterator.hasNext())
			{
				transformedIterator.fwd();
	
				// we have to add the offset of our new image
				// relative to it's starting point (0,0,0)
				loc.x = transformedIterator.getIntPosition( 0 ) + minX;
				loc.y = transformedIterator.getIntPosition( 1 ) + minY;
				loc.z = transformedIterator.getIntPosition( 2 ) + minZ;			
	
				// transform back into the original image
				trans.transform( loc );
	
				// take care for the coordinate offsets
				//adaptPoint( loc );
				
				location[ 0 ] = loc.x;
				location[ 1 ] = loc.y;
				location[ 2 ] = loc.z;

				interpolator.moveTo( location );				
				//interpolator.setPosition( location );
				
				transformedIterator.type().set( interpolator.type() );
				
				++c;
			}
		}
		return true;
	}
	/*
	protected void adaptPoints(final Point3d[] points)
	{
		for (final Point3d point : points)
			adaptPoint(point);
	}

	protected void adaptPoints(final Point3f[] points)
	{
		for (final Point3f point : points)
			adaptPoint(point);
	}

	protected void adaptPoint(final Point3d point)
	{
		point.y = -point.y;
		point.z = -point.z;
	}

	protected void adaptPoint(final Point3f point)
	{
		point.y = -point.y;
		point.z = -point.z;
	}
	*/
}
