package net.imglib2.ops.image;

public abstract class AbstractImage {

	private long[] dims;
	private String[] axes;
	
	protected AbstractImage(long[] dims, String[] axes) {
		this.dims = dims;
		this.axes = axes;
	}

	protected long totalElements(long[] dimens) {
		long num = 1;
		for (long d : dimens)
			num *= d;
		return num;
	}
	
	protected int elementNumber(long[] pos) {
		int delta = 1;
		int num = 0;
		for (int i = 0; i < pos.length; i++) {
			num += delta * pos[i];
			delta *= dims[i];
		}
		return num;
	}
	
	public int numDimensions() {
		return dims.length;
	}
	
	public long dimension(int i) {
		return dims[i];
	}
	
	public String axis(int i) {
		return axes[i];
	}
	
	public long[] dimensions() { return dims; }
	
	public String[] axes() { return axes; }
}
