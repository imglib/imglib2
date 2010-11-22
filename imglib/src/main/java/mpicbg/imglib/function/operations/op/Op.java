package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public interface Op< A extends RealType<A> > {
	public void fwd();
	public void compute(A output);
	public void getImages(Set<Image<A>> images);
	public void init(A ref);
}
