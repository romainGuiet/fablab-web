package net.collaud.fablab.ws.query;

import com.owlike.genson.annotation.JsonProperty;

/**
 *
 * @author gaetan
 */
public class QueryReservationFind {

	@JsonProperty
	private String start;

	@JsonProperty
	private String end;

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "{" + "start=" + start + ", end=" + end + '}';
	}
}
