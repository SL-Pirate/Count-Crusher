module lk.ac.iit.countcrusher {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;

    opens lk.ac.iit.countcrusher to javafx.fxml;
    exports lk.ac.iit.countcrusher;
}