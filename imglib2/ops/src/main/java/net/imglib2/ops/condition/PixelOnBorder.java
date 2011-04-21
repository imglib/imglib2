package net.imglib2.ops.condition;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.img.Img;

public class PixelOnBorder<T extends RealType<T>> implements Condition<T>
{
	private final double borderValue;
	private final long[] imageDims;
	private final long[] neighPos;
	private final RandomAccess<T> neighAccessor;
	
	public PixelOnBorder(final Img<T> image, final double borderValue)
	{
		this.borderValue = borderValue;
		this.imageDims = new long[image.numDimensions()];
		image.dimensions(this.imageDims);
		this.neighPos = new long[image.numDimensions()];
		this.neighAccessor = image.randomAccess();
	}
	
	@Override
	public boolean isSatisfied(final Cursor<T> cursor, final long[] position)
	{
		double currPixValue = cursor.get().getRealDouble();
		
		if (currPixValue != borderValue)
			return false;
		
		// look left
		if (position[0] > 0)
		{
			this.neighPos[0] = position[0]-1;
			this.neighPos[1] = position[1];
			this.neighAccessor.setPosition(this.neighPos);
			if (this.neighAccessor.get().getRealDouble() != borderValue)
				return true;
		}

		// look right
		if (position[0] < this.imageDims[0]-1)
		{
			this.neighPos[0] = position[0]+1;
			this.neighPos[1] = position[1];
			this.neighAccessor.setPosition(this.neighPos);
			if (this.neighAccessor.get().getRealDouble() != borderValue)
				return true;
		}

		// look up
		if (position[1] > 0)
		{
			this.neighPos[0] = position[0];
			this.neighPos[1] = position[1]-1;
			this.neighAccessor.setPosition(this.neighPos);
			if (this.neighAccessor.get().getRealDouble() != borderValue)
				return true;
		}

		// look down
		if (position[1] < this.imageDims[1]-1)
		{
			this.neighPos[0] = position[0];
			this.neighPos[1] = position[1]+1;
			this.neighAccessor.setPosition(this.neighPos);
			if (this.neighAccessor.get().getRealDouble() != borderValue)
				return true;
		}

		return false;
	}

}
