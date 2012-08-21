package net.imglib2.display.projectors.screenimages;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import net.imglib2.display.ScreenImage;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.ByteType;

/**
 * Creates an {@link Image} from a ByteType ArrayImg
 * @author zinsmaie
 *
 */
public class ByteScreenImage extends ArrayImg<ByteType, ByteArray> implements
                ScreenImage {


        private static final ColorSpace CS = ColorSpace
                        .getInstance(ColorSpace.CS_GRAY);

        private static final int[] BITS = new int[] { 8 };

        private static final ColorModel GREY8_COLOR_MODEL = new ComponentColorModel(
                        CS, BITS, false, false, Transparency.OPAQUE,
                        DataBuffer.TYPE_BYTE);


        private final BufferedImage m_image;

        public ByteScreenImage(ByteArray data, long[] dim) {
                super(data, dim, 1);
                byte[] sourceArray = data.getCurrentStorageArray();
                m_image = createBufferedImage(sourceArray, (int) dim[0],
                                (int) dim[1]);
        }

        public static BufferedImage createBufferedImage(byte[] sourceArray,
                        int width, int height) {

                DataBuffer buffer = new DataBufferByte(sourceArray,
                                sourceArray.length);

                SampleModel model = new PixelInterleavedSampleModel(
                                DataBuffer.TYPE_BYTE, width, height, 1, width,
                                new int[] { 0 });
                WritableRaster raster = Raster.createWritableRaster(model,
                                buffer, null);

                return new BufferedImage(GREY8_COLOR_MODEL, raster, false, null);
        }

        @Override
        public Image image() {
                return m_image;
        }

}