package railway;

//CustomLinkedList.java
class CustomLinkedList {
	Node head;
	public void add(Object data) {
		Node newNode = new Node(data);
		if (head == null) {
			head = newNode;
		} else {
			Node current = head;
			while (current.next != null) {
				current = current.next;
			}
			current.next = newNode;
		}
	}
	public Object get(int index) {
		if (index < 0)
			return null;
		Node current = head;
		int count = 0;
		while (current != null) {
			if (count == index) {
				return current.data;
			}
			count++;
			current = current.next;
		}
		return null;
	}

	// Get index of data
	public int indexOf(Object data) {
		if (data == null) return -1;

		Node current = head;
		int index = 0;
		String target = data.toString().trim();

		while (current != null) {
			if (current.data != null && current.data.toString().trim().equalsIgnoreCase(target)) {
				return index;
			}
			index++;
			current = current.next;
		}
		return -1;
	}
	public boolean remove(Object data) {
		if (head == null)
			return false;
		if (head.data.equals(data)) {
			head = head.next;
			return true;
		}
		Node current = head;
		while (current.next != null) {
			if (current.next.data.equals(data)) {
				current.next = current.next.next;
				return true;
			}
			current = current.next;
		}
		return false;
	}
	public int size() {
		int count = 0;
		Node current = head;
		while (current != null) {
			count++;
			current = current.next;
		}
		return count;
	}
	public boolean isEmpty() {
		return head == null;
	}
	public void display() {
		Node current = head;
		if (current == null) {
			System.out.println("List is empty.");
			return;
		}
		System.out.print("[ ");
		while (current != null) {
			System.out.print(current.data.toString());
			if (current.next != null) {
				System.out.print(" -> ");
			}
			current = current.next;
		}
		System.out.println(" ]");
	}
}

// Node.java helper entity
class Node {
	Object data;
	Node next;
	public Node(Object data) {
		this.data = data;
		this.next = null;
	}
}
