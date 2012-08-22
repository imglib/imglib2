package net.imglib2.ops.operation.img.unary;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.img.unary.ImgConvert;
import net.imglib2.ops.operation.img.unary.ImgConvert.ImgConversionTypes;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.ops.convolution.DirectImageConvolution;
import org.knime.knip.core.types.OutOfBoundsStrategyEnum;
import org.knime.knip.core.util.ImgUtils;

/**
 * Input: Outline Image {@link ExtractOutlineImg}
 *
 * @author dietzc, schoenenbergerf
 *
 */
public class CalculatePerimeter implements
                UnaryOutputOperation<Img<BitType>, DoubleType> {

        private final ImgConvert<BitType, UnsignedShortType> m_convert;

        private final DirectImageConvolution<UnsignedShortType, UnsignedShortType, UnsignedShortType, Img<UnsignedShortType>, Img<UnsignedShortType>> m_conv;

        public CalculatePerimeter() {
                // TODO make out of bounds
                m_conv = new DirectImageConvolution<UnsignedShortType, UnsignedShortType, UnsignedShortType, Img<UnsignedShortType>, Img<UnsignedShortType>>(
                                getKernel(), false,
                                OutOfBoundsStrategyEnum.MIRROR_SINGLE);

                // TODO don't do this directly!
                m_convert = new ImgConvert<BitType, UnsignedShortType>(
                                new BitType(), new UnsignedShortType(),
                                ImgConversionTypes.DIRECT);
        }

        private static synchronized Img<UnsignedShortType> getKernel() {
                @SuppressWarnings("unchecked")
                final ArrayImg<UnsignedShortType, ShortArray> img = (ArrayImg<UnsignedShortType, ShortArray>) new ArrayImgFactory<UnsignedShortType>()
                                .create(new long[] { 3, 3 },
                                                new UnsignedShortType());

                final short[] storage = img.update(null)
                                .getCurrentStorageArray();

                storage[0] = 10;
                storage[1] = 2;
                storage[2] = 10;
                storage[3] = 2;
                storage[4] = 1;
                storage[5] = 2;
                storage[6] = 10;
                storage[7] = 2;
                storage[8] = 10;

                return img;
        }

        @Override
        public DoubleType createEmptyOutput(final Img<BitType> op) {
                return new DoubleType();
        }

        @Override
        public DoubleType compute(final Img<BitType> op, final DoubleType r) {
                final Img<UnsignedShortType> img = m_conv.compute(m_convert
                                .compute(op), ImgUtils.createEmptyCopy(op,
                                new UnsignedShortType()));
                final Cursor<UnsignedShortType> c = img.cursor();

                int catA = 0;
                int catB = 0;
                int catC = 0;

                while (c.hasNext()) {
                        c.fwd();
                        final int curr = c.get().get();

                        switch (curr) {
                        case 15:
                        case 7:
                        case 25:
                        case 5:
                        case 17:
                        case 27:
                                catA++;
                                break;
                        case 21:
                        case 33:
                                catB++;
                                break;
                        case 13:
                        case 23:
                                catC++;
                                break;
                        }

                }

                r.set(catA + catB * Math.sqrt(2) + catC
                                * ((1d + Math.sqrt(2)) / 2d));

                return r;
        }

        @Override
        public UnaryOutputOperation<Img<BitType>, DoubleType> copy() {
                return new CalculatePerimeter();
        }

        @Override
        public DoubleType compute(final Img<BitType> in) {
                return compute(in, createEmptyOutput(in));
        }
}
