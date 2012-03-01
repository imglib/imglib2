package net.imglib2.algorithm.kdtree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.RealPoint;

import org.junit.Test;

public class VolumetricSearchTest {

	@Test
	public void testVolumetricSearch() {
		final ArrayList<FinalInterval> list = new ArrayList<FinalInterval>();
		list.add(new FinalInterval(new long[] {0,0}, new long [] { 1,1} ));
		list.add(new FinalInterval(new long[] {2,2}, new long [] { 3,3} ));
		new VolumetricSearch<FinalInterval>(list);
	}

	@Test
	public void testFind() {
		final Random random = new Random(12345);
		//
		// Make up 100 random examples which hopefully will encompass
		// more corner cases than I can think of.
		//
		final long [] min = new long[3];
		final long [] max = new long[3];
		final double [] position = new double[3];
		for (int i=0; i<100; i++) {
			final int nIntervals = random.nextInt(50) + 1;
			final ArrayList<FinalInterval> list = new ArrayList<FinalInterval>();
			for (int j=0;j<nIntervals; j++) {
				for (int k=0; k<3; k++) {
					min[k] = Math.abs(random.nextLong()) % 100;
					max[k] = min[k] + Math.abs(random.nextLong()) % 100;
				}
				list.add(new FinalInterval(min, max));
			}
			for (int j=0; j<3; j++) {
				position[j] = random.nextDouble() * 100.0;
			}
			final VolumetricSearch<FinalInterval> vs = new VolumetricSearch<FinalInterval>(list);
			final List<FinalInterval> result = vs.find(new RealPoint(position));
			for (final FinalInterval interval:result) {
				for (int j=0; j<3; j++) {
					assertTrue(position[j] >= interval.realMin(j));
					assertTrue(position[j] <= interval.realMax(j));
				}
			}
			for (final FinalInterval interval:list) {
				if (result.contains(interval)) continue;
				boolean good = true;
				for (int j=0; j<3; j++) {
					good &= position[j] >= interval.realMin(j);
					good &= position[j] <= interval.realMax(j);
				}
				assertFalse(good);
			}
		}

	}

}
