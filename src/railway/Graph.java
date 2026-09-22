package railway;

//Graph.java
class Graph {
	private String[] stations;
	private int numStations;
	private CustomLinkedList[] adjList;
	public static class Edge {
		String destination;
		int weight; // Holds distance in km
		String source;
		public Edge(String source, String destination, int weight) {
			this.source = source;
			this.destination = destination;
			this.weight = weight;
		}
		public Edge(String destination, int weight) {
			this.destination = destination;
			this.weight = weight;
		}
	}
	public Graph(int capacity) {
		stations = new String[capacity];
		numStations = 0;
		adjList = new CustomLinkedList[capacity];
		for (int i = 0; i < capacity; i++) {
			adjList[i] = new CustomLinkedList();
		}
	}
	public int addStation(String stationName) {
		stationName = stationName.trim();
		if (getIndex(stationName) != -1)
			return -1;
		if (numStations < stations.length) {
			stations[numStations] = stationName;
			return numStations++;
		}
		return -1;
	}
	public int getIndex(String stationName) {
		stationName = stationName.trim();
		for (int i = 0; i < numStations; i++) {
			if (stations[i] != null && stations[i].equals(stationName)) {
				return i;
			}
		}
		return -1;
	}
	public void addConnection(String station1, String station2, int weight) {
		station1 = station1.trim();
		station2 = station2.trim();
		int index1 = getIndex(station1);
		int index2 = getIndex(station2);
		if (index1 != -1 && index2 != -1) {
			adjList[index1].add(new Edge(station2, weight));
			adjList[index2].add(new Edge(station1, weight));
		}
	}
	// Direct edge distance lookup (used by the recommendation engine)
	public int getDirectDistance(String station1, String station2) {
		if (station1 == null || station2 == null) return -1;
		station1 = station1.trim();
		station2 = station2.trim();
		int idx1 = getIndex(station1);
		if (idx1 == -1) return -1;
		Node current = adjList[idx1].head;
		while (current != null) {
			Edge edge = (Edge) current.data;
			if (edge.destination.equals(station2)) return edge.weight;
			current = current.next;
		}
		return -1;
	}
	//  PathResult Modified for Distance
	public static class PathResult {
		public int distance; // TOTAL DISTANCE (KM)
		public String path;
		public PathResult(int distance, String path) {
			this.distance = distance;
			this.path = path;
		}
		@Override
		public String toString() {
			return "Total Distance: " + distance + " km | Path: " + path;
		}
	}
	//  BFS Modified to track and sum distance (weight) -- finds the FEWEST-HOP route
	public PathResult findShortestHopPath(String startStation, String endStation) {
		startStation = startStation.trim();
		endStation = endStation.trim();
		int startIndex = getIndex(startStation);
		int endIndex = getIndex(endStation);
		if (startIndex == -1 || endIndex == -1) {
			return new PathResult(-1, "Station not found or not connected.");
		}
		// 1. Initialization
		boolean[] visited = new boolean[numStations];
		String[] prevStation = new String[numStations];
		CustomQueue queue = new CustomQueue();
		int[] dist = new int[numStations];
		for (int i = 0; i < numStations; i++) {
			visited[i] = false;
			prevStation[i] = null;
			dist[i] = Integer.MAX_VALUE;
		}
		// Start traversal
		visited[startIndex] = true;
		dist[startIndex] = 0;
		queue.enqueue(stations[startIndex]);
		// 2. Main Traversal Loop (BFS Structure)
		while (!queue.isEmpty()) {
			String uName = (String) queue.dequeue();
			int u = getIndex(uName);
			Node currentEdgeNode = adjList[u].head;
			while (currentEdgeNode != null) {
				Edge edge = (Edge) currentEdgeNode.data;
				int v = getIndex(edge.destination);
				int weight = edge.weight;

				// Pure BFS logic (finds fewest hops)
				if (v != -1 && !visited[v]) {

					// Distance summation along the path of fewest hops
					if (dist[u] != Integer.MAX_VALUE) {
						dist[v] = dist[u] + weight;
					}

					visited[v] = true;
					prevStation[v] = uName;
					queue.enqueue(stations[v]);
				}
				currentEdgeNode = currentEdgeNode.next;
			}
		}
		// 3. Result Construction
		if (dist[endIndex] == Integer.MAX_VALUE) {
			return new PathResult(-1, "Path not found.");
		}
		// Reconstruct Path (using CustomStack)
		StringBuilder pathBuilder = new StringBuilder();
		int current = endIndex;
		CustomStack pathStack = new CustomStack();
		while (prevStation[current] != null) {
			pathStack.push(stations[current]);
			current = getIndex(prevStation[current]);
		}
		pathStack.push(stations[startIndex]);
		while (!pathStack.isEmpty()) {
			pathBuilder.append((String) pathStack.pop());
			if (!pathStack.isEmpty()) {
				pathBuilder.append(" -> ");
			}
		}
		return new PathResult(dist[endIndex], pathBuilder.toString());
	}
	// NEW: Dijkstra's Algorithm -- finds the MINIMUM-DISTANCE route (weighted shortest path)
	public PathResult findMinimumDistancePath(String startStation, String endStation) {
		startStation = startStation.trim();
		endStation = endStation.trim();
		int startIndex = getIndex(startStation);
		int endIndex = getIndex(endStation);
		if (startIndex == -1 || endIndex == -1) {
			return new PathResult(-1, "Station not found or not connected.");
		}
		int[] dist = new int[numStations];
		String[] prevStation = new String[numStations];
		boolean[] visited = new boolean[numStations];
		for (int i = 0; i < numStations; i++) {
			dist[i] = Integer.MAX_VALUE;
			prevStation[i] = null;
			visited[i] = false;
		}
		dist[startIndex] = 0;
		CustomMinHeap minHeap = new CustomMinHeap();
		minHeap.insert(startIndex, 0);
		while (!minHeap.isEmpty()) {
			HeapNode currentNode = minHeap.extractMin();
			int u = currentNode.stationIndex;
			if (visited[u]) continue; // stale heap entry, skip
			visited[u] = true;
			if (u == endIndex) break; // shortest distance to destination finalized
			Node edgeNode = adjList[u].head;
			while (edgeNode != null) {
				Edge edge = (Edge) edgeNode.data;
				int v = getIndex(edge.destination);
				if (v != -1 && !visited[v] && dist[u] != Integer.MAX_VALUE) {
					int newDist = dist[u] + edge.weight;
					if (newDist < dist[v]) {
						dist[v] = newDist;
						prevStation[v] = stations[u];
						minHeap.insert(v, newDist);
					}
				}
				edgeNode = edgeNode.next;
			}
		}
		if (dist[endIndex] == Integer.MAX_VALUE) {
			return new PathResult(-1, "Path not found.");
		}
		// Reconstruct Path (using CustomStack)
		StringBuilder pathBuilder = new StringBuilder();
		int current = endIndex;
		CustomStack pathStack = new CustomStack();
		while (prevStation[current] != null) {
			pathStack.push(stations[current]);
			current = getIndex(prevStation[current]);
		}
		pathStack.push(stations[startIndex]);
		while (!pathStack.isEmpty()) {
			pathBuilder.append((String) pathStack.pop());
			if (!pathStack.isEmpty()) {
				pathBuilder.append(" -> ");
			}
		}
		return new PathResult(dist[endIndex], pathBuilder.toString());
	}
	// NEW (GUI): all station names currently in the network, for populating dropdowns
	public String[] getAllStationNames() {
		String[] result = new String[numStations];
		System.arraycopy(stations, 0, result, 0, numStations);
		return result;
	}
	public int getNumStations() {
		return numStations;
	}
	// Railway Network Visualization -- returns a formatted String (GUI-friendly)
	public String getNetworkMapString() {
		StringBuilder sb = new StringBuilder();
		sb.append("=================== RAILWAY NETWORK MAP ===================\n");
		int connectionCount = 0;
		for (int i = 0; i < numStations; i++) {
			sb.append("\n[" + stations[i] + "]\n");
			Node current = adjList[i].head;
			if (current == null) {
				sb.append("    (no connections)\n");
			}
			while (current != null) {
				Edge edge = (Edge) current.data;
				sb.append("    |-- " + edge.weight + " km --> " + edge.destination + "\n");
				connectionCount++;
				current = current.next;
			}
		}
		sb.append("\n-------------------------------------------------------------\n");
		sb.append("Total Stations    : " + numStations + "\n");
		sb.append("Total Connections : " + (connectionCount / 2) + "\n");
		sb.append("-------------------------------------------------------------");
		return sb.toString();
	}
}
