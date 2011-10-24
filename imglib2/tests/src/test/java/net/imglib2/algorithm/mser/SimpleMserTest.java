package net.imglib2.algorithm.mser;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.algorithm.mser.SimpleMserComponentHandler.SimpleMserProcessor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.IntType;

public class SimpleMserTest< T extends IntegerType< T > > implements SimpleMserProcessor< T >
{
	final ImageStack stack;
	final int w;
	final int h;
	
	public SimpleMserTest( final ImageStack stack, final int w, final int h )
	{
		this.stack = stack;
		this.w = w;
		this.h = h;
	}

	@Override
	public void foundNewMinimum( SimpleMserEvaluationNode< T > node )
	{
		// System.out.println( "found MSER " + node );

		ByteProcessor byteProcessor = new ByteProcessor( w, h );
		byte[] pixels = ( byte[] )byteProcessor.getPixels();
		for ( Localizable l : node.locations )
		{
			int x = l.getIntPosition( 0 );
			int y = l.getIntPosition( 1 );
			pixels[ y * w + x ] = (byte)(255 & 0xff);
		}
		stack.addSlice( null, byteProcessor );		
	}

	public static final int[][] testData = new int[][] {
	{ 100, 100, 100, 100, 100, 100, 100 },
	{ 100, 100, 100, 100, 100, 100, 100 },
	{ 100, 100, 100,   0,  50,  50, 100 },
	{ 100, 100, 100,   0,   0,  50, 100 },
	{ 100, 100, 100,  50,   0,  50, 100 },
	{ 100, 100, 100, 100, 100, 100, 100 },
	{ 100, 100, 100, 100, 100, 100, 100 } };

	public static void main( String[] args )
	{
		final long delta = 20;
		
		new ImageJ();
		
		Img< IntType > img = null;
		
		boolean load = true;
		if ( load )
		{
			try
			{
				ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
				final ImgOpener io = new ImgOpener();
				img = io.openImg( "/home/tobias/workspace/data/img1.tif", imgFactory, new IntType() );
			}
			catch ( Exception e )
			{
				e.printStackTrace();
				return;
			}
		}
		else
		{
			// fill input image with test data
			ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
			img = imgFactory.create( new long[] { testData[ 0 ].length, testData.length }, new IntType() );
			int[] pos = new int[ 2 ];
			Cursor< IntType > c = img.localizingCursor();
			while ( c.hasNext() )
			{
				c.fwd();
				c.localize( pos );
				c.get().set( testData[ pos[ 1 ] ][ pos[ 0 ] ] );
			}
		}
	
		ImageJFunctions.show( img );
		IJ.run( "Enhance Contrast", "saturated=0.35" );

		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		final int w = ( int ) dimensions[0];
		final int h = ( int ) dimensions[1];

		ImageStack stack = new ImageStack( w, h );
		SimpleMserTest< IntType > procNewMser = new SimpleMserTest< IntType >( stack, w, h );
		final SimpleMserComponentHandler< IntType > handler = new SimpleMserComponentHandler< IntType >( img.numDimensions(), new IntType( Integer.MAX_VALUE ), delta, procNewMser );
		final ComponentTree< IntType, SimpleMserComponent< IntType > > tree = new ComponentTree< IntType, SimpleMserComponent< IntType > >( img, handler, handler );
		tree.run();
		ImagePlus imp = new ImagePlus("components", stack);
		imp.show();
	}

} 
