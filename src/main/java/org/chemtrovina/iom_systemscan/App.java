package org.chemtrovina.iom_systemscan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.chemtrovina.iom_systemscan.config.DataSourceConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

public class App extends Application {

    public static AnnotationConfigApplicationContext springContext;
    public static void main(String[] args) {
        springContext = new AnnotationConfigApplicationContext(DataSourceConfig.class);

        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/invoiceData-feature.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 832);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }



}