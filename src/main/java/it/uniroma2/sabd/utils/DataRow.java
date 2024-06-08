package it.uniroma2.sabd.utils;

import java.io.Serializable;

public class DataRow implements Serializable {

    private final String timestamp;
    private final String serialNumber;
    private final String model;
    private final Long failure;
    private final Long vault_id;

    public DataRow(String timestamp, String serialNumber, String model, Long failure, Long vault_id) {
        this.timestamp = timestamp;
        this.serialNumber = serialNumber;
        this.model = model;
        this.failure = failure;
        this.vault_id = vault_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Long getFailure() {
        return failure;
    }

    public Long getVault_id() {
        return vault_id;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return getTimestamp() + ", " + getSerialNumber() + ", " + getFailure() + ", " + getVault_id();
    }

}


