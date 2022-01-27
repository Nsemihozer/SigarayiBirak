package com.cotyoragames.sigarayibirak;

public class StatusClass {
    String Tarih;
    int status;

    public StatusClass(String tarih, int status) {
        Tarih = tarih;
        this.status = status;
    }

    public String getTarih() {
        return Tarih;
    }

    public void setTarih(String tarih) {
        Tarih = tarih;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
