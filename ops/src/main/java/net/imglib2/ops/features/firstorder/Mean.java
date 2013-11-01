package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.geometric.Area;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;
import org.knime.knip.core.features.firstorder.sums.Sum;

public class Mean<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private Sum<T> m_sum = new Sum<T>();

    @RequiredFeature
    private Area m_area = new Area();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Mean";
    }

    @Override
    public DoubleType recompute() {
        return new DoubleType(m_sum.get().get() / m_area.get().get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mean<T> copy() {
        return new Mean<T>();
    }
}
