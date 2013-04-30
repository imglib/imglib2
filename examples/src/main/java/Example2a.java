/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Here we want to copy an Image into another one using a generic method
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example2a
{
	public Example2a() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > img = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// copy the image, as it is a generic method it also works with FloatType
		Img< FloatType > duplicate = copyImage( img );

		// display the copy
		ImageJFunctions.show( duplicate );
	}

	/**
	 * Generic, type-agnostic method to create an identical copy of an Img
	 *
	 * @param input - the Img to copy
	 * @return - the copy of the Img
	 */
	public < T extends Type< T > > Img< T > copyImage( final Img< T > input )
	{
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it implements
		// the Interval interface
		Img< T > output = input.factory().create( input, input.firstElement() );

		// create a cursor for both images
		Cursor< T > cursorInput = input.cursor();
		Cursor< T > cursorOutput = output.cursor();

		// iterate over the input
		while ( cursorInput.hasNext())
		{
			// move both cursors forward by one pixel
			cursorInput.fwd();
			cursorOutput.fwd();

			// set the value of this pixel of the output image to the same as the input,
			// every Type supports T.set( T type )
			cursorOutput.get().set( cursorInput.get() );
		}

		// return the copy
		return output;
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2a();
	}
}
