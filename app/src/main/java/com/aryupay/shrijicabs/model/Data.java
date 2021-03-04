package com.aryupay.shrijicabs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("clientlocation")
    @Expose
    private Clientlocation clientlocation;
    @SerializedName("vendorlocation")
    @Expose
    private Vendorlocation vendorlocation;

    public Clientlocation getClientlocation() {
        return clientlocation;
    }

    public void setClientlocation(Clientlocation clientlocation) {
        this.clientlocation = clientlocation;
    }

    public Vendorlocation getVendorlocation() {
        return vendorlocation;
    }

    public void setVendorlocation(Vendorlocation vendorlocation) {
        this.vendorlocation = vendorlocation;
    }

}
