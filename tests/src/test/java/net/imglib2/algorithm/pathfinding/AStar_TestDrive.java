package net.imglib2.algorithm.pathfinding;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class AStar_TestDrive
{

	public static < T extends NativeType< T > & RealType< T >> void main( final String[] args )
	{
		ImageJ.main( args );

		final ImagePlus imp = new ImagePlus( "/Users/tinevez/Desktop/Data/PathExample.tif" );
		final Img< T > wrap = ImageJFunctions.wrap( imp );
		final long[] start = new long[] { 0, 0 };
		final long[] end = new long[] { 63, 63 };

		imp.show();

		final JSlider slider = new JSlider( 0, 255 );
		slider.addChangeListener( new ChangeListener()
		{

			@Override
			public void stateChanged( final ChangeEvent arg0 )
			{
				final int val = slider.getValue();
				System.out.println( "For heuristics strength = " + val );
				final AStar< T > aStar = new EuclidianDistanceAStar< T >( wrap, start, end, val );
				if ( !aStar.checkInput() || !aStar.process() )
				{
					System.err.println( aStar.getErrorMessage() );
					return;
				}

				System.out.println( "Pathfinding done in " + aStar.getProcessingTime() + " ms." );
				System.out.println( "Expanded " + aStar.getExpandedNodeNumber() + " nodes." );
				final PathIterable< T > result = aStar.getResult();
				System.out.println( "Path length = " + result.length() + ", number of steps = " + result.size() );

				final int nPoints = ( int ) result.size();
				final int[] xPoints = new int[ nPoints ];
				final int[] yPoints = new int[ nPoints ];
				int index = 0;
				final Cursor< T > cursor = result.cursor();
				while ( cursor.hasNext() )
				{
					cursor.fwd();
					xPoints[ index ] = cursor.getIntPosition( 0 );
					yPoints[ index ] = cursor.getIntPosition( 1 );
					index++;
				}

				final PolygonRoi roi = new PolygonRoi( xPoints, yPoints, nPoints, Roi.POLYLINE );
				imp.setRoi( roi );
				imp.updateAndDraw();
			}
		} );

		final JFrame frame = new JFrame( "Change heuristics strength." );
		frame.getContentPane().add( slider );
		frame.setVisible( true );

	}

}
