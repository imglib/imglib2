package net.imglib2.ops.features.providers;

import java.awt.Polygon;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;
import org.knime.knip.core.util.PolygonTools;

public class PolygonProvider extends AbstractFeature<Polygon> {

    @RequiredFeature
    BinaryMaskIntervalProvider iiUpdater = new BinaryMaskIntervalProvider();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Polygon Tracer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PolygonProvider copy() {
        return new PolygonProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Polygon recompute() {
        final RandomAccessibleInterval<BitType> ii = iiUpdater.get();
        return PolygonTools.extractPolygon(ii, new int[ii.numDimensions()]);
    }
}
