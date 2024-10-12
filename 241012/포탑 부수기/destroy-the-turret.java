import java.util.*;
import java.io.*;
public class Main {
	static int n, m, k;
	static int[][] field;
	static List<Unit> units = new ArrayList<>();

	static Set<Unit> visited;
	static final int[] dy = {0, 1, 0, -1};
	static final int[] dx = {1, 0, -1, 0};
	static int minPath;
	static List<Unit> realPath;
	static final int[] dy2 = {-1, -1, 0, 1, 1, 1, 0, -1};
	static final int[] dx2 = {0, 1, 1, 1, 0, -1, -1, -1};
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		k = Integer.parseInt(st.nextToken());
		field = new int[n][m];

		int idx = 0;
		for(int i=0; i<n; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<m; j++) {
				field[i][j] = Integer.parseInt(st.nextToken());
				if(field[i][j] != 0) {
					units.add(new Unit(i, j, 0, field[i][j]));
				}
			}
		}
		minPath = m+n+1;
		for(int turn=1; turn<=k; turn++) {
			visited = new HashSet<>();
//			System.out.println("====================");
//			System.out.println("공격전");
//			for(int i=0; i<n; i++) {
//				for(int j=0; j<m; j++) {
//					System.out.print(field[i][j] + " ");
//				}
//				System.out.println();
//			}
			
			int fromIdx = selectFromIdx();
			int targetIdx = selectTargetIdx();
			attack(fromIdx, targetIdx, turn);
			if(units.size() == 1) {
				break;
			}
//			System.out.println("공격후");
//			
//
//			for(int i=0; i<n; i++) {
//				for(int j=0; j<m; j++) {
//					System.out.print(field[i][j] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println(units);
//			System.out.println("====================");
		}
		int answer = 0;
		for(int i=0; i<n; i++) {
			for(int j=0; j<m; j++) {
				if(field[i][j] > 0) {
					answer = Math.max(answer, field[i][j]);
				}
//				System.out.print(field[i][j] + " ");
			}
//			System.out.println();
		}
		System.out.println(answer);
	}
	public static void attack(int fromIdx, int targetIdx, int turn) {
		Unit from = units.get(fromIdx);
		Unit target = units.get(targetIdx);
//		from 공격력 업 해줘야함
		from.e += n+m;
		realPath = null;
//		System.out.println("공격자: " +from);
//		System.out.println("피해자: " + target);
		lazerAttack(new ArrayList<>(), from, from, target, new HashSet<>());
		if(realPath != null) {
//			System.out.println("레이저 공격");
//			System.out.println(target);
//			System.out.println(realPath);
			if(field[target.y][target.x] < from.e) {
				field[target.y][target.x] = 0;
			}else {
				field[target.y][target.x] -= from.e;				
			}
			visited.add(target);
			int half = Math.floorDiv(from.e, 2);
			
			for(Unit p: realPath) {
				if(visited.contains(p)) {
					continue;
				}
				if(field[p.y][p.x] < half) {
					field[p.y][p.x] = 0;
				}else {
					field[p.y][p.x] -=  half;
				}
				visited.add(p);
			}			
		}else {
//			System.out.println("포탄 공격");
			
			bombAttack(from, target);
		}
//		공격 후 공격자 필드 공격력, 턴 업데이트
		field[units.get(fromIdx).y][units.get(fromIdx).x] = from.e;
		units.get(fromIdx).t = turn;
		
//		포탑 부서짐 로직
//		공격 불능인 포탑 삭제
		Set<Unit> alive = new HashSet<>();
//		System.out.println("공격과 관련된 포탑: " + visited);
		for(Unit v: visited) {
			if(field[v.y][v.x] == 0) {
				
				units.remove(v);
			}else {
				alive.add(v);
			}
		}
		
//		공격 받은 뒤 visited에 있는 애들 제외하고 포탑 정비 들어감
		Set<Unit> healSet = new HashSet<>();
		for(int i=0; i<n; i++) {
			for(int j=0; j<m; j++) {
				Unit heal = new Unit(i, j);
				if(!visited.contains(heal) && field[i][j] > 0) {
					field[i][j]++;
					healSet.add(heal);
				}
			}
		}
		for(int i=0; i<units.size(); i++) {
			if(healSet.contains(units.get(i))) {
				units.get(i).e = field[units.get(i).y][units.get(i).x];
			}else if(alive.contains(units.get(i))){
				units.get(i).e = field[units.get(i).y][units.get(i).x];
			}
		}
		
		

		return;
	}
	
	public static void lazerAttack(List<Unit> path, Unit now, Unit from, Unit target, Set<Unit> searchVisited) {

		
//		공격 대상자는 공격자 공격력만큼 피해
//		경로에 있는 포탑은 공격자 공격력 절반만큼 피해
//		공격자 리스트에 해당 공격자의 턴 증가
//		path에는 now(공격 대상자)가 없는 상황
		if(target.equals(now)) {
			if(minPath > path.size()) {
				minPath = path.size();
//				System.out.println(path);
				realPath = new ArrayList<>(path);
			}
			return ;
		}
		searchVisited.add(now);
		path.add(now);
		for(int i=0; i<4; i++) {
			int nexty = now.y + dy[i];
			int nextx = now.x + dx[i];
			if(nexty < 0) {
				nexty = n-1;
			}
			if(nexty >= n) {
				nexty %= n;
			}
			if(nextx < 0) {
				nextx = m-1;
			}
			if(nextx >= m) {
				nextx %= m;
			}
			if(field[nexty][nextx] == 0) {
				continue;
			}
			Unit next = new Unit(nexty, nextx);
			if(searchVisited.contains(next)) {
				continue;
			}
			lazerAttack(path, next, from, target, searchVisited);
			
		}
		path.remove(now);
		searchVisited.remove(now);
		
		return ;
	}
//	포탄공격
	public static void bombAttack(Unit from, Unit target) {
		if(field[target.y][target.x] < from.e ) {
			field[target.y][target.x] = 0;
		}else {
			field[target.y][target.x] -= from.e;
		}
		visited.add(target);
		int half = Math.floorDiv(from.e, 2);
		for(int i=0; i<8; i++) {
			int nexty = target.y + dy2[i];
			int nextx = target.x + dx2[i];
			if(nexty < 0) {
				nexty = n-1;
			}
			if(nexty >= n) {
				nexty %= n;
			}
			if(nextx < 0) {
				nextx = m-1;
			}
			if(nextx >= m) {
				nextx %= m;
			}
			if(field[nexty][nextx] == 0) {
				continue;
			}
			Unit next = new Unit(nexty, nextx);
			if(visited.contains(next)) {
				continue;
			}
//			System.out.println(next);
			if(field[nexty][nextx] < half) {
				field[nexty][nextx] = 0;
			}else {
				field[nexty][nextx] -= half;
			}
			visited.add(next);
		}
	}
//	공격자 인덱스 얻어오기, 공업, 턴업은 여기서x
	public static int selectFromIdx() {
		Collections.sort(units);
		visited.add(units.get(0));
		return 0;
	}
//	공격대상자 인덱스 얻어오기, 공다운은 여기서x
	public static int selectTargetIdx() {
		Collections.sort(units);
		return units.size()-1;
	}
	static class Unit implements Comparable<Unit>{
		int y;
		int x;
		int t;
		int e;
		
		public Unit(int y, int x) {
			super();
			this.y = y;
			this.x = x;
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
			Unit other = (Unit) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public int compareTo(Unit o) {
//			공격력이 가장 낮은게 우선
			if(this.e != o.e){
				return this.e - o.e;
//			공격력이 같다면 가장 최근에 공격했던 유닛 우선
			}
			else if(this.t != o.t) {
				return o.t - this.t;
//			턴이 같다면 행, 열의 합이 더 큰게 우선
			}else if(o.y + o.x != (this.y +this.x)) {
				return (o.y + o.x) - (this.y +this.x);
//			행열 합도 같다면 열이 큰게 우선
			}else {
				return o.y - this.y;
			}

		}

		public Unit(int y, int x, int t, int e) {
			super();
			this.y = y;
			this.x = x;
			this.t = t;
			this.e = e;
		}

		@Override
		public String toString() {
			return "[행:" + y + ", 열:" + x + ", 턴:" + t + ", 공격력:" + e + "]";
		}
		
	}

}