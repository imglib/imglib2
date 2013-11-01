package net.imglib2.ops.features.testing;

import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.iterable.unary.Max;
import net.imglib2.ops.operation.iterable.unary.Mean;
import net.imglib2.ops.operation.iterable.unary.Min;
import net.imglib2.ops.operation.iterable.unary.StdDeviation;
import net.imglib2.ops.operation.iterable.unary.Sum;
import net.imglib2.ops.operation.iterable.unary.Variance;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class ImgLib2Tests<T extends RealType<T>> {

    private Mean<T, DoubleType> m_mean;

    private Variance<T, DoubleType> m_variance;

    private Sum<T, DoubleType> m_sum;

    private StdDeviation<T, DoubleType> m_stdDev;

    private Min<T, DoubleType> m_min;

    private Max<T, DoubleType> m_max;

    public ImgLib2Tests() {
        m_mean = new Mean<T, DoubleType>();
        m_sum = new Sum<T, DoubleType>();
        m_variance = new Variance<T, DoubleType>();
        m_stdDev = new StdDeviation<T, DoubleType>();
        m_min = new Min<T, DoubleType>();
        m_max = new Max<T, DoubleType>();
    }

    public void runFirstOrderTest(final IterableInterval<T> ii) {
        //        System.out.println(m_mean.compute(ii.cursor(), new DoubleType()).get());
        m_sum.compute(ii.cursor(), new DoubleType()).get();
        System.out.println(m_variance.compute(ii.cursor(), new DoubleType()).get());
        //        m_stdDev.compute(ii.cursor(), new DoubleType()).get();
        //        m_max.compute(ii.cursor(), new DoubleType()).get();
        //        m_min.compute(ii.cursor(), new DoubleType()).get();
    }
}
