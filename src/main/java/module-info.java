module devs.fmm.rfc_01 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires java.prefs;

    opens devs.fmm.rfc_01 to javafx.fxml;
    exports devs.fmm.rfc_01;

    // Abrir subpaquetes a JavaFX
    opens devs.fmm.rfc_01.controller to javafx.fxml;

    // Exportar subpaquetes
    exports devs.fmm.rfc_01.controller;
    exports devs.fmm.rfc_01.model;
    exports devs.fmm.rfc_01.service;
    exports devs.fmm.rfc_01.service.impl;
}