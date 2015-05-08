/**
 * 
 */
package net.imglib2.transform.integer.shear;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.transform.integer.BoundingBox;

/**
 * Forward implementation of the most simple case of a shear transform:
 * coordinate[ shearDimension ] += coordinate[ referenceDimension ]
 * 
 * @author Philipp Hanslovsky <hanslovskyp@janelia.hhmi.org>
 *
 */
public class ShearTransform extends AbstractShearTransform
{
	

	/**
	 * @param nDim					Number of dimensions (source and target dimensions must be the same)
	 * @param shearDimension		Dimension to be sheared.
	 * @param referenceDimension	Dimension used as reference for shear.
	 */
	public ShearTransform(
			int nDim, 
			int shearDimension,
			int referenceDimension ) 
	{
		super( nDim, shearDimension, referenceDimension );
		this.inverse = new InverseShearTransform( 
				nDim, 
				shearDimension,
				referenceDimension,
				this );
	}
	
	
	/**
	 * Protected constructor for passing an inverse to avoid construction of unnecessary objects.
	 * 
	 * @param nDim					Number of dimensions (source and target dimensions must be the same)
	 * @param shearDimension		Dimension to be sheared.
	 * @param referenceDimension	Dimension used as reference for shear.
	 * @param inverse				
	 */
	protected ShearTransform( 
			int nDim, 
			int shearDimension,
			int referenceDimension, 
			AbstractShearTransform inverse ) 
	{
		super( nDim, shearDimension, referenceDimension, inverse );
	}


	@Override
	public void apply( long[] source, long[] target ) 
	{
		assert source.length >= this.nDim && target.length >= nDim;
		System.arraycopy( source, 0, target, 0, source.length );
		target[ shearDimension ] += target[ referenceDimension ];
	}
	

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length >= this.nDim && target.length >= nDim;
		System.arraycopy( source, 0, target, 0, source.length );
		target[ shearDimension ] += target[ referenceDimension ];
	}
	

	@Override
	public void apply( Localizable source, Positionable target ) 
	{
		assert source.numDimensions() >= this.nDim && target.numDimensions() >= nDim;
		target.setPosition( source );
		target.setPosition( source.getLongPosition( shearDimension ) + source.getLongPosition( referenceDimension ), shearDimension );
	}
	

	@Override
	public ShearTransform copy()
	{
//		do just return this; ?
		return new ShearTransform( this.nDim, this.shearDimension, this.referenceDimension );
	}


	@Override
	public long[] getShear()
	{
		long[] shear = new long[ this.nDim ];
		shear[ this.shearDimension ] = 1;
		return shear;
	}
	
	
	@Override public BoundingBox transform( BoundingBox bb )
	{
		bb.orderMinMax();
		long[] c = bb.corner2;
		// assume that corner2[ i ] > corner1[ i ]
		long diff = c[ this.referenceDimension ] - bb.corner1[ this.referenceDimension ];
		c[ this.shearDimension ] +=diff;
		return bb;
	}
	

}
