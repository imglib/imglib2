package net.imglib2.ops;

public final class Bool implements Comparable<Bool> {
	private boolean bool;
	
	public Bool() { bool = false; }
	public Bool(boolean b) { bool = b; }
	public void setBool(boolean b) { bool = b; }
	public boolean getBool() { return bool; }

	@Override
	public int compareTo(Bool other) {
		if (bool) {
			if (other.getBool())
				return 0;
			return 1;
		}
		// bool == false
		if (other.getBool())
			return -1;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bool) {
			Bool other = (Bool) obj;
			return bool == other.getBool();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		int tmp = 0;
		if (bool)
			return tmp = 1;
		result = 31 * result + tmp;
		return result;
	}
}

