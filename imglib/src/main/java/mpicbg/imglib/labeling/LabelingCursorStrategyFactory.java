package mpicbg.imglib.labeling;

public interface LabelingCursorStrategyFactory<T extends Comparable<T>, L extends Labeling<T>> {
	public LabelingCursorStrategy<T, L> createLabelingCursorStrategy(L labeling);
}
