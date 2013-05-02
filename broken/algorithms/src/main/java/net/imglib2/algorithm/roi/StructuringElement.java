
package net.imglib2.algorithm.roi;

import net.imglib2.img.ImgCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.logic.BitType;

/**
 * TODO
 *
 */
public class StructuringElement extends ArrayImg<BitType,BitAccess> {
	
	private final long[] offset;
	private String name;
	
	static private final int sizeOf(final long[] dim) {
		long a = dim[0];
		for (int i=1; i<dim.length; i++) a *= dim[i];
		return (int) a;
	}
	
	public StructuringElement(final long[] dimensions, final String name)
	{
		super(new BitArray(sizeOf(dimensions)), dimensions, 1);
		this.name = name;
		offset = new long[dimensions.length];
		
		for (int i = 0; i < dimensions.length; ++i)
		{
			offset[i] = dimensions[i] / 2;
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public long[] getOffset()
	{
		return offset;
	}
	
	public static StructuringElement createBall(final int nd, final double radius)
	{
		StructuringElement strel;
		ImgCursor<BitType> cursor;
		final long[] dims = new long[nd];
		final long[] pos = new long[nd];
		double dist;
		
		for (int i = 0; i < dims.length; ++i)
		{
			dims[i] = (int)(radius * 2 + 1);
		}
		strel = new StructuringElement(dims, "Ball Structure " + nd + "D, " + radius);
		
		cursor = strel.cursor();
		
		while (cursor.hasNext())
		{
			dist = 0;
			cursor.fwd();
			cursor.localize(pos);
			for (int i = 0; i < dims.length; ++i)
			{
				dist += Math.pow(pos[i] - strel.offset[i], 2);
			}
			dist = Math.sqrt(dist);
			
			if (dist <= radius)
			{
				cursor.get().setOne();
			}
			else
			{
				cursor.get().setZero();
			}
		}
		
		return strel;
	}
	
	public static StructuringElement createCube(final int nd, final int length)
	{
		StructuringElement strel;
		ImgCursor<BitType> cursor;
		final long[] dims = new long[nd];
		for (int i = 0; i < nd; ++i)
		{
			dims[i] = length;
		}
		
		strel = new StructuringElement(dims, "Cube Structure " + length);
		cursor = strel.cursor(); 
		
		while (cursor.hasNext())
		{
			cursor.fwd();
			cursor.get().setOne();
		}
		
		return strel;
	}
	
	public static StructuringElement createBar(int nd, int length, int lengthDim)
	{		
		if (lengthDim >= nd)
		{
			throw new RuntimeException("Invalid bar dimension " + lengthDim + ". Only have " + nd +
					" dimensions.");
		}
		final long[] dims = new long[nd];
		ImgCursor<BitType> cursor;
		StructuringElement strel;
		
		for (int i = 0; i < nd; ++i)
		{
			if (i == lengthDim)
			{
				dims[i] = length;
			}
			else
			{
				dims[i] = 1;
			}
		}
		
		strel = new StructuringElement(dims, "Bar " + lengthDim + " of " + nd + ", " + length);
		cursor = strel.cursor();
		
		while(cursor.hasNext())
		{
			cursor.fwd();
			cursor.get().setOne();
		}
		
		return strel;	
	}

}
