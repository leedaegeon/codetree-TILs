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
		
//		field = rotate(1, 1, 4);
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
			
		}
		
		System.out.println(moveDist);
		System.out.println(exit.y + " " + exit.x);
	}
	public static void move() {
		boolean[] removed = new boolean[ls.size()];
//		System.out.println("before "+ls);
//		System.out.println(exit);
//		for(int i=1; i<n+1; i++) {
//			for(int j=1; j<n+1; j++) {
//				System.out.print(field[i][j] + " ");
//			}
//			System.out.println();
//		}
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
				removed[i] = true;
			}
		}
//		System.out.println("after "+ls);

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
		int minDist = n;
		for(int i=0; i<ls.size(); i++) {
			int testDistY = Math.abs(exit.y - ls.get(i).y);
			int testDistX = Math.abs(exit.x - ls.get(i).x);
			int testDist = Math.max(testDistY, testDistX);
			if(minDist > testDist) {		
				minDist = testDist;
			}
		}
//		System.out.println(minDist);
		int[] squareInfo = getSqureStart(minDist);
		
		
		int y, x, l;
		y = squareInfo[0];
		x = squareInfo[1];
		l = squareInfo[2];

//		System.out.println(y + ", " + x + " 길이: " + l);
		field = rotate(y, x, l);
	}
	public static int[] getSqureStart(int minDist) {
		boolean eFlag = false;
		boolean pFlag = false;
		for(int i=1; i< n+2 - minDist; i++) {
			for(int j=1; j<n+2 -minDist; j++) {
				eFlag = false;
				pFlag = false;
				if(!eFlag) {
					if(exit.y >=i && exit.y <= i+minDist
							&& exit.x >= j && exit.x <= j+minDist) {
						eFlag = true;
					}
				}
				if(!pFlag) {
					for(Cord p: ls) {
						if(p.y >=i && p.y <= i+minDist
								&& p.x >= j && p.x <= j+minDist) {
							pFlag = true;
							break;
						}
					}
				}
				if(eFlag && pFlag) {
					return new int[] {i, j, minDist};
				}
				
			}
		}
		
		return null;
	}
	public static int getDist(Cord c, Cord t) {
		return Math.abs(c.y-t.y) + Math.abs(c.x - t.x);
	}
	
	public static int[][] rotate(int k, int t, int l) {
		int[][] temp = new int[n+2][n+2];
		for(int i=0; i<n+2; i++) {
			temp[i] = field[i].clone();
		}

//		1 ~ 3까지

		boolean[] visited = new boolean[ls.size()];
		boolean eVisited = false;
		for(int i=0; i<=l; i++) {
			for(int j=0; j<=l; j++) {
				temp[k+i][t+j] = field[k+l-j][t+i];
				if(temp[k+i][t+j] >= 1) {
					temp[k+i][t+j]--;
				}
//				System.out.println((k+i) + ", " + (t+j) + " <= " + (k+l-j) + ", " + (t+i));
				Cord check = new Cord(k+l-j, t+i);
				for(int idx = 0; idx < ls.size(); idx++) {
					if(visited[idx]) {
						continue;
					}
					if (ls.get(idx).equals(check)) {
		                ls.get(idx).y = k+i;
		                ls.get(idx).x = t+j;
		                visited[idx] = true;
		            }	 
				}
				if (!eVisited && exit.equals(check)) {
		            exit.y = k+i;
		            exit.x = t+j;
		            eVisited = true;
		        }
			}
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