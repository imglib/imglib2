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
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Here we want to copy an ArrayImg into a CellImg using a generic method,
 * but we cannot do it with simple Cursors as they have a different iteration order.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example2b
{

	public Example2b() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > img = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// copy the image into a CellImg with a cellsize of 20x20
//		Img< FloatType > duplicate = copyImageWrong( img, new CellImgFactory< FloatType >( 20 ) );
		Img< FloatType > duplicate = copyImageCorrect( img, new CellImgFactory< FloatType >( 20 ) );

		// display the copy and the original
		ImageJFunctions.show( img );
		ImageJFunctions.show( duplicate );
	}

	/**
	 * WARNING: This method makes a mistake on purpose!
	 */
	public < T extends Type< T >> Img< T > copyImageWrong( final Img< T > input,
		final ImgFactory< T > imgFactory )
	{
		// create a new Image with the same dimensions but the other imgFactory
		// note that the input provides the size for the new image as it
		// implements the Interval interface
		Img< T > output = imgFactory.create( input, input.firstElement() );

		// create a cursor for both images
		Cursor< T > cursorInput = input.cursor();
		Cursor< T > cursorOutput = output.cursor();

		// iterate over the input cursor
		while ( cursorInput.hasNext())
		{
			// move both forward
			cursorInput.fwd();
			cursorOutput.fwd();

			// set the value of this pixel of the output image, every Type supports T.set( T type )
			cursorOutput.get().set( cursorInput.get() );
		}

		// return the copy
		return output;
	}

	/**
	 * This method copies the image correctly, using a RandomAccess.
	 */
  public < T extends Type< T >> Img< T > copyImageCorrect( final Img< T > input,
    final ImgFactory< T > imgFactory )
  {
    // create a new Image with the same dimensions but the other imgFactory
    // note that the input provides the size for the new image by implementing the Interval interface
    Img< T > output = imgFactory.create( input, input.firstElement() );

    // create a cursor that automatically localizes itself on every move
    Cursor< T > cursorInput = input.localizingCursor();
    RandomAccess< T > randomAccess = output.randomAccess();

    // iterate over the input cursor
    while ( cursorInput.hasNext())
    {
      // move input cursor forward
      cursorInput.fwd();

      // set the output cursor to the position of the input cursor
      randomAccess.setPosition( cursorInput );

      // set the value of this pixel of the output image, every Type supports T.set( T type )
      randomAccess.get().set( cursorInput.get() );
    }

    // return the copy
    return output;
  }

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2b();
	}
}
