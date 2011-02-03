package mpicbg.imglib.interpolation;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

public abstract class ContainerInterpolatorFactory< T > extends InterpolatorFactory<T, Container<T>>
{
	public ContainerInterpolatorFactory( OutOfBoundsFactory<T, Container<T>> outOfBoundsStrategyFactory )
	{
		super(outOfBoundsStrategyFactory);
	}
}
