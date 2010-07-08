package mpicbg.imglib.algorithm.roi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.HashMap;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.IntegerType;
import mpicbg.imglib.type.numeric.integer.ShortType;

/**
 * A connected-components labeling algorithm.  This class allows the connected-components labeling
 * of pixels containing a given value in an Image.
 * 
 * @author Larry Lindsey
 */
public class ConnectedComponents<T extends ComparableType<T>, S extends IntegerType<S>>
	extends ROIAlgorithm<T, S>{

	private final StructuringElement strel;
	private final T value;
	private final S one;
	private final S zero;
	private S nextLabel;
	private final HashMap<Integer, Vector<S>> eqTable;
	private final ArrayList<S> labelList; 
	private LocalizableByDimCursor<S> outCursor;
	private LocalizableByDimCursor<T> inCursor;
	
	public static enum Connection {FULL, HALF};
	public static enum Pattern {RASTER, RANDOM};
	
	/**
	 * Creates a ConnectedComponents for labeling the true pixels in a {@link BitType}
	 * {@link Image}, with labels represented in a {@link ShortType} Image.
	 * @param im the Image to label.
	 * @param connection pass ConnectedComponents.FULL for a fully-connected labeling (in 2D, this
	 * is 8-connected), or ConnectedComponents.HALF for a labeling connected only on rectangular
	 * grid lines (in 2D, this is 4-connected).
	 * @param pattern pass ConnectedComponents.RASTER for an Image whos {@Cursor}s curse in a 
	 * raster pattern, or ConnectedComponents.RANDOM for those that don't.
	 * @return a ShortType Image representing the connected components labeling. 
	 */
	public static ConnectedComponents<BitType, ShortType> 
		connectedComponentsBWShortLabeler(Image<BitType> im, Connection connection, Pattern pattern)
	{
		BitType b = new BitType();
		b.setOne();
		
		return connectedComponentsLabeler(im, b, new ShortType(), connection, pattern);
	}
	
	/**
	 * Creates a ConnectedComponents for labeling the pixels in an {@link Image} whose values
	 * correspond to the value given by the argument val. 
	 * @param <R> The {@link Type} corresponding to the input Image.
	 * @param <Q> The {@link IntegerType} corresponding to the output label Image.
	 * @param im the Image to label.
	 * @param val the value in the input Image to label.
	 * @param type an output example Type.
	 * @param connection pass ConnectedComponents.FULL for a fully-connected labeling (in 2D, this
	 * is 8-connected), or ConnectedComponents.HALF for a labeling connected only on rectangular
	 * grid lines (in 2D, this is 4-connected).
	 * @param pattern pass ConnectedComponents.RASTER for an Image whos {@Cursor}s curse in a 
	 * raster pattern, or ConnectedComponents.RANDOM for those that don't.
	 * @return an Image representing the connected components labeling. 
	 */
	public static <R extends ComparableType<R>, Q extends IntegerType<Q>> ConnectedComponents<R, Q> 
		connectedComponentsLabeler(Image<R> im, R val, Q type, Connection connection, Pattern pattern)
	{
		StructuringElement s;
		
		switch (connection)
		{
		case FULL:
			switch (pattern)
			{
			case RASTER:
				s = fullConnectedRaster(im.getNumDimensions());
				break;
			case RANDOM:
				s = fullConnectedRandom(im.getNumDimensions());
				break;
			default:
				return null;
			}
			break;
		case HALF:
			switch (pattern)
			{
			case RASTER:
				s = halfConnectedRaster(im.getNumDimensions());
				break;
			case RANDOM:
				s = halfConnectedRandom(im.getNumDimensions());
				break;
			default:
				return null;
			}
			break;
		default:
				return null;
		}
		
		return new ConnectedComponents<R, Q>(type, im, s, val);
	}
	
	public static StructuringElement halfConnectedRaster(int dim)
	{
		StructuringElement s;
		LocalizableByDimCursor<BitType> c;
		int[] size = new int[dim];
		int[] pos = new int[dim];
		
		for (int i = 0; i < dim; ++i)
		{
			size[i] = 3;
		}		 
		s = new StructuringElement(size, "Strel right connected raster " + dim);
		c = s.createLocalizableByDimCursor();
		
		Arrays.fill(pos, 1);
		
		for (int i = 0; i < dim; ++i)
		{
			pos[i] = 0;
			c.setPosition(pos);
			c.getType().setOne();
			pos[i] = 1;
		}
		
		c.close();
		
		return s;		
	}
	
	
	public static StructuringElement halfConnectedRandom(int dim)
	{
		StructuringElement s;
		LocalizableByDimCursor<BitType> c;
		int[] size = new int[dim];
		int[] pos = new int[dim];
		
		Arrays.fill(size, 3);		 
		s = new StructuringElement(size, "Strel right connected any path " + dim);
		c = s.createLocalizableByDimCursor();
		
		Arrays.fill(pos, 1);
		
		for (int i = 0; i < dim; ++i)
		{
			pos[i] = 0;
			c.setPosition(pos);
			c.getType().setOne();
			pos[i] = 2;
			c.setPosition(pos);
			c.getType().setOne();
			pos[i] = 1;
		}
		
		c.close();
		
		return s;
	}
			
	public static StructuringElement fullConnectedRaster(int dim)
	{
		StructuringElement s;
		LocalizableByDimCursor<BitType> c;
		int[] size = new int[dim];
		int pos[] = new int[dim];
		
		Arrays.fill(size, 3);
		s = new StructuringElement(size, "Strel full connected raster " + dim);
		c = s.createLocalizableByDimCursor();
		
		for (int i = dim; i > 0; --i)
		{
			fullConnectedRasterHelper(c, i, pos);
		}
		
		c.close();
		return s;
	}
	
	protected static void fullConnectedRasterHelper(LocalizableByDimCursor<BitType> c, int dim, int[] pos)
	{
		Arrays.fill(pos, 0);
		int cnt = 0;
		double lim = Math.pow(3, dim - 1);
		
		for (int i = dim; i < pos.length; ++i)
		{
			pos[i] = 1;
		}
		
		while(cnt <  lim)
		{
			int i = 0;			

			c.setPosition(pos);
			c.getType().setOne();
			
			++cnt;
			++pos[0];
			while(pos[i] >= 3)
			{
				pos[i] = 2;
				++i;
				++pos[i];
			}
		}
	}
	
	public static StructuringElement fullConnectedRandom(int dim)
	{
		StructuringElement s = StructuringElement.createCube(dim, 3);
		int[] pos = new int[dim];
		LocalizableByDimCursor<BitType> c = s.createLocalizableByDimCursor();
		
		for (int i = 0; i < dim; ++i)
		{
			pos[i] = 1;
		}
		
		c.setPosition(pos);
		c.getType().setZero();
		c.close();
		
		s.setName("Strel full connected any path " + dim);
		
		return s;
	}
	
	public ConnectedComponents(S type, Image<T> imageIn, 
			StructuringElement s, T labelValue) 
	{
		super(type, imageIn, s.getDimensions());

	    value = labelValue;
		strel = s;
		eqTable = new HashMap<Integer, Vector<S>>();
		labelList = new ArrayList<S>();
		
		one = type.clone();
		one.setOne();
		zero = type.clone();
		zero.setZero();		
		
		nextLabel = one.clone();
		
		outCursor = super.getOutputImage().createLocalizableByDimCursor();
		inCursor = imageIn.createLocalizableByDimCursor();
		
		while(outCursor.hasNext())
		{
			outCursor.fwd();
			outCursor.getType().setZero();
		}
		
		outCursor.reset();
	}
	
	protected void fixMapOrder(ArrayList<S> labels, Vector<S> labelEquiv)
	{
		S cumMax = labelEquiv.get(0).clone();
		
		for (int i = 0; i < labelEquiv.size(); ++i)
		{
			S from = labels.get(i);
			S to = labelEquiv.get(i);
			
			if (to.compareTo(from) == 0)
			{
				S replace;				
				replace = cumMax.clone();
				
				cumMax.add(one);
				labelEquiv.set(i, replace);					
				for (int j = i + 1; j < labelEquiv.size(); ++j)
				{
					if (labelEquiv.get(j).compareTo(from) == 0)
					{
						labelEquiv.set(j, replace);
					}
				}
			}
			
		}
	}
	
	@Override
	public boolean process()
	{
		if (super.process())
		{			
			final Vector<S> labelEquiv = new Vector<S>();
			final HashMap<Integer, S> labelMap = new HashMap<Integer, S>();	
			
			for (int i = 0; i < labelList.size(); ++i)
			{
				S equiv = labelList.get(i);
				S nextEquiv = eqTable.get(equiv.getInteger()).get(0);
				while (equiv.compareTo(nextEquiv) != 0)
				{
					equiv = nextEquiv;
					nextEquiv = eqTable.get(equiv.getInteger()).get(0);
				}
				labelEquiv.add(equiv);
			}
			
			fixMapOrder(labelList, labelEquiv);
			
			labelMap.put(0, zero);
			
			for (int i = 0; i < labelList.size(); ++i)
			{
				labelMap.put(labelList.get(i).getInteger(), labelEquiv.get(i));
			}
			
			
			
			outCursor.reset();
			
			while (outCursor.hasNext())
			{
				S label;
				outCursor.fwd();
				label = labelMap.get(outCursor.getType().getInteger());
				outCursor.getType().set(label);
			}
			
			eqTable.clear();
			outCursor.close();
			inCursor.close();
			nextLabel = one.clone();
			
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	protected boolean patchOperation(int[] inPos,
			RegionOfInterestCursor<T> cursor)
	{
		final LocalizableByDimCursor<BitType> strelCursor = strel.createLocalizableByDimCursor();
		final int outPos[] = new int[inPos.length];
		final int roiPos[] = new int[inPos.length];
		inCursor.setPosition(inPos);
	
		if (inCursor.getType().compareTo(value) == 0)
		{
			S label = zero.clone();
		
			while (cursor.hasNext())
			{
				cursor.fwd();
				cursor.getPosition(roiPos);
				strelCursor.setPosition(roiPos);
				if (strelCursor.getType().get())
				{
					if (cursor.getType().compareTo(value) == 0)
					{
						outCursor.setPosition(outputOffsetPosition(inPos, roiPos, outPos));
						updateLabel(label, outCursor.getType());
					}
				}
			}
			
			if (label.compareTo(zero) == 0)
			{
				newLabel(label);
			}
			
			outCursor.setPosition(inPos);
			outCursor.getType().set(label);
			
		}

		return true;
	}
	
	private void newLabel(S label)
	{
		label.set(nextLabel);
		Vector<S> eqList = new Vector<S>();

		eqList.add(label);
		labelList.add(label);
		eqTable.put(label.getInteger(), eqList);
				
		nextLabel.add(one);
	}

	protected void updateLabel(S label, S neighborLabel)
	{
		if (label.compareTo(zero) == 0)
		{
			label.set(neighborLabel);
		}
		else if (label.compareTo(neighborLabel) != 0 && neighborLabel.compareTo(zero) != 0)
		{
			mapEquiv(label, neighborLabel);
			mapEquiv(neighborLabel, label);
			if (label.compareTo(neighborLabel) > 0)
			{
				label.set(neighborLabel);
			}
		}
	}
	
	protected void mapEquiv(S from, S to)
	{
		Vector<S> equivs = eqTable.get(from.getInteger());
		int c = 0, i = 0;
		
		while (i < equivs.size() && (c = equivs.get(i).compareTo(to)) < 0)
		{
			++i;
		}
		
		if (i == equivs.size())
		{
			equivs.add(to);
		}
		else if (c != 0)
		{
			equivs.insertElementAt(to, i);
		}
				
	}
}
