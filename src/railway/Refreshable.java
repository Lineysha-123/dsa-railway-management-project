package railway;

// Refreshable.java
// Implemented by any panel whose dropdowns (train numbers, station names, etc.)
// need to be re-populated every time the panel becomes visible, since trains
// and stations can be added from other panels in between visits.
public interface Refreshable {
	void refreshData();
}
