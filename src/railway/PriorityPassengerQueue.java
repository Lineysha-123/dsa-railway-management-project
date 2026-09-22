package railway;

// PriorityPassengerQueue.java
// Custom Priority Queue implemented using a priority-sorted linked list.
// Priority levels: 1 = Senior Citizen (highest), 2 = Ladies, 3 = General.
// Equal priorities preserve FIFO (First-In, First-Out) arrival order.
class PriorityPassengerQueue {
	private PriorityNode head;
	private int count = 0;

	public PriorityPassengerQueue() {
		this.head = null;
		this.count = 0;
	}

	public void enqueue(Passenger p, int priority) {
		PriorityNode newNode = new PriorityNode(p, priority);
		if (head == null || priority < head.priority) {
			newNode.next = head;
			head = newNode;
		} else {
			PriorityNode current = head;
			// Stable FIFO: advance past any node with priority <= incoming priority
			while (current.next != null && current.next.priority <= priority) {
				current = current.next;
			}
			newNode.next = current.next;
			current.next = newNode;
		}
		count++;
	}

	public Passenger dequeue() {
		if (head == null) return null;
		Passenger p = head.passenger;
		head = head.next;
		count--;
		return p;
	}

	public PriorityNode getHead() {
		return head;
	}

	public boolean isEmpty() {
		return head == null;
	}

	public int size() {
		return count;
	}

	public Passenger peek() {
		return head != null ? head.passenger : null;
	}
}

// PriorityNode helper entity
class PriorityNode {
	Passenger passenger;
	int priority;
	PriorityNode next;

	public PriorityNode(Passenger passenger, int priority) {
		this.passenger = passenger;
		this.priority = priority;
		this.next = null;
	}
}
