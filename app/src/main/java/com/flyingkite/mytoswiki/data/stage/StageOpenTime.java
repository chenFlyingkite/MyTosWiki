package com.flyingkite.mytoswiki.data.stage;

public class StageOpenTime {
    public Stage stage;
    public String open = "";

    public StageOpenTime() {}
    public StageOpenTime(String name, String icon, String openAt, String link) {
        Stage s = new Stage();
        s.link = link;
        s.name = name;
        s.icon = icon;
        stage = s;
        open = openAt;
    }
}
