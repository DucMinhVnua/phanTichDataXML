package com.example.adapter;

public class obj {
    String url;
    int i;
    float phanTram;
    String ten;
    String ngayThang;

    public obj(String url, int i, float phanTram, String ten, String ngayThang) {
        this.url = url;
        this.i = i;
        this.phanTram = phanTram;
        this.ten = ten;
        this.ngayThang = ngayThang;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public float getPhanTram() {
        return phanTram;
    }

    public void setPhanTram(float phanTram) {
        this.phanTram = phanTram;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNgayThang() {
        return ngayThang;
    }

    public void setNgayThang(String ngayThang) {
        this.ngayThang = ngayThang;
    }
}
