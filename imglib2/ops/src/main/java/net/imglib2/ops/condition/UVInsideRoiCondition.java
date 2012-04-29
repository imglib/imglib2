package net.imglib2.ops.condition;

import net.imglib2.RealRandomAccess;
import net.imglib2.ops.Condition;
import net.imglib2.roi.RegionOfInterest;
import net.imglib2.type.logic.BitType;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class UVInsideRoiCondition implements Condition<long[]> {

	private final RegionOfInterest roi;
	private final RealRandomAccess<BitType> accessor;
	
	public UVInsideRoiCondition(RegionOfInterest roi) {
		this.roi = roi;
		this.accessor = roi.realRandomAccess();
	}
	
	@Override
	public boolean isTrue(long[] val) {
		accessor.setPosition(val[0],0); // U == index 0
		accessor.setPosition(val[1],1); // V == index 1
		return accessor.get().get();
	}

	@Override
	public UVInsideRoiCondition copy() {
		return new UVInsideRoiCondition(roi);
	}
	
}
