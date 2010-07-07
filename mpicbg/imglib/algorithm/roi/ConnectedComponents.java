package mpicbg.imglib.algorithm.roi;

import java.util.ArrayList;
import java.util.Vector;
import java.util.HashMap;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.IntegerType;

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
