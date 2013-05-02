/* BSD-licensed reimplementation */

package util;

/**
 * A simple accumulator of double values.
 *
 * The original, GPL-licensed version took great pains of trying to maintain
 * numerical stability. Since this reimplementation is simply supporting the
 * ImgLib2 examples, we do not do that here.
 *
 * @author Johannes Schindelin
 */
public class RealSum { protected double sum;

	public void add(double value) {
		sum += value;
	}

	public double getSum() {
		return sum;
	}
}
