package ru.mora.character_v1;

import java.io.Serializable;

public class Character implements Serializable {
    String img_uri;
    String name;
    int age;
    String sex;
    String race;
    int str;
    int dex;
    int con;
    int intl;
    int wis;
    int charm;

    public Character(String img_uri, String name, int age, String sex, String race, int str,
                     int dex, int con, int intl, int wis, int charm) {
        this.img_uri = img_uri;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.race = race;
        this.str = str;
        this.dex = dex;
        this.con = con;
        this.intl = intl;
        this.wis = wis;
        this.charm = charm;
    }


}
