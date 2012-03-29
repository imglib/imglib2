package net.imglib2.algorithm.pde;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsRandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Gradient<T extends RealType<T>> extends MultiThreadedBenchmarkAlgorithm implements OutputAlgorithm<Img<FloatType>> {

	private final Img<T> input;
	private Img<FloatType> output;

	/*
	 * CONSTRUCTOR	
	 */

	public Gradient(final Img<T> input) {
		this.input = input;
		long[] dimensions = new long[input.numDimensions()+1];
		for (int i = 0; i < dimensions.length-1; i++) {
			dimensions[i] = input.dimension(i);
		}
		dimensions[dimensions.length-1] = input.numDimensions();
		try {
			this.output = input.factory().imgFactory(new FloatType()).create(dimensions , new FloatType());
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public boolean process() {
		
		long start = System.currentTimeMillis();
		
		Cursor<T> in = input.localizingCursor();
		RandomAccess<FloatType> oc = output.randomAccess();
		T zero = input.firstElement().createVariable();
		OutOfBoundsRandomAccess<T> ra = Views.extendValue(input, zero).randomAccess();
		
		float central, diff;
		
		int newdim = input.numDimensions();
		
		while (in.hasNext()) {
			in.fwd();

			// Position neighborhood cursor;
			ra.setPosition(in);
			
			// Position output cursor
			for (int i = 0; i < input.numDimensions(); i++) {
				oc.setPosition(in.getLongPosition(i), i);
			}
			oc.setPosition(0, newdim);
			
			// Central value
			central =  in.get().getRealFloat();

			// Gradient
			for (int i = 0; i < input.numDimensions(); i++) {
				ra.fwd(i);
				diff = central - ra.get().getRealFloat();
				ra.bck(i);
				
				oc.get().set(diff);
				oc.fwd(newdim);
			}
			
		}
		
		processingTime = System.currentTimeMillis() - start;
		return true;
	}


	@Override
	public Img<FloatType> getResult() {
		return output;
	}

}
