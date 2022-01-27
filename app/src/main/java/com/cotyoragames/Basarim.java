package com.cotyoragames;

public class Basarim {
    private String type,ad,desc;
    private int progress;
    private boolean done;

    public Basarim(String type, String ad, String desc, int progress, boolean done) {
        this.type = type;
        this.ad = ad;
        this.desc = desc;
        this.progress = progress;
        this.done = done;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
