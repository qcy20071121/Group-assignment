package com.weeee;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class App extends Application {
    private static Stage primaryStage;
    private static Scene scene;
    private static List<Officer> generals;
    private static Officer emperor;
    private static Officer chiefOfMilitary;

    static void setRoot(String fxml) throws java.io.IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static javafx.scene.Parent loadFXML(String fxml) throws java.io.IOException {
        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // 节点二维坐标 (X, Y)
    private static final int[][] nodeCoords = {
        {0, 0},         // 0-indexed unused
        {120, 250},     // Node 1 (Camp)
        {300, 150},     // Node 2
        {270, 40},      // Node 3
        {380, 90},      // Node 4
        {570, 100},     // Node 5
        {470, 200},     // Node 6
        {670, 100},     // Node 7
        {600, 280},     // Node 8
        {720, 310},     // Node 9
        {370, 380}      // Node 10
    };

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Three Kingdoms: Battle of Red Cliff - Sun Wu System");

        // 初始化数据
        initData();

        showMapSimulatorView();
    }

    private void initData() {
        emperor = OfficerData.getEmperor();
        chiefOfMilitary = OfficerData.getChiefOfMilitary();
        generals = OfficerData.getGenerals();

        // 构造算法依赖的图
        EnemyGraph.build();
        FoodDFS.build();
    }

    private void showMapSimulatorView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #f5f5f5;");

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        Label header = new Label("Battlefield Map Path Simulator");
        // header.setStyle("-fx-text-fill: #333333; -fx-font-size: 20px; -fx-font-weight: bold;");
        topBar.getChildren().addAll(header);
        layout.setTop(topBar);

        // 左侧绘制区域
        Pane mapPane = new Pane();
        mapPane.setPrefSize(780, 450);

        // 绘制静态地图的连线
        drawMapLines(mapPane);

        // 右侧控制区域
        VBox controls = new VBox(15);
        controls.setPadding(new Insets(15));
        controls.setPrefWidth(260);
        controls.setStyle("-fx-background-color: #eaeaea; -fx-border-color: #cccccc;");

        Label lblTarget = new Label("Select Target Node (2-10):");
        lblTarget.setStyle("-fx-text-fill: #333333;");
        ComboBox<Integer> cbTarget = new ComboBox<>();
        for (int i = 2; i <= 10; i++) cbTarget.getItems().add(i);
        cbTarget.setValue(8);

        Label lblAlg = new Label("Select Routing Algorithm:");
        lblAlg.setStyle("-fx-text-fill: #333333;");
        ComboBox<String> cbAlg = new ComboBox<>();
        cbAlg.getItems().addAll("BFS (Shortest Steps)", "Dijkstra (Shortest Time)", "A* Search (Shortest Time)", "Best First Search (Greedy Distance)");
        cbAlg.setValue("BFS (Shortest Steps)");

        Label lblGen = new Label("Select Officer (For Dijkstra / A*):");
        lblGen.setStyle("-fx-text-fill: #333333;");
        ComboBox<String> cbGen = new ComboBox<>();
        // 所有武将
        List<Officer> allOfficers = new ArrayList<>(generals);
        allOfficers.add(emperor);
        allOfficers.add(chiefOfMilitary);
        for (Officer o : allOfficers) {
            cbGen.getItems().add(o.name + " (" + o.type + ")");
        }
        cbGen.setValue("Sun Quan (King)");

        Button btnAnimate = new Button("Run & Animate Path");
        btnAnimate.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAnimate.setPrefWidth(230);

        TextArea txtResult = new TextArea();
        txtResult.setEditable(false);

        controls.getChildren().addAll(lblTarget, cbTarget, lblAlg, cbAlg, lblGen, cbGen, btnAnimate, txtResult);
        
        HBox body = new HBox(20);
        body.setPadding(new Insets(15));
        body.getChildren().addAll(mapPane, controls);
        layout.setCenter(body);

        // 动画逻辑
        Circle soldier = new Circle(8, Color.RED);
        soldier.setVisible(false);
        mapPane.getChildren().add(soldier);

        btnAnimate.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                int target = cbTarget.getValue();
                String alg = cbAlg.getValue();
                Officer selectedOfficer = emperor;
                String selName = cbGen.getValue().split(" \\(")[0];
                for (Officer o : allOfficers) {
                    if (o.name.equals(selName)) {
                        selectedOfficer = o;
                        break;
                    }
                }

                List<Integer> path = new ArrayList<>();
                if (alg.startsWith("BFS")) {
                    path = getBfsPath(target);
                    txtResult.setText("BFS Shortest Steps:\nPath: " + path + "\nSteps: " + (path.size() - 1));
                } else if (alg.startsWith("Dijkstra")) {
                    path = getDijkstraPath(selectedOfficer, target, txtResult);
                } else if (alg.startsWith("A* Search")) {
                    path = getAStarPath(selectedOfficer, target, txtResult);
                } else if (alg.startsWith("Best First Search")) {
                    path = getBestFirstSearchPath(target, txtResult);
                }

                // 播放移动动画
                playSoldierAnimation(mapPane, soldier, path);
            }
        });

        scene = new Scene(layout, 1110, 560);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawMapLines(Pane pane) {
        pane.getChildren().clear();
        int[][] edges = {
            {1, 6, 20}, {1, 3, 18}, {5, 6, 17}, {7, 8, 19}, {7, 9, 17}, {1, 10, 16}, {9, 10, 18}, // Flat
            {1, 2, 10}, {5, 7, 10}, {6, 7, 23}, {8, 10, 12}, // Forest
            {2, 4, 10}, {3, 4, 12}, {4, 5, 12}, {8, 9, 7}, // Swamp
            {3, 7, 28}, {6, 8, 35} // Plank
        };

        String[] terrains = {
            "flat", "flat", "flat", "flat", "flat", "flat", "flat",
            "forest", "forest", "forest", "forest",
            "swamp", "swamp", "swamp", "swamp",
            "plank", "plank"
        };

        Map<String, Color> terrainColors = new HashMap<>();
        terrainColors.put("flat", Color.BLUE);
        terrainColors.put("forest", Color.GREEN);
        terrainColors.put("swamp", Color.ORANGE);
        terrainColors.put("plank", Color.RED);

        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0];
            int v = edges[i][1];
            int dist = edges[i][2];
            String terr = terrains[i];

            Line line = new Line(nodeCoords[u][0], nodeCoords[u][1], nodeCoords[v][0], nodeCoords[v][1]);
            Color color = terrainColors.get(terr);
            line.setStroke(color);
            line.setStrokeWidth(3);
            pane.getChildren().add(line);
            
            Label lbl = new Label(dist + "km");
            lbl.setLayoutX((nodeCoords[u][0] + nodeCoords[v][0]) / 2.0 - 10);
            lbl.setLayoutY((nodeCoords[u][1] + nodeCoords[v][1]) / 2.0 - 8);
            pane.getChildren().add(lbl);
        }

        for (int i = 1; i <= 10; i++) {
            Circle nodeCircle = new Circle(15, Color.web("#ffffff"));
            nodeCircle.setStroke(Color.web("#777777"));
            nodeCircle.setStrokeWidth(2);
            nodeCircle.setLayoutX(nodeCoords[i][0]);
            nodeCircle.setLayoutY(nodeCoords[i][1]);

            Label lblName = new Label(String.valueOf(i));
            lblName.setLayoutX(nodeCoords[i][0] - 5);
            lblName.setLayoutY(nodeCoords[i][1] - 8);

            pane.getChildren().addAll(nodeCircle, lblName);
        }
    }

    private List<Integer> getBfsPath(int target) {
        int[] dist = new int[11];
        Arrays.fill(dist, Integer.MAX_VALUE);
        int[] parent = new int[11];
        Arrays.fill(parent, -1);

        Queue<Integer> q = new LinkedList<>();
        dist[1] = 0;
        q.add(1);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : EnemyGraph.adj.get(u)) {
                if (dist[v] == Integer.MAX_VALUE) {
                    dist[v] = dist[u] + 1;
                    parent[v] = u;
                    q.add(v);
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path);
        return path;
    }

    private List<Integer> getDijkstraPath(Officer officer, int target, TextArea resultArea) {
        double baseSpeed = officer.type.equalsIgnoreCase("cavalry") ? 2.0 : 1.0;
        double[] minTime = new double[11];
        Arrays.fill(minTime, Double.MAX_VALUE);
        int[] parent = new int[11];
        Arrays.fill(parent, -1);

        PriorityQueue<double[]> pq = new PriorityQueue<>(new Comparator<double[]>() {
            @Override
            public int compare(double[] a, double[] b) {
                return Double.compare(a[1], b[1]);
            }
        });
        minTime[1] = 0.0;
        pq.add(new double[]{1, 0.0});

        while (!pq.isEmpty()) {
            double[] curr = pq.poll();
            int u = (int) curr[0];
            double t = curr[1];

            if (t > minTime[u]) continue;
            if (u == target) break;

            for (EnemyGraph.Edge edge : EnemyGraph.graph.get(u)) {
                int v = edge.to;
                double mult = getMultiplier(officer.type, edge.terrain);
                double speed = baseSpeed * mult;
                double travelTime = edge.distance / speed;

                if (minTime[u] + travelTime < minTime[v]) {
                    minTime[v] = minTime[u] + travelTime;
                    parent[v] = u;
                    pq.add(new double[]{v, minTime[v]});
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path);

        resultArea.setText("Dijkstra Shortest Time:\nOfficer: " + officer.name + "\nPath: " + path + "\nTime: " + String.format("%.2f", minTime[target]) + " hours");
        return path;
    }

    private void playSoldierAnimation(Pane pane, Circle soldier, List<Integer> path) {
        if (path.isEmpty()) return;
        
        soldier.setVisible(true);
        soldier.setCenterX(nodeCoords[path.get(0)][0]);
        soldier.setCenterY(nodeCoords[path.get(0)][1]);

        Timeline timeline = new Timeline();
        int stepDuration = 800; // ms
        soldier.setTranslateX(0);
        soldier.setTranslateY(0);

        for (int i = 0; i < path.size(); i++) {
            int nodeIdx = path.get(i);
            KeyValue kvX = new KeyValue(soldier.centerXProperty(), (double) nodeCoords[nodeIdx][0]);
            KeyValue kvY = new KeyValue(soldier.centerYProperty(), (double) nodeCoords[nodeIdx][1]);
            KeyFrame kf = new KeyFrame(Duration.millis(i * stepDuration), kvX, kvY);
            timeline.getKeyFrames().add(kf);
        }
        timeline.play();
    }

    private double getMultiplier(String troopType, String terrain) {
        String t = troopType.toLowerCase();
        String m = terrain.toLowerCase();
        if (t.contains("cavalry")) {
            switch (m) {
                case "flat": return 3.0;
                case "forest": return 0.8;
                case "swamp": return 0.3;
                case "plank": return 0.5;
                default: return 1.0;
            }
        } else if (t.contains("archer")) {
            switch (m) {
                case "flat": return 2.0;
                case "forest": return 1.0;
                case "swamp": return 2.5;
                case "plank": return 0.5;
                default: return 1.0;
            }
        } else if (t.contains("infantry")) {
            switch (m) {
                case "flat": return 2.0;
                case "forest": return 2.5;
                case "swamp": return 1.0;
                case "plank": return 0.5;
                default: return 1.0;
            }
        }
        return 1.0;
    }

    private double getEuclideanDistance(int u, int v) {
        double dx = nodeCoords[u][0] - nodeCoords[v][0];
        double dy = nodeCoords[u][1] - nodeCoords[v][1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    static class AStarNode implements Comparable<AStarNode> {
        int id;
        double g;
        double f;

        public AStarNode(int id, double g, double f) {
            this.id = id;
            this.g = g;
            this.f = f;
        }

        @Override
        public int compareTo(AStarNode o) {
            return Double.compare(this.f, o.f);
        }
    }

    private List<Integer> getAStarPath(Officer officer, int target, TextArea resultArea) {
        double baseSpeed = officer.type.equalsIgnoreCase("cavalry") ? 2.0 : 1.0;
        double maxSpeed = baseSpeed * 3.0;

        double[] minTime = new double[11];
        Arrays.fill(minTime, Double.MAX_VALUE);
        int[] parent = new int[11];
        Arrays.fill(parent, -1);

        PriorityQueue<AStarNode> pq = new PriorityQueue<>();
        minTime[1] = 0.0;
        double hStart = getEuclideanDistance(1, target) / maxSpeed;
        pq.add(new AStarNode(1, 0.0, hStart));

        while (!pq.isEmpty()) {
            AStarNode curr = pq.poll();
            int u = curr.id;
            double g = curr.g;

            if (g > minTime[u]) continue;
            if (u == target) break;

            for (EnemyGraph.Edge edge : EnemyGraph.graph.get(u)) {
                int v = edge.to;
                double mult = getMultiplier(officer.type, edge.terrain);
                double speed = baseSpeed * mult;
                double travelTime = edge.distance / speed;

                if (minTime[u] + travelTime < minTime[v]) {
                    minTime[v] = minTime[u] + travelTime;
                    parent[v] = u;
                    double h = getEuclideanDistance(v, target) / maxSpeed;
                    pq.add(new AStarNode(v, minTime[v], minTime[v] + h));
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path);

        resultArea.setText("A* Search Shortest Time:\nOfficer: " + officer.name + "\nPath: " + path + "\nTime: " + String.format("%.2f", minTime[target]) + " hours");
        return path;
    }

    static class GBFSNode implements Comparable<GBFSNode> {
        int id;
        double h;

        public GBFSNode(int id, double h) {
            this.id = id;
            this.h = h;
        }

        @Override
        public int compareTo(GBFSNode o) {
            return Double.compare(this.h, o.h);
        }
    }

    private List<Integer> getBestFirstSearchPath(int target, TextArea resultArea) {
        PriorityQueue<GBFSNode> pq = new PriorityQueue<>();
        boolean[] visited = new boolean[11];
        int[] parent = new int[11];
        Arrays.fill(parent, -1);

        pq.add(new GBFSNode(1, getEuclideanDistance(1, target)));
        visited[1] = true;

        while (!pq.isEmpty()) {
            GBFSNode curr = pq.poll();
            int u = curr.id;

            if (u == target) break;

            for (int v : EnemyGraph.adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    pq.add(new GBFSNode(v, getEuclideanDistance(v, target)));
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path);

        resultArea.setText("Best First Search Path:\nPath: " + path + "\nSteps: " + (path.size() - 1));
        return path;
    }
}