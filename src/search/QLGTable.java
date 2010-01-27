package search;

import java.io.Serializable;
import java.util.HashMap;

public class QLGTable implements Serializable {

	private HashMap<QLGState, Double> table = new HashMap<QLGState, Double>();

	public double getStateValue(QLGState qls) {
		if (table.get(qls) == null) {
			return 0.0;
		} else {
			return table.get(qls);
		}
	}

	public void updateStateValue(QLGState qls, double value) {
		table.put(qls, value);
	}

}
