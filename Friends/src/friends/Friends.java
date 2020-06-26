package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		if(g == null || p1 == null || p2 == null)
			return null;
		
		ArrayList<String> path = new ArrayList<String>();
		
		boolean[] visited = new boolean [g.members.length];
		
		Queue<Person> queue = new Queue<Person>();
		
		Person[] visitedAlready = new Person[g.members.length];
		
		int i = g.map.get(p1);
		
		queue.enqueue(g.members[i]);
		
		visited[i] = true;
		
		while(!queue.isEmpty()) {
			Person curr = queue.dequeue();
			
			int currIndex = g.map.get(curr.name);
			
			visited[currIndex] = true;
			
			if(curr.first == null) {
				return null;
			}
			
			Friend next = curr.first;
			
			while(next != null) {
				if(!visited[next.fnum]) {
					visited[next.fnum] = true;
					visitedAlready[next.fnum] = curr;
					queue.enqueue(g.members[next.fnum]);
					
					if(g.members[next.fnum].name.equals(p2)) {
						curr = g.members[next.fnum];
						
						while(!curr.name.equals(p1)) {
							path.add(0,curr.name);
							curr = visitedAlready[g.map.get(curr.name)];
						}
						path.add(0,p1);
						return path;
					}
				}
				next = next.next;
			}
		}
		return null;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		if(g == null || school == null) {
			return null;
		}
		
		ArrayList<ArrayList<String>> group = new ArrayList<ArrayList<String>>();
		
		boolean[] visited = new boolean[g.members.length];
		
		return BFS(g,g.members[0], group, visited, school);
		
	}
	
	private static ArrayList<ArrayList<String>> BFS(Graph g, Person start, ArrayList<ArrayList<String>> group, boolean[] visited, String school) {
		ArrayList<String> result = new ArrayList<String>();
		
		Queue<Person> queue = new Queue<Person>();
		
		queue.enqueue(start);
		visited[g.map.get(start.name)] = true;
		
		Friend next;
		
		if(start.school == null || !start.school.equals(school)) {
			queue.dequeue();
			
			for(int i = 0; i < g.members.length; i++) {
				if(!visited[i])
					return BFS(g,g.members[i], group, visited, school);
			}
		}
			while(!queue.isEmpty()) {
				Person curr = queue.dequeue();
				next = curr.first;
				
				result.add(curr.name);
				
				while(next != null) {
					if(!visited[next.fnum]) {
						if(g.members[next.fnum].school == null) {
							visited[next.fnum] = true;
							next = next.next;
							continue;
						}
						else if(g.members[next.fnum].school.equals(school)) {
							queue.enqueue(g.members[next.fnum]);
						}
						visited[next.fnum] = true;
					}
					next = next.next;
				}
			}
		if(group.isEmpty() || !result.isEmpty()) {
			group.add(result);
		}
		for(int j = 0; j < g.members.length; j++) {
			if(!visited[j])
				return BFS(g, g.members[j], group, visited, school);
		}
		return group;
	}
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		if(g == null) 
			return null;
		
		ArrayList<String> connectors = new ArrayList<String>();
		
		boolean []visited = new boolean[g.members.length];
		int[] numofDFS = new int[g.members.length];
		int[] back = new int[g.members.length];
		ArrayList<String> backPeople = new ArrayList<String>();
		int count = 0;
		
		for(int i = 1; i < g.members.length; i++) {
			if(!visited[i])
				connectors = DFS(connectors, g, visited, g.members[i], numofDFS, back, count, backPeople , true);
		}
		return connectors;
		
	}
	private static ArrayList<String> DFS(ArrayList<String> connectors, Graph g,boolean[] visited, Person start, int[] numofDFS, int[] back, int count, ArrayList<String>backPeople, boolean startingPoint){
		visited[g.map.get(start.name)] = true;
		
		numofDFS[g.map.get(start.name)] = count;
		back[g.map.get(start.name)] = count;
		
		Friend neighbor = start.first;
		
		while(neighbor != null) {
			if(!visited[neighbor.fnum]) {
				count++;
				connectors = DFS(connectors, g,visited,g.members[neighbor.fnum], numofDFS, back,count, backPeople, false);
				
				if(numofDFS[g.map.get(start.name)] <= back[neighbor.fnum]) {
					if(!connectors.contains(start.name) && !startingPoint || !connectors.contains(start.name) && backPeople.contains(start.name))
						connectors.add(start.name);
				}
				else {
					back[g.map.get(start.name)] = Math.min(back[g.map.get(start.name)], back[neighbor.fnum]);
				}
				backPeople.add(start.name);
			}
			else {
				back[g.map.get(start.name)] = Math.min(back[g.map.get(start.name)], numofDFS[neighbor.fnum]);
			}
			neighbor = neighbor.next;
		}
		return connectors;
	}
}

