package net.imglib2.ops.descriptors.tamura.features;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class Coarseness extends AbstractFeatureModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;
	
	private int[][] greyValues;
	
	@Override
	public String name() 
	{
		return "Coarseness";
	}

	@Override
	protected double calculateFeature() 
	{
		final Cursor< ? extends RealType< ? > > cursor = ii.cursor();
		final int minVal = (int)ii.firstElement().getMinValue();
		
		greyValues =  new int[(int) ii.dimension(0)][(int) ii.dimension(1)];
		
		// filll gray values
        while (cursor.hasNext()) 
        {
            cursor.fwd();

            final int x = (int)(cursor.getIntPosition(0) - ii.min(0));
            final int y = (int)(cursor.getIntPosition(1) - ii.min(1));

            greyValues[x][y] = (int)cursor.get().getRealDouble() - minVal;
        }
        
        double result = 0;

        for (int i = 1; i < (greyValues.length - 1); i++) {
            for (int j = 1; j < (greyValues[i].length - 1); j++) {
                result = result + Math.pow(2, this.sizeLeadDiffValue(i, j));
            }
        }

        result = (1.0 / ii.size()) * result;
		
		return result;
	}

	private final int sizeLeadDiffValue(final int x, final int y) 
	{
        double result = 0, tmp;
        int maxK = 1;

        for (int k = 0; k < 3; k++) 
        {
            tmp = Math.max(this.differencesBetweenNeighborhoodsHorizontal(x, y, k),
                             this.differencesBetweenNeighborhoodsVertical(x, y, k));
            if (result < tmp) 
            {
                maxK = k;
                result = tmp;
            }
        }
        return maxK;
	}

	private final double differencesBetweenNeighborhoodsVertical(final int x, final int y, final int k) 
	{
        double result = 0;
        result = Math.abs(this.averageOverNeighborhoods(x, y + (int)Math.pow(2, k - 1), k)
                        - this.averageOverNeighborhoods(x, y - (int)Math.pow(2, k - 1), k));
        return result;
	}

	private final double differencesBetweenNeighborhoodsHorizontal(final int x, final int y, final int k) 
	{
        double result = 0;
        result = Math.abs(this.averageOverNeighborhoods(x + (int)Math.pow(2, k - 1), y, k)
                        - this.averageOverNeighborhoods(x - (int)Math.pow(2, k - 1), y, k));
        return result;
	}
	
	private final double averageOverNeighborhoods(final int x, final  int y, final int k) {
        double result = 0, border;
        border = Math.pow(2, 2 * k);
        int x0 = 0, y0 = 0;

        for (int i = 0; i < border; i++) {
            for (int j = 0; j < border; j++) {
                x0 = (x - (int)Math.pow(2, k - 1)) + i;
                y0 = (y - (int)Math.pow(2, k - 1)) + j;
                if (x0 < 0) {
                    x0 = 0;
                }
                if (y0 < 0) {
                    y0 = 0;
                }
                if (x0 >= greyValues.length) {
                    x0 = greyValues.length - 1;
                }
                if (y0 >= greyValues[0].length) {
                    y0 = greyValues[0].length - 1;
                }

                result = result + greyValues[x0][y0];
            }
        }
        result = (1 / Math.pow(2, 2 * k)) * result;
        return result;
	}

}
