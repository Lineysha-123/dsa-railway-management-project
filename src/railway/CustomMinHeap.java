package railway;

class CustomMinHeap {
	private HeapNode[] heap;
	private int size;
	private static final int MAX_SIZE = 200;
	public CustomMinHeap() {
		heap = new HeapNode[MAX_SIZE];
		size = 0;
	}
	public boolean isEmpty() {
		return size == 0;
	}
	public void insert(int stationIndex, int distance) {
		if (size >= MAX_SIZE) return;
		heap[size] = new HeapNode(stationIndex, distance);
		int i = size;
		size++;
		while (i > 0) {
			int parent = (i - 1) / 2;
			if (heap[parent].distance > heap[i].distance) {
				swap(parent, i);
				i = parent;
			} else {
				break;
			}
		}
	}
	public HeapNode extractMin() {
		if (size == 0) return null;
		HeapNode min = heap[0];
		size--;
		heap[0] = heap[size];
		heap[size] = null;
		heapifyDown(0);
		return min;
	}
	private void heapifyDown(int i) {
		int left = 2 * i + 1;
		int right = 2 * i + 2;
		int smallest = i;
		if (left < size && heap[left].distance < heap[smallest].distance) smallest = left;
		if (right < size && heap[right].distance < heap[smallest].distance) smallest = right;
		if (smallest != i) {
			swap(i, smallest);
			heapifyDown(smallest);
		}
	}
	private void swap(int a, int b) {
		HeapNode temp = heap[a];
		heap[a] = heap[b];
		heap[b] = temp;
	}
}

// HeapNode helper entity for min-heap priority indexing
class HeapNode {
	int stationIndex;
	int distance;
	public HeapNode(int stationIndex, int distance) {
		this.stationIndex = stationIndex;
		this.distance = distance;
	}
}
