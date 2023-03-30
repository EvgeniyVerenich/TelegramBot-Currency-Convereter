package com.company.Enums;

public enum Commands {

    HELP("/help","-> List of commands")
    , CURRENCY("/set_currency" ,"-> Set Original and Target Currency");

    private final String name;
    private final String description;

    Commands(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
