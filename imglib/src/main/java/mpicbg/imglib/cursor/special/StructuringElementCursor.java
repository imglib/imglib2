package mpicbg.imglib.cursor.special;

import java.util.Arrays;

import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.ComplexType;

public class StructuringElementCursor<T extends Type<T>> extends CursorImpl<T>
	implements LocalizableCursor<T> {

	private LocalizableCursor<?> patchCenterCursor;
	private final LocalizableByDimCursor<T> cursor;
	private final int[][] path;
	private final int n;
	private final int numDimensions;
	private int pathPos;
	
	/**
	 * centerOffsetPos is used in the case where the strel cursor should 
	 * iterate over a kernel.  In this case, getPosition should return
	 * non-negative values, but we might still want cursor to iterate in the
	 * input image over a patch that is centered about the position indicated
	 * by patchCenterPos.
	 */
	private final int[] kernelOffsetPos;
	
	/**
	 * strelPos holds the strel cursor location, which is returned by getPosition.
	 */
	private int[] strelPos;
	/**
	 * patchCenterPos holds the current location of the patch center cursor.
	 */
	private final int[] patchCenterPos;
	/**
	 * cursorSetPos gets the location that the LocalizableByDimCursor is set to.
	 * This is equivalent to patchCenterPos + strelPos + kernelOffsetPos
	 */
	private final int[] cursorSetPos;
	
	public static int[] halveArray(int[] array)
	{
	    for (int i = 0; i < array.length; ++i)
	    {
	        array[i] /= 2;
	    }
	    return array;
	}

	public static <R extends ComplexType<R>> int[][] imageToPath(
	        final Image<R> im)
	{
	    return imageToPath(im, halveArray(im.getDimensions()));
	}
	
	public static <R extends ComplexType<R>> int[][] imageToPath(
	        final Image<R> im, int[] strelImageCenter)
    {
	    LocalizableCursor<R> cursor = im.createLocalizableCursor();
	    int[] pos = new int[im.getNumDimensions()];
	    int count = 0;
	    int[][] path;
	    
        if (strelImageCenter == null)
        {
            int[] imDim = im.getDimensions();
            strelImageCenter = new int[imDim.length];
            for (int i = 0; i < imDim.length; ++i)
            {
                strelImageCenter[i] = imDim[i] / 2;
            }
        }
        
        while (cursor.hasNext())
        {
            cursor.fwd();            
            if (cursor.getType().getRealDouble() != 0)
            {
                ++count;
            }
        }
        
        cursor.reset();
        path = new int[count][im.getNumDimensions()];
        count = 0;
        
        while (cursor.hasNext())
        {
            cursor.fwd();            
            if (cursor.getType().getRealDouble() != 0)
            {      
               cursor.getPosition(pos);
                for (int i = 0; i < pos.length; ++i)
                {
                    pos[i] -= strelImageCenter[i];
                }
                System.arraycopy(pos, 0, path[count], 0, path[count].length);
                ++count;
            }
        }
        
        cursor.close();
        return path;
    }
	
	public static int[][] sizeToPath(final int[] size)
	{
	    return sizeToPath(size, halveArray(size.clone()));
	}
	
	public static int[][] sizeToPath(final int[] size, int[] patchCenter)
	{
	    int n = 1;
	    int d = size.length;
	    int[][] path;
	    
	    for (int s : size)
	    {
	        n *= s;
	    }

	    path = new int[n][d];
	    
        if (patchCenter == null)
        {
            patchCenter = new int[d];
            for (int j = 0; j < d; ++j)
            {
                patchCenter[j] = size[j] / 2;
            }
        }
        	    
	    for (int j = 0; j < d; ++j)
	    {
	        path[0][j] = -patchCenter[j];
	    }
	    	    	    
	    for (int i = 1; i < n; ++i)
	    {
	        int j = 0;
	        System.arraycopy(path[i - 1], 0, path[i], 0, d);
	        
	        path[i][0]++;
	        
	        while(path[i][j] >= (size[j] - patchCenter[j]) && j < d - 1)
	        {
	            path[i][j] = -patchCenter[j];
	            path[i][j+1]++;	            
	            j++;	            
	        }	       
	    }
	    
	    return path;
	}
	
	public <R extends ComplexType<R>> 
	    StructuringElementCursor(final LocalizableByDimCursor<T> cursor, 
            final Image<R> strelImage) {
        this(cursor, strelImage, null);
    }
	
	public <R extends ComplexType<R>> 
	    StructuringElementCursor(final LocalizableByDimCursor<T> cursor, 
            final Image<R> strelImage, final int[] strelImageCenter) {
        this(cursor, imageToPath(strelImage, strelImageCenter));
    }
	
	public StructuringElementCursor(final LocalizableByDimCursor<T> cursor,
            final int[] size)
    {
        this(cursor, sizeToPath(size, null));
    }
	
	public StructuringElementCursor(final LocalizableByDimCursor<T> cursor,
	        final int[] size, final int[] patchCenter)
	{
	    this(cursor, sizeToPath(size, patchCenter));
	}
	
	public StructuringElementCursor(final LocalizableByDimCursor<T> cursor,
	        final StructuringElementCursor<?> strelCursor)
	{
	    this(cursor, strelCursor.path);
	}
	
	public StructuringElementCursor(final LocalizableByDimCursor<T> cursor, 
	        final int[][] inPath) {
		super(cursor.getStorageContainer(), cursor.getImage());		
		patchCenterCursor = cursor.getImage().createLocalizableCursor();
		this.cursor = cursor;
		n = inPath.length;
		numDimensions = inPath[0].length;		
		path = new int[n][numDimensions];
		patchCenterPos = new int[numDimensions];
		cursorSetPos = new int[numDimensions];
		kernelOffsetPos = new int[numDimensions];
		Arrays.fill(kernelOffsetPos, 0);
		
		for (int j = 0; j < n; ++j)
		{
		    System.arraycopy(inPath[j], 0, path[j], 0, inPath[j].length);
		}
						
		reset();
		
		while (hasNext())
		{
		    fwd();
		    System.out.println(getPositionAsString());
		}
		System.out.println();
		reset();
	}
	
	public void setKernelOffset(final int[] ko)
	{
	    System.arraycopy(ko, 0, kernelOffsetPos, 0, kernelOffsetPos.length);
	}
	
	public void centerKernel(final int[] dim)
	{	    
	    System.arraycopy(dim, 0, kernelOffsetPos, 0, kernelOffsetPos.length);
	    halveArray(kernelOffsetPos);
	}
	
	public void setPatchCenterCursor(final LocalizableCursor<?> newPCC)
	{
	    patchCenterCursor.close();
	    patchCenterCursor = newPCC;
	    reset();
	}
	
	public LocalizableCursor<?> getPatchCenterCursor()
	{
	    return patchCenterCursor;
	}

	@Override
	public void close() {
		patchCenterCursor.close();
		super.isClosed = true;
	}
	
	public void closeAll()
	{
	    cursor.close();
	    close();
	}

	@Override
	public int getStorageIndex() {
		return cursor.getStorageIndex();
	}

	@Override
	public T getType() {		
		return cursor.getType();
	}

	@Override
	public void reset() {
		pathPos = -1;
		patchCenterCursor.getPosition(patchCenterPos);
		
		for (int i = 0; i < numDimensions; ++i)
		{
		    patchCenterPos[i] -= kernelOffsetPos[i];
		}
	}
	
	@Override
	public boolean hasNext() {		
		return pathPos + 1 < n;
	}

	@Override
	public void fwd() {
		++pathPos;
		strelPos = path[pathPos];
		
		for (int j = 0; j < numDimensions; ++j)
		{
		    cursorSetPos[j] = patchCenterPos[j] + strelPos[j];
		}
		
		cursor.setPosition(cursorSetPos);
		
	}

	public boolean patchHasNext()
	{
	    return patchCenterCursor.hasNext();
	}
	
	public boolean patchFwd()
	{
	    if (patchCenterCursor.hasNext())
	    {
	        patchCenterCursor.fwd();
	        reset();
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}
	
	public void patchReset()
	{
	    patchCenterCursor.reset();
	    reset();
	}
	
	@Override
	public void getPosition(final int[] position) {
		System.arraycopy(strelPos, 0, position, 0, numDimensions);
	}

	@Override
	public int[] getPosition() {		
		return strelPos.clone();
	}

	@Override
	public int getPosition(final int dim) {
		return strelPos[dim];
	}

	@Override
	public String getPositionAsString() {
	    String pos = "(" + strelPos[ 0 ];
        
        for (int d = 1; d < numDimensions; d++ )
        {
            pos += ", " + strelPos[ d ];
        }
        
        pos += ")";
        
        return pos;
	}	
	
	public int getPathLength()
	{
	    return n;
	}
}
