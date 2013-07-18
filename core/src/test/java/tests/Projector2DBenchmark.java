package tests;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYProjector;
import net.imglib2.display.projectors.Projector2D;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.img.sparse.NtreeImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.view.Views;

public class Projector2DBenchmark
{

	public static void main( String[] args )
	{
		//long[] DIMS = new long[] { 2000, 811, 4};
		//long[] DIMS = new long[] { 200, 311};
		long[] DIMS = new long[] { 400, 911};
		
		// set up
		final Converter< UnsignedByteType, UnsignedByteType > conv = new Converter< UnsignedByteType, UnsignedByteType >()
		{
			@Override
			public void convert( UnsignedByteType input, UnsignedByteType output )
			{
				output.setReal( input.getRealDouble() / 2.0 );
			}
		};

		final Img< UnsignedByteType > input = createRandomImg( DIMS );

		// test same iteration order
		Img< UnsignedByteType > output = new ArrayImgFactory< UnsignedByteType >().create(DIMS, new UnsignedByteType() );
		test( conv, input, output );

		// test different iteration order
		output = new CellImgFactory< UnsignedByteType >().create( DIMS, new UnsignedByteType() );
		test( conv, input, output );
		
		// test with n-tree image
		output = new NtreeImgFactory< UnsignedByteType >().create( DIMS, new UnsignedByteType() );
		test( conv, input, output );
		
		// and planar image 
		output = new PlanarImgFactory< UnsignedByteType >().create( DIMS, new UnsignedByteType() );
		test( conv, input, output );
	}

	protected static void test( final Converter< UnsignedByteType, UnsignedByteType > conv, final Img< UnsignedByteType > input, final Img<UnsignedByteType> output)
	{
				final Img<UnsignedByteType> output2 = output.copy(); 
		
				//testing projector 2d
				BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
				{
					@Override
					public void run()
					{
						Projector2D< UnsignedByteType, UnsignedByteType > projector = new Projector2D< UnsignedByteType, UnsignedByteType >( 0, 1, input, output, conv);
						projector.map();
					}
				} );
		
				//testing XYProjector	
				BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
				{
					@Override
					public void run()
					{
						XYProjector< UnsignedByteType, UnsignedByteType > projector = new XYProjector< UnsignedByteType, UnsignedByteType >( input, output2, conv );
						projector.map();
					}
				} );
				
				//check for equality
		
				
				Cursor<UnsignedByteType> c = Views.flatIterable( output).cursor();
				Cursor<UnsignedByteType> c2 = Views.flatIterable( output2).cursor();
				while (c.hasNext()) {
					c.next();
					c2.next();
					Double a = c.get().getRealDouble();
					Double b = c2.get().getRealDouble();
					
					if (!a.equals( b )) {
						System.out.println("ERROR outputs are not equal");
					}
				}
	}

	private static Img< UnsignedByteType > createRandomImg( long[] dims )
	{
		ArrayImgFactory< UnsignedByteType > fac = new ArrayImgFactory< UnsignedByteType >();
		Img< UnsignedByteType > img = fac.create( dims, new UnsignedByteType() );

		Random r = new Random();

		Cursor< UnsignedByteType > c = img.cursor();
		while ( c.hasNext() )
		{
			c.next();
			UnsignedByteType value = new UnsignedByteType();
			value.setReal( r.nextDouble() * 255.0 );
			c.get().set( value );
		}

		return img;
	}
}
