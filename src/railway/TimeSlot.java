package railway;

//TimeSlot.java
class TimeSlot {
	String stationName;
	String arrivalTime;
	String departureTime;
	public TimeSlot(String stationName, String arrivalTime, String departureTime) {
		this.stationName = stationName;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}
	@Override
	public String toString() {
		return String.format("%-15s | ARR: %-5s | DEP: %-5s", stationName, arrivalTime, departureTime);
	}
}
