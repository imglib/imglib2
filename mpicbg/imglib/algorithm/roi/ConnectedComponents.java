package mpicbg.imglib.algorithm.roi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.HashMap;

import mpicbg.imglib.algorithm.OutputAlgorithm;
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

	/*
	 *TODO: Possibly allow multiple values to be labeled at the same time, but as different
	 *segments.  In this case, there are (at least) two questions that would need to be addressed:
	 *
	 *1) How to handle mapping labels for different values in the Image.  Maybe this should be
	 *left to the user?  For instance, if I have a binary Image with N number of foreground
	 *segments and M number of background segments, should 1..N represent the foreground labels
	 *and N+1..M+N represent background?  How would this information be returned to the user?
	 *
	 *2) Handle multiple strels?  For instance, we might want a 4-connected foreground and an
	 *8-connected background.  In this case, we have to keep a HashMap of strels corresponding
	 *to the values in the Image, but this added complexity might make it not worth it.  
	 *
	 *
	 */
	
	
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
	 * @param pattern pass ConnectedComponents.RASTER for an Image whose {@Cursor}s curse in a 
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
	
	/**
	 * Creates a StructuringElement that, when used with a ConnectedComponents, will result
	 * in a labeling connected only along rectangular grid lines (4-connected in the 2D case)
	 * of an Image that curses in a raster-scan pattern.  
	 * @param dim the dimensionality of the resulting StructuringElement.
	 * @return the StructuringElement as described above. 
	 */
	public static StructuringElement halfConnectedRaster(int dim)
	{
		StructuringElement s;
		LocalizableByDimCursor<BitType> c;
		int[] size = new int[dim];
		int[] pos = new int[dim];

		//These strels are all 3x3x...3
		for (int i = 0; i < dim; ++i)
		{
			size[i] = 3;
		}		 
		s = new StructuringElement(size, "Strel half connected raster path " + dim);
		c = s.createLocalizableByDimCursor();
		
		/*
		 * The location of the pixels in the strel that should be nonzero all have ones everywhere
		 * except in one place.  Here, iterate across the values in the position array, setting each
		 * one to zero, in turn.
		 */
		
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
	
	/**
	 * Creates a StructuringElement that, when used with a ConnectedComponents, will result
	 * in a labeling connected only along rectangular grid lines (4-connected in the 2D case)
	 * of an Image regardless of its scan pattern.  
	 * @param dim the dimensionality of the resulting StructuringElement.
	 * @return the StructuringElement as described above. 
	 */
	public static StructuringElement halfConnectedRandom(int dim)
	{
		/*
		 * NOTE:  For now, there shouldn't be much of a complexity difference between using
		 * a half-connected strel and a fully-connected strel, because StructuringElement doesn't 
		 * yet have the necessary innards to cause a RegionOfInterestCursor to curse over only
		 * its nonzero locations. 
		 */
		StructuringElement s;
		LocalizableByDimCursor<BitType> c;
		int[] size = new int[dim];
		int[] pos = new int[dim];
		
		Arrays.fill(size, 3);		 
		s = new StructuringElement(size, "Strel half connected random path " + dim);
		c = s.createLocalizableByDimCursor();
		
		Arrays.fill(pos, 1);
		
		/*
		 * Runs the same way as a raster pattern strel, except we have to take into account the
		 * pixels that would be "forward" in a raster scan as well.
		 */
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

	/**
	 * Creates a StructuringElement that, when used with a ConnectedComponents, will result
	 * in a fully-connected labeling (8-connected in the 2D case) of an Image that curses in a
	 * raster-scan pattern.  
	 * @param dim the dimensionality of the resulting StructuringElement.
	 * @return the StructuringElement as described above. 
	 */
	public static StructuringElement fullConnectedRaster(int dim)
	{
		StructuringElement s;
		LocalizableByDimCursor<BitType> c;
		int[] size = new int[dim];
		int pos[] = new int[dim];
		
		Arrays.fill(size, 3);
		s = new StructuringElement(size, "Strel full connected raster path " + dim);
		c = s.createLocalizableByDimCursor();
		
		/*
		 * To create a 3D strel, for example, set all pixels at locations [x x 0] to one.  Next,
		 * set all pixels at locations [x 0 1] to one.  Finally, set all pixels at location [0 1 1]
		 * (there is only one) to one.
		 * 
		 * Here, this is abstracted to any dimension.
		 */
		for (int i = dim; i > 0; --i)
		{
			fullConnectedRasterHelper(c, i, pos);
		}
		
		c.close();
		return s;
	}
	
	
	/**
	 * Sets the bits for a strel, for a given dimension. 
	 * @param c a cursor created from the strel in question.
	 * @param dim the dimension in question.
	 * @param pos an int array, to avoid creating one for each iteration.
	 */
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
	
	/**
	 * Creates a StructuringElement that, when used with a ConnectedComponents, will result
	 * in a fully-connected labeling (8-connected in the 2D case) of an Image regardless of its
	 * scan pattern. 
	 * @param dim the dimensionality of the resulting StructuringElement.
	 * @return the StructuringElement as described above. 
	 */
	public static StructuringElement fullConnectedRandom(int dim)
	{
		StructuringElement s = StructuringElement.createCube(dim, 3);
		int[] pos = new int[dim];
		LocalizableByDimCursor<BitType> c = s.createLocalizableByDimCursor();
		
		/*
		 * This one is the simplest.  Create a cube strel of size 3, then set
		 * the center pixel to zero.
		 */
		for (int i = 0; i < dim; ++i)
		{
			pos[i] = 1;
		}
		
		c.setPosition(pos);
		c.getType().setZero();
		c.close();
		
		s.setName("Strel full connected random path " + dim);
		
		return s;
	}
	
	/**
	 * ConnectedComponents constructor creates an {@link OutputAlgorithm} that generates a
	 * connected-components label-Image from the input Image and a corresponding value.  For
	 * instance, to label a BitType Image, labelValue might be a BitType that is set to true.
	 * This would correspond to the most common implementation of a connected components labeling
	 * on a black-and-white image. 
	 * 
	 * @param type an example {@link Type} corresponding to the output label Image's type.
	 * @param imageIn the Image to be labeled.
	 * @param s a StructuringElement, used to define connectedness.
	 * @param labelValue the value to label, all other values are given a 0-label.
	 * 
	 * @see #fullConnectedRandom(int) fullConnectedRandom,
	 * @see #halfConnectedRandom(int) halfConnectedRandom,
	 * @see #fullConnectedRaster(int) fullConnectedRaster,  
	 * @see #halfConnectedRaster(int) halfConnectedRaster,
	 * @see #connectedComponentsBWShortLabeler(Image, Connection, Pattern)
	 * connectedComponentsBWShortLabeler,
	 * @see #connectedComponentsLabeler(Image, ComparableType, IntegerType, Connection, Pattern)
	 *  connectedComponentsLabeler 
	 */
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
			
			/*
			 * Example equivalence map:
			 * 1 -> 1 2
			 * 2 -> 1 2
			 * 3 -> 3
			 * 4 -> 2 4 7
			 * 5 -> 5
			 * 6 -> 6
			 * 7 -> 4 7
			 * 
			 * The following loop collapses the equivalence map down to
			 * 1 -> 1
			 * 2 -> 1
			 * 3 -> 3
			 * 4 -> 1
			 * 5 -> 5
			 * 6 -> 6
			 * 7 -> 1
			 */
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
			
			/*
			 * Notice in the above example that after collapsing the equivalence map down,
			 * we had values 1, 3, 5, and 6, where we would prefer 1, 2, 3 and 4.  The call
			 * to fixMapOrder makes that happen.
			 */
			fixMapOrder(labelList, labelEquiv);
			
			//Make sure we have the zero default included in the lookup table.
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
			nextLabel = one.clone();
			outCursor.reset();
			inCursor.reset();
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void close()
	{
		super.close();
		inCursor.close();
		outCursor.close();
	}
	
	@Override
	protected S patchOperation(int[] inPos,
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
			
			return label;			
		}
		else
		{
			return zero;
		}
	}
	
	/**
	 * Assigns an as-of-yet unused value to label.
	 * @param label a Type that will receive a new, unused label value.
	 */
	private void newLabel(S label)
	{
		label.set(nextLabel);
		Vector<S> eqList = new Vector<S>();

		eqList.add(label);
		labelList.add(label);
		eqTable.put(label.getInteger(), eqList);
				
		nextLabel.add(one);
	}

	/**
	 * Update the value of label.  If neighborLabel is nonzero and less than label, assign label
	 * to its value.
	 * @param label
	 * @param neighborLabel
	 */
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
	
	/**
	 * Add an equivalence map from the Type from to the Type to.  This is done in such as 
	 * way that the lists stored in the HashMap are always sorted.  This will be useful during the
	 * second pass, when the equivalence values are assigned.
	 * @param from
	 * @param to
	 */
	protected void mapEquiv(S from, S to)
	{
		Vector<S> equivs = eqTable.get(from.getInteger());
		int c = 0, i = 0;
		
		/*
		 * Possibly could use a binary search, but it would only be worthwhile if we have a lot of
		 * equivalences.
		 */		
		while (i < equivs.size() && (c = equivs.get(i).compareTo(to)) < 0)
		{
			++i;
		}
				
		if (i == equivs.size())
		{
			//If i is equal to the list size, just add to the end.
			equivs.add(to);
		}
		else if (c != 0)
		{
			//if c == 0, then the value is already stored in the list.
			equivs.insertElementAt(to, i);
		}
				
	}
}
