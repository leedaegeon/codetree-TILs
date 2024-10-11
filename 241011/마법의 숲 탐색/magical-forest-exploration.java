import java.util.*;
import java.io.*;
public class Main {
	static int[][] field;
	static int r, c, k;
	static int[] centerCol;
	static int[] exitDir;
	static int[][] exitCord;
	static final int[] dy = {-1, 0, 1, 0};
	static final int[] dx = {0, 1, 0 ,-1};
	static int answer;
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		r = Integer.parseInt(st.nextToken());
		c = Integer.parseInt(st.nextToken());
		k = Integer.parseInt(st.nextToken());
		centerCol = new int[k];
		exitDir = new int[k];
		exitCord = new int[k][2];
//		System.out.println(k);
		for(int i=0; i<k; i++) {
			st = new StringTokenizer(br.readLine());
			
			centerCol[i] = Integer.parseInt(st.nextToken());
			exitDir[i] = Integer.parseInt(st.nextToken());
		}
		initMap();
		for(int num = 1; num <= k; num++) {
			int ci = 1;
			int cj = centerCol[num-1];
//			Cord g = new Cord(ci, cj);
//			System.out.println(ci + ", " + cj);
			move(ci, cj, num-1);
//			for(int i=0; i<r+4; i++) {
//				for(int j=0; j<c+2; j++) {
//					System.out.print(field[i][j] + " ");
//				}
//				System.out.println();
//			}
		
		}
		System.out.println(answer);
	}
	public static void move(int ci, int cj, int num) {
		while(true) {
			
//			아래이동 시도
			if(field[ci+2][cj] == 0
					&&field[ci+1][cj-1] ==0
					&&field[ci+1][cj+1] == 0) {
				ci += 1;
			}else if(field[ci-1][cj-1] == 0          //안되면 왼쪽 이동 시도				
					&& field[ci][cj-2] == 0
					&& field[ci+1][cj-1] == 0
					&& field[ci+2][cj-1] == 0
					&&field[ci+1][cj-2] == 0) {
				
				ci += 1;
				cj -= 1;
				if(exitDir[num] == 0) {
					exitDir[num] = 3;
				}else {
					exitDir[num]-=1;
				}
			}else if(field[ci-1][cj+1] == 0 		 // 안되면 오른쪽 시도
					&&field[ci][cj+2] == 0
					&&field[ci+1][cj+1] == 0
					&&field[ci+1][cj+2] == 0
					&&field[ci+2][cj+1] == 0) {
				ci += 1;
				cj += 1;
				exitDir[num]+=1;
				exitDir[num] %= 4;
			}else {
				break; 								//   위 모든 시도가 실패하면 break
			}
		}
		if(ci < 4) {   //현재 행이 4보다 작으면 맵 초기화 후 리턴
			initMap();
		}else {         // value 계산을 위한 bfs
			field[ci][cj] = num+1;
			for(int d=0; d<4; d++) {
				field[ci+dy[d]][cj+dx[d]] = num+1;
			}
			exitCord[num][0] = ci + dy[exitDir[num]];
			exitCord[num][1] = cj + dx[exitDir[num]];
			
			Queue<int[]> q = new ArrayDeque<>();
			q.offer(new int[]{ci, cj});
			boolean[][] visited = new boolean[r+4][c+2];
			visited[ci][cj] = true;
			int value = -1;
			while(!q.isEmpty()) {
				int[] now = q.poll();
				for(int d=0; d<4; d++) {
					int nexty = now[0] + dy[d];
					int nextx = now[1] + dx[d];
					if(field[nexty][nextx] == -1 || visited[nexty][nextx] || field[nexty][nextx] == 0) {
						continue;
					}
					int nowGolNum = field[now[0]][now[1]]-1;
					if(field[now[0]][now[1]] == field[nexty][nextx]) {
						q.offer(new int[] {nexty, nextx});
						visited[nexty][nextx] = true;
					}
					else if(exitCord[nowGolNum][0] == now[0] 
							&& exitCord[nowGolNum][1] == now[1]) { // 현재 위치가 이 골렘의 출구라면
						q.offer(new int[] {nexty,nextx});
						visited[nexty][nextx] = true;
					}
						
				}
				if(value < now[0]) {
					value = now[0];
				}
			}
			
//			System.out.println("골렘 번호" + num);
//			System.out.println("이동 행" + (value -2));
//			System.out.println("-------------------------");
			answer += value - 2;
		}

	}

	public static void initMap() {
		field = new int[r+4][c+2];
		for(int i=0; i<field.length; i++) {
			field[i][0] = -1;
			field[i][c+1] = -1;
		}
		Arrays.fill(field[r+3], -1);
	}
}