package mpicbg.imglib.scripting.math2.fn;

import java.util.Set;
import mpicbg.imglib.image.Image;

public interface IFunction {
	public double eval();
	public void findImages(Set<Image<?>> images);
}