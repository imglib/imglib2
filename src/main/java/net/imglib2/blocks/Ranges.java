package net.imglib2.blocks;

import java.util.List;
import java.util.Objects;

/**
 * Find {@link Ranges.Range ranges} for one dimension.
 * <p>
 * Split the requested interval into ranges covering (possibly partial)
 * cells of the input image. The requested interval is given by start
 * coordinate {@code bx} (in the extended source image) and size of the
 * block to copy {@code bw}, in a particular dimension. The full size of the
 * (non-extended) image in this dimension is given by {@code iw}, the size
 * of a (non-truncated) cell in this dimension is given by {@code cw}.
 * <p>
 * Out-of-bounds values are handled depending on {@code Extension} strategy.
 * Use {@link #forExtension(Extension)} to pick a particular strategy.
 */
@FunctionalInterface
interface Ranges
{
	/**
	 * Find ranges for one dimension.
	 * <p>
	 * Split the requested interval into ranges covering (possibly partial)
	 * cells of the input image. The requested interval is given by start
	 * coordinate {@code bx} (in the extended source image) and size of the
	 * block to copy {@code bw}, in a particular dimension. The full size of the
	 * (non-extended) image in this dimension is given by {@code iw}, the size
	 * of a (non-truncated) cell in this dimension is given by {@code cw}.
	 * <p>
	 * Out-of-bounds values are handled depending on {@code Extension} strategy.
	 * Use {@link #forExtension(Extension)} to pick a particular strategy.
	 *
	 * @param bx
	 * 		start of block in source coordinates (in pixels)
	 * @param bw
	 * 		width of block to copy (in pixels)
	 * @param iw
	 * 		source image width (in pixels)
	 * @param cw
	 * 		source cell width (in pixels)
	 */
	List< Range > findRanges( long bx, int bw, long iw, int cw );

	/**
	 *
	 * CONSTANT: Out-of-bounds values are set to a constant.
	 * @param extension
	 * @return
	 */
	static Ranges forExtension( Extension extension )
	{
		switch ( extension.type() )
		{
		case CONSTANT:
			return RangesImpl.FIND_RANGES_CONSTANT;
		case MIRROR_SINGLE:
			return RangesImpl.FIND_RANGES_MIRROR_SINGLE;
		case MIRROR_DOUBLE:
			return RangesImpl.FIND_RANGES_MIRROR_DOUBLE;
		case BORDER:
			return RangesImpl.FIND_RANGES_BORDER;
		default:
			throw new IllegalArgumentException( "Extension type not supported: " + extension.type() );
		}
	}

	// TODO javadoc
	//    FORWARD
	//    BACKWARD
	//    STAY do not move, always take the same input value (used for border extension)
	//    CONSTANT don't take value from input (use constant OOB value instead)
	enum Direction
	{
		FORWARD,
		BACKWARD,
		STAY,
		CONSTANT;
	}

	/**
	 * Instructions for copying along a particular dimension. (To copy an <em>n</em>-dimensional
	 * region, <em>n</em> Ranges are combined.)
	 * <p>
	 * Copy {@code w} elements to coordinates {@code x} through {@code x + w}
	 * (exclusive) in destination, from source cell with {@code gridx} grid
	 * coordinate, starting at coordinate {@code cellx} within cell, and from
	 * there moving in {@code dir} for successive source elements.
	 * <p>
	 * It is guaranteed that all {@code w} elements fall within the same cell
	 * (i.e., primitive array).
	 */
	class Range
	{
		final int gridx;
		final int cellx;
		final int w;
		final Direction dir;
		final int x;

		public Range( final int gridx, final int cellx, final int w, final Direction dir, final int x )
		{
			this.gridx = gridx;
			this.cellx = cellx;
			this.w = w;
			this.dir = dir;
			this.x = x;
		}

		@Override
		public String toString()
		{
			return "Range{gridx=" + gridx + ", cellx=" + cellx + ", w=" + w + ", dir=" + dir + ", x=" + x + '}';
		}

		@Override
		public boolean equals( final Object o )
		{
			if ( this == o )
				return true;
			if ( o == null || getClass() != o.getClass() )
				return false;
			final Range range = ( Range ) o;
			return gridx == range.gridx && cellx == range.cellx && w == range.w && x == range.x && dir == range.dir;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash( gridx, cellx, w, dir, x );
		}
	}
}
