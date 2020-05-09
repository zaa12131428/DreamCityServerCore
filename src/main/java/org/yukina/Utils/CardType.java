package org.yukina.Utils;

public enum CardType {
    Y("§c年费","Y"),
    Q("§b季费","Q"),
    M("§a月费","M"),
    D("§9体验卡","D");

    private String name;
    private String value;

    CardType(String name,String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue(){
        return value;
    }

}
