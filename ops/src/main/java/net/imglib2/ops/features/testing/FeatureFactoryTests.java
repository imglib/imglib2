package net.imglib2.ops.features.testing;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.features.firstorder.GeometricMean;
import net.imglib2.ops.features.firstorder.HarmonicMean;
import net.imglib2.ops.features.firstorder.Kurtosis;
import net.imglib2.ops.features.firstorder.Max;
import net.imglib2.ops.features.firstorder.Mean;
import net.imglib2.ops.features.firstorder.Min;
import net.imglib2.ops.features.firstorder.Skewness;
import net.imglib2.ops.features.firstorder.StdDeviation;
import net.imglib2.ops.features.firstorder.Variance;
import net.imglib2.ops.features.firstorder.moments.Moment1AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment2AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment3AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment4AboutMean;
import net.imglib2.ops.features.geometric.Area;
import net.imglib2.ops.features.geometric.Circularity;
import net.imglib2.ops.features.geometric.Perimeter;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.features.ASM;
import net.imglib2.ops.features.haralick.features.ClusterPromenence;
import net.imglib2.ops.features.haralick.features.ClusterShade;
import net.imglib2.ops.features.haralick.features.Contrast;
import net.imglib2.ops.features.haralick.features.Correlation;
import net.imglib2.ops.features.haralick.features.DifferenceVariance;
import net.imglib2.ops.features.haralick.features.Entropy;
import net.imglib2.ops.features.haralick.features.ICM1;
import net.imglib2.ops.features.haralick.features.ICM2;
import net.imglib2.ops.features.haralick.features.IFDM;
import net.imglib2.ops.features.haralick.features.SumAverage;
import net.imglib2.ops.features.haralick.features.SumEntropy;
import net.imglib2.ops.features.haralick.features.SumVariance;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.Feature;
import org.knime.knip.core.features.MyFeatureSet;
import org.knime.knip.core.features.firstorder.sums.Sum;
import org.knime.knip.core.features.firstorder.sums.SumOfInverses;
import org.knime.knip.core.features.firstorder.sums.SumOfLogs;
import org.knime.knip.core.features.firstorder.sums.SumOfSquares;

public class FeatureFactoryTests<T extends RealType<T>> {

    private TestFeatureSet<T> m_myFeatureSet;

    public FeatureFactoryTests() {
        m_myFeatureSet = new MyFeatureSet<T>();
        m_myFeatureSet.register(new Mean<T>());
        m_myFeatureSet.register(new Area());
        m_myFeatureSet.register(new Variance<T>());
        m_myFeatureSet.register(new StdDeviation<T>());
        m_myFeatureSet.register(new Skewness<T>());
        m_myFeatureSet.register(new Kurtosis<T>());
        m_myFeatureSet.register(new Sum<T>());
        m_myFeatureSet.register(new SumOfInverses<T>());
        m_myFeatureSet.register(new SumOfLogs<T>());
        m_myFeatureSet.register(new SumOfSquares<T>());
        m_myFeatureSet.register(new Moment1AboutMean<T>());
        m_myFeatureSet.register(new Moment2AboutMean<T>());
        m_myFeatureSet.register(new Moment3AboutMean<T>());
        m_myFeatureSet.register(new Moment4AboutMean<T>());
        m_myFeatureSet.register(new GeometricMean<T>());
        m_myFeatureSet.register(new HarmonicMean<T>());
        m_myFeatureSet.register(new Max<T>());
        m_myFeatureSet.register(new Min<T>());
        m_myFeatureSet.register(new Perimeter());
        m_myFeatureSet.register(new Circularity<T>());

        // Haralick Features
        m_myFeatureSet.registerHidden(new HaralickCoocMatrix<T>(32, 1, MatrixOrientation.ANTIDIAGONAL));

        m_myFeatureSet.register(new ASM());
        m_myFeatureSet.register(new ClusterPromenence());
        m_myFeatureSet.register(new ClusterShade());
        m_myFeatureSet.register(new Correlation());
        m_myFeatureSet.register(new Contrast());
        m_myFeatureSet.register(new DifferenceVariance());
        m_myFeatureSet.register(new Entropy());
        m_myFeatureSet.register(new ICM1());
        m_myFeatureSet.register(new ICM2());
        m_myFeatureSet.register(new IFDM());
        m_myFeatureSet.register(new SumAverage());
        m_myFeatureSet.register(new SumEntropy());
        m_myFeatureSet.register(new SumVariance());
        m_myFeatureSet.register(new net.imglib2.ops.features.haralick.features.Variance());

    }

    public void runFirstOrderTest(final IterableInterval<T> ii) {
        Iterator<Feature<DoubleType>> iterator = m_myFeatureSet.iterator(ii);
        while (iterator.hasNext()) {
            Feature next = iterator.next();
            System.out.println(next.name() + ": " + next.get());
        }
    }
}
