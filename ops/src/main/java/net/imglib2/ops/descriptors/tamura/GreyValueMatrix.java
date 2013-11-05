package net.imglib2.ops.descriptors.tamura;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.Module;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class GreyValueMatrix extends AbstractModule< int[][] >  
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;
	
	private int[][] greyValues;
	
	@Override
	public boolean isEquivalentModule(Module<?> output) 
	{
		return GreyValueMatrix.class.isAssignableFrom(output.getClass());
	}

	@Override
	public boolean hasCompatibleOutput(Class<?> clazz) 
	{
		return clazz.isAssignableFrom(int[][].class);
	}

	@Override
	protected int[][] recompute() 
	{
		final Cursor< ? extends RealType< ? > > cursor = ii.cursor();
		final int minVal = (int)ii.firstElement().getMinValue();
		
		greyValues =  new int[(int) ii.dimension(0)][(int) ii.dimension(1)];
		
		// fill gray values
        while (cursor.hasNext()) 
        {
            cursor.fwd();

            final int x = (int)(cursor.getIntPosition(0) - ii.min(0));
            final int y = (int)(cursor.getIntPosition(1) - ii.min(1));

            greyValues[x][y] = (int)cursor.get().getRealDouble() - minVal;
        }
          
        return greyValues;
	}
}
