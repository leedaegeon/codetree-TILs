import java.io.*;
import java.util.*;
public class Main {
        // 여기에 코드를 작성해주세요.
	static final int[] dy = {-1, 0, 1, 0};
	static final int[] dx = {0, 1, 0, -1};
	static int[][] field;
	static int l, n, q;
	public static void main(String[] args) throws IOException{

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		l = Integer.parseInt(st.nextToken());
		n = Integer.parseInt(st.nextToken());
		q = Integer.parseInt(st.nextToken());
		field = new int[l][l];
		for(int i=0; i<l; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<l; j++) {
				field[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		List<Unit> units = new ArrayList<>();
		
		int[] unitHP = new int[n];
		for(int i=0; i<n; i++) {
			st = new StringTokenizer(br.readLine());
			int y, x, dy, dx, hp;
			y = Integer.parseInt(st.nextToken())-1;
			x = Integer.parseInt(st.nextToken())-1;
			dy = Integer.parseInt(st.nextToken());
			dx = Integer.parseInt(st.nextToken());
			hp = Integer.parseInt(st.nextToken());
			Unit unit = new Unit(y, x, dy, dx, hp);
			units.add(unit);
			unitHP[i] = hp;
		}
		Set<Integer> out = new HashSet<>();
		int[][] map = new int[l][l];

		int [] damage = new int[n];
		for(int i=0; i<n; i++) {
			damage[i] = unitHP[i];
		}
		for(int i=0; i<q; i++) {
			st = new StringTokenizer(br.readLine());
			int num = Integer.parseInt(st.nextToken())-1;
			int dir = Integer.parseInt(st.nextToken());
			oper(units, num, dir, damage);
		}
		int answer = 0;
		for(int i=0; i<n; i++) {
			if(damage[i] == 0) {
				continue;
			}
			answer += (unitHP[i] - damage[i]);
		}
//		System.out.println(Arrays.toString(damage));
		System.out.println(answer);
    }
    public static void oper(List<Unit> units, int startIdx, int dir, int[] damage) {
		Set<Integer> uSet = new HashSet<>();
		
		Queue<Integer> q = new ArrayDeque<>();
		q.add(startIdx);
		uSet.add(startIdx);
		int[] dmg = new int[units.size()];
		while(!q.isEmpty()) {
			int nowIdx = q.poll();
			Unit now = units.get(nowIdx);
			int nexty = now.y + dy[dir];
			int nextx = now.x + dx[dir];
			for(int i=nexty; i<nexty + now.h; i++) {
				for(int j=nextx; j<nextx + now.w; j++) {
					if(i < 0 || i >= l || j < 0 || j >= l || field[i][j] == 2) {
						return;
					}
					if(field[i][j] == 1) {
						dmg[nowIdx]++;
					}
				}
			}
			for(int i=0; i<units.size(); i++) {
				if(uSet.contains(i)|| damage[i] == 0) {
					continue;
				}
				Unit u = units.get(i);
				if(nexty <= u.y + u.h -1 && nextx <= u.x + u.w - 1 && nexty + now.h -1 >= u.y && nextx + now.w >= u.x -1) {
					q.add(i);
					uSet.add(i);
				}
			}
		}
		dmg[startIdx] = 0;
		for(int idx: uSet) {
			units.get(idx).y += dy[dir];
			units.get(idx).x += dx[dir];
			damage[idx] -= dmg[idx];
		}
		return;
		
	}
    static class Unit{
		int y;
		int x;
		int h;
		int w;
		int hp;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + h;
			result = prime * result + w;
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
			Unit other = (Unit) obj;
			if (h != other.h)
				return false;
			if (w != other.w)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		public Unit(int y, int x, int h, int w, int hp) {
			super();
			this.y = y;
			this.x = x;
			this.h = h;
			this.w = w;
			this.hp = hp;
		}
		@Override
		public String toString() {
			return "Unit [y=" + y + ", x=" + x + ", h=" + h + ", w=" + w + ", hp=" + hp + "]";
		}
		
		
	}
}