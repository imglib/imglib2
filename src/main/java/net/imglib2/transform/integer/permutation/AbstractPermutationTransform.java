/**
 *
 */
package net.imglib2.transform.integer.permutation;

import net.imglib2.transform.InvertibleTransform;

/**
 * Bijective integer transform mapping between integer coordinates in [0,n-1].
 * Currently, this transform handles only coordinates in the integer range
 * because it is implemented using primitive arrays with integer indices for
 * efficiency reasons.  Expect this permutation to be transferred to long
 * coordinates some time in more distant future.
 *
 * @author Philipp Hanslovsky <hanslovskyp@janelia.hhmi.org>
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public abstract class AbstractPermutationTransform implements InvertibleTransform
{

	final protected int[] lut;

	final protected int[] inverseLut;

	/**
	 * @param lut
	 *            must be a bijective permutation over its index set, i.e. for a
	 *            lut of length n, the sorted content of the array must be
	 *            [0,...,n-1] which is the index set of the lut.
	 * @param numSourceDimensions
	 * @param numTargetDimensions
	 */
	public AbstractPermutationTransform( final int[] lut )
	{
		super();
		this.lut = lut.clone();

		this.inverseLut = new int[ lut.length ];
		for ( int i = 0; i < lut.length; ++i )
			this.inverseLut[ lut[ i ] ] = i;
	}

	public int apply( final int x )
	{
		return this.lut[ x ];
	}

	public long applyChecked( final int x )
	{
		if ( x < 0 )
			return -Long.MAX_VALUE;
		else if ( x >= this.lut.length )
			return Long.MAX_VALUE;
		else
			return this.apply( x );
	}

	public int applyInverse( final int y )
	{
		return this.inverseLut[ y ];
	}

	public long applyInverseChecked( final int y )
	{
		if ( y < 0 )
			return -Long.MAX_VALUE;
		else if ( y >= this.lut.length )
			return Long.MAX_VALUE;
		else
			return this.applyInverse( y );
	}

	public int[] getLutCopy()
	{
		return this.lut.clone();
	}

	public int[] getInverseLutCopy()
	{
		return this.inverseLut.clone();
	}

	final static public boolean checkBijectivity( final int[] lut )
	{
		final int[] inverseLut = new int[ lut.length ];
		try
		{
			for ( int i = 0; i < lut.length; ++i )
				inverseLut[ i ] = -1;
			for ( int i = 0; i < lut.length; ++i )
				inverseLut[ lut[ i ] ] = i;
			for ( int i = 0; i < lut.length; ++i )
				if ( inverseLut[ i ] == -1 )
					return false;
			return true;
		}
		catch ( final ArrayIndexOutOfBoundsException e )
		{
			return false;
		}
	}

}
