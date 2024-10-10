import java.util.*;
import java.io.*;
public class Main {
	static final int[] dy = {-1, 0, 1, 0};
	static final int[] dx = {0, 1, 0, -1};
	static int r,c;
	static int[][] field;
	static int answer = 0;
	static int k;
	static Map<Integer, Cord> exitMap = new HashMap<>();
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		r = Integer.parseInt(st.nextToken());
		c = Integer.parseInt(st.nextToken());
		k = Integer.parseInt(st.nextToken());
		int[] centers = new int[k];
		int[] exits = new int[k];
		for(int i=0; i< k; i++) {
			st = new StringTokenizer(br.readLine());
			centers[i] = Integer.parseInt(st.nextToken());
			exits[i] = Integer.parseInt(st.nextToken());
		}
		
		field = new int[r+5][c+2]; 
//		0, 1, 2, 3 + r
		for(int j=0; j<field.length; j++) {
			field[j][0] = -1;
			field[j][c+1] = -1;
		}
		for(int j=0; j<field[0].length; j++) {
			field[r+4][j] = -1;
			field[0][j] = -1;
		}
//		for(int i=4; i < field.length-1; i++) {
//			for(int j=1; j < field[0].length-1; j++) {
//				System.out.print(field[i][j] + " ");
//				
//			}
//			System.out.println();
//		}
//		반드시 행 값 더할때 초기 수치 고려해야함
		for(int gNum=1; gNum<=k; gNum++) {
//			2, 중심
			Golam g = new Golam(gNum, new Cord(2, centers[gNum-1]), exits[gNum-1]);
			
			Golam newG = golMove(g);
			if(newG != null) {
				field[newG.t.y][newG.t.x] = gNum;
				field[newG.c.y][newG.c.x] = gNum;
				field[newG.r.y][newG.r.x] = gNum;
				field[newG.l.y][newG.l.x] = gNum;
				field[newG.b.y][newG.b.x] = gNum;
				field[newG.getExitCord().y][newG.getExitCord().x] = k+1;
				exitMap.put(gNum, newG.getExitCord());
//				System.out.println("====================");
//				for(int i=4; i < field.length-1; i++) {
//					for(int j=1; j < field[0].length-1; j++) {
//						System.out.print(field[i][j] + " ");
//						
//					}
//					System.out.println();
//				}
//				System.out.println(answer);
				
			}else {
//				System.out.println("숲 초기화");
				field = new int[r+5][c+2]; 

				for(int j=0; j<field.length; j++) {
					field[j][0] = -1;
					field[j][c+1] = -1;
				}
				for(int j=0; j<field[0].length; j++) {
					field[r+4][j] = -1;
					field[0][j] = -1;
				}
			}
		}
		
		System.out.println(answer);
	}
	
	private static Golam golMove(Golam g) {
//		System.out.println("현재 번호: " + g.num);
		Golam promise = null;
		int nowValue = 0;
		boolean[][][] visited = new boolean[r+4][c+1][5];
		Queue<Golam> q= new ArrayDeque<>();
		q.offer(g);
		
		while(!q.isEmpty()) {
			Golam now = q.poll();
//			System.out.println(now);
			while(true) {
				if(goBottom(now, visited)) {	
					Cord nextCenter = new Cord(now.c.y + dy[2], now.c.x);
					now.setCenter(nextCenter);
					
					visited[now.c.y][now.c.x][4] = true;
					visited[now.t.y][now.t.x][0] = true;
					visited[now.r.y][now.r.x][1] = true;
					visited[now.b.y][now.b.x][2] = true;
					visited[now.l.y][now.l.x][3] = true;
				}else {
					break;
				}
			}
			
			if(goLeft(now, visited)) {
				Cord leftCenter = new Cord(now.c.y, now.c.x + dx[3]);
				Golam nextG = new Golam(g.num, leftCenter, now.exit);
				if(goBottom(nextG, visited)) {
					visited[nextG.t.y][nextG.t.x][0] = true;
					visited[nextG.b.y][nextG.b.x][2] = true;
					visited[nextG.l.y][nextG.l.x][3] = true;
					
					Cord nextCenter = new Cord(nextG.c.y += dy[2], nextG.c.x);
					nextG.rotate(3);
					nextG = new Golam(g.num, nextCenter, nextG.exit);
					q.offer(nextG);
					visited[now.b.y][now.b.x][2] = true;
					visited[now.l.y][now.l.x][3] = true;
				}
			}
			if(goRight(now, visited)) {
				Cord rightCenter = new Cord(now.c.y, now.c.x + dx[1]);
				Golam nextG = new Golam(g.num, rightCenter, now.exit);
				if(goBottom(nextG, visited)) {
					visited[nextG.t.y][nextG.t.x][0] = true;
					visited[nextG.r.y][nextG.r.x][1] = true;
					visited[nextG.b.y][nextG.b.x][2] = true;
					
					Cord nextCenter = new Cord(nextG.c.y += dy[2], nextG.c.x);
					nextG.rotate(1);
					nextG = new Golam(g.num, nextCenter, nextG.exit);
					q.offer(nextG);
					visited[now.b.y][now.b.x][2] = true;
					visited[now.l.y][now.l.x][3] = true;
				}
			}
//			다음 움직여볼 수 있는 골렘이 없고 현재는 가장 아래에 와있으며, 
//			현재 골렘의 어느 한 부위라도 숲 밖에 있으면
//			점수 계산
//			int nextValue = getValue(now);
////			if(nowValue <= nextValue) {
////				nowValue = nextValue;
////				promise = now;
////			}
			if(!goBottom(now, visited)) {
				nowValue = getValue(now);;
				promise = now;

			}
			if(q.isEmpty()) {
//				System.out.println(now + " null");
				if(now.t.y <= 3 || now.c.y <= 3 || now.b.y <= 3) {
					
					return null;
				}
			}
			
//			
		}
		answer += nowValue;
		return promise;
	}
	private static int getValue(Golam g) {
//		now의 센터를 필드의 0과 -1이 아닌 곳을 갈 수 있다.
//		기존 골렘의 출구 좌표는 -2
//		now의 출구좌표에서 다른 골렘을 갈 수 있다.
//		now의 출구 좌표가 아니라면 다른 골렘으로 못간다.
		Queue<Cord> q = new ArrayDeque<>();
		q.offer(g.c);
		int maxValue = g.c.y;
		Set<Cord> golamSet = new HashSet<>();
		golamSet.add(g.t);
		golamSet.add(g.l);
		golamSet.add(g.r);
		golamSet.add(g.c);
		golamSet.add(g.b);
		Cord temp = null;
		Cord exit = g.getExitCord();
		boolean[][] visited = new boolean[r+5][c+2];
		visited[g.c.y][g.c.x] = true;
//		System.out.println("---------------------------");
		while(!q.isEmpty()) {
			Cord now = q.poll();
			
//			System.out.println(now);
			for(int i=0; i<4; i++) {
				int nexty = now.y + dy[i];
				int nextx = now.x + dx[i];
				Cord next = new Cord(nexty, nextx);
				if(field[nexty][nextx] == -1 || visited[nexty][nextx]) {
					continue;
				}
				if(!golamSet.contains(next) && field[nexty][nextx] == 0) {
					continue;
				}
				if(golamSet.contains(now) && golamSet.contains(next)) {
					q.offer(next);
					visited[next.y][next.x] = true;
				}else if(golamSet.contains(now) && now.equals(exit)
						&& field[next.y][next.x] != 0) {
					q.offer(next);
					visited[next.y][next.x] = true;
				}else if(exitMap.get(field[now.y][now.x]) != null && exitMap.get(field[now.y][now.x]).equals(next)) {
					q.offer(next);
					visited[next.y][next.x] = true;
				}else if(field[now.y][now.x]== field[next.y][next.x] ) {
					q.offer(next);
					visited[next.y][next.x] = true;
				}else if(field[now.y][now.x]== k+1 && field[nexty][nextx] != 0) {
					q.offer(next);
					visited[next.y][next.x] = true;
				}
				
				
				
//				
			}
			if(maxValue < now.y) {
				maxValue = now.y;
				temp = new Cord(now.y, now.x);
			}
		}
//		System.out.println(temp + " " + (maxValue-3));
		return maxValue-3;
	}
	private static boolean goRight(Golam g, boolean[][][] visited) {
		Cord  nt, nl, nr;
		nt = new Cord(g.t.y + dy[1], g.t.x + dx[1]);
		nl = new Cord(g.l.y + dy[1], g.l.x + dx[1]);
		nr = new Cord(g.r.y + dy[1], g.r.x + dx[1]);
		if(field[nl.y][nl.x] != 0 || visited[nl.y][nl.x][3]) {
			return false;
		}
		if(field[nt.y][nt.x] != 0 || visited[nt.y][nt.x][0]) {
			return false;
		}
		if(field[nr.y][nr.x] != 0 || visited[nr.y][nr.x][1]) {
			return false;
		}
		return true;
	}
	private static boolean goLeft(Golam g, boolean[][][] visited) {
		Cord  nt, nl, nr;
		nt = new Cord(g.t.y + dy[3], g.t.x + dx[3]);
		nl = new Cord(g.l.y + dy[3], g.l.x + dx[3]);
		nr = new Cord(g.r.y + dy[3], g.r.x + dx[3]);
		if(field[nl.y][nl.x] != 0 || visited[nl.y][nl.x][3]) {
			return false;
		}
		if(field[nt.y][nt.x] != 0 || visited[nt.y][nt.x][0]) {
			return false;
		}
		if(field[nr.y][nr.x] != 0 || visited[nr.y][nr.x][1]) {
			return false;
		}
		
		return true;
	}
	private static boolean goBottom(Golam g, boolean[][][] visited) {
		Cord nl, nb, nr;
		nl = new Cord(g.l.y + dy[2], g.l.x + dx[2]);
		nb = new Cord(g.b.y + dy[2], g.b.x + dx[2]);
		nr = new Cord(g.r.y + dy[2], g.r.x + dx[2]);
		
		if(field[nl.y][nl.x] != 0 || visited[nl.y][nl.x][3]) {
			return false;
		}
		if(field[nb.y][nb.x] != 0 || visited[nb.y][nb.x][4]) {
			return false;
		}
		if(field[nr.y][nr.x] != 0 || visited[nr.y][nr.x][1]) {
			return false;
		}
		
		return true;
	}

	static class Golam{
		int num;
		int exit;
		Cord c;
		Cord t;
		Cord r;
		Cord b;
		Cord l;
		public Golam(int num, Cord c, int exit) {
			super();
			this.num = num;
			this.c = c;
			this.t = new Cord(c.y - 1, c.x);
			this.r = new Cord(c.y, c.x+1);
			this.b = new Cord(c.y+1, c.x);
			this.l = new Cord(c.y, c.x-1);
			this.exit = exit;
		}
		public void setCenter(Cord c) {
			this.c = c;
			this.t = new Cord(c.y - 1, c.x);
			this.r = new Cord(c.y, c.x+1);
			this.b = new Cord(c.y+1, c.x);
			this.l = new Cord(c.y, c.x-1);
		}
		@Override
		public String toString() {
			return "Golam #"+ this.num + "[중앙 =" + c + ", 탑 =" + t + ", 오른쪽 =" + r + ", 아래 =" + b + ", 왼쪽 =" + l + "]" + " 출구: " + this.getExitCord();
		}
		public void rotate(int dir) {
			//right
			if(dir == 1) {
				this.exit++;
				this.exit %=4;
			}else if(dir == 3) {
				if(this.exit == 0) {
					this.exit = 3;
				}else {
					this.exit--;
				}
			}
		}
		public Cord getExitCord() {
			if(this.exit == 0) {
				return t;
			}else if(this.exit == 1) {
				return r;
			}else if(this.exit == 2) {
				return b;
			}else {
				return l;
			}
		}
		
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
			return "[" + (y-3) + ", " + (x) + "]";
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
}