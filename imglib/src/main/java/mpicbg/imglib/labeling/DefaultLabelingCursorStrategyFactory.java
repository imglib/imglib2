package mpicbg.imglib.labeling;

public class DefaultLabelingCursorStrategyFactory<T extends Comparable<T>, L extends Labeling<T>> implements
		LabelingCursorStrategyFactory<T, L> {

	@Override
	public LabelingCursorStrategy<T, L> createLabelingCursorStrategy(L labeling) {
		// TODO Auto-generated method stub
		return new DefaultLabelingCursorStrategy<T,L>(labeling);
	}

}
