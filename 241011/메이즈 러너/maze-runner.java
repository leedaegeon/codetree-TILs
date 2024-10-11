import java.util.*;
import java.io.*;
public class Main {
	static int[][] field;
	static int n, m, turn;
	static Cord exit;
	static List<Cord> ls = new ArrayList<>();
	static final int[] dy = {-1, 1, 0, 0};
	static final int[] dx = {0, 0, 1, -1};
	static int moveDist;
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		turn = Integer.parseInt(st.nextToken());
		field = new int [n+2][n+2];
		initMap();
		for(int i=1; i<n+1; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=1; j<n+1; j++) {
				field[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for(int i=0; i<m; i++) {
			st = new StringTokenizer(br.readLine());
			ls.add(new Cord(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
		}
		st = new StringTokenizer(br.readLine());
		exit = new Cord(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		
//		field = rotate(1, 1, 2);
//		for(int i=1; i<n+1; i++) {
//			for(int j=1; j<n+1; j++) {
//				System.out.print(field[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println();
		
		for(int time = 0; time< turn; time++) {
			move();
//			System.out.println("----------------");
//			System.out.print("출구 좌표: ");
//			System.out.println(exit);
//			for(int i=1; i<n+1; i++) {
//				for(int j=1; j<n+1; j++) {
//					System.out.print(field[i][j] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println(ls);
		}
//		
		System.out.println(moveDist);
		System.out.println(exit.y + " " + exit.x);
	}
	public static void move() {
		boolean[] removed = new boolean[ls.size()];
		for(int i=0; i<ls.size(); i++) {
			Cord now = new Cord(ls.get(i).y, ls.get(i).x);
			int nowDist = getDist(now, exit);
			int dir = -1;
			for(int d=0; d<4; d++) {
				Cord next = new Cord(now.y + dy[d], now.x + dx[d]);
				if(field[next.y][next.x] != 0) {
					continue;
				}
				int nextDist = getDist(next, exit);
				if(nowDist > nextDist) {
					nowDist = nextDist;
					dir = d;
					break;
				}
			}
			if(dir != -1) {
				ls.get(i).y = now.y + dy[dir];
				ls.get(i).x = now.x + dx[dir];
				moveDist++;
			}
			if(exit.equals(ls.get(i))) {
//				ls.remove(i);
				removed[i] = true;
			}
		}
		int idx = 0;
		for(int i=0; i<removed.length; i++) {
			if(removed[i]) {
				ls.remove(idx);
			}else {
				idx++;
			}
		}
		if(ls.size() == 0) {
			return;
		}
		int minDist = 21;
		PriorityQueue<Cord> pq = new PriorityQueue<>((o1, o2)->{
			if(o1.y == o2.y) {
				return o1.x - o2.x; 
			}
			return o1.y - o2.y;
			 
		});
		for(int i=0; i<ls.size(); i++) {
			int testDist = getDist(ls.get(i), exit);
			if(minDist > testDist) {
				pq.clear();
				pq.offer(ls.get(i));
				minDist = testDist;
			}else if(minDist == testDist) {
				pq.offer(ls.get(i));

			}
		}
		Cord target = pq.poll();
		int yDiff = Math.abs(target.y - exit.y);
		int xDiff = Math.abs(target.x - exit.x);
		int y, x, l;
		y = 0;
		x = 0;
		l = 0;
//		System.out.println("다음 출구 좌표 결정의 타겟: " + target);
		if(yDiff == xDiff) {
			pq = new PriorityQueue<>((o1, o2)->{
				if(o1.y == o2.y) {
					return o1.x - o2.x; 
				}
				return o1.y - o2.y;
				 
			});
			pq.offer(target);
			pq.offer(exit);
			Cord leftTop = pq.poll();
			y = leftTop.y;
			x = leftTop.x;
			l = yDiff;
		}else if(exit.y == target.y) {
			l = xDiff;
			x = Math.min(exit.x, target.x);
			y = exit.y - l;
			while(y < 1) {
				y++;
			}
		}else if(exit.x == target.x) {
			l = yDiff;
			y = Math.min(exit.y, target.y);
			x = exit.x - l;			
			while(x< 1) {
				x++;
			}
		}
		else {
			l = Math.max(xDiff, yDiff);
			
			if(exit.y > target.y && exit.x > target.x) { // e기준 t가 1사분면
				y = exit.y - l;
				x = exit.x - l;
				while(y<1) {
					y++;
				}
				while(x < 1) {
					x++;
				}
			}else if(exit.y > target.y && exit.x < target.x) { // e기준 t가 2사분면
				y = exit.y - l;
				x = exit.x;
				while(y < 1) {
					y++;
				}
				
			}else if(exit.y < target.y && exit.x > target.x) { // e 기준 t가 3사분면(왼쪽 아래)
				y = exit.y;
				x = exit.x - l;
				while(x < 1) {
					x++;
				}
			}else if(exit.y < target.y && exit.x < target.x) {
				y = exit.y;
				x = exit.x;
			}
		}
//		System.out.println(y + ", " + x + " 길이: " + l);
		field = rotate(y, x, l);
	}
	public static int getDist(Cord c, Cord t) {
		return Math.abs(c.y-t.y) + Math.abs(c.x - t.x);
	}
	
	public static int[][] rotate(int k, int t, int l) {
		int[][] temp = new int[n+2][n+2];
		for(int i=0; i<n+2; i++) {
			temp[i] = field[i].clone();
		}
		boolean flag = true;
		boolean[] visited = new boolean[ls.size()];
		while(l >= 1) {
			for(int i=0; i<l; i++) {
				Cord check = new Cord(k, t+i);
				for(int idx=0; idx<ls.size(); idx++) {
					if(ls.get(idx).equals(check) && !visited[idx]) {
						ls.get(idx).y = k+i;
						ls.get(idx).x = t+l;
						visited[idx] = true;
					}
				}
				if(exit.equals(check) && flag) {
					exit.y = k+i;
					exit.x = t+l;
					flag = false;
				}
				temp[k+i][t+l] = field[k][t+i];
				if(temp[k+i][t+l] >= 1) {
					temp[k+i][t+l]--;
				}
//				
			}
			for(int i=l, j=0; i>=1 && j<l; i--, j++) {
				Cord check = new Cord(k+j, t+l);
				for(int idx=0; idx<ls.size(); idx++) {
					if(ls.get(idx).equals(check) && !visited[idx]) {
						ls.get(idx).y = k+l;
						ls.get(idx).x = t+i;
						visited[idx] = true;
					}
				}
				if(exit.equals(check) && flag) {
					exit.y = k+l;
					exit.x = t+i;
					flag = false;
				}
				temp[k+l][t+i] = field[k+j][t+l];
				if(temp[k+l][t+i] >= 1) {
					temp[k+l][t+i]--;
				}
			}
			for(int i=l; i>=1; i--) {
				Cord check = new Cord(k+l, t+i);
				for(int idx=0; idx<ls.size(); idx++) {
					if(ls.get(idx).equals(check) && !visited[idx]) {
						ls.get(idx).y = k+i;
						ls.get(idx).x = t;
						visited[idx] = true;
					}
				}
				if(exit.equals(check) && flag) {
					exit.y = k+i;
					exit.x = t;
					flag = false;
				}
				temp[k+i][t] = field[k+l][t+i];
				if(temp[k+i][t] >= 1) {
					temp[k+i][t]--;
				}
			}
			for(int i=0, j=l; i<l && j>=1; i++, j--) {
				Cord check = new Cord(k+j, t);
				for(int idx=0; idx<ls.size(); idx++) {
					if(ls.get(idx).equals(check) && !visited[idx]) {
						ls.get(idx).y = k;
						ls.get(idx).x = t+i;
						visited[idx] = true;
					}
				}
				if(exit.equals(check)&& flag) {
					exit.y = k;
					exit.x = t+i;
					flag = false;
				}
				temp[k][t+i] = field[k+j][t];
				if(temp[k][t+i] >= 1) {
					temp[k][t+i]--;
				}
			}
			
			l-=2;
			k++;
			t++;
		}
		if(temp[k][t]>=1) {
			temp[k][t]--;
		}
		return temp;
	}
	static class Cord{
		int y;
		int x;
		public Cord(int y, int x) {
			super();
			this.y = y;
			this.x = x;
		}
		@Override
		public String toString() {
			return "[" + y + ", " + x + "]";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Cord other = (Cord) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
	}
	
	public static void initMap() {
		Arrays.fill(field[0], 10);
		Arrays.fill(field[n+1], 10);
		for(int i=0; i<n+2; i++) {
			field[i][0] = 10;
			field[i][n+1] = 10;
		}
	}
}