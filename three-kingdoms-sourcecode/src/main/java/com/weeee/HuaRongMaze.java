package com.weeee;

import java.util.*;

public class HuaRongMaze {
    static int[][] maze = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1},
        {2,0,0,0,1,0,0,0,1,0,0,0,1},
        {1,0,1,1,1,0,1,0,1,0,1,1,1},
        {1,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,0,1,1,1,1,1,1,1,1,0,1,1},
        {1,0,0,0,1,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,0,1,1,1,0,1},
        {1,0,1,0,0,0,0,0,1,0,0,0,1},
        {1,0,1,0,1,0,1,1,1,1,1,1,1},
        {1,0,0,0,1,0,0,0,0,0,0,0,3},
        {1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    static int sx=1, sy=0, ex=9, ey=12;
    static int[] dx={-1,1,0,0}, dy={0,0,-1,1};

    public static void bfs() {
        Queue<int[]> q = new LinkedList<>();
        boolean[][] vis = new boolean[maze.length][maze[0].length];
        int[][][] prev = new int[maze.length][maze[0].length][2];
        
        q.add(new int[]{sx, sy});
        vis[sx][sy] = true;
        
        while (!q.isEmpty()) {
            int[] c = q.poll();
            int cx = c[0];
            int cy = c[1];
            
            if (cx == ex && cy == ey) {
                List<String> path = new ArrayList<>();
                int currX = ex;
                int currY = ey;
                
                while (currX != sx || currY != sy) {
                    path.add("(" + currX + "," + currY + ")");
                    int[] p = prev[currX][currY];
                    currX = p[0];
                    currY = p[1];
                }
                path.add("(" + sx + "," + sy + ")");
                Collections.reverse(path);
                
                System.out.println("Escape Path:");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < path.size(); i++) {
                    sb.append(path.get(i));
                    if (i < path.size() - 1) {
                        sb.append(" -> ");
                    }
                }
                System.out.println(sb.toString());
                return;
            }
            
            for (int d = 0; d < 4; d++) {
                int nx = cx + dx[d];
                int ny = cy + dy[d];
                
                if (nx >= 0 && ny >= 0 && nx < maze.length && ny < maze[0].length && maze[nx][ny] != 1 && !vis[nx][ny]) {
                    vis[nx][ny] = true;
                    q.add(new int[]{nx, ny});
                    prev[nx][ny] = new int[]{cx, cy};
                }
            }
        }
    }
}