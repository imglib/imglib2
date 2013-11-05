package net.imglib2.ops.descriptors.tamura.features;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.descriptors.AbstractDescriptorModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.tamura.GreyValueMatrix;
import net.imglib2.type.numeric.RealType;

public class Directionality extends AbstractDescriptorModule
{
	@ModuleInput
	GreyValueMatrix greyValueMatrix;

	private int[][] greyValues;
	
    private static final double[][] filterH = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};

    private static final double[][] filterV = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};

	@Override
	public String name() {
		return "Directionality Tamura";
	}

	@Override
	protected double[] recompute() {
 
		greyValues = greyValueMatrix.get();
		
        final double[] histogram = new double[16];
        final double maxResult = 3;
        final double binWindow = maxResult / (histogram.length - 1);
        int bin = -1;
        for (int x = 1; x < (greyValues.length - 1); x++) {
            for (int y = 1; y < (greyValues[x].length - 1); y++) {
                bin =
                        (int)(((Math.PI / 2) + Math.atan(this.calculateDeltaV(x, y) / this.calculateDeltaH(x, y))) / binWindow);
                histogram[bin]++;
            }
        }
             
		return histogram;
	}
	
    /**
     * @return
     */
    private final double calculateDeltaH(final int x, final int y) {
        double result = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result = result + (greyValues[(x - 1) + i][(y - 1) + j] * filterH[i][j]);
            }
        }

        return result;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    private final double calculateDeltaV(final int x, final int y) {
        double result = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result = result + (greyValues[(x - 1) + i][(y - 1) + j] * filterV[i][j]);
            }
        }
        return result;
    }

}
