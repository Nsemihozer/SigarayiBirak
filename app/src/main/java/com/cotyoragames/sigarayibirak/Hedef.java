package com.cotyoragames.sigarayibirak;

public class Hedef {
    String Name;
    int Miktar;

    public Hedef(String name, int miktar) {
        Name = name;
        Miktar = miktar;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getMiktar() {
        return Miktar;
    }

    public void setMiktar(int miktar) {
        Miktar = miktar;
    }
}
