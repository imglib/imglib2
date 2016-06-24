/**
 * 
 */
package net.imglib2.transform.integer.shear;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.transform.InvertibleTransform;
import net.imglib2.transform.integer.BoundingBoxTransform;

/**
 * 
 * Most simple case of a shear transform that just implements
 * for a integer valued coordinate:
 * coordinate[ shearDimension ] += coordinate[ referenceDimension ] (forward)
 * coordinate[ shearDimension ] -= coordinate[ referenceDimension ] (backward)
 * 
 * This abstract class holds the inverse and implements applyInverse in
 * terms of inverse.apply
 * 
 * @author Philipp Hanslovsky (hanslovskyp@janelia.hhmi.org)
 *
 */
public abstract class AbstractShearTransform implements InvertibleTransform, BoundingBoxTransform
{
	
	
	protected final int nDim;
	protected final int shearDimension;
	protected final int referenceDimension;
	
	protected AbstractShearTransform inverse;
	
	
	protected AbstractShearTransform(
			int nDim,
			int shearDimension,
			int referenceDimension )
	{
		this( nDim, shearDimension, referenceDimension, null );
	}
	
	
	protected AbstractShearTransform(
			int nDim,
			int shearDimension,
			int referenceDimension,
			AbstractShearTransform inverse )
	{
		this.nDim               = nDim;
		this.shearDimension     = shearDimension;
		this.referenceDimension = referenceDimension;
		this.inverse            = inverse;
	}
	
	
	public int getReferenceDimension() {
		return referenceDimension;
	}


	@Override
	public int numSourceDimensions() 
	{
		return this.numDimensions();
	}
	

	@Override
	public int numTargetDimensions() 
	{
		return this.numDimensions();
	}
	
	
	public int numDimensions() 
	{
		return nDim;
	}
	
	
	public int getShearDimension()
	{
		return this.shearDimension;
	}
	

	@Override
	public void applyInverse( long[] source, long[] target ) 
	{
		// for inverse, source and target need to be switched
		this.inverse.apply( target, source );
	}
	

	@Override
	public void applyInverse(int[] source, int[] target) 
	{
		// for inverse, source and target need to be switched
		this.inverse.apply( target, source );
	}
	

	@Override
	public void applyInverse( Positionable source, Localizable target ) 
	{
		// for inverse, source and target need to be switched
		this.inverse.apply( target, source );
	}
	

	@Override
	public AbstractShearTransform inverse() 
	{
		return this.inverse;
	}
	
	
	public abstract long[] getShear();

	
	public abstract AbstractShearTransform copy();
	
	
}
