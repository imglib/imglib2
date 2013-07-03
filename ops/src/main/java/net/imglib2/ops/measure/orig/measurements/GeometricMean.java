package net.imglib2.ops.measure.orig.measurements;

import net.imglib2.ops.measure.orig.Measurement;


public class GeometricMean implements Measurement {

	private Product product;
	private ElementCount numElems;

	public GeometricMean(Product product, ElementCount numElems) {
		this.product = product;
		this.numElems = numElems;
	}
	
	@Override
	public double getValue() {
		return Math.pow(product.getValue(), 1.0/numElems.getValue());
	}

}
