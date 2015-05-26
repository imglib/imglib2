/**
 *
 */
package net.imglib2.transform.integer.permutation;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * @author Philipp Hanslovsky <hanslovskyp@janelia.hhmi.org>
 *
 *         Apply bijective permutation to one coordinate axis only.
 *
 */
public class SingleDimensionPermutationTransform extends AbstractPermutationTransform
{

	final protected int numSourceDimensions;

	final protected int numTargetDimensions;

	final protected int d;

	/**
	 * @param lut
	 * @param numSourceDimensions
	 *            dimensionality of source
	 * @param numTargetDimensions
	 *            dimensionality of target
	 * @param d
	 *            dimension which shall be transformed. Must be smaller than
	 *            {@link #numSourceDimensions} and {@link #numTargetDimensions}
	 */
	public SingleDimensionPermutationTransform(
			final int[] lut,
			final int numSourceDimensions,
			final int numTargetDimensions,
			final int d )
	{
		super( lut );
		assert d > 0 && d < numTargetDimensions && d < numSourceDimensions;
		this.numSourceDimensions = numSourceDimensions;
		this.numTargetDimensions = numTargetDimensions;
		this.d = d;
	}

	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		System.arraycopy( target, 0, source, 0, this.numSourceDimensions );
		source[ this.d ] = this.applyInverse( ( int ) target[ this.d ] );
	}

	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		System.arraycopy( target, 0, source, 0, this.numSourceDimensions );
		source[ this.d ] = this.applyInverse( target[ this.d ] );
	}

	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		source.setPosition( target );
		source.setPosition( this.applyInverse( target.getIntPosition( this.d ) ), this.d );
	}

	@Override
	public SingleDimensionPermutationTransform inverse()
	{
		return new SingleDimensionPermutationTransform( this.inverseLut, this.numSourceDimensions, this.numTargetDimensions, this.d );
	}

	@Override
	public int numSourceDimensions()
	{
		return this.numSourceDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return this.numTargetDimensions;
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		System.arraycopy( source, 0, target, 0, this.numTargetDimensions );
		target[ this.d ] = this.apply( ( int ) source[ this.d ] );
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		System.arraycopy( source, 0, target, 0, this.numTargetDimensions );
		target[ this.d ] = this.apply( source[ this.d ] );
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		target.setPosition( source );
		target.setPosition( this.apply( source.getIntPosition( this.d ) ), this.d );
	}

}
