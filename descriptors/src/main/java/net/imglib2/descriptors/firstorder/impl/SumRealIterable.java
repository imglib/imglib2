package net.imglib2.descriptors.firstorder.impl;

import java.util.Iterator;

import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.sums.Sum;
import net.imglib2.type.numeric.RealType;

public class SumRealIterable extends Sum
{
	@ModuleInput
	Iterable< ? extends RealType< ? >> i;

	@Override
	public double calculateFeature()
	{
		double res = 0;
		Iterator< ? extends RealType< ? >> iterator = i.iterator();

		while ( iterator.hasNext() )
		{
			res += iterator.next().getRealDouble();
		}

		return res;
	}
}
