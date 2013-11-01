package net.imglib2.ops.features.geometric;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.type.numeric.real.DoubleType;

public class CenterOfGravityForDim extends AbstractFeature<DoubleType> {

    @RequiredFeature
    CenterOfGravity centerOfGravity;

    // defines the dim
    private final int dim;

    public CenterOfGravityForDim(final int dim) {
        this.dim = dim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Center of Gravity For Dimension " + dim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode() + dim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CenterOfGravityForDim copy() {
        return new CenterOfGravityForDim(dim);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        return new DoubleType(centerOfGravity.get()[dim]);
    }
}
