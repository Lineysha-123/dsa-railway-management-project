package railway;

//CustomStack.java
class CustomStack {
	private Object[] stackArray;
	private int top;
	private static final int MAX_SIZE = 50;
	public CustomStack() {
		stackArray = new Object[MAX_SIZE];
		top = -1;
	}
	public void push(Object data) {
		if (top >= MAX_SIZE - 1) {
			System.out.println("Stack overflow: Cannot push undo action.");
			return;
		}
		stackArray[++top] = data;
	}
	public Object pop() {
		if (top < 0) {
			return null;
		}
		return stackArray[top--];
	}
	public boolean isEmpty() {
		return top == -1;
	}

	public int size() {
		return top + 1;
	}
}
