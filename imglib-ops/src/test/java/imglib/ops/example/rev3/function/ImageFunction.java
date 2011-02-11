package imglib.ops.example.rev3.function;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

// turn an image into a function we can access
// some thought needs to be given to how multithreading would work with such an approach

public class ImageFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	private LocalizableByDimCursor<T> cursor;
	
	public ImageFunction(Image<T> image)  // TODO - OutOfBoundsStrategy too? Or handled by choice of cursor????
	{
		this.cursor = image.createLocalizableByDimCursor();
	}

	@Override
	public void evaluate(int[] position, T output)
	{
		// TODO - setPosition is slower than simple dimensional changes. think of best way to do this
		this.cursor.setPosition(position);
		
		double sampleValue = this.cursor.getType().getRealDouble();
		
		output.setReal(sampleValue);
	}
	
	@Override
	public T createVariable()
	{
		return this.cursor.getType().createVariable();
	}
	
	public Image<T> getImage()
	{
		return this.cursor.getImage();
	}
}
