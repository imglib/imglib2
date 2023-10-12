package net.imglib2;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.real.DoubleType;

import java.util.stream.IntStream;

public class StreamifiedViewsTest {
	public static void main(String[] args) {
		final int w = 10;
		final int h = 8;
		double[] data = IntStream.range(0, w*h).asDoubleStream().toArray();
		Img<DoubleType> img = ArrayImgs.doubles(data, w, h);

		final StreamifiedView<DoubleType> view = img.view()
				.translate(1, 2)
				.expandValue(new DoubleType(0), 1, 1)
				.permute(0, 1);
	}
}
