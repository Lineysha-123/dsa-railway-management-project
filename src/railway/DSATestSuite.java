package railway;

/**
 * DSATestSuite.java
 * Comprehensive Automated Verification Suite for Custom Data Structures and Algorithms.
 * Validates:
 * 1. CustomLinkedList (Singly-linked list operations)
 * 2. CustomStack (LIFO operations & seat recycling)
 * 3. CustomQueue (FIFO operations for BFS)
 * 4. CustomMinHeap (Binary min-heap for Dijkstra's shortest path)
 * 5. PriorityPassengerQueue (Multi-tier priority queue with FIFO stability)
 * 6. Graph (BFS fewest-hops vs Dijkstra minimum-distance route calculation)
 * 7. RailwayService (Seat allocation, LIFO cancellation recycling & waitlist auto-promotion)
 */
public class DSATestSuite {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("     RAILWAY MANAGEMENT SYSTEM - DSA VERIFICATION SUITE       ");
        System.out.println("===============================================================\n");

        testCustomLinkedList();
        testCustomStack();
        testCustomQueue();
        testCustomMinHeap();
        testPriorityPassengerQueue();
        testGraphAlgorithms();
        testServiceLifecycleAndSeatRecycling();

        System.out.println("\n===============================================================");
        System.out.println("TEST SUMMARY: " + testsPassed + " Passed, " + testsFailed + " Failed.");
        if (testsFailed == 0) {
            System.out.println("STATUS: ALL CUSTOM DATA STRUCTURES & ALGORITHMS VERIFIED 100%!");
        } else {
            System.out.println("STATUS: SOME TESTS FAILED. PLEASE CHECK LOGS.");
            System.exit(1);
        }
        System.out.println("===============================================================");
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            testsFailed++;
            System.err.println("  [FAIL] " + testName);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            testsFailed++;
            System.err.println("  [FAIL] " + testName + " -> Expected: " + expected + ", Got: " + actual);
        }
    }

    // 1. Custom Singly-Linked List Test
    private static void testCustomLinkedList() {
        System.out.println("--- 1. Testing CustomLinkedList ---");
        CustomLinkedList list = new CustomLinkedList();
        assertTrue("Initially empty", list.isEmpty());
        assertEquals("Initial size is 0", 0, list.size());

        list.add("Delhi");
        list.add("Mumbai");
        list.add("Chennai");
        assertEquals("Size after 3 additions", 3, list.size());
        assertEquals("Get element at index 0", "Delhi", list.get(0));
        assertEquals("Get element at index 1", "Mumbai", list.get(1));
        assertEquals("Get element at index 2", "Chennai", list.get(2));

        assertEquals("indexOf existing element", 1, list.indexOf("Mumbai"));
        assertEquals("indexOf non-existing element", -1, list.indexOf("Kolkata"));

        assertTrue("Remove Mumbai", list.remove("Mumbai"));
        assertEquals("Size after removal", 2, list.size());
        assertEquals("New index 1 after removal", "Chennai", list.get(1));
    }

    // 2. Custom Stack Test (LIFO)
    private static void testCustomStack() {
        System.out.println("\n--- 2. Testing CustomStack (LIFO) ---");
        CustomStack stack = new CustomStack();
        assertTrue("Initially empty", stack.isEmpty());
        assertEquals("Initial size is 0", 0, stack.size());

        stack.push(101);
        stack.push(102);
        stack.push(103);
        assertEquals("Size after 3 pushes", 3, stack.size());

        // LIFO order verification
        assertEquals("First pop is 103 (LIFO)", 103, stack.pop());
        assertEquals("Second pop is 102", 102, stack.pop());
        assertEquals("Third pop is 101", 101, stack.pop());
        assertTrue("Empty after popping all", stack.isEmpty());
        assertEquals("Pop on empty returns null", null, stack.pop());
    }

    // 3. Custom Queue Test (FIFO)
    private static void testCustomQueue() {
        System.out.println("\n--- 3. Testing CustomQueue (FIFO) ---");
        CustomQueue queue = new CustomQueue();
        assertTrue("Initially empty", queue.isEmpty());

        queue.enqueue("First");
        queue.enqueue("Second");
        queue.enqueue("Third");

        assertEquals("Peek returns First", "First", queue.peek());
        assertEquals("First dequeue is 'First' (FIFO)", "First", queue.dequeue());
        assertEquals("Second dequeue is 'Second'", "Second", queue.dequeue());
        assertEquals("Third dequeue is 'Third'", "Third", queue.dequeue());
        assertTrue("Queue is empty after 3 dequeues", queue.isEmpty());
        assertEquals("Dequeue on empty queue returns null", null, queue.dequeue());
    }

    // 4. Custom Binary Min-Heap Test (Dijkstra Priority Queue)
    private static void testCustomMinHeap() {
        System.out.println("\n--- 4. Testing CustomMinHeap (Dijkstra Priority Queue) ---");
        CustomMinHeap minHeap = new CustomMinHeap();
        assertTrue("Initially empty", minHeap.isEmpty());

        // Insert nodes with station indices and distances in non-sorted order
        minHeap.insert(1, 1500); // Delhi -> Kolkata
        minHeap.insert(2, 200);  // Mumbai -> Pune
        minHeap.insert(3, 1400); // Delhi -> Mumbai
        minHeap.insert(4, 1200); // Mumbai -> Chennai

        // Min-heap property: minimum distance must always be extracted first
        HeapNode min1 = minHeap.extractMin();
        assertEquals("Smallest distance extracted first (200 km)", 200, min1.distance);
        assertEquals("Station index for min1 is 2 (Pune)", 2, min1.stationIndex);

        HeapNode min2 = minHeap.extractMin();
        assertEquals("Second smallest distance extracted (1200 km)", 1200, min2.distance);

        HeapNode min3 = minHeap.extractMin();
        assertEquals("Third smallest distance extracted (1400 km)", 1400, min3.distance);

        HeapNode min4 = minHeap.extractMin();
        assertEquals("Fourth smallest distance extracted (1500 km)", 1500, min4.distance);

        assertTrue("Min-heap empty after extracting all", minHeap.isEmpty());
        assertEquals("ExtractMin on empty returns null", null, minHeap.extractMin());
    }

    // 5. Priority Queue Test (Senior Citizen > Ladies > General with Stable FIFO)
    private static void testPriorityPassengerQueue() {
        System.out.println("\n--- 5. Testing PriorityPassengerQueue (Waitlist Priority Engine) ---");
        PriorityPassengerQueue pq = new PriorityPassengerQueue();
        assertTrue("Initially empty", pq.isEmpty());
        assertEquals("Initial size 0", 0, pq.size());

        // Create mock passengers
        Passenger general1 = new Passenger("John Doe", "T1", "TR100", "111", 30, "M", "A", "B", "25/12/2026", "WAITLIST", "SLPR", 300, "08:00");
        Passenger general2 = new Passenger("Bob Smith", "T2", "TR100", "222", 35, "M", "A", "B", "25/12/2026", "WAITLIST", "SLPR", 300, "08:00");
        Passenger ladies = new Passenger("Alice Wonder", "T3", "TR100", "333", 28, "F", "A", "B", "25/12/2026", "WAITLIST", "SLPR", 300, "08:00");
        Passenger senior = new Passenger("Elder Grand", "T4", "TR100", "444", 68, "M", "A", "B", "25/12/2026", "WAITLIST", "SLPR", 300, "08:00");

        // Enqueue: General 1 -> General 2 -> Ladies -> Senior
        pq.enqueue(general1, general1.getPriorityLevel()); // priority 3
        pq.enqueue(general2, general2.getPriorityLevel()); // priority 3
        pq.enqueue(ladies, ladies.getPriorityLevel());     // priority 2
        pq.enqueue(senior, senior.getPriorityLevel());     // priority 1

        assertEquals("Queue size is 4", 4, pq.size());

        // Dequeue order must be: Senior (1) -> Ladies (2) -> General 1 (3, arrived first) -> General 2 (3, arrived second)
        Passenger p1 = pq.dequeue();
        assertEquals("First dequeued is Senior Citizen (Priority 1)", "Elder Grand", p1.name);

        Passenger p2 = pq.dequeue();
        assertEquals("Second dequeued is Ladies (Priority 2)", "Alice Wonder", p2.name);

        Passenger p3 = pq.dequeue();
        assertEquals("Third dequeued is General 1 (Priority 3, earlier arrival)", "John Doe", p3.name);

        Passenger p4 = pq.dequeue();
        assertEquals("Fourth dequeued is General 2 (Priority 3, later arrival)", "Bob Smith", p4.name);

        assertTrue("PQ is empty after dequeuing all", pq.isEmpty());
    }

    // 6. Graph Algorithms Test (BFS Fewest Hops vs Dijkstra Minimum Distance)
    private static void testGraphAlgorithms() {
        System.out.println("\n--- 6. Testing Graph Algorithms (BFS vs Dijkstra) ---");
        Graph graph = new Graph(10);
        graph.addStation("A");
        graph.addStation("B");
        graph.addStation("C");
        graph.addStation("D");

        // Path 1: Direct hop A -> D has 1 hop, but distance 1000 km
        graph.addConnection("A", "D", 1000);
        // Path 2: A -> B -> C -> D has 3 hops, but total distance 100 + 100 + 100 = 300 km
        graph.addConnection("A", "B", 100);
        graph.addConnection("B", "C", 100);
        graph.addConnection("C", "D", 100);

        // BFS finds fewest hops: A -> D (1 hop)
        Graph.PathResult bfsResult = graph.findShortestHopPath("A", "D");
        assertEquals("BFS finds fewest-hop path A -> D", "A -> D", bfsResult.path);
        assertEquals("BFS path distance is 1000 km", 1000, bfsResult.distance);

        // Dijkstra finds minimum physical distance: A -> B -> C -> D (300 km)
        Graph.PathResult dijkstraResult = graph.findMinimumDistancePath("A", "D");
        assertEquals("Dijkstra finds min distance path A -> B -> C -> D", "A -> B -> C -> D", dijkstraResult.path);
        assertEquals("Dijkstra min distance is 300 km", 300, dijkstraResult.distance);
    }

    // 7. Full Service Lifecycle: Booking, Seat Stack Recycling & Waitlist Auto-Promotion
    private static void testServiceLifecycleAndSeatRecycling() {
        System.out.println("\n--- 7. Testing Service Lifecycle (Booking, Stack Recycling & Waitlist Promotion) ---");
        RailwayService service = new RailwayService();

        // 1. Initial train T123 has route Delhi -> Mumbai -> Pune
        // AC capacity = 3. Let's book 3 AC tickets to saturate capacity.
        String res1 = service.bookTicket("T123", "Pass 1", "111", "30", "M", "25/12/2026", "Delhi", "Pune", "AC", "08:00");
        assertTrue("Booking 1 confirmed (AC-01)", res1.contains("Seat: AC-01"));

        String res2 = service.bookTicket("T123", "Pass 2", "222", "30", "M", "25/12/2026", "Delhi", "Pune", "AC", "08:00");
        assertTrue("Booking 2 confirmed (AC-02)", res2.contains("Seat: AC-02"));

        String res3 = service.bookTicket("T123", "Pass 3", "333", "30", "M", "25/12/2026", "Delhi", "Pune", "AC", "08:00");
        assertTrue("Booking 3 confirmed (AC-03)", res3.contains("Seat: AC-03"));

        // 2. Book 4th ticket: AC is full (3/3), should go to waiting list
        String res4 = service.bookTicket("T123", "Senior Waitlisted", "444", "65", "M", "25/12/2026", "Delhi", "Pune", "AC", "08:00");
        assertTrue("Pass 4 waitlisted with Senior Citizen priority", res4.contains("Added to waiting list") && res4.contains("Senior Citizen"));

        // 3. Cancel Booking 2 (Seat AC-02).
        // The service should:
        // a) Release AC-02 and push '2' onto acFreedSeats (CustomStack)
        // b) Automatically promote 'Senior Waitlisted' from waitingList (PriorityPassengerQueue)
        // c) Pop '2' from acFreedSeats stack and assign AC-02 to the promoted passenger!
        String cancelRes = service.cancelTicket("P1002");
        assertTrue("Cancellation success", cancelRes.contains("cancelled. Seat AC-02 released"));
        assertTrue("Automatic promotion of waitlisted passenger to seat AC-02", cancelRes.contains("Promoted: Senior Waitlisted") && cancelRes.contains("AC-02"));

        // 4. Verify promoted passenger ticket details
        String ticket4Details = service.getTicketDetails("P1004");
        assertTrue("Promoted passenger now has STATUS BOOKED", ticket4Details.contains("Status:         BOOKED"));
        assertTrue("Promoted passenger now holds seat AC-02", ticket4Details.contains("Seat:           AC-02"));

        // 5. Verify Multi-Field Ticket Search (Item 4)
        String searchByName = service.searchTickets("Pass 1");
        assertTrue("Search by name finds ticket", searchByName.contains("P1001") && searchByName.contains("Pass 1"));

        String searchByPhone = service.searchTickets("333");
        assertTrue("Search by phone finds ticket", searchByPhone.contains("P1003") && searchByPhone.contains("Pass 3"));

        // 6. Verify Ticket Receipt Export (Item 3)
        String exportRes = service.exportTicketReceipt("P1001");
        assertTrue("Ticket receipt exported successfully", exportRes.contains("SUCCESS: Official ticket receipt saved") && exportRes.contains("P1001"));
        java.io.File receiptFile = new java.io.File("receipts/ticket_P1001.txt");
        assertTrue("Receipt file exists on disk", receiptFile.exists());
        receiptFile.delete();
        new java.io.File("receipts").delete();

        // 7. Verify Station Schedule Setting & Lookup
        String schedRes = service.setStationSchedule("T123", "Mumbai", "11:00", "11:15");
        assertTrue("Station schedule updated successfully", schedRes.contains("Success: Schedule updated for station 'Mumbai'"));
        assertEquals("Station arrival time updated", "11:00", service.getStationArrival("T123", "Mumbai"));
        assertEquals("Station departure time updated", "11:15", service.getStationDeparture("T123", "Mumbai"));
    }
}
