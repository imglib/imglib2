package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public interface Op< R extends RealType<R> > {
	public void fwd();
	public void compute(R output);
	public void getImages(Set<Image<?>> images);
	public void init(R ref);
}