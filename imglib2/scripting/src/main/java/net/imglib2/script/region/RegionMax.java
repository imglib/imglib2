package net.imglib2.script.region;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.region.fn.ARegionFn;
import net.imglib2.type.numeric.RealType;

public class RegionMax<T extends RealType<T>> extends ARegionFn<T>
{
	public RegionMax(Img<T> img, RandomAccess<T> ra, long span) {
		super(img, ra, span);
	}

	@Override
	protected final double fnR(final double r, final double a) {
		return Math.max(r, a);
	}
}
