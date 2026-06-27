module com.weeee {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.weeee to javafx.fxml;
    exports com.weeee;
}
