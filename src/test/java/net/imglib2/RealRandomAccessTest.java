package net.imglib2;

import net.imglib2.position.FunctionRealRandomAccessible;
import net.imglib2.type.numeric.real.DoubleType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class RealRandomAccessTest
{

	@Test
	public void testSetPositionAndGet()
	{

		final int numRandomLocations = 20;
		final Random rng = new Random(10);

		final RealRandomAccessible<DoubleType> accessible = new FunctionRealRandomAccessible<>(
				3,
				() -> (s, t) -> t.setReal(s.getDoublePosition(0) + s.getDoublePosition(1) + s.getDoublePosition(2)),
				DoubleType::new);

		compareAt(accessible, 0.0, 0.0, 0.0);
		compareAt(accessible, 0.0f, 0.0f, 0.0f);
		compareAtRealLocalizable(accessible, 0.0, 0.0, 0.0);

		for (int i = 0; i < numRandomLocations; ++i) {
			compareAt(accessible, rng.nextDouble(), rng.nextDouble(), rng.nextDouble());
			compareAt(accessible, rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
			compareAtRealLocalizable(accessible, rng.nextDouble(), rng.nextDouble(), rng.nextDouble());
		}


	}

	private static void compareAt(final RealRandomAccessible<DoubleType> accessible, final double... position) {
		Assert.assertEquals(
				DoubleStream.of(position).sum(),
				accessible.realRandomAccess().setPositionAndGet(position).getRealDouble(),
				1e-10);
	}

	private static void compareAt(final RealRandomAccessible<DoubleType> accessible, final float... position) {
		Assert.assertEquals(
				IntStream.range(0, position.length).mapToDouble(d -> position[d]).sum(),
				accessible.realRandomAccess().setPositionAndGet(position).getRealDouble(),
				1e-10);
	}

	private static void compareAtRealLocalizable(final RealRandomAccessible<DoubleType> accessible, double... position) {
		Assert.assertEquals(
				DoubleStream.of(position).sum(),
				accessible.realRandomAccess().setPositionAndGet(new RealPoint(position)).getRealDouble(),
				1e-10);
	}

}
