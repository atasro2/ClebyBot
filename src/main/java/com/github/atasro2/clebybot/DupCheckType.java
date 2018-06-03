package com.github.atasro2.clebybot;

public enum DupCheckType {
    NORMAL,
    PERMISSIVE,
    DISABLED;

    @Override
    public String toString(){
        return name().toLowerCase();
    }
}
