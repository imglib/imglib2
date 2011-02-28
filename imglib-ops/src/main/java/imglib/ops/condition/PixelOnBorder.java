package imglib.ops.condition;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class PixelOnBorder<T extends RealType<T>> implements Condition<T>
{
	private final double borderValue;
	private final int[] imageDims;
	private final int[] neighPos;
	private final LocalizableByDimCursor<T> neighCursor;
	
	public PixelOnBorder(final Image<T> image, final double borderValue)
	{
		this.borderValue = borderValue;
		this.imageDims = image.getDimensions();
		this.neighPos = image.createPositionArray();
		this.neighCursor = image.createLocalizableByDimCursor();
	}
	
	@Override
	public boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position)
	{
		double currPixValue = cursor.getType().getRealDouble();
		
		if (currPixValue != borderValue)
			return false;
		
		// look left
		if (position[0] > 0)
		{
			this.neighPos[0] = position[0]-1;
			this.neighPos[1] = position[1];
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		// look right
		if (position[0] < this.imageDims[0]-1)
		{
			this.neighPos[0] = position[0]+1;
			this.neighPos[1] = position[1];
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		// look up
		if (position[1] > 0)
		{
			this.neighPos[0] = position[0];
			this.neighPos[1] = position[1]-1;
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		// look down
		if (position[1] < this.imageDims[1]-1)
		{
			this.neighPos[0] = position[0];
			this.neighPos[1] = position[1]+1;
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		return false;
	}

}
