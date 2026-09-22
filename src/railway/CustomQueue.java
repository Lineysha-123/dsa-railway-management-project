package railway;

//CustomQueue.java
class CustomQueue {
	private Node front;
	private Node rear;
	public void enqueue(Object data) {
		Node newNode = new Node(data);
		if (rear == null) {
			front = rear = newNode;
		} else {
			rear.next = newNode;
			rear = newNode;
		}
	}
	public Object dequeue() {
		if (front == null) {
			return null;
		}
		Object data = front.data;
		front = front.next;
		if (front == null) {
			rear = null;
		}
		return data;
	}
	public Object peek() {
		return front != null ? front.data : null;
	}
	public boolean isEmpty() {
		return front == null;
	}
}
