package net.imglib2.ops.example.rev3.function;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

// turn an image into a function we can access
// some thought needs to be given to how multithreading would work with such an approach

public final class ImageFunction implements IntegerIndexedScalarFunction
{
	private final Img<? extends RealType<?>> img;
	private final RandomAccess<? extends RealType<?>> accessor;
	
	public ImageFunction(Img<? extends RealType<?>> image)  // TODO - OutOfBoundsStrategy too? Or handled by choice of cursor????
	{
		this.img = image;
		this.accessor = image.randomAccess();
	}

	@Override
	public double evaluate(long[] position)
	{
		// TODO - setPosition is slower than simple dimensional changes. think of best way to do this
		this.accessor.setPosition(position);
		
		return this.accessor.get().getRealDouble();
	}
	
	public Img<? extends RealType<?>> getImg()
	{
		return this.img;
	}
}
