package org.chemtrovina.iom_systemscan.model;

import javafx.scene.Parent;

public class FxmlPage {
    private Parent view;
    private Object controller;

    public FxmlPage(Parent view, Object controller) {
        this.view = view;
        this.controller = controller;
    }

    public Parent getView() {
        return view;
    }

    public Object getController() {
        return controller;
    }
}
