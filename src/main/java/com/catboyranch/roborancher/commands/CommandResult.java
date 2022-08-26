package com.catboyranch.roborancher.commands;

public interface CommandResult {
    void success();
    void error(String message);
    void successQuiet();
}
