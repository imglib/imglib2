package mpicbg.imglib.cursor.special;

import java.util.ArrayList;

import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.RealType;

public class StructuringElementCursor<T extends Type<T>> extends CursorImpl<T>
	implements LocalizableCursor<T> {

	private LocalizableCursor<?> patchCenterCursor;
	private final LocalizableByDimCursor<T> cursor;
	private final int[][] path;
	private final int n;
	private final int numDimensions;
	private int pathPos;
	private int[] strelPos;
	private final int[] patchCenterPos;
	private final int[] cursorSetPos;

	private static <R extends RealType<R>> int[][] imageToPath(
	        final Image<R> im, int[] offset)
    {
	    ArrayList<int[]> pathArray = new ArrayList<int[]>();
	    LocalizableCursor<R> cursor = im.createLocalizableCursor();
	    int[] pos = new int[im.getNumDimensions()];
	    
        if (offset == null)
        {
            int[] imDim = im.getDimensions();
            offset = new int[imDim.length];
            for (int i = 0; i < imDim.length; ++i)
            {
                offset[i] = imDim[i] / 2;
            }
        }
        
        while (cursor.hasNext())
        {
            cursor.fwd();
            if (cursor.getType().getRealDouble() != 0)
            {
                cursor.getDimensions(pos);
                for (int i = 0; i < pos.length; ++i)
                {
                    pos[i] += offset[i];
                }
                pathArray.add(pos.clone());
            }
        }
        
        cursor.close();
        return (int[][]) pathArray.toArray();
    }
	
	public static int[][] sizeToPath(final int[] size, int[] offset)
	{
	    int n = 1;
	    int d = size.length;
	    int[][] path;
	    
	    for (int s : size)
	    {
	        n *= s;
	    }

	    path = new int[n][d];
	    
        if (offset == null)
        {
            offset = new int[d];
            for (int j = 0; j < d; ++j)
            {
                offset[j] = -size[j] / 2;
            }
        }
        	    
	    for (int j = 0; j < d; ++j)
	    {
	        path[0][j] = offset[j];
	    }
	    
	    for (int i = 1; i < n; ++i)
	    {
	        int j = 0;
	        System.arraycopy(path[i - 1], 0, path[i], 0, d);
	        
	        path[i][0]++;
	        
	        while(path[i][j] >= (size[j] + offset[j]) && j < d - 1)
	        {
	            path[i][j] = offset[j];
	            path[i][j+1]++;
	            j++;
	        }
	            
	    }
	    
	    return path;
	}
	
	public <R extends RealType<R>> 
	    StructuringElementCursor(final LocalizableByDimCursor<T> cursor, 
            final Image<R> strelImage) {
        this(cursor, strelImage, null);
    }
	
	public <R extends RealType<R>> 
	    StructuringElementCursor(final LocalizableByDimCursor<T> cursor, 
            final Image<R> strelImage, final int[] offset) {
        this(cursor, imageToPath(strelImage, offset));
    }
	
	public StructuringElementCursor(final LocalizableByDimCursor<T> cursor,
            final int[] size)
    {
        this(cursor, sizeToPath(size, null));
    }
	
	public StructuringElementCursor(final LocalizableByDimCursor<T> cursor,
	        final int[] size, final int[] offset)
	{
	    this(cursor, sizeToPath(size, offset));
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
		
		for (int j = 0; j < n; ++j)
		{
		    for (int k = 0; k < numDimensions; ++k)
		    {
		        path[j][k] = inPath[j][k];
		    }
		}
		
		reset();
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

	
	
	
}
