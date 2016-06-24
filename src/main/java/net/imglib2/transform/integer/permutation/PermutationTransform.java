/**
 *
 */
package net.imglib2.transform.integer.permutation;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * Bijective integer transform mapping between integer coordinates in [0,n-1].
 *
 * @author Stephan Saalfeld (saalfelds@janelia.hhmi.org)
 * @author Philipp Hanslovsky (hanslovskyp@janelia.hhmi.org)
 */
public class PermutationTransform extends AbstractPermutationTransform
{
	final protected int numSourceDimensions;

	final protected int numTargetDimensions;

	/**
	 *
	 * @param lut
	 *            must be a bijective permutation over its index set, i.e. for a
	 *            lut of length <em>n</em>, the sorted content the array must be
	 *            [0,...,n-1] which is the index set of the lut.
	 * @param numSourceDimensions
	 * @param numTargetDimensions
	 */
	public PermutationTransform( final int[] lut, final int numSourceDimensions, final int numTargetDimensions )
	{
		super( lut );
		this.numSourceDimensions = numSourceDimensions;
		this.numTargetDimensions = numTargetDimensions;

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
		assert source.length >= this.numTargetDimensions && target.length >= this.numTargetDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numTargetDimensions; ++d )
			target[ d ] = this.apply( ( int ) source[ d ] );
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		assert source.length >= this.numTargetDimensions && target.length >= this.numTargetDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numTargetDimensions; ++d )
			target[ d ] = this.apply( this.lut[ source[ d ] ] );
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		assert source.numDimensions() >= this.numTargetDimensions && target.numDimensions() >= this.numTargetDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numTargetDimensions; ++d )
			target.setPosition( this.apply( source.getIntPosition( d ) ), d );
	}

	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		assert source.length >= this.numSourceDimensions && target.length >= this.numSourceDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numSourceDimensions; ++d )
			source[ d ] = this.applyInverse( ( int ) target[ d ] );
	}

	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		assert source.length >= this.numSourceDimensions && target.length >= this.numSourceDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numSourceDimensions; ++d )
			source[ d ] = this.applyInverse( target[ d ] );
	}

	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		assert source.numDimensions() >= this.numSourceDimensions && target.numDimensions() >= this.numSourceDimensions: "Dimensions do not match.";

		for ( int d = 0; d < this.numSourceDimensions; ++d )
			source.setPosition( this.applyInverse( target.getIntPosition( d ) ), d );
	}

	@Override
	public PermutationTransform inverse()
	{
		return new PermutationTransform( this.inverseLut, this.numTargetDimensions, this.numSourceDimensions );
	}

	/**
	 * Test whether a given lut can be applied to an interval. This means that
	 * the interval is a hypercube at min = 0<sup><em>n</em></sup> with size =
	 * lut.length.
	 *
	 * @param interval
	 * @param lut
	 * @return
	 */
	static public boolean checkInterval( final Interval interval, final int[] lut )
	{
		for ( int d = 0; d < interval.numDimensions(); ++d )
		{
			if ( !( interval.min( d ) == 0 && interval.dimension( d ) == lut.length ) )
				return false;
		}
		return true;
	}

}
