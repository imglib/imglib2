package net.imglib2.ops.descriptors.firstorder.percentile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.imglib2.IterableInterval;
import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.Module;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.tamura.GreyValueMatrix;
import net.imglib2.type.numeric.RealType;

public class SortedValues extends AbstractModule<double[]> 
{
	@ModuleInput
	private IterableInterval< ? extends RealType< ? >> ii;

	@Override
	public boolean isEquivalentModule(Module<?> output) 
	{
		return SortedValues.class.isAssignableFrom(output.getClass());
	}

	@Override
	public boolean hasCompatibleOutput(Class<?> clazz) 
	{
		return clazz.isAssignableFrom(double[].class);
	}

	@Override
	protected double[] recompute() 
	{
		double[] values = new double[(int) ii.size()];
		
		final Iterator< ? extends RealType< ? > > it = ii.iterator();
		int i = 0;
		while(it.hasNext())
		{
			values[i] = it.next().getRealDouble();
			i++;
		}
	
        Arrays.sort(values);
        return values;
	}
}
