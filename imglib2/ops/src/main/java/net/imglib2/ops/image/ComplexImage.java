package net.imglib2.ops.image;

import net.imglib2.ops.Complex;

public class ComplexImage extends AbstractImage {
	private double[] realData;
	private double[] imagData;
	
	public ComplexImage(long[] dims, String[] axes) {
		super(dims, axes);
		long totalElements = totalElements(dims);
		if (totalElements > Integer.MAX_VALUE)
			throw new IllegalArgumentException("image dimensions too large");
		this.realData = new double[(int)totalElements];
		this.imagData = new double[(int)totalElements];
	}
	
	public void getComplex(long[] index, Complex c) {
		int element = elementNumber(index);
		c.setReal(realData[element]);
		c.setImag(imagData[element]);
	}
	
	public void setComplex(long[] index, Complex c) {
		int element = elementNumber(index);
		realData[element] = c.getReal();
		imagData[element] = c.getImag();
	}
}
