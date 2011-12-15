package net.imglib2.ops.sandbox;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.IterableInterval;
import net.imglib2.roi.RectangleRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.real.DoubleType;

public class NewImageAssign<U extends ComplexType<U>, V extends ComplexType<V>> {
	
	Img<U> img;
	double[] origin;
	double[] span;
	NewFunc<U,V> func;
	
	public NewImageAssign(Img<U> img, double[] origin, double[] span, NewFunc<U,V> func) {
		this.img = img;
		this.origin = origin;
		this.span = span;
		this.func = func;
	}
	
	public void assign() {
		RegionOfInterest r = null;
		
		RectangleRegionOfInterest roi = new RectangleRegionOfInterest(
                origin, span);
		double[] pos = new double[2];
		Img<DoubleType> img = null;
		NewFunc<DoubleType,DoubleType> newFunc = new NewAvgFunc<DoubleType,DoubleType>(new DoubleType());
		DoubleType output = new DoubleType();
		IterableInterval<DoubleType> ii = roi.getIterableIntervalOverROI(img);
		Cursor<DoubleType> iiC = ii.cursor();
		while (iiC.hasNext()) {
		         iiC.fwd();
		}
		iiC.reset();
		roi.setOrigin(new double[] { 7, 123242 });
		while (iiC.hasNext()) {
		         iiC.fwd();
	// TODO - ARG
//		         newFunc.evaluate(iiC, output);
		         iiC.localize(pos);
		         System.out.println(Arrays.toString(pos));
		}
		roi.setOrigin(new double[] { 0, 0 });
		iiC.reset();
		while (iiC.hasNext()) {
			iiC.fwd();
		    iiC.localize(pos);
		    System.out.println(Arrays.toString(pos));
		}
	}
}
