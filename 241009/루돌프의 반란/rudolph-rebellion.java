import java.io.*;
import java.util.*;

import javax.management.RuntimeErrorException;
import javax.xml.ws.handler.MessageContext.Scope;
public class Main {
	static final int[] rdy = {-1, -1, 0, 1, 1, 1, 0, -1};
	static final int[] rdx = {0, 1, 1, 1, 0, -1, -1, -1};
//	static final int[] rdy = {-1, 0, 1, 0, -1, 1, 1, -1};
//	static final int[] rdx = {0, 1, 0, -1, 1, 1, -1, -1};
	static final int[] sdy = {-1, 0, 1, 0};
	static final int[] sdx = {0, 1, 0, -1};
	static int n, m, p, c, d;
	static boolean[] death;
	static int[] stun;
	static int[] score;
	public static void main(String[] args) throws IOException{
//		n = 5;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		p = Integer.parseInt(st.nextToken());
		c = Integer.parseInt(st.nextToken());
		d = Integer.parseInt(st.nextToken());
		
		st = new StringTokenizer(br.readLine());
		
		Cord dolf = new Cord(0, Integer.parseInt(st.nextToken())-1, Integer.parseInt(st.nextToken())-1);
//		List<Cord> santas = new ArrayList<>();
		Cord[] santas = new Cord[p]; 
		for(int i=0; i<p; i++) {
			st = new StringTokenizer(br.readLine());
			int num = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			santas[num-1] = new Cord(num-1, y-1, x-1);
		}
		death = new boolean[p];
		stun = new int[p];
		score = new int[p];
//		print(santas, dolf);
		for(int i=0; i<m; i++) {
			int cnt = 0;
		
			
			game(santas, dolf, i);
			for(int j=0; j<death.length; j++) {
				if(!death[j]) {
					score[j]++;
				}else {
					cnt++;
				}
			}
			if(cnt == p) {
				break;
			}
		}
		for(int i=0; i<score.length; i++) {
			System.out.print(score[i] + " ");
		}
	}
	public static void print(Cord[] santas, Cord dolf) {
		System.out.println(Arrays.toString(stun));
		System.out.println(Arrays.toString(death));
		System.out.println(Arrays.toString(santas));
		System.out.println(Arrays.toString(score));
		boolean flag = false;
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				if(dolf.equals(new Cord(0, i, j))) {
					System.out.print("D ");
					flag = true;
					
				}
				if(!flag) {
					for(int k=0; k<santas.length; k++) {
						if(santas[k].equals(new Cord(0, i, j))) {
							System.out.print(santas[k].num + " ");
							flag = true;
							break;
						}
					}
				}
				if(!flag) {
					System.out.print("_ ");
				}
				flag = false;
			}
			System.out.println();
		}
		System.out.println("=======================");
	}
	
	public static void game(Cord[] santas, Cord dolf, int turn) {
//		루돌프 이동 -> !death만 공격
//		System.out.println(turn + " 번쨰 루돌프 이동");
		dolfMove(santas, dolf, turn);
		
//		print(santas, dolf);
//		산타 이동 -> !death && turn >= stuned[]
//		System.out.println(turn + " 번쨰 산타 이동");
		santaMove(santas, dolf, turn);
		
//		print(santas, dolf);
//			충돌 체크 -> 산타 이동의 반대 방향으로 d칸 이동, 충돌된 산타는 점수 획득, stun, 산타 이동 방향으로 연쇄적 1칸 이동
	}
	public static void santaMove(Cord[] santas, Cord dolf, int turn) {
		int[] dirs = new int[santas.length];
		int[] dists = new int[santas.length];
		Arrays.fill(dists, Integer.MAX_VALUE);
		Arrays.fill(dirs, -1);
		for(int i=0; i<santas.length; i++) {
			
			if(stun[i] <= turn && !death[i]) {
				
				int dist = getDist(dolf,  santas[i]);
				int dir = -1;
				for(int d=0; d<4; d++) {
					boolean flag = false;
					int nexty = santas[i].y + sdy[d];
					int nextx = santas[i].x + sdx[d];
					Cord next = new Cord(santas[i].num, nexty, nextx);
					if(!isValid(next)) {
						continue;
					}
					for(int j=0; j<santas.length; j++) {
						if(next.equals(santas[j])) {
							flag = true;
							break;
						}
					}
					if(flag) {
						continue;
					}
					int nextDist = getDist(dolf, next);
					if(dist <= nextDist) {
						continue;
					}
					dist = nextDist;
					dir = d;
				}
				dists[i] = dist;
				dirs[i] = dir;
				if(dir != -1) {
					santas[i].y += sdy[dirs[i]];
					santas[i].x += sdx[dirs[i]];
				}
				if(dists[i] == 0) {
//					System.out.println("산타가 돌프한테 충돌 " + santas[i]);
//					충돌
					crashSantaToDolf(santas, santas[i], turn, dirs[i]);
				}
			}
		}
		
		
	}
	
	public static void crashSantaToDolf(Cord[] santas, Cord stunSanta, int turn, int dir) {
		dir += 2;
		dir %= 4;
		stun[stunSanta.num] = turn + 2;
		score[stunSanta.num] += d;
		Queue<Cord> q = new ArrayDeque<>();
		q.add(stunSanta);
		boolean[] visited = new boolean[santas.length];
		visited[stunSanta.num] = true;
		int scale = d;
		
		while(!q.isEmpty()) {
			Cord now = q.poll();
			int nexty = now.y + sdy[dir]*scale;
			int nextx = now.x + sdx[dir]*scale;
			scale = 1;
			Cord next = new Cord(now.num, nexty, nextx);
			if(!isValid(next)) {
				santas[now.num].y = nexty;
				santas[now.num].x = nextx;
				death[now.num] = true;
				stun[now.num] = Integer.MAX_VALUE;
				continue;
			}
			santas[now.num].y = nexty;
			santas[now.num].x = nextx;
			for(int i=0; i<santas.length; i++) {
				if(!visited[i] && next.equals(santas[i])) {
					q.offer(santas[i]);
					visited[i] = true;

				}
			}
		}
	}
	
	public static void dolfMove(Cord[] santas, Cord dolf, int turn) {
//		가장 가까운 산타 탐색 -> 우선순위 적용
		PriorityQueue<Cord> pq = new PriorityQueue<>();
		int dist = Integer.MAX_VALUE;
		for(int i=0; i<santas.length; i++) {
			
			if(!death[i]) {
				int nextDist = getDist(dolf, santas[i]);
				if(dist > nextDist) {
					dist = nextDist;
					pq = new PriorityQueue<>();
					pq.add(santas[i]);
				}else if(dist == nextDist){
					pq.add(santas[i]);
				}
			}
		}
//		산타 방향으로 한칸 이동
		Cord target = pq.poll();
		int dir = -1;
		for(int i=0; i<8; i++) {
			int nexty = dolf.y + rdy[i];
			int nextx = dolf.x + rdx[i];
			Cord next = new Cord(0, nexty, nextx);
			if(!isValid(next)) {
				continue;
			}
			int nextDist = getDist(next, target);
			if(dist >= nextDist) {
				dist = nextDist;
				dir = i;
			}
		}
//		if(dir == -1) {
//			System.out.println(turn);
//			System.out.println(dolf);
//			System.out.println(Arrays.toString(death));
//			System.out.println(target);
//		}
		dolf.y += rdy[dir];
		dolf.x += rdx[dir];
//		충돌 체크 -> 충돌된 산타는 점수 획득, stun, 루돌프 방향으로 연쇄적 이동
		if(dist == 0) {
//			System.out.println("돌프가 산타한테 충돌 " + target);
			crashDolfToSanta(santas, target, turn, dir);
		}
	}

	public static void crashDolfToSanta(Cord[] santas, Cord stunSanta, int turn, int dir) {
		
		stun[stunSanta.num] = turn+2;
		score[stunSanta.num] += c;
		
		Queue<Cord> q = new ArrayDeque<>();
		q.add(stunSanta);
		boolean[] visited = new boolean[santas.length];
		visited[stunSanta.num] = true;
		int scale = c;
		while(!q.isEmpty()) {
			Cord now = q.poll();
			int nexty = now.y + rdy[dir]*scale;
			int nextx = now.x + rdx[dir]*scale;
			scale = 1;
			Cord next = new Cord(now.num, nexty, nextx);
			if(!isValid(next)) {
				santas[now.num].y = nexty;
				santas[now.num].x = nextx;
				death[now.num] = true;
				stun[now.num] = Integer.MAX_VALUE;
				continue;
			}
			santas[now.num].y = nexty;
			santas[now.num].x = nextx;
			for(int i=0; i<santas.length; i++) {
				if(!visited[i] && next.equals(santas[i])) {
					q.offer(santas[i]);
					visited[i] = true;
				}
			}
			
		}
		
	}
	public static boolean isValid(Cord c1) {
		if(c1.y < 0
				|| c1.y >= n
				|| c1.x < 0
				|| c1.x >= n) {
			return false;
		}
		return true;
	}
	public static int getDist(Cord c1, Cord c2) {
		int y = (c1.y - c2.y)*(c1.y - c2.y);
		int x = (c1.x - c2.x)*(c1.x - c2.x);
		return y+x;
	}
	static class Cord implements Comparable<Cord>{
		int num;
		int y;
		int x;
		
	

		public Cord(int num, int y, int x) {
			super();
			this.num = num;
			this.y = y;
			this.x = x;
		}

		@Override
		public int compareTo(Cord o) {
			if(this.y == o.y) {
				return o.x - this.x;
			}else {
				return o.y - this.y;
			}
		}

		

		@Override
		public String toString() {
			return "[num=" + num + ", y=" + y + ", x=" + x + "]";
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