package net.imglib2.algorithm.componenttree.pixellist;

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.algorithm.componenttree.ComponentTree;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

public final class PixelListComponentTree< T extends Type< T > > implements Component.Handler< PixelListComponentIntermediate< T > >, Iterable< PixelListComponent< T > >
{
	public static < T extends RealType< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, boolean darkToBright )
	{
		final int numDimensions = input.numDimensions();
		long size = 1;
		for ( int d = 0; d < numDimensions; ++d )
			size *= input.dimension( d );
		if( size > Integer.MAX_VALUE ) {
			int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / new LongType().getEntitiesPerPixel(), 1.0 / numDimensions );
			return buildComponentTree( input, type, new CellImgFactory< LongType >( cellSize ), darkToBright );
		} else
			return buildComponentTree( input, type, new ArrayImgFactory< LongType >(), darkToBright );
	}

	public static < T extends RealType< T > > PixelListComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, final ImgFactory< LongType > imgFactory, boolean darkToBright )
	{
		T max = type.createVariable();
		max.setReal( darkToBright ? type.getMaxValue() : type.getMinValue() );
		System.out.println( "max = " + max );
		final PixelListComponentGenerator< T > generator = new PixelListComponentGenerator< T >( max, input, imgFactory );
		final PixelListComponentTree< T > tree = new PixelListComponentTree< T >();
		ComponentTree.buildComponentTree( input, generator, tree, darkToBright );
		return tree;
	}

	private PixelListComponent< T > root;

	private final ArrayList< PixelListComponent< T > > nodes;

	public PixelListComponentTree()
	{
		root = null;
		nodes = new ArrayList< PixelListComponent< T > >();
	}

	@Override
	public void emit( PixelListComponentIntermediate< T > intermediate )
	{
		final PixelListComponent< T > component = new PixelListComponent< T >( intermediate );
		root = component;
		nodes.add( component );
	}

	@Override
	public Iterator< PixelListComponent< T > > iterator()
	{
		return nodes.iterator();
	}

	public PixelListComponent< T > root()
	{
		return root;
	}
}
