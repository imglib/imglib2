package net.imglib2.script.region;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.region.fn.ARegionFn;
import net.imglib2.type.numeric.RealType;

public class RegionMean<T extends RealType<T>> extends ARegionFn<T>
{
	private final double size;
	
	public RegionMean(Img<T> img, RandomAccess<T> ra, long span) {
		super(img, ra, span);
		this.size = img.size();
	}

	@Override
	protected final double fn0(final double a) {
		return a / size;
	}
	
	@Override
	protected final double fnE(final double r) {
		return r;
	}

	@Override
	protected final double fnR(final double r, final double a) {
		return r + a / size;
	}
}
