package org.vexillum;

public class SearchConditions {
    private String search = null;
    private Integer type = null;
    private Integer region = null;
    private String[] startLetters = new String[2];

    public SearchConditions() { }

    public SearchConditions(String search, Integer type, Integer region, String[] startLetters) {
        this.search = search;
        this.type = type;
        this.region = region;
        this.startLetters = startLetters;
    }

    public String getSearch() {
        return search;
    }
    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getRegion() {
        return region;
    }
    public void setRegion(Integer region) {
        this.region = region;
    }

    public String[] getStartLetters() {
        return startLetters;
    }
    public void setStartLetters(String[] startLetters) {
        this.startLetters = startLetters;
    }
}
