package net.imglib2.ops.features.geometric;

import java.awt.Polygon;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetPolygon;
import net.imglib2.type.numeric.real.DoubleType;

public class Area2DPolygon extends AbstractFeature<DoubleType> {

    @RequiredFeature
    GetPolygon provider;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Area Polygon";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Area2DPolygon copy() {
        return new Area2DPolygon();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {

        Polygon input = provider.get();

        double sum1 = 0.0f;
        double sum2 = 0.0f;

        // Yang Mingqiang:
        // A Survey of Shape Feature Extraction Techniques
        // in Pattern Recognition Techniques, Technology and Applications, 2008
        for (int i = 0; i < input.npoints - 1; i++) {
            sum1 += input.xpoints[i] * input.ypoints[i + 1];
            sum2 += input.ypoints[i] * input.xpoints[i + 1];
        }

        sum1 += input.xpoints[input.npoints - 1] * input.ypoints[0];
        sum2 += input.ypoints[input.npoints - 1] * input.xpoints[0];

        double result = Math.abs(sum1 - sum2) / 2;

        return new DoubleType(result);
    }

}
