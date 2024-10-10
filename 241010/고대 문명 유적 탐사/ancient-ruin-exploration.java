import java.io.*;
import java.util.*;
public class Main {
	static final int[] dy = {-1, 0, 1, 0};
	static final int[] dx = {0, 1, 0, -1};
	
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		int k,m;
		k = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		

		int [][] arr = new int[5][5];
		for(int i=0; i<5; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<5; j++) {
				arr[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		st = new StringTokenizer(br.readLine());
		Queue<Integer> wall = new ArrayDeque<>();
		while(st.hasMoreTokens()) {
			wall.offer(Integer.parseInt(st.nextToken()));
		}
		
		
//		for(int i=0; i<5; i++) {
//			System.out.println(Arrays.toString(arr[i]));
//		}
//		System.out.println("---origin----");
		
		
		int[][] promiseArr = null;
		for(int i=0; i<k; i++) {
			int maxValue = 0;
			int selectDegree = 0;
			
			for(int y=1; y<4; y++) {
				for(int x=1; x<4; x++) {
					int[][] rotated = deepCopy(arr);
					for(int r=0; r<3; r++) {
						rotated = rotate90(rotated, new Cord(y,x));
						int value = getValue(rotated);
						if(maxValue < value) {
							maxValue = value;
							promiseArr = deepCopy(rotated);
							selectDegree = r;
						}else if(maxValue == value && selectDegree > r) {
							promiseArr = deepCopy(rotated);
							selectDegree = r;
						}
					}
				}
			}
			if(maxValue!=0) {
				
				while(true) {
					boolean[][] findZero = new boolean[5][5];
					List<Cord> zeros = findConnect(promiseArr, findZero);
					Collections.sort(zeros);
					fillNewNum(wall, zeros, promiseArr);
					int newValue = 0;
					newValue = getValue(promiseArr);
					if(newValue == 0) {
						break;
					}
					maxValue += newValue;
				}
				

			}else {
				break;
			}
			arr = promiseArr;
			System.out.print(maxValue + " ");
		}
	}
	private static void fillNewNum(Queue<Integer> wall, List<Cord> zeros, int[][] promiseArr) {
		for(int i=0; i<zeros.size(); i++) {
			int num = wall.poll();
			promiseArr[zeros.get(i).y][zeros.get(i).x] = num;
		}
		return;
	}
	public static void print(int[][] arr) {
		for(int i=0; i<5; i++) {
			System.out.println(Arrays.toString(arr[i]));
		}
	}
	
	public static List<Cord> findConnect(int[][] arr, boolean[][] visited) {
		List<Cord> ret = new ArrayList<>();
		for(int i=0; i<arr.length; i++) {
			for(int j=0; j<arr[0].length; j++) {
				int cnt = 0;
				List<Cord> zeros = new ArrayList<>();
				if(!visited[i][j]) {
					cnt++;
					Cord cord = new Cord(i, j);
					Queue<Cord> q = new ArrayDeque<>();
					q.offer(cord);
					visited[cord.y][cord.x] = true;
					zeros.add(cord);
					while(!q.isEmpty()) {
						Cord now = q.poll();
						for(int dir=0; dir<4; dir++) {
							int nexty = now.y + dy[dir];
							int nextx = now.x + dx[dir];
							if(nexty < 0 || nexty >= 5|| nextx < 0 || nextx >= 5 || arr[nexty][nextx] != arr[now.y][now.x] || visited[nexty][nextx]) {
								continue;
							}
							visited[nexty][nextx] = true;
							Cord next = new Cord(nexty, nextx);
							q.add(next);
							zeros.add(next);
							cnt++;
						}
					}
				}
				if(cnt >= 3) {
					for(Cord c: zeros) {
						ret.add(c);
					}
				}
			}
		}
		return ret;
		
		
	}
	
	
	public static int[][] rotate90(int[][] arr, Cord center){
		int[][] copyArr = new int[5][5];
		copyArr = deepCopy(arr);
		int si,sj;
		si = center.y - 1;
		sj = center.x - 1;
		copyArr[si][sj+2] = arr[si][sj];
		copyArr[si+1][sj+2] = arr[si][sj+1];
		copyArr[si+2][sj+2] = arr[si][sj+2];
		
		copyArr[si+2][sj+1] = arr[si+1][sj+2];
		copyArr[si+2][sj] = arr[si+2][sj+2];
		
		copyArr[si+1][sj] = arr[si+2][sj+1];
		copyArr[si][sj] = arr[si+2][sj];
		
		copyArr[si][sj+1] = arr[si+1][sj];
		copyArr[si][sj+2] = arr[si][sj];
		copyArr[center.y][center.x] = arr[center.y][center.x]; 
		return copyArr;
	}
	
	public static int getValue(int[][] arr) {
		int value = 0;
		boolean[][] visited = new boolean[5][5];
		for(int i=0; i<arr.length; i++) {
			for(int j=0; j<arr[0].length; j++) {
				if(!visited[i][j]) {
					
					value += findAdj(arr, new Cord(i, j), visited);
				}
			}
		}
		return value;
	}
	
	public static int findAdj(int[][] arr, Cord cord, boolean[][] visited) {
		Queue<Cord> q = new ArrayDeque<>();
		q.offer(cord);
		int value = 1;
		visited[cord.y][cord.x] = true;
		while(!q.isEmpty()) {
			Cord now = q.poll();
			for(int i=0; i<4; i++) {
				int nexty = now.y + dy[i];
				int nextx = now.x + dx[i];
				if(nexty < 0 || nexty >= 5|| nextx < 0 || nextx >= 5 || arr[nexty][nextx] != arr[now.y][now.x] || visited[nexty][nextx]) {
					continue;
				}
				visited[nexty][nextx] = true;
				q.add(new Cord(nexty, nextx));
				
				value++;
			}
		}
		if(value >= 3) {
			return value;
		}else {
			return 0;
		}
		
	}
	
	
	public static int[][] deepCopy(int[][] arr){
		int[][] retArr = new int[5][5];
		for(int i=0; i<5; i++) {
			retArr[i] = arr[i].clone();
		}
		return retArr;
	}
	static class Cord implements Comparable<Cord>{
		int y;
		int x;
		public Cord(int y, int x) {
			super();
			this.y = y;
			this.x = x;
		}
		@Override
		public String toString() {
			return "Cord [y=" + y + ", x=" + x + "]";
		}
		@Override
		public int compareTo(Cord o) {
			if(this.x == o.x) {
				return o.y - this.y;
			}else {
				return this.x - o.x;
			}
		}
		
		
		
	}
}