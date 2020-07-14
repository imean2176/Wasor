package com.wasor.modal;

public class Rac {
    private String tenrac;
    private String loairac;
    private String motaloairac;
    private String cachxuly;

    public Rac() {
    }

    public Rac(String tenrac, String loairac, String motaloairac, String cachxuly) {
        this.tenrac = tenrac;
        this.loairac = loairac;
        this.motaloairac = motaloairac;
        this.cachxuly = cachxuly;
    }
// Getter Methods

    public String getTenrac() {
        return tenrac;
    }

    public String getLoairac() {
        return loairac;
    }

    public String getMotaloairac() {
        return motaloairac;
    }

    public String getCachxuly() {
        return cachxuly;
    }

    // Setter Methods

    public void setTenrac(String tenrac) {
        this.tenrac = tenrac;
    }

    public void setLoairac(String loairac) {
        this.loairac = loairac;
    }

    public void setMotaloairac(String motaloairac) {
        this.motaloairac = motaloairac;
    }

    public void setCachxuly(String cachxuly) {
        this.cachxuly = cachxuly;
    }
}
