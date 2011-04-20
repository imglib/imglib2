
import ij.ImageJ;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import net.imglib2.algorithm.labeling.BinaryInterpolation2D;
import net.imglib2.container.shapelist.ShapeList;
import net.imglib2.container.shapelist.ShapeListCached;
import net.imglib2.image.Image;
import net.imglib2.image.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;

public class BinaryInterpolationBehavior {

	static public final void main(String[] args) {
		
		int w = 600,
			h = 400;

		BitType background = new BitType(false);
		BitType foreground = new BitType(true);

		final ShapeList<BitType> shapeList1 = new ShapeListCached<BitType>(new int[]{w, h}, background, 32);
		shapeList1.addShape(new Rectangle(50, 50, 200, 200), foreground, new int[]{0});
		final Image<BitType> img1 = new Image<BitType>(shapeList1, shapeList1.getBackground(), "ShapeListContainer");

		final ShapeList<BitType> shapeList2 = new ShapeListCached<BitType>(new int[]{w, h}, background, 32);
		shapeList2.addShape(new Ellipse2D.Float(200, 60, 180, 180), foreground, new int[]{0});
		final Image<BitType> img2 = new Image<BitType>(shapeList2, shapeList2.getBackground(), "ShapeListContainer");

		new ImageJ();
		ImageJFunctions.displayAsVirtualStack(img1).show();
		ImageJFunctions.displayAsVirtualStack(img2).show();
		
		BinaryInterpolation2D interpol = new BinaryInterpolation2D(img1, img2, 0.5f);
		if (!interpol.checkInput() || !interpol.process()) {
			System.out.println("Error: " + interpol.getErrorMessage());
		}

		ImageJFunctions.displayAsVirtualStack(interpol.getResult()).show();
		
		System.out.println("Done!");
	}
}
