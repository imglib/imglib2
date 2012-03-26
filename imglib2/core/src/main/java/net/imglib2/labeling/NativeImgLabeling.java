/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.labeling;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.IntAccess;


/**
 * A labeling backed by a native image that takes a
 * labeling type backed by an int array.
 * 
 * @param <T> the type of labels assigned to pixels
 *
 * @author Lee Kamentsky
 * @author leek
 */
public class NativeImgLabeling<T extends Comparable<T>> 
	extends AbstractNativeLabeling<T, IntAccess>{

	final NativeImg<LabelingType<T>, ? extends IntAccess> img;
	
	/**
	 * Create a labeling backed by the default image storage factory
	 * 
	 * @param dim dimensions of the image
	 */
	public NativeImgLabeling(long [] dim) {
		this(dim, new ArrayImgFactory<LabelingType<T>>());
	}
	/**
	 * Create a labeling backed by an image from a custom factory
	 * 
	 * @param dim dimensions of the image
	 * @param imgFactory the custom factory to be used to create the backing storage 
	 */
	public NativeImgLabeling(long[] dim, NativeImgFactory<LabelingType<T>> imgFactory) {
		this(dim, new DefaultROIStrategyFactory<T>(), imgFactory);
	}

	/**
	 * Create a labeling backed by a native image with custom strategy and image factory
	 * 
	 * @param dim - dimensions of the labeling
	 * @param strategyFactory - the strategy factory that drives iteration and statistics
	 * @param imgFactory - the image factory to generate the native image
	 */
	public NativeImgLabeling(
			long[] dim, 
			LabelingROIStrategyFactory<T> strategyFactory, 
			NativeImgFactory<LabelingType<T>> imgFactory) {
		super(dim, strategyFactory);
		this.img = imgFactory.createIntInstance(dim, 1); 
	}
	
	@Override
	public RandomAccess<LabelingType<T>> randomAccess() {
		return img.randomAccess();
	}

	/* (non-Javadoc)
	 * @see net.imglib2.labeling.AbstractNativeLabeling#setLinkedType(net.imglib2.labeling.LabelingType)
	 */
	@Override
	public void setLinkedType(LabelingType<T> type) {
		super.setLinkedType(type);
		img.setLinkedType(type);
	}

	@Override
	public Object iterationOrder()
	{
		return img.iterationOrder();
	}

	@Override
	public Cursor<LabelingType<T>> cursor() {
		return img.cursor();
	}

	@Override
	public Cursor<LabelingType<T>> localizingCursor() {
		return img.localizingCursor();
	}

	@Override
	public ImgFactory<LabelingType<T>> factory() {
		return img.factory();
	}

	@Override
	public IntAccess update(Object updater) {
		return img.update(updater);
	}
	@Override
	public NativeImgLabeling<T> copy()
	{
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final NativeImgLabeling<T> result = new NativeImgLabeling<T>(dimension, (NativeImgFactory) factory());
		LabelingType<T> type = new LabelingType<T>(result);
		result.setLinkedType(type);
		final Cursor<LabelingType<T>> cursor1 = img.cursor();
		final Cursor<LabelingType<T>> cursor2 = result.img.cursor();
		
		while ( cursor1.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();
			
			cursor2.get().set( cursor1.get() );
		}
		
		return result;
		
	}
}
