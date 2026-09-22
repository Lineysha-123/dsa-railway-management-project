package railway;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// RailwayService.java - ALL BUSINESS LOGIC - COMPLETE FIXED VERSION
public class RailwayService {

	private Train[] trains = new Train[10];
	private int trainCount = 0;
	private PriorityPassengerQueue waitingList = new PriorityPassengerQueue();
	// Holds tickets that were cancelled while still on the waiting list. They must
	// leave the active waitingList (a cancelled ticket must never be promoted to a
	// seat), but per the history requirement their record still has to be
	// findable afterwards -- so they move here instead of disappearing.
	private CustomLinkedList cancelledWhileWaitlisted = new CustomLinkedList();
	private Graph railwayNetwork = new Graph(20);
	private int ticketIdCounter = 1001;

	private static final int BASE_FARE = 150;
	private static final double DISTANCE_RATE = 0.5;
	private static final double WEEKEND_SURCHARGE_MULTIPLIER = 1.20;

	public RailwayService() {
		initializeData();
	}

	private void initializeData() {
		railwayNetwork.addStation("Delhi");
		railwayNetwork.addStation("Mumbai");
		railwayNetwork.addStation("Kolkata");
		railwayNetwork.addStation("Chennai");
		railwayNetwork.addStation("Pune");
		railwayNetwork.addConnection("Delhi", "Mumbai", 1400);
		railwayNetwork.addConnection("Delhi", "Kolkata", 1500);
		railwayNetwork.addConnection("Mumbai", "Chennai", 1200);
		railwayNetwork.addConnection("Kolkata", "Chennai", 1600);
		railwayNetwork.addConnection("Mumbai", "Pune", 200);

		Train t1 = new Train("Rajdhani Express", "T123");
		t1.route.add("Delhi");
		t1.route.add("Mumbai");
		t1.route.add("Pune");
		t1.schedule.add(new TimeSlot("Delhi", "N/A", "08:00"));
		t1.schedule.add(new TimeSlot("Mumbai", "10:30", "10:45"));
		t1.schedule.add(new TimeSlot("Pune", "12:00", "N/A"));
		trains[trainCount++] = t1;
	}

	// ================= LOOKUP METHODS =================
	public String[] getTrainNumbers() {
		String[] result = new String[trainCount];
		for (int i = 0; i < trainCount; i++) result[i] = trains[i].trainNumber;
		return result;
	}

	public String[] getStationNames() {
		return railwayNetwork.getAllStationNames();
	}

	public Train getTrain(String number) {
		for (int i = 0; i < trainCount; i++) {
			if (trains[i].trainNumber.equals(number)) return trains[i];
		}
		return null;
	}

	private boolean isTrainNumberDuplicate(String number) {
		for (int i = 0; i < trainCount; i++) {
			if (trains[i].trainNumber.equals(number)) return true;
		}
		return false;
	}

	public String[] getRouteStationsArray(String trainNumber) {
		Train train = getTrain(trainNumber);
		if (train == null) return new String[0];
		String[] result = new String[train.route.size()];
		Node current = train.route.head;
		int i = 0;
		while (current != null) { result[i++] = (String) current.data; current = current.next; }
		return result;
	}

	// ================= FARE HELPERS =================

	private int getSeatCategoryCharge(String category) {
		if (category == null) return -1;
		switch (category.trim().toUpperCase()) {
			case "AC": return 500;
			case "SLPR": return 150;
			case "GEN": return 0;
			default: return -1;
		}
	}

	private boolean isWeekend(String dateOfJourney) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate date = LocalDate.parse(dateOfJourney.trim(), formatter);
			DayOfWeek day = date.getDayOfWeek();
			return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
		} catch (DateTimeParseException | NullPointerException e) {
			return false;
		}
	}

	private int getTrainRouteDistance(Train train, String source, String destination) {
		int srcIdx = train.route.indexOf(source.trim());
		int destIdx = train.route.indexOf(destination.trim());
		if (srcIdx == -1 || destIdx == -1 || srcIdx >= destIdx) return -1;

		int totalDistance = 0;
		Node current = train.route.head;
		int idx = 0;
		String prevStation = null;

		while (current != null) {
			String station = (String) current.data;
			if (idx > srcIdx && idx <= destIdx) {
				int d = railwayNetwork.getDirectDistance(prevStation, station);
				if (d == -1) return -1;
				totalDistance += d;
			}
			prevStation = station;
			idx++;
			current = current.next;
		}
		return totalDistance;
	}

	private int calculateFareForTrain(Train train, String source, String destination, String category, String date) {
		int distance = getTrainRouteDistance(train, source, destination);
		if (distance == -1) return -1;

		int categoryCharge = getSeatCategoryCharge(category);
		if (categoryCharge == -1) return -1;

		double fare = BASE_FARE + (distance * DISTANCE_RATE) + categoryCharge;
		if (isWeekend(date)) fare *= WEEKEND_SURCHARGE_MULTIPLIER;
		return (int) Math.round(fare);
	}

	private int timeToInt(String time) {
		if (time == null || time.equalsIgnoreCase("N/A")) return -1;
		try {
			String[] parts = time.split(":");
			return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]);
		} catch (Exception e) {
			return -1;
		}
	}

	private String formatTime(int timeInt) {
		if (timeInt == -1) return "N/A";
		return String.format("%02d:%02d", timeInt / 100, timeInt % 100);
	}

	// ================= SEAT ALLOCATION =================

	private String allocateSeat(Train train, String category) {
		String cat = category.trim().toUpperCase();
		switch (cat) {
			case "AC":
				if (!train.acFreedSeats.isEmpty()) {
					int seatNum = (Integer) train.acFreedSeats.pop();
					train.acAllocated++;
					return "AC-" + String.format("%02d", seatNum);
				}
				if (train.acAllocated < train.acCapacity) {
					train.acAllocated++;
					return "AC-" + String.format("%02d", train.acAllocated);
				}
				return null;
			case "SLPR":
				if (!train.slprFreedSeats.isEmpty()) {
					int seatNum = (Integer) train.slprFreedSeats.pop();
					train.slprAllocated++;
					return "SL-" + String.format("%02d", seatNum);
				}
				if (train.slprAllocated < train.slprCapacity) {
					train.slprAllocated++;
					return "SL-" + String.format("%02d", train.slprAllocated);
				}
				return null;
			case "GEN":
				if (!train.genFreedSeats.isEmpty()) {
					int seatNum = (Integer) train.genFreedSeats.pop();
					train.genAllocated++;
					return "GN-" + String.format("%02d", seatNum);
				}
				if (train.genAllocated < train.genCapacity) {
					train.genAllocated++;
					return "GN-" + String.format("%02d", train.genAllocated);
				}
				return null;
			default:
				return null;
		}
	}

	private void releaseSeat(Train train, String category, String seatNumber) {
		if (category == null || seatNumber == null || !seatNumber.contains("-")) return;
		try {
			int num = Integer.parseInt(seatNumber.substring(seatNumber.lastIndexOf('-') + 1));
			switch (category.trim().toUpperCase()) {
				case "AC": train.acFreedSeats.push(num); break;
				case "SLPR": train.slprFreedSeats.push(num); break;
				case "GEN": train.genFreedSeats.push(num); break;
			}
		} catch (NumberFormatException e) {
			// malformed seat number
		}
	}

	private boolean isStationInRoute(Train train, String station) {
		Node current = train.route.head;
		String clean = station.trim();
		while (current != null) {
			if (((String) current.data).trim().equalsIgnoreCase(clean)) return true;
			current = current.next;
		}
		return false;
	}

	// ================= 1. ADD TRAIN =================

	public String addNewTrain(String name, String number) {
		if (name == null || name.trim().isEmpty() || number == null || number.trim().isEmpty()) {
			return "Validation Error: Train name and number cannot be empty.";
		}
		if (trainCount >= trains.length) return "Error: Train capacity full (max " + trains.length + " trains).";
		if (isTrainNumberDuplicate(number.trim())) {
			return "Validation Error: Train Number " + number + " already exists. Operation aborted.";
		}
		Train newTrain = new Train(name.trim(), number.trim());
		trains[trainCount++] = newTrain;
		return "Success: Train " + number + " (" + name + ") added.\nAC capacity: " + newTrain.acCapacity
				+ " | SLPR capacity: " + newTrain.slprCapacity + " | GEN capacity: " + newTrain.genCapacity;
	}

	// ================= 2. DISPLAY ALL TRAINS =================

	public String listTrains() {
		if (trainCount == 0) return "No trains available.";
		StringBuilder sb = new StringBuilder("--- Available Trains ---\n");
		for (int i = 0; i < trainCount; i++) {
			sb.append((i + 1)).append(". ").append(trains[i].toString()).append("\n");
		}
		return sb.toString();
	}

	// ================= 3. ADD STATION TO ROUTE =================

	public String addStationToRoute(String trainNumber, String station, String distanceStr) {
		Train train = getTrain(trainNumber);
		if (train == null) return "Error: Train not found.";
		if (station == null || station.trim().isEmpty()) return "Validation Error: Station name cannot be empty.";
		String cleanStation = station.trim();

		String lastStation = null;
		if (!train.route.isEmpty()) {
			lastStation = ((String) train.route.get(train.route.size() - 1)).trim();
		}
		train.route.add(cleanStation);
		railwayNetwork.addStation(cleanStation);
		// Automatically synchronize train schedule with the route
		boolean hasStationInSchedule = false;
		Node scNode = train.schedule.head;
		while (scNode != null) {
			TimeSlot ts = (TimeSlot) scNode.data;
			if (ts.stationName.equalsIgnoreCase(cleanStation)) {
				hasStationInSchedule = true;
				break;
			}
			scNode = scNode.next;
		}
		if (!hasStationInSchedule) {
			train.schedule.add(new TimeSlot(cleanStation, "N/A", "N/A"));
		}

		StringBuilder sb = new StringBuilder();
		if (lastStation != null) {
			int distance;
			try {
				distance = Integer.parseInt(distanceStr.trim());
				if (distance <= 0) {
					sb.append("Validation Warning: Distance must be positive. Using default 10 km.\n");
					distance = 10;
				}
			} catch (Exception e) {
				sb.append("Validation Warning: Invalid distance entered. Using default 100 km.\n");
				distance = 100;
			}
			railwayNetwork.addConnection(lastStation, cleanStation, distance);
			sb.append("Info: New network connection added: " + lastStation + " <-> " + cleanStation + " (" + distance + " km)\n");
		}
		sb.append("Success: Station " + cleanStation + " added to route for " + train.trainNumber);
		return sb.toString();
	}

	// ================= 4. DISPLAY TRAIN ROUTE =================

	public String getTrainRouteDisplay(String trainNumber) {
		Train train = getTrain(trainNumber);
		if (train == null) return "Error: Train not found.";
		StringBuilder sb = new StringBuilder("--- Route for " + train.trainName + " (" + train.trainNumber + ") ---\n");
		Node current = train.route.head;
		if (current == null) { sb.append("Route is empty."); return sb.toString(); }
		sb.append("[ ");
		while (current != null) {
			sb.append(current.data.toString());
			if (current.next != null) sb.append(" -> ");
			current = current.next;
		}
		sb.append(" ]");
		return sb.toString();
	}

	// ================= 5. SCHEDULE =================

	public String applySchedule(String trainNumber, String[] arrivals, String[] departures) {
		Train train = getTrain(trainNumber);
		if (train == null) return "Error: Train not found.";
		String[] stations = getRouteStationsArray(trainNumber);
		if (stations.length == 0) return "Error: Train has no route defined yet.";

		for (int i = 0; i < stations.length; i++) {
			String arrival = (i == 0) ? "N/A" : (arrivals[i] == null || arrivals[i].trim().isEmpty() ? "N/A" : arrivals[i].trim());
			String departure = (i == stations.length - 1) ? "N/A" : (departures[i] == null || departures[i].trim().isEmpty() ? "N/A" : departures[i].trim());
			if (!isValidTimeFormat(arrival) || !isValidTimeFormat(departure)) {
				return "Validation Error: Invalid time format at station '" + stations[i] + "' (use HH:MM or N/A). Schedule aborted.";
			}
		}

		for (int i = 1; i < stations.length; i++) {
			String prevDepStr = (i-1 == stations.length - 1) ? "N/A" : departures[i-1];
			String currArrStr = arrivals[i];
			int prevDep = timeToInt(prevDepStr);
			int currArr = timeToInt(currArrStr);
			if (prevDep != -1 && currArr != -1 && currArr < prevDep) {
				return "Validation Error: Arrival at " + stations[i] + " (" + currArrStr +
						") is before departure from " + stations[i-1] + " (" + prevDepStr + ")";
			}
			int arr = timeToInt(arrivals[i]);
			int dep = timeToInt(departures[i]);
			if (arr != -1 && dep != -1 && dep < arr) {
				return "Validation Error: Departure before arrival at " + stations[i];
			}
		}

		CustomLinkedList newSchedule = new CustomLinkedList();
		for (int i = 0; i < stations.length; i++) {
			String arrival = (i == 0) ? "N/A" : (arrivals[i] == null || arrivals[i].trim().isEmpty() ? "N/A" : arrivals[i].trim());
			String departure = (i == stations.length - 1) ? "N/A" : (departures[i] == null || departures[i].trim().isEmpty() ? "N/A" : departures[i].trim());
			newSchedule.add(new TimeSlot(stations[i].trim(), arrival, departure));
		}
		train.schedule = newSchedule;
		return "Success: Schedule updated for Train " + train.trainNumber + ".\n\n" + buildScheduleDisplay(train);
	}

	public TimeSlot getStationTimeSlot(String trainNumber, String stationName) {
		Train t = getTrain(trainNumber);
		if (t == null || stationName == null) return null;
		Node current = t.schedule.head;
		while (current != null) {
			TimeSlot ts = (TimeSlot) current.data;
			if (ts.stationName.equalsIgnoreCase(stationName.trim())) return ts;
			current = current.next;
		}
		return null;
	}

	public String getStationArrival(String trainNumber, String stationName) {
		TimeSlot ts = getStationTimeSlot(trainNumber, stationName);
		return ts != null ? ts.arrivalTime : "N/A";
	}

	public String getStationDeparture(String trainNumber, String stationName) {
		TimeSlot ts = getStationTimeSlot(trainNumber, stationName);
		return ts != null ? ts.departureTime : "N/A";
	}

	// Set or update the schedule for an individual station on a train's route
	public String setStationSchedule(String trainNumber, String stationName, String arrival, String departure) {
		Train train = getTrain(trainNumber);
		if (train == null) return "Error: Train not found.";
		if (stationName == null || stationName.trim().isEmpty()) return "Validation Error: Please select a station.";
		String cleanStation = stationName.trim();
		if (train.route.indexOf(cleanStation) == -1) {
			return "Validation Error: Station '" + cleanStation + "' is not on the route for Train " + train.trainNumber + ".";
		}

		String arr = (arrival == null || arrival.trim().isEmpty()) ? "N/A" : arrival.trim();
		String dep = (departure == null || departure.trim().isEmpty()) ? "N/A" : departure.trim();

		if (!isValidTimeFormat(arr)) return "Validation Error: Invalid arrival time '" + arr + "'. Use HH:MM format (24-hour) or N/A.";
		if (!isValidTimeFormat(dep)) return "Validation Error: Invalid departure time '" + dep + "'. Use HH:MM format (24-hour) or N/A.";

		// Origin station has no arrival
		if (train.route.indexOf(cleanStation) == 0 && !arr.equalsIgnoreCase("N/A")) {
			arr = "N/A";
		}
		// Final terminus station has no departure
		if (train.route.indexOf(cleanStation) == train.route.size() - 1 && !dep.equalsIgnoreCase("N/A")) {
			dep = "N/A";
		}

		boolean updated = false;
		Node current = train.schedule.head;
		while (current != null) {
			TimeSlot ts = (TimeSlot) current.data;
			if (ts.stationName.equalsIgnoreCase(cleanStation)) {
				ts.arrivalTime = arr;
				ts.departureTime = dep;
				updated = true;
				break;
			}
			current = current.next;
		}

		if (!updated) {
			train.schedule.add(new TimeSlot(cleanStation, arr, dep));
		}

		return "Success: Schedule updated for station '" + cleanStation + "' on Train " + train.trainNumber + " (" + train.trainName + ").\n\n" + buildScheduleDisplay(train);
	}

	private boolean isValidTimeFormat(String time) {
		if (time == null) return false;
		if (time.equalsIgnoreCase("N/A")) return true;
		return time.matches("\\d{2}:\\d{2}");
	}

	public String getTrainScheduleDisplay(String trainNumber) {
		Train train = getTrain(trainNumber);
		if (train == null) return "Error: Train not found.";
		return buildScheduleDisplay(train);
	}

	private String buildScheduleDisplay(Train train) {
		StringBuilder sb = new StringBuilder();
		sb.append("--- Schedule for " + train.trainName + " (" + train.trainNumber + ") ---\n");
		sb.append("-------------------------------------------------\n");
		sb.append(String.format("%-15s | %-9s | %-9s%n", "Station", "Arrival", "Departure"));
		sb.append("-------------------------------------------------\n");
		Node current = train.schedule.head;
		while (current != null) {
			TimeSlot ts = (TimeSlot) current.data;
			sb.append(ts.toString()).append("\n");
			current = current.next;
		}
		sb.append("-------------------------------------------------");
		if (train.schedule.isEmpty()) sb.append("No schedule set yet.");
		return sb.toString();
	}

	// ================= 6. BOOK TICKET =================

	public static class FareQuote {
		public int distance;
		public int baseFare;
		public double distanceCharge;
		public int categoryCharge;
		public boolean weekend;
		public int totalFare;
		public String error;
	}

	public FareQuote quoteFare(String source, String destination, String category, String dateOfJourney) {
		FareQuote q = new FareQuote();
		if (source.trim().equalsIgnoreCase(destination.trim())) {
			q.error = "Source and Destination stations cannot be the same.";
			return q;
		}
		if (railwayNetwork.getIndex(source.trim()) == -1 || railwayNetwork.getIndex(destination.trim()) == -1) {
			q.error = "Source or Destination station does not exist in the railway network.";
			return q;
		}
		int categoryCharge = getSeatCategoryCharge(category);
		if (categoryCharge == -1) { q.error = "Invalid seat category. Use AC, SLPR, or GEN."; return q; }
		Graph.PathResult shortest = railwayNetwork.findMinimumDistancePath(source, destination);
		q.distance = shortest.distance >= 0 ? shortest.distance : 0;
		q.baseFare = BASE_FARE;
		q.distanceCharge = q.distance * DISTANCE_RATE;
		q.categoryCharge = categoryCharge;
		q.weekend = isWeekend(dateOfJourney);
		double subtotal = q.baseFare + q.distanceCharge + q.categoryCharge;
		if (q.weekend) subtotal *= WEEKEND_SURCHARGE_MULTIPLIER;
		q.totalFare = (int) Math.round(subtotal);
		return q;
	}

	public String bookTicket(String trainNumber, String name, String contact, String ageStr, String gender,
							 String dateOfJourney, String source, String destination, String seatCategory, String preferredTime) {
		Train train = getTrain(trainNumber);
		if (train == null) return "Error: Train not found.";
		if (name == null || name.trim().isEmpty()) return "Validation Error: Passenger name is required.";
		int age;
		try {
			age = Integer.parseInt(ageStr.trim());
		} catch (Exception e) {
			return "Validation Error: Invalid age entered. Booking aborted.";
		}
		String cleanSource = source.trim();
		String cleanDestination = destination.trim();

		if (cleanSource.equalsIgnoreCase(cleanDestination)) {
			return "Validation Error: Source and Destination stations cannot be the same.";
		}
		if (railwayNetwork.getIndex(cleanSource) == -1 || railwayNetwork.getIndex(cleanDestination) == -1) {
			return "Validation Error: Source or Destination station does not exist in the global railway network.";
		}

		int fare = calculateFareForTrain(train, cleanSource, cleanDestination, seatCategory, dateOfJourney);
		if (fare == -1) return "Validation Error: Unable to calculate fare. Check route or category.";

		if (!isStationInRoute(train, source) || !isStationInRoute(train, destination)) {
			return "Validation Error: Source/Destination stations are NOT in the train route. Booking aborted.";
		}
		int sourceIndex = train.route.indexOf(cleanSource);
		int destinationIndex = train.route.indexOf(cleanDestination);
		if (sourceIndex == -1 || destinationIndex == -1 || sourceIndex >= destinationIndex) {
			return "Validation Error: Invalid route order. Source must come before Destination in the train sequence.";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Fare Breakdown:\n");
		sb.append("  Base Fare        : Rs. " + BASE_FARE + "\n");
		int distance = getTrainRouteDistance(train, cleanSource, cleanDestination);
		sb.append("  Distance Charge  : Rs. " + String.format("%.2f", distance * DISTANCE_RATE) + " (" + distance + " km)\n");
		sb.append("  Category Charge  : Rs. " + getSeatCategoryCharge(seatCategory) + "\n");
		if (isWeekend(dateOfJourney)) sb.append("  Weekend Surcharge: +20%\n");
		sb.append("  ------------------------------\n");
		sb.append("  Total Fare       : Rs. " + fare + "\n\n");

		String ticketId = "P" + (ticketIdCounter++);
		String cleanCategory = seatCategory.trim().toUpperCase();
		String allocatedSeat = allocateSeat(train, cleanCategory);
		Passenger p = new Passenger(name.trim(), ticketId, train.trainNumber, contact == null ? "N/A" : contact.trim(),
				age, gender == null ? "N/A" : gender.trim(), cleanSource, cleanDestination, dateOfJourney.trim(),
				allocatedSeat != null ? allocatedSeat : "WAITLIST", cleanCategory, fare,
				preferredTime == null ? "N/A" : preferredTime.trim());

		if (allocatedSeat != null) {
			p.status = Passenger.STATUS_BOOKED;
			train.bookedPassengers.add(p);
			sb.append("Success: Ticket booked. ID: " + ticketId + " | Seat: " + allocatedSeat + " | Fare: Rs. " + fare);
		} else {
			p.status = Passenger.STATUS_WAITLISTED;
			int priority = Passenger.computePriority(age, gender);
			waitingList.enqueue(p, priority);
			sb.append("Info: " + cleanCategory + " class full on " + train.trainNumber
					+ ". Added to waiting list (Priority: " + p.getPriorityLabel() + "). ID: " + ticketId + " | Fare: Rs. " + fare);
		}
		return sb.toString();
	}

	// ================= 7. TICKET DETAILS =================

	// Searches both the per-train booking records (BOOKED or CANCELLED tickets)
	// and the waiting list (WAITLISTED tickets). Records are never deleted, so
	// a cancelled ticket is still found here -- with its status shown as CANCELLED --
	// instead of appearing as if it never existed.
	private Passenger findPassengerByTicketId(String targetTicketId) {
		for (int i = 0; i < trainCount; i++) {
			Node current = trains[i].bookedPassengers.head;
			while (current != null) {
				Passenger p = (Passenger) current.data;
				if (p.ticketId.equalsIgnoreCase(targetTicketId)) return p;
				current = current.next;
			}
		}
		PriorityNode current = waitingList.getHead();
		while (current != null) {
			if (current.passenger.ticketId.equalsIgnoreCase(targetTicketId)) return current.passenger;
			current = current.next;
		}
		Node histNode = cancelledWhileWaitlisted.head;
		while (histNode != null) {
			Passenger p = (Passenger) histNode.data;
			if (p.ticketId.equalsIgnoreCase(targetTicketId)) return p;
			histNode = histNode.next;
		}
		return null;
	}

	public String getTicketDetails(String targetTicketId) {
		Passenger p = findPassengerByTicketId(targetTicketId);
		if (p == null) return "Error: Ticket ID '" + targetTicketId + "' not found.";
		StringBuilder sb = new StringBuilder();
		sb.append("TICKET DETAILS:\n------------------------------------\n");
		sb.append("Ticket ID:      " + p.ticketId + "\n");
		sb.append("Status:         " + p.status + "\n");
		sb.append("Train No:       " + p.trainNumber + "\n");
		sb.append("Passenger Name: " + p.name + "\n");
		sb.append("Age/Gender:     " + p.age + "/" + p.gender + "\n");
		sb.append("Contact No:     " + p.contactNumber + "\n");
		sb.append("Route:          " + p.sourceStation + " -> " + p.destinationStation + "\n");
		sb.append("Date:           " + p.dateOfJourney + "\n");
		sb.append("Seat:           " + p.seatNumber + " (" + p.seatCategory + ")\n");
		sb.append("Ticket Fare:    Rs. " + p.ticketFare + "\n");
		sb.append("------------------------------------");
		return sb.toString();
	}

	// Multi-Field Search (Ticket ID, Passenger Name, Contact Number)
	public String searchTickets(String query) {
		if (query == null || query.trim().isEmpty()) {
			return "Validation Error: Please enter a Ticket ID, Passenger Name, or Contact Number to search.";
		}
		String q = query.trim().toLowerCase();
		CustomLinkedList matches = new CustomLinkedList();

		for (int i = 0; i < trainCount; i++) {
			Node current = trains[i].bookedPassengers.head;
			while (current != null) {
				Passenger p = (Passenger) current.data;
				if (matchesPassenger(p, q)) matches.add(p);
				current = current.next;
			}
		}

		PriorityNode pNode = waitingList.getHead();
		while (pNode != null) {
			if (matchesPassenger(pNode.passenger, q)) matches.add(pNode.passenger);
			pNode = pNode.next;
		}

		Node histNode = cancelledWhileWaitlisted.head;
		while (histNode != null) {
			Passenger p = (Passenger) histNode.data;
			if (matchesPassenger(p, q)) matches.add(p);
			histNode = histNode.next;
		}

		if (matches.isEmpty()) {
			return "No tickets found matching query: '" + query + "'.\nTip: You can search by Ticket ID (e.g. P1001), passenger name, or phone number.";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("=== TICKET SEARCH RESULTS ('" + query + "') - " + matches.size() + " MATCH(ES) ===\n\n");
		Node curr = matches.head;
		int rank = 1;
		while (curr != null) {
			Passenger p = (Passenger) curr.data;
			sb.append(rank++).append(". ").append(p.toString()).append("\n");
			curr = curr.next;
		}
		return sb.toString();
	}

	private boolean matchesPassenger(Passenger p, String q) {
		if (p == null) return false;
		if (p.ticketId != null && p.ticketId.toLowerCase().contains(q)) return true;
		if (p.name != null && p.name.toLowerCase().contains(q)) return true;
		if (p.contactNumber != null && p.contactNumber.toLowerCase().contains(q)) return true;
		return false;
	}

	// Printable Ticket Receipt Generator (Export to text file)
	public String exportTicketReceipt(String ticketIdOrQuery) {
		if (ticketIdOrQuery == null || ticketIdOrQuery.trim().isEmpty()) {
			return "Validation Error: Please specify a Ticket ID, Passenger Name, or Contact to export receipt.";
		}
		String q = ticketIdOrQuery.trim();
		Passenger p = findPassengerByTicketId(q);

		if (p == null) {
			String qLower = q.toLowerCase();
			for (int i = 0; i < trainCount; i++) {
				Node current = trains[i].bookedPassengers.head;
				while (current != null) {
					Passenger cand = (Passenger) current.data;
					if (matchesPassenger(cand, qLower)) { p = cand; break; }
					current = current.next;
				}
				if (p != null) break;
			}
		}

		if (p == null) {
			return "Error: No passenger ticket found matching '" + ticketIdOrQuery + "'.";
		}

		Train t = getTrain(p.trainNumber);
		String trainName = t != null ? t.trainName : "Express";

		StringBuilder receipt = new StringBuilder();
		receipt.append("======================================================================\n");
		receipt.append("            SMART RAILWAY NETWORK - OFFICIAL TICKET RECEIPT           \n");
		receipt.append("======================================================================\n\n");
		receipt.append(String.format(" TICKET NUMBER  : %-22s BOOKING STATUS : %s\n", p.ticketId, p.status));
		receipt.append(String.format(" TRAIN DETAILS  : %s (%s)\n", trainName, p.trainNumber));
		receipt.append(String.format(" PASSENGER NAME : %-22s AGE / GENDER   : %d / %s\n", p.name, p.age, p.gender));
		receipt.append(String.format(" CONTACT NUMBER : %-22s PRIORITY TIER  : %s\n", p.contactNumber, p.getPriorityLabel()));
		receipt.append(String.format(" JOURNEY ROUTE  : %s  ==>  %s\n", p.sourceStation, p.destinationStation));
		receipt.append(String.format(" TRAVEL DATE    : %-22s SEAT ALLOCATION: %s (%s)\n", p.dateOfJourney, p.seatNumber, p.seatCategory));
		receipt.append(String.format(" TOTAL FARE     : Rs. %-18d PAYMENT STATUS : CONFIRMED (PAID)\n\n", p.ticketFare));
		receipt.append("======================================================================\n");
		receipt.append(" TRAVEL INSTRUCTIONS & SYSTEM POLICIES:\n");
		receipt.append(" 1. Please carry this receipt along with a valid government photo ID.\n");
		receipt.append(" 2. For waitlisted tickets, seat promotion is executed automatically\n");
		receipt.append("    via demographic Priority Queue (Senior Citizens > Ladies > General).\n");
		receipt.append(" 3. Cancellations instantly release seats to the LIFO category stack.\n");
		receipt.append("======================================================================\n");

		try {
			java.io.File dir = new java.io.File("receipts");
			if (!dir.exists()) dir.mkdirs();
			java.io.File file = new java.io.File(dir, "ticket_" + p.ticketId + ".txt");
			try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
				writer.write(receipt.toString());
			}
			return "SUCCESS: Official ticket receipt saved to: receipts/ticket_" + p.ticketId + ".txt\n\n" + receipt.toString();
		} catch (Exception e) {
			return "Receipt Generated (Preview):\n\n" + receipt.toString();
		}
	}

	// ================= 8. CANCEL TICKET =================

	public String cancelTicket(String ticketId) {
		if (ticketId == null || ticketId.trim().isEmpty()) return "Validation Error: Ticket ID is required.";

		Passenger target = null;
		Train targetTrain = null;
		for (int i = 0; i < trainCount; i++) {
			Train t = trains[i];
			Node current = t.bookedPassengers.head;
			while (current != null) {
				Passenger p = (Passenger) current.data;
				if (p.ticketId.equalsIgnoreCase(ticketId.trim())) {
					target = p;
					targetTrain = t;
					break;
				}
				current = current.next;
			}
			if (target != null) break;
		}

		// A waitlisted passenger (never allocated a seat) can also be cancelled --
		// they simply leave the waiting list with no seat to release.
		if (target == null) {
			PriorityNode current = waitingList.getHead();
			while (current != null) {
				if (current.passenger.ticketId.equalsIgnoreCase(ticketId.trim())) {
					Passenger waitlisted = current.passenger;
					waitlisted.status = Passenger.STATUS_CANCELLED;
					removeFromWaitingList(waitlisted);
					cancelledWhileWaitlisted.add(waitlisted);
					return "Success: Waitlisted ticket " + ticketId + " for " + waitlisted.name + " cancelled.";
				}
				current = current.next;
			}
			Node histNode = cancelledWhileWaitlisted.head;
			while (histNode != null) {
				Passenger p = (Passenger) histNode.data;
				if (p.ticketId.equalsIgnoreCase(ticketId.trim())) {
					return "Error: Ticket " + ticketId + " has already been cancelled.";
				}
				histNode = histNode.next;
			}
			return "Error: Ticket ID not found.";
		}

		if (Passenger.STATUS_CANCELLED.equals(target.status)) {
			return "Error: Ticket " + ticketId + " has already been cancelled.";
		}

		// Mark cancelled instead of deleting -- the record stays visible in
		// getTicketDetails()/history exactly as it was, just with a new status.
		target.status = Passenger.STATUS_CANCELLED;
		if (target.seatNumber != null && !target.seatNumber.equals("WAITLIST")) {
			releaseSeat(targetTrain, target.seatCategory, target.seatNumber);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Success: Ticket " + ticketId + " cancelled. Seat " + target.seatNumber + " released.\n");
		sb.append(promoteFromWaitingList(targetTrain));
		return sb.toString();
	}

	private void removeFromWaitingList(Passenger target) {
		PriorityPassengerQueue tempQueue = new PriorityPassengerQueue();
		while (!waitingList.isEmpty()) {
			Passenger p = waitingList.dequeue();
			if (p != target) tempQueue.enqueue(p, p.getPriorityLevel());
		}
		while (!tempQueue.isEmpty()) {
			Passenger p = tempQueue.dequeue();
			waitingList.enqueue(p, p.getPriorityLevel());
		}
	}

	private String promoteFromWaitingList(Train targetTrain) {
		if (waitingList.isEmpty()) return "Waiting list is empty.";

		PriorityPassengerQueue tempQueue = new PriorityPassengerQueue();
		String result = "No waiting passenger could be promoted.";

		while (!waitingList.isEmpty()) {
			Passenger candidate = waitingList.dequeue();
			String seat = allocateSeat(targetTrain, candidate.seatCategory);
			if (seat != null) {
				candidate.seatNumber = seat;
				candidate.status = Passenger.STATUS_BOOKED;
				targetTrain.bookedPassengers.add(candidate);
				result = "Promoted: " + candidate.name + " (Priority: " + candidate.getPriorityLabel() +
						") to seat " + seat + " (Ticket: " + candidate.ticketId + ")";
				while (!waitingList.isEmpty()) {
					Passenger remaining = waitingList.dequeue();
					tempQueue.enqueue(remaining, remaining.getPriorityLevel());
				}
				while (!tempQueue.isEmpty()) {
					Passenger p = tempQueue.dequeue();
					waitingList.enqueue(p, p.getPriorityLevel());
				}
				return result;
			} else {
				tempQueue.enqueue(candidate, candidate.getPriorityLevel());
			}
		}
		while (!tempQueue.isEmpty()) {
			Passenger p = tempQueue.dequeue();
			waitingList.enqueue(p, p.getPriorityLevel());
		}
		return result;
	}

	// ================= 9. WAITING QUEUE =================

	public String getWaitingQueueDisplay() {
		if (waitingList.isEmpty()) return "Waiting list is empty.";
		StringBuilder sb = new StringBuilder("--- Waiting Passenger Queue (Priority Order) ---\n");
		int rank = 1;
		PriorityNode current = waitingList.getHead();
		while (current != null) {
			sb.append(rank + ". [" + current.passenger.getPriorityLabel() + "] " + current.passenger + "\n");
			rank++;
			current = current.next;
		}
		return sb.toString();
	}

	// ================= 10 & 11. PATHFINDING =================

	public String findShortestHopPath(String start, String end) {
		if (start.trim().equalsIgnoreCase(end.trim())) return "Error: Starting and Destination stations cannot be the same.";
		Graph.PathResult result = railwayNetwork.findShortestHopPath(start, end);
		if (result.distance >= 0) return "Fewest-Hop Route from " + start.trim() + " to " + end.trim() + ":\n" + result;
		return "Error: " + result.path;
	}

	public String findMinimumDistancePath(String start, String end) {
		if (start.trim().equalsIgnoreCase(end.trim())) return "Error: Starting and Destination stations cannot be the same.";
		Graph.PathResult result = railwayNetwork.findMinimumDistancePath(start, end);
		if (result.distance >= 0) return "Minimum Distance Route from " + start.trim() + " to " + end.trim() + ":\n" + result;
		return "Error: " + result.path;
	}

	// ================= 12. TRAIN RECOMMENDATION =================

	public String recommendTrains(String source, String destination, String category, String date) {
		if (source.trim().equalsIgnoreCase(destination.trim())) return "Validation Error: Source and Destination stations cannot be the same.";
		if (getSeatCategoryCharge(category) == -1) return "Validation Error: Invalid seat category.";

		Train minDistanceTrain = null, cheapestTrain = null, earliestTrain = null, minStopsTrain = null;
		int minDistance = Integer.MAX_VALUE, minFare = Integer.MAX_VALUE, minStops = Integer.MAX_VALUE, earliestTime = Integer.MAX_VALUE;
		boolean anyMatch = false;

		for (int i = 0; i < trainCount; i++) {
			Train t = trains[i];
			int srcIdx = t.route.indexOf(source);
			int destIdx = t.route.indexOf(destination);
			if (srcIdx == -1 || destIdx == -1 || srcIdx >= destIdx) continue;
			anyMatch = true;

			int segmentDistance = getTrainRouteDistance(t, source, destination);
			if (segmentDistance >= 0 && segmentDistance < minDistance) {
				minDistance = segmentDistance;
				minDistanceTrain = t;
			}

			int fare = calculateFareForTrain(t, source, destination, category, date);
			if (fare >= 0 && fare < minFare) {
				minFare = fare;
				cheapestTrain = t;
			}

			int stops = destIdx - srcIdx;
			if (stops < minStops) {
				minStops = stops;
				minStopsTrain = t;
			}

			int depTime = getDepartureTimeAt(t, source);
			if (depTime != -1 && depTime < earliestTime) {
				earliestTime = depTime;
				earliestTrain = t;
			}
		}

		if (!anyMatch) return "No trains found connecting " + source + " to " + destination + " in valid route order.";

		StringBuilder sb = new StringBuilder("--- Recommendations for " + source + " -> " + destination + " ---\n");
		sb.append("1. Minimum Distance Train : " + (minDistanceTrain != null ? minDistanceTrain.trainNumber + " (" + minDistance + " km)" : "N/A") + "\n");
		sb.append("2. Cheapest Train         : " + (cheapestTrain != null ? cheapestTrain.trainNumber + " (Rs. " + minFare + ")" : "N/A") + "\n");
		sb.append("3. Earliest Departure     : " + (earliestTrain != null ? earliestTrain.trainNumber + " (" + formatTime(earliestTime) + ")" : "N/A (no schedule data)") + "\n");
		sb.append("4. Minimum Stops Train    : " + (minStopsTrain != null ? minStopsTrain.trainNumber + " (" + minStops + " stop(s))" : "N/A"));
		return sb.toString();
	}

	private int getDepartureTimeAt(Train t, String stationName) {
		Node current = t.schedule.head;
		while (current != null) {
			TimeSlot ts = (TimeSlot) current.data;
			if (ts.stationName.equalsIgnoreCase(stationName.trim())) return timeToInt(ts.departureTime);
			current = current.next;
		}
		return -1;
	}

	// ================= 13. NETWORK MAP =================

	public String getNetworkMap() {
		return railwayNetwork.getNetworkMapString();
	}

	// NOTE: The previous global "Undo Last Operation" feature has been removed.
	// It let an admin silently reverse ANY passenger's booking or cancellation
	// (deleting the record on undo-of-booking, or fabricating a fresh record with
	// no history on undo-of-cancellation), regardless of who performed it or why.
	// That is not a legitimate admin operation, so instead of "fixing" it, a
	// passenger's own cancelTicket() call now correctly marks a ticket CANCELLED
	// (see above) and its history is preserved permanently via Passenger.status --
	// which is what undo was really trying, incorrectly, to provide.

	// ================= DASHBOARD SUMMARY =================

	public String getDashboardSummary() {
		int totalBooked = 0;
		for (int i = 0; i < trainCount; i++) totalBooked += trains[i].getTotalBooked();
		StringBuilder sb = new StringBuilder();
		sb.append("Trains in service   : " + trainCount + "\n");
		sb.append("Stations in network : " + railwayNetwork.getNumStations() + "\n");
		sb.append("Tickets booked      : " + totalBooked + "\n");
		sb.append("Passengers waiting  : " + waitingList.size());
		return sb.toString();
	}

	// ================= CHECK AVAILABILITY (For Passenger Panel) =================

	public String checkAvailability(String source, String destination, String date) {
		if (source == null || destination == null || source.trim().isEmpty() || destination.trim().isEmpty()) {
			return "Please select source and destination stations.";
		}
		if (source.trim().equalsIgnoreCase(destination.trim())) {
			return "Validation Error: Source and Destination stations cannot be the same.";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("=== AVAILABLE TRAINS ===\n");
		sb.append("Route: ").append(source).append(" -> ").append(destination).append("\n");
		sb.append("Date: ").append(date == null ? "Not specified" : date).append("\n\n");

		boolean found = false;
		for (int i = 0; i < trainCount; i++) {
			Train t = trains[i];
			int srcIdx = t.route.indexOf(source.trim());
			int destIdx = t.route.indexOf(destination.trim());
			if (srcIdx != -1 && destIdx != -1 && srcIdx < destIdx) {
				found = true;
				sb.append("🚆 ").append(t.trainName).append(" (").append(t.trainNumber).append(")\n");
				sb.append("   Route: ").append(t.route.toString()).append("\n");
				sb.append("   AC Available : ").append(t.getAcAvailable()).append("/").append(t.acCapacity).append("\n");
				sb.append("   SLPR Available: ").append(t.getSlprAvailable()).append("/").append(t.slprCapacity).append("\n");
				sb.append("   GEN Available : ").append(t.getGenAvailable()).append("/").append(t.genCapacity).append("\n");
				for (String cat : new String[]{"AC", "SLPR", "GEN"}) {
					int fare = calculateFareForTrain(t, source.trim(), destination.trim(), cat, date);
					if (fare > 0) {
						sb.append("   Fare (").append(cat).append("): Rs.").append(fare).append("\n");
					}
				}
				sb.append("\n");
			}
		}
		if (!found) {
			sb.append("No trains available on this route.\n");
			sb.append("Try selecting different stations or add a train route first.");
		}
		return sb.toString();
	}
}