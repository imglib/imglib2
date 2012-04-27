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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.type.AbstractNativeType;

/**
 * The LabelingType represents a labeling of a pixel with zero or more
 * labelings of type T. Each labeling names a distinct object in the
 * image space.
 * 
 * @param <T> the desired type of the pixel labels, for instance Integer to number objects or String for user-assigned label names
 *
 * @author Lee Kamentsky
 */
public class LabelingType<T extends Comparable<T>> extends AbstractNativeType<LabelingType<T>> {
	final protected long [] generation;

	// the NativeContainer
	final NativeLabeling<T, ? extends IntAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	IntAccess b = null;
	
	protected final LabelingMapping<T, Integer> mapping;
	
	/**
	 * Constructor for mirroring state with another labeling
	 * @param storage
	 * @param mapping
	 * @param generation
	 */
	protected LabelingType(NativeLabeling<T, ? extends IntAccess> storage, LabelingMapping<T, Integer> mapping, long [] generation) {
		this.storage = storage;
		this.mapping = mapping;
		this.generation = generation;
	}
	// this is the constructor if you want it to read from an array
	public LabelingType( NativeLabeling<T, ? extends IntAccess> storage ) { 
		this.storage = storage;
		mapping = storage.getMapping();
		generation = new long [1];
	}
	
	// this is the constructor if you want it to be a variable
	public LabelingType( List<T> value ) { 
		storage = null;
		b = new IntArray(1);
		mapping = new LabelingMapping<T, Integer>(new Integer(0));
		generation = new long [1];
		this.setLabeling(value);
	}
	
	@SuppressWarnings("unchecked")
	public LabelingType(T value) {
		this(Arrays.asList(value));
	}
	
	// this is the constructor if you want it to be a variable
	public LabelingType() {
		this(new ArrayList<T>());
	}
	
	/**
	 * Get the labels defined at the type's current pixel or 
	 * @return a list of the labelings at the current location.
	 */
	public List<T> getLabeling()
	{
		return mapping.listAtIndex(b.getValue(i));
	}
	
	/**
	 * Set the labeling at the current pixel
	 * @param labeling
	 */
	public void setLabeling(List<T> labeling) {
		b.setValue(i, mapping.indexOf(labeling));
		generation[0]++;
	}
	
	public void setLabeling(T [] labeling) {
		setLabeling(Arrays.asList(labeling));
	}
	
	/**
	 * Assign a pixel a single label
	 * @param label - the label to assign
	 */
	public void setLabel(T label) {
		List<T> labeling = new ArrayList<T>(1);
		labeling.add(label);
		setLabeling(labeling);
	}
	
	/**
	 * This method returns the canonical object for the given labeling.
	 * SetLabeling will work faster if you pass it the interned object
	 * instead of one created by you.
	 * 
	 * @param labeling
	 * @return
	 */
	public List<T> intern(List<T> labeling)
	{
		return mapping.intern(labeling);
	}
	
	/**
	 * Return the canonical labeling object representing the single labeling.
	 * SetLabeling will work faster if you use this object.
	 * @param label - a label for a pixel.
	 * @return - the canonical labeling with the single label.
	 */
	public List<T> intern(T label) {
		List<T> labeling = new ArrayList<T>(1);
		labeling.add(label);
		return intern(labeling);
	}
	@Override
	public int getEntitiesPerPixel() {
		return 1;
	}


	@Override
	public void updateContainer(Object o) {
		b = storage.update(o);
	}

	@Override
	public LabelingType<T> createVariable() {
		return new LabelingType<T>();
	}

	@Override
	public LabelingType<T> copy() {
		return new LabelingType<T>(getLabeling());
	}

	@Override
	public void set(LabelingType<T> c) {
		setLabeling(c.getLabeling());
	}

	@Override
	public String toString() {
		return getLabeling().toString();
	}

	/**
	 * Get the labels known by the type
	 * @return a list of all labels in the type's associated storage
	 */
	List<T> getLabels() {
		return mapping.getLabels();
	}

	/**
	 * The underlying storage has an associated generation which is
	 * incremented every time the storage is modified. For cacheing, it's
	 * often convenient or necessary to know whether the storage has
	 * changed to know when the cache is invalid. The strategy is to
	 * save the generation number at the time of cacheing and invalidate
	 * the cache if the number doesn't match.
	 * 
	 * @return the generation of the underlying storage
	 */
	long getGeneration() {
		return generation[0];
	}

	@Override
	public NativeImg<LabelingType<T>, ?> createSuitableNativeImg(
			NativeImgFactory<LabelingType<T>> storageFactory, long[] dim) {
		final NativeImg<LabelingType<T>, ? extends IntAccess> container = storageFactory.createIntInstance( dim, 1 );
		
		if (! (container instanceof NativeLabeling)) {
			throw new UnsupportedOperationException("The factory did not return a NativeLabeling, it returned a " + container.getClass().getName());
		}
		// create a Type that is linked to the container
		final LabelingType<T> linkedType = new LabelingType<T>( (NativeLabeling<T, ? extends IntAccess>)container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}

	@Override
	public LabelingType<T> duplicateTypeOnSameNativeImg() {
		LabelingType<T> t = new LabelingType<T>(storage, mapping, generation);
		return t;
	}

	public LabelingMapping<T, Integer> getMapping() {
		return mapping;
	}
}
