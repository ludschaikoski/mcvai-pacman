package search;

import java.io.Serializable;
import java.util.HashMap;

public class QLTable implements Serializable {

	private HashMap<QLState, Double> table = new HashMap<QLState, Double>();

	public double getStateValue(QLState qls) {
		if (table.get(qls) == null) {
			return 0.0;
		} else {
			return table.get(qls);
		}
	}

	public void updateStateValue(QLState qls, double value) {
		table.put(qls, value);
	}

}
