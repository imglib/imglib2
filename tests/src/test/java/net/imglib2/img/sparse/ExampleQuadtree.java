package net.imglib2.img.sparse;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.integer.IntType;

public class ExampleQuadtree
{
	final static public void main( final String[] args )
	{
		new ImageJ();

		Img< IntType > array = null;
		final ImgFactory< IntType > arrayFactory = new ArrayImgFactory< IntType >();
		try
		{
			final ImgOpener io = new ImgOpener();
			array = io.openImg( "/home/tobias/workspace/data/quadtree.tif", arrayFactory, new IntType() );
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
			return;
		}

		ImageJFunctions.show( array, "array" );


		final NtreeImgFactory< IntType > ntreeFactory = new NtreeImgFactory< IntType >();
		final Img< IntType > quadtree = ntreeFactory.create( array, new IntType() );

		// copy to sparse img
		final Cursor< IntType > dst = quadtree.localizingCursor();
		final RandomAccess< IntType > src = array.randomAccess();
		while( dst.hasNext() )
		{
			dst.fwd();
			src.setPosition( dst );
			dst.get().set( src.get() );
		}
		/*
		final RandomAccess< IntType > dst = quadtree.randomAccess();
		final Cursor< IntType > src = array.localizingCursor();
		while( src.hasNext() )
		{
			src.fwd();
			dst.setPosition( src );
			dst.get().set( src.get() );
		}
		*/

		ImageJFunctions.show( quadtree, "quadtree" );

		System.out.println( "done" );
	}

}
