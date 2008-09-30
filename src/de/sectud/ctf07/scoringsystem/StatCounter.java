package de.sectud.ctf07.scoringsystem;

import java.util.HashMap;

/**
 * This class allows to count occurrences of 'key's. A key can be any class that
 * is a) hashable and b) comparable with equals.
 * 
 * @author Hans-Christian Esperer
 * 
 * @param <K>
 *            Datatype of the key
 */
public class StatCounter<K> {
	/**
	 * Internal stats map
	 */
	private HashMap<K, MutableLong> map = new HashMap<K, MutableLong>();

	/**
	 * Construct a new class instance
	 */
	public StatCounter() {
	}

	/**
	 * Count element key.
	 * 
	 * @param key
	 *            element to count
	 */
	public void count(K key) {
		MutableLong ml = map.get(key);
		if (ml == null) {
			ml = new MutableLong();
			map.put(key, ml);
		}
		ml.inc();
	}

	/**
	 * Return how many times 'key' was counted.
	 * 
	 * @param key
	 *            element to get occurrence count of
	 * @return how many times 'key' was counted.
	 */
	public long getCount(K key) {
		MutableLong ml = map.get(key);
		if (ml == null) {
			return 0;
		}
		return ml.getValue();
	}

	/**
	 * Internal class. This implements a mutable long. Since the standard 'Long'
	 * class is not mutable, we'd have to remove an element and re-add it with
	 * an updated value to a Map if we were to change its value. Both the remove
	 * and the insert are preceded by a binary search which takes time. Using a
	 * mutable datatybe reduces this amount of time by the factor 2.
	 * 
	 * @author hc
	 * 
	 */
	public class MutableLong {
		/**
		 * Value
		 */
		private long value;

		/**
		 * Get our current value
		 * 
		 * @return current value
		 */
		public long getValue() {
			return value;
		}

		/**
		 * Set our value
		 * 
		 * @param value
		 *            value to use
		 */
		public void setValue(long value) {
			this.value = value;
		}

		/**
		 * Instanciate this class with an initial value of 0
		 */
		public MutableLong() {
			this(0);
		}

		/**
		 * Instanciate this class specifying an initial value
		 * 
		 * @param value
		 *            value to use initially
		 */
		public MutableLong(long value) {
			this.value = value;
		}

		/**
		 * Increase our current value
		 */
		public void inc() {
			value++;
		}

		/**
		 * Decrease our current value
		 */
		public void dec() {
			value--;
		}
	}
}
