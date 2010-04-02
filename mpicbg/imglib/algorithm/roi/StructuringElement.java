package mpicbg.imglib.algorithm.roi;

import ij.IJ;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.exception.ImgLibException;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.logic.BitType;

public class StructuringElement extends Image<BitType> {
	
	private final int[] offset;
	
	public StructuringElement(final int[] dimensions, final String name)
	{
		this(new ImageFactory<BitType>(new BitType(), new ArrayContainerFactory()), dimensions, name);
	}
	
	public StructuringElement(final ImageFactory<BitType> factory, final int[] dimensions, final String name)
	{
		super(factory, dimensions, name);
		offset = new int[dimensions.length];
		
		for (int i = 0; i < dimensions.length; ++i)
		{
			offset[i] = dimensions[i] / 2;
		}
	}
	
	public int[] getOffset()
	{
		return offset;
	}
	
	public static StructuringElement createBall(final int nd, final double radius)
	{
		StructuringElement strel;
		LocalizableCursor<BitType> cursor;
		final int[] dims = new int[nd];
		final int[] pos = new int[nd];
		double dist;
		
		for (int i = 0; i < dims.length; ++i)
		{
			dims[i] = (int)(radius * 2 + 1);
		}
		strel = new StructuringElement(dims, "Ball Structure " + nd + "D, " + radius);
		
		cursor = strel.createLocalizableCursor();
		
		while (cursor.hasNext())
		{
			dist = 0;
			cursor.fwd();
			cursor.getPosition(pos);
			for (int i = 0; i < dims.length; ++i)
			{
				dist += Math.pow(pos[i] - strel.offset[i], 2);
			}
			dist = Math.sqrt(dist);
			
			if (dist <= radius)
			{
				cursor.getType().setOne();
			}
			else
			{
				cursor.getType().setZero();
			}
		}
		cursor.close();
		strel.removeCursor(cursor);
		
		return strel;
	}
	
	public static StructuringElement createCube(final int nd, final int length)
	{
		StructuringElement strel;
		Cursor<BitType> cursor;
		final int[] dims = new int[nd];
		for (int i = 0; i < nd; ++i)
		{
			dims[i] = length;
		}
		
		strel = new StructuringElement(dims, "Cube Structure " + length);
		cursor = strel.createCursor(); 
		
		while (cursor.hasNext())
		{
			cursor.fwd();
			cursor.getType().setOne();
		}
		
		cursor.close();
		strel.removeCursor(cursor);
		
		return strel;
	}
	
	public static StructuringElement createBar(int nd, int length, int lengthDim)
	{		
		if (lengthDim >= nd)
		{
			throw new RuntimeException("Invalid bar dimension " + lengthDim + ". Only have " + nd +
					" dimensions.");
		}
		final int [] dims = new int[nd];
		Cursor<BitType> cursor;
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
		cursor = strel.createCursor();
		
		while(cursor.hasNext())
		{
			cursor.fwd();
			cursor.getType().setOne();
		}
		
		cursor.close();
		strel.removeCursor(cursor);
		
		return strel;	
	}

}
