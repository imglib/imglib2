package net.imglib2.img;

public interface WrappedImg<T> extends Img<T> {
	Img<T> getImg();
}
