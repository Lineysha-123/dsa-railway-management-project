package railway;

// Train.java - Complete version
class Train {
	String trainName;
	String trainNumber;
	CustomLinkedList route;
	CustomLinkedList schedule;
	CustomLinkedList bookedPassengers;

	// Category-wise seat allocation
	int acCapacity = 3;
	int slprCapacity = 4;
	int genCapacity = 5;
	int maxCapacity; // total across all categories

	int acAllocated = 0;
	int slprAllocated = 0;
	int genAllocated = 0;

	// Stacks of seat numbers freed by cancellations, reused before minting new ones
	CustomStack acFreedSeats = new CustomStack();
	CustomStack slprFreedSeats = new CustomStack();
	CustomStack genFreedSeats = new CustomStack();

	public Train(String name, String number) {
		this.trainName = name;
		this.trainNumber = number;
		this.route = new CustomLinkedList();
		this.bookedPassengers = new CustomLinkedList();
		this.schedule = new CustomLinkedList();
		this.maxCapacity = acCapacity + slprCapacity + genCapacity;
	}

	@Override
	public String toString() {
		return "Train: " + trainName + " (" + trainNumber + ") | Booked: " + getTotalBooked() + "/"
				+ maxCapacity + " | Seats -> AC:" + acAllocated + "/" + acCapacity + ", SLPR:" + slprAllocated + "/"
				+ slprCapacity + ", GEN:" + genAllocated + "/" + genCapacity;
	}

	// Booking records are never deleted (cancelled tickets stay for history),
	// so "how many are currently booked" must count only active (BOOKED-status)
	// records rather than the raw list size.
	public int getTotalBooked() {
		int count = 0;
		Node current = bookedPassengers.head;
		while (current != null) {
			Passenger p = (Passenger) current.data;
			if (Passenger.STATUS_BOOKED.equals(p.status)) count++;
			current = current.next;
		}
		return count;
	}

	public int getTotalCapacity() {
		return acCapacity + slprCapacity + genCapacity;
	}

	// ================= AVAILABLE SEATS GETTERS =================
	// These are used by RailwayService.checkAvailability()
	public int getAcAvailable() {
		return acCapacity - acAllocated + acFreedSeats.size();
	}

	public int getSlprAvailable() {
		return slprCapacity - slprAllocated + slprFreedSeats.size();
	}

	public int getGenAvailable() {
		return genCapacity - genAllocated + genFreedSeats.size();
	}
}