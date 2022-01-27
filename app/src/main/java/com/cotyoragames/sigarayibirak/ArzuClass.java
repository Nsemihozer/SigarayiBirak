package com.cotyoragames.sigarayibirak;

public class ArzuClass {
    Long tarih;
    int status;

    public Long getTarih() {
        return tarih;
    }

    public void setTarih(Long tarih) {
        this.tarih = tarih;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArzuClass(Long tarih, int status) {
        this.tarih = tarih;
        this.status = status;
    }
}
