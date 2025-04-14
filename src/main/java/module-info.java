module org.chemtrovina.iom_systemscan {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires spring.jdbc;
    requires java.sql;
    requires spring.context;
    requires spring.beans;
    requires java.desktop;

    opens org.chemtrovina.iom_systemscan to javafx.fxml;
    exports org.chemtrovina.iom_systemscan;
    exports org.chemtrovina.iom_systemscan.controller;
    opens org.chemtrovina.iom_systemscan.controller to javafx.fxml;
    opens org.chemtrovina.iom_systemscan.model to javafx.base;
    exports org.chemtrovina.iom_systemscan.config;
    exports org.chemtrovina.iom_systemscan.repository.base;
    exports org.chemtrovina.iom_systemscan.repository.impl;
    exports org.chemtrovina.iom_systemscan.service;
}