package net.imglib2.script.region;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.region.fn.ARegionFn;
import net.imglib2.type.numeric.RealType;

public class RegionMin<T extends RealType<T>> extends ARegionFn<T>
{
	public RegionMin(Img<T> img, RandomAccess<T> ra, long span) {
		super(img, ra, span);
	}

	@Override
	protected final double fnR(final double r, final double a) {
		return Math.min(r, a);
	}
}
