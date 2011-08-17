package net.imglib2.ops.image;

import net.imglib2.ops.Real;

public class RealImage extends AbstractImage {
	private double[] realData;
	
	public RealImage(long[] dims, String[] axes) {
		super(dims, axes);
		long totalElements = totalElements(dims);
		if (totalElements > Integer.MAX_VALUE)
			throw new IllegalArgumentException("image dimensions too large");
		this.realData = new double[(int)totalElements];
	}
	
	public void getReal(long[] index, Real r) {
		r.setReal(realData[elementNumber(index)]);
	}
	
	public void setReal(long[] index, Real r) {
		realData[elementNumber(index)] = r.getReal();
	}
	
}
