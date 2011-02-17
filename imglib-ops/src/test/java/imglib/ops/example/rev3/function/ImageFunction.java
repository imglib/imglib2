package imglib.ops.example.rev3.function;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

// turn an image into a function we can access
// some thought needs to be given to how multithreading would work with such an approach

public final class ImageFunction implements IntegerIndexedScalarFunction
{
	private final LocalizableByDimCursor<? extends RealType<?>> cursor;
	
	public ImageFunction(Image<? extends RealType<?>> image)  // TODO - OutOfBoundsStrategy too? Or handled by choice of cursor????
	{
		this.cursor = image.createLocalizableByDimCursor();
	}

	@Override
	public double evaluate(int[] position)
	{
		// TODO - setPosition is slower than simple dimensional changes. think of best way to do this
		this.cursor.setPosition(position);
		
		return this.cursor.getType().getRealDouble();
	}
	
	public Image<? extends RealType<?>> getImage()
	{
		return this.cursor.getImage();
	}
}
