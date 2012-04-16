/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
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

package imglib.ops.condition;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

/**
 * TODO
 *
 */
public class PixelOnBorder<T extends RealType<T>> implements Condition<T>
{
	private final double borderValue;
	private final int[] imageDims;
	private final int[] neighPos;
	private final LocalizableByDimCursor<T> neighCursor;
	
	public PixelOnBorder(final Image<T> image, final double borderValue)
	{
		this.borderValue = borderValue;
		this.imageDims = image.getDimensions();
		this.neighPos = image.createPositionArray();
		this.neighCursor = image.createLocalizableByDimCursor();
	}
	
	@Override
	public boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position)
	{
		double currPixValue = cursor.getType().getRealDouble();
		
		if (currPixValue != borderValue)
			return false;
		
		// look left
		if (position[0] > 0)
		{
			this.neighPos[0] = position[0]-1;
			this.neighPos[1] = position[1];
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		// look right
		if (position[0] < this.imageDims[0]-1)
		{
			this.neighPos[0] = position[0]+1;
			this.neighPos[1] = position[1];
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		// look up
		if (position[1] > 0)
		{
			this.neighPos[0] = position[0];
			this.neighPos[1] = position[1]-1;
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		// look down
		if (position[1] < this.imageDims[1]-1)
		{
			this.neighPos[0] = position[0];
			this.neighPos[1] = position[1]+1;
			this.neighCursor.setPosition(this.neighPos);
			if (this.neighCursor.getType().getRealDouble() != borderValue)
				return true;
		}

		return false;
	}

}
