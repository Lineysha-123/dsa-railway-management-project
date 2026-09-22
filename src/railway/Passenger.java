package railway;

//Passenger.java
class Passenger {
	String name;
	String ticketId;
	String trainNumber;
	String contactNumber;
	int age;
	String gender;
	String sourceStation;
	String destinationStation;
	String dateOfJourney;
	String seatNumber;
	String seatCategory;
	int ticketFare;
	String preferredDepartureTime;

	// Lifecycle status of this ticket. A Passenger record is never deleted once
	// created -- it only transitions between these states, so booking history
	// (including cancellations) is always preserved and queryable.
	// BOOKED     -> currently holds a confirmed seat
	// WAITLISTED -> currently on the waiting list, no seat yet
	// CANCELLED  -> was booked, passenger cancelled; record is kept for history
	public static final String STATUS_BOOKED = "BOOKED";
	public static final String STATUS_WAITLISTED = "WAITLISTED";
	public static final String STATUS_CANCELLED = "CANCELLED";

	String status = STATUS_BOOKED;

	public Passenger(String name, String ticketId, String trainNumber, String contactNumber, int age, String gender,
			String sourceStation, String destinationStation, String dateOfJourney, String seatNumber,
			String seatCategory, int ticketFare, String preferredDepartureTime) {
		this.name = name;
		this.ticketId = ticketId;
		this.trainNumber = trainNumber;
		this.contactNumber = contactNumber;
		this.age = age;
		this.gender = gender;
		this.sourceStation = sourceStation;
		this.destinationStation = destinationStation;
		this.dateOfJourney = dateOfJourney;
		this.seatNumber = seatNumber;
		this.seatCategory = seatCategory;
		this.ticketFare = ticketFare;
		this.preferredDepartureTime = preferredDepartureTime;
	}
	// NEW: Priority-queue helpers for the Waiting List
	// 1 = Senior Citizen (highest), 2 = Ladies, 3 = General
	public static int computePriority(int age, String gender) {
		if (age >= 60) return 1;
		if (gender != null && gender.trim().equalsIgnoreCase("F")) return 2;
		return 3;
	}
	public int getPriorityLevel() {
		return computePriority(this.age, this.gender);
	}
	public String getPriorityLabel() {
		int level = getPriorityLevel();
		if (level == 1) return "Senior Citizen";
		if (level == 2) return "Ladies";
		return "General";
	}
	@Override
	public String toString() {
		return String.format("ID: %s | Status: %-10s | Passenger: %s | Train: %s | Seat: %s (%s) | Route: %s -> %s | Fare: Rs.%d | Date: %s",
				ticketId, status, name, trainNumber, seatNumber, seatCategory, sourceStation, destinationStation, ticketFare,
				dateOfJourney);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Passenger) {
			Passenger other = (Passenger) obj;
			return this.ticketId != null && this.ticketId.equals(other.ticketId);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ticketId != null ? ticketId.hashCode() : 0;
	}
}
