package net.imglib2.ops.descriptors.firstorder.percentile;

import net.imglib2.ops.descriptors.AbstractFeatureModule;

public abstract class Percentile extends AbstractFeatureModule
{
	protected double calculatePercentile(double p, double[] v) {
		double[] values = v;//sortedValues.get();
		final int size = values.length;
		
		if (size == 0)
			return Double.NaN;
		
		if (size  == 1)
			return values[0];
		
		double n = size;
        double pos = p * (n + 1);
        double fpos = Math.floor(pos);
        int intPos = (int) fpos;
        double dif = pos - fpos;

        if (pos < 1) 
            return values[0];
        
        if (pos >= n) 
            return values[size - 1];
        
        double lower = values[intPos - 1];
        double upper = values[intPos];
        
        return lower + dif * (upper - lower);
	}	
}
