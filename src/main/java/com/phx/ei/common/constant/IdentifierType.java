package com.phx.ei.common.constant;

public enum IdentifierType {
    EMAIL,
    PHONE;
    public String getIdentifierType() {
            return "ID_TYPE_" + this.name();
        }
}