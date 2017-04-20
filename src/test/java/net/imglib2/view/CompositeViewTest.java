package net.imglib2.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.util.Pair;
import net.imglib2.view.composite.*;
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

    private void testNoArgumentDefaultsToLast() {
        long size = dims[dims.length - 1];
        {
            CompositeIntervalView< ShortType, ? extends GenericComposite< ShortType > > ref = Views.collapse(img);
            CompositeIntervalView< ShortType, ? extends GenericComposite< ShortType > > comp = Views.collapse(img, dims.length - 1);

            for (Pair<? extends GenericComposite<ShortType>, ? extends GenericComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i) {
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
                }
            }
        }


        {
            CompositeIntervalView<ShortType, RealComposite<ShortType>> ref = Views.collapseReal(img);
            CompositeIntervalView<ShortType, RealComposite<ShortType>> comp = Views.collapseReal(img, dims.length - 1);

            for (Pair<RealComposite<ShortType>, RealComposite<ShortType>> p : Views.interval(Views.pair(ref, comp), ref)) {
                for (int i = 0; i < size; ++i)
                    Assert.assertTrue(p.getA().get(i).valueEquals(p.getB().get(i)));
            }
        }


        {
            CompositeIntervalView<ShortType, NumericComposite<ShortType>> ref = Views.collapseNumeric(img);
            CompositeIntervalView<ShortType, NumericComposite<ShortType>> comp = Views.collapseNumeric(img, dims.length - 1);

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

        testNoArgumentDefaultsToLast();

        for ( int d = 0; d < dims.length; ++d ) {
            {
                CompositeIntervalView<ShortType, ? extends GenericComposite<ShortType>> collapsed = Views.collapse(img, d);
                testStrided( collapsed, dims[ d ], strides[ d ] );
            }

            {
                CompositeIntervalView<ShortType, RealComposite<ShortType>> collapsed = Views.collapseReal(img, d);
                testStrided( collapsed, dims[ d ], strides[ d ] );
            }

            {
                CompositeIntervalView<ShortType, NumericComposite<ShortType>> collapsed = Views.collapseNumeric(img, d);
                testStrided( collapsed, dims[ d ], strides[ d ] );
            }
        }
    }

}
