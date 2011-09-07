package net.imglib2.io.img.virtual;


import static org.junit.Assert.*;

import java.net.URL;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;

import org.junit.Test;

public class VirtualImgTest {

	@Test
	public void testVariousThings() {
		
		// open image
		
		VirtualImg<?> image = null;
		try {
			URL fileURL = getClass().getResource("/TestImage.tif");
			image = VirtualImg.create(fileURL.getFile(), false);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		assertNotNull(image);
		
		// test out factory
		
		ImgFactory<? extends RealType<?>> factory = image.factory();
		try {
			factory.create(new long[]{1,2,3}, null);
		} catch (UnsupportedOperationException e) {
			assertTrue(true);
		}

		// test out cursor
		
		long numElements = 0;
		VirtualCursor<? extends RealType<?>> cursor = image.cursor();
		while (cursor.hasNext()) {
			cursor.next();
			numElements++;
		}
		assertEquals(20*30*10, numElements);
		assertNotNull(cursor.getCurrentPlane());
		
		// test out random access
		
		VirtualRandomAccess<? extends RealType<?>> accessor = image.randomAccess();
		long[] pos = new long[3];
		for (int x = 0; x < 20; x++) {
			for (int z = 0; z < 10; z++) {
				for (int y = 0; y < 30; y++) {
					pos[0] = x;
					pos[1] = y;
					pos[2] = z;
					accessor.setPosition(pos);
					assertEquals(x + 2*y + 3*z, accessor.get().getRealDouble(), 0);
				}
			}
		}
		assertNotNull(accessor.getCurrentPlane());

		long xDim = image.dimension(0);
		
		// test byte only code
		try {
			URL fileURL = getClass().getResource("/TestImage.tif");
			image = VirtualImg.create(fileURL.getFile(), true);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
		assertNotNull(image);
		assertEquals(xDim*2, image.dimension(0));
		
		accessor = image.randomAccess();
		for (int x = 0; x < 20; x++) {
			for (int z = 0; z < 10; z++) {
				for (int y = 0; y < 30; y++) {
					short expected = (short) (x + 2*y + 3*z); 
					int hi = (expected & 0xff00) >> 8;
					int lo = expected & 0xff;
					pos[1] = y;
					pos[2] = z;
					pos[0] = 2*x;
					accessor.setPosition(pos);
					assertEquals(hi, accessor.get().getRealDouble(), 0);
					pos[0] = 2*x+1;
					accessor.setPosition(pos);
					assertEquals(lo, accessor.get().getRealDouble(), 0);
				}
			}
		}
		assertNotNull(accessor.getCurrentPlane());
	}
}
