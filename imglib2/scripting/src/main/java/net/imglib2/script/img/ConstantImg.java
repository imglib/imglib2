/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.img;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.script.img.fn.ConstantCursor;
import net.imglib2.script.img.fn.ConstantRandomAccess;

/**
 * An {@link Img} that returns the same value for all pixels.
 * 
 * Literally it returns the same value instance given to the constructor.
 * If you edit that instance, then the image is therefore altered.
 * 
 * @param <T>
 * @author Albert Cardona
 */
public class ConstantImg<T> extends AbstractImg<T>
{
	protected final T value;

	public ConstantImg(final T value, final long[] size) {
		super(size);
		this.value = value;
	}
	static protected class ConstantImgFactory<W> extends ImgFactory<W>
	{
		@Override
		public Img<W> create(long[] dim, W type) {
			return new ConstantImg<W>(type, dim);
		}

		@Override
		public <S> ImgFactory<S> imgFactory(S type)
				throws IncompatibleTypeException {
			return new ConstantImgFactory<S>();
		}	
	}

	@Override
	public ConstantImgFactory<T> factory() {
		return new ConstantImgFactory<T>();
	}

	@Override
	public Img<T> copy() {
		return new ConstantImg<T>(value, dimension.clone());
	}

	@Override
	public RandomAccess<T> randomAccess() {
		return new ConstantRandomAccess<T>(dimension, value);
	}
	
	@Override
	public AbstractCursor<T> cursor() {
		return new ConstantCursor<T>(dimension, value);
	}

	@Override
	public Cursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public Object iterationOrder()
	{
		return new FlatIterationOrder( this );
	}
}
