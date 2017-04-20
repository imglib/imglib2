package net.imglib2.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.operators.ValueEquals;
import net.imglib2.util.Pair;
import net.imglib2.view.composite.Composite;
import net.imglib2.view.composite.CompositeIntervalView;
import net.imglib2.view.composite.CompositeIntervalViewOfFirstDimension;
import net.imglib2.view.composite.CompositeView;
import net.imglib2.view.composite.GenericComposite;
import net.imglib2.view.composite.GenericCompositeIntervalView;
import net.imglib2.view.composite.NumericComposite;
import net.imglib2.view.composite.RealComposite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Philipp Hanslovsky
 *
 *
 */
public class CompositeViewTest {

    long[] dims = { 3, 4, 5, 6 };

    long[] strides = { 1, 3, 12, 60 };

    ArrayImg<ShortType, ? > img = ArrayImgs.shorts( dims );

    @Before
    public void fillImg() {
        short val = 0;
        for ( ShortType i : img ) {
            i.set( val++ );
        }
    }

    private void testLast() {
        long size = dims[dims.length - 1];
        {
            CompositeIntervalView<ShortType, ? extends GenericComposite<ShortType>> ref = Views.collapse(img);
            GenericCompositeIntervalView<ShortType, ? extends GenericComposite<ShortType>, ?> comp = Views.collapseDth(img, dims.length - 1);

            for (Pair<? extends GenericComposite<ShortType>, ? extends GenericComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i)
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
            }
        }


        {
            CompositeIntervalView<ShortType, RealComposite<ShortType>> ref = Views.collapseReal(img);
            GenericCompositeIntervalView<ShortType, RealComposite<ShortType>, ?> comp = Views.collapseRealDth(img, dims.length - 1);

            for (Pair<RealComposite<ShortType>, RealComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i)
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
            }
        }


        {
            CompositeIntervalView<ShortType, NumericComposite<ShortType>> ref = Views.collapseNumeric(img);
            GenericCompositeIntervalView<ShortType, NumericComposite<ShortType>, ?> comp = Views.collapseNumericDth(img, dims.length - 1);

            for (Pair<NumericComposite<ShortType>, NumericComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i) {
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
                }
            }
        }

    }

    private void testFirst() {
        long size = dims[dims.length - 1];
        {
            CompositeIntervalViewOfFirstDimension<ShortType, ? extends GenericComposite<ShortType>> ref = Views.collapseFirst(img);
            GenericCompositeIntervalView<ShortType, ? extends GenericComposite<ShortType>, ?> comp = Views.collapseDth(img, 0);

            for (Pair<? extends GenericComposite<ShortType>, ? extends GenericComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i)
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
            }
        }


        {
            CompositeIntervalViewOfFirstDimension<ShortType, RealComposite<ShortType>> ref = Views.collapseRealFirst(img);
            GenericCompositeIntervalView<ShortType, RealComposite<ShortType>, ?> comp = Views.collapseRealDth(img, 0);

            for (Pair<RealComposite<ShortType>, RealComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i)
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
            }
        }


        {
            CompositeIntervalViewOfFirstDimension<ShortType, NumericComposite<ShortType>> ref = Views.collapseNumericFirst(img);
            GenericCompositeIntervalView<ShortType, NumericComposite<ShortType>, ?> comp = Views.collapseNumericDth(img, 0);

            for (Pair<NumericComposite<ShortType>, NumericComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i) {
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
                }
            }
        }

    }

    private static void testStrided(RandomAccessibleInterval< ? extends Composite< ? extends IntegerType< ? > > > rai, long numChannels, long stride ) {
        for ( Composite< ? extends IntegerType< ? > > c : Views.flatIterable( rai ) ) {
            for ( int i = 0; i < numChannels - 1; ++i ) {
                long v1 = c.get( i ).getIntegerLong();
                long v2 = c.get( i + 1 ).getIntegerLong();
                Assert.assertEquals( stride, v2 - v1 );
            }

        }
    }

    @Test
    public void testComposites() {

        testFirst();

        testLast();

        for ( int d = 0; d < dims.length; ++d ) {
            {
                GenericCompositeIntervalView<ShortType, ? extends GenericComposite<ShortType>, ?> collapsed = Views.collapseDth(img, d);
                testStrided( collapsed, dims[ d ], strides[ d ] );
            }

            {
                GenericCompositeIntervalView<ShortType, RealComposite<ShortType>, ?> collapsed = Views.collapseRealDth(img, d);
                testStrided( collapsed, dims[ d ], strides[ d ] );
            }

            {
                GenericCompositeIntervalView<ShortType, NumericComposite<ShortType>, ?> collapsed = Views.collapseNumericDth(img, d);
                testStrided( collapsed, dims[ d ], strides[ d ] );
            }
        }
    }

}
