package net.imglib2.ops.util.metadata;

import net.imglib2.meta.Sourced;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public class SourcedImpl implements Sourced {

	private String m_source = "";

	public SourcedImpl() {
	}

	public SourcedImpl(String source) {
		m_source = source;
	}

	public SourcedImpl(Sourced sourced) {
		m_source = sourced.getSource();
	}

	@Override
	public void setSource(String source) {
		m_source = source;
	}

	@Override
	public String getSource() {
		return m_source;
	}

}
