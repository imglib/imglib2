package net.imglib2.ops.features.geometric;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;

public class Circularity<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private Perimeter m_perimeter = new Perimeter();

    @RequiredFeature
    private Area m_area = new Area();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Circularity";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Circularity<T> copy() {
        return new Circularity<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {

        // circularity = 4pi(area/perimeter^2)
        // A circularity value of 1.0 indicates a perfect circle.
        // As the value approaches 0.0, it indicates an increasingly elongated polygon.
        // http://rsbweb.nih.gov/ij/plugins/circularity.html
        final double result = 4 * Math.PI * (m_area.get().get() / Math.pow(m_perimeter.get().get(), 2));
        return new DoubleType(result);
    }

}
