package net.imglib2.algorithm.mser;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.IntType;

public class ComponentTreeTest< T extends IntegerType< T > > implements ComponentHandler< MserComponent< T > >, MserProcessor< T >
{
	MserComponentHandler< T > handler;
	final ImageStack stack;
	final int w;
	final int h;
	
	public ComponentTreeTest( final ImageStack stack, final int w, final int h, final T delta )
	{
		this.handler = new MserComponentHandler< T >( delta, this );
		this.stack = stack;
		this.w = w;
		this.h = h;
	}

	@Override
	public void emit( MserComponent< T > component )
	{
//		System.out.println( "emit " + component);		
//		ByteProcessor byteProcessor = new ByteProcessor( w, h );
//		byte[] pixels = ( byte[] )byteProcessor.getPixels();
//		for ( Localizable l : component.locations )
//		{
//			int x = l.getIntPosition( 0 );
//			int y = l.getIntPosition( 1 );
//			pixels[ y * w + x ] = (byte)(255 & 0xff);
//		}
//		stack.addSlice( null, byteProcessor );
//
//		
//		
//		for ( int y = 0; y < h; ++y )
//		{
//			System.out.print("| ");
//			for ( int x = 0; x < w; ++x )
//			{
//				boolean set = false;
//				for ( Localizable l : component.locations )
//					if( l.getIntPosition( 0 ) == x && l.getIntPosition( 1 ) == y )
//						set = true;
//				System.out.print( set ? "x " : ". " );
//			}
//			System.out.println("|");
//		}
		
		handler.emit( component );
	}

	/* (non-Javadoc)
	 * @see net.imglib2.algorithm.mser.MserProcessor#foundMser(net.imglib2.algorithm.mser.MserEvaluationNode)
	 */
	@Override
	public void foundNewMinimum( MserEvaluationNode< T > node )
	{
		System.out.println( "found MSER " + node );

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

//	public static final int[][] testData = new int[][] {
//	{ 0, 9, 0, 1, 4, 0, 2 },
//	{ 8, 9, 3, 4, 5, 0, 3 },
//	{ 7, 0, 3, 1, 2, 0, 4 },
//	{ 5, 5, 5, 5, 5, 5, 5 },
//	{ 7, 1, 2, 3, 4, 5, 6 },
//	{ 3, 3, 1, 0, 1, 5, 1 },
//	{ 3, 3, 0, 8, 2, 1, 1 } };
		
	public static final int[][] testData = new int[][] {
	{ 100, 100, 100, 100, 100, 100, 100 },
	{ 100, 100, 100, 100, 100, 100, 100 },
	{ 100, 100, 100,   0,  50,  50, 100 },
	{ 100, 100, 100,   0,   0,  50, 100 },
	{ 100, 100, 100,  50,   0,  50, 100 },
	{ 100, 100, 100, 100, 100, 100, 100 },
	{ 100, 100, 100, 100, 100, 100, 100 } };
		
//	public static final int[][] testData = new int[][] {
//	{ 0, 0, 7, 7, 9, 9, 9 },
//	{ 0, 0, 7, 7, 9, 9, 9 },
//	{ 0, 0, 7, 7, 9, 9, 9 },
//	{ 0, 0, 7, 7, 9, 9, 9 },
//	{ 0, 0, 7, 7, 9, 9, 9 },
//	{ 0, 0, 7, 7, 9, 9, 9 },
//	{ 0, 0, 7, 7, 9, 9, 9 } };
		
	public static void main( String[] args )
	{
		new ImageJ();
		
		Img< IntType > img = null;
		
		boolean load = true;
		if ( load )
		{
			try
			{
				ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
				final ImgOpener io = new ImgOpener();
//				img = io.openImg( "/home/tobias/workspace/data/wingclip.tif", imgFactory, new IntType() );
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
		ComponentTreeTest< IntType > handler = new ComponentTreeTest< IntType >( stack, w, h, new IntType( 10 ) );
		final MserComponentGenerator< IntType > generator = new MserComponentGenerator< IntType >( new IntType( Integer.MAX_VALUE ) );
		final ComponentTree< IntType, MserComponent< IntType > > tree = new ComponentTree< IntType, MserComponent< IntType > >( img, generator, handler );
		tree.run();
		ImagePlus imp = new ImagePlus("components", stack);
		imp.show();
	}
}