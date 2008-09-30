package de.sectud.ctf07.scoringsystem;

/**
 * Hacked class; should allow an arbitrary amount of hashable and comparable
 * items; instead it allows only for two strings to be used. Each string can be
 * null.
 * 
 * The idea of this class is to be a hashable tuple. (Think of it as a python
 * tuple, with the exception that ATM it has to contain exactly two strings...)
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class StringPair {
	private final String string1;

	private final String string2;

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((string1 == null) ? 0 : string1.hashCode());
		result = PRIME * result + ((string2 == null) ? 0 : string2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StringPair other = (StringPair) obj;
		if (string1 == null) {
			if (other.string1 != null)
				return false;
		} else if (!string1.equals(other.string1))
			return false;
		if (string2 == null) {
			if (other.string2 != null)
				return false;
		} else if (!string2.equals(other.string2))
			return false;
		return true;
	}

	public StringPair(final String string1, final String string2) {
		super();
		this.string1 = string1;
		this.string2 = string2;
	}

	public String getString1() {
		return string1;
	}

	public String getString2() {
		return string2;
	}
}
