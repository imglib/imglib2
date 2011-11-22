package net.imglib2.algorithm.fft;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class LocalNeighborhoodCursorBehavior {

	public static final void main(String[] args) {
		
		try {
			
			Img<FloatType> img = new ArrayImgFactory<FloatType>().create(new int[]{1, 1}, new FloatType());
			LocalNeighborhoodCursor<FloatType> lnc =
				new LocalNeighborhoodCursor<FloatType>(Views.extendMirrorSingle(img).randomAccess(), 1);
			Cursor<FloatType> cursor = img.cursor();
			
			long[] pos = new long[img.numDimensions()];
			long[] lpos = new long[img.numDimensions()];
			
			while (cursor.hasNext()) {
				cursor.fwd();
				cursor.localize(pos);
				lnc.reset(pos);
				
				StringBuilder sb = new StringBuilder();
				sb.append("At pos: ").append(pos[0]);
				for (int i=1; i<pos.length; ++i) {
					sb.append(',').append(pos[i]);
				}
				sb.append(":\n");
				
				int x = 0;
				
				while (lnc.hasNext()) {
					lnc.fwd();
					lnc.localize(lpos);
					sb.append(' ').append(lpos[0]);
					for (int i=1; i<lpos.length; ++i) {
						sb.append(',').append(lpos[i]);
					}
					sb.append('\n');
					
					x++;
					System.out.println("x: " + x);
				}
				System.out.println(sb.toString());
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
