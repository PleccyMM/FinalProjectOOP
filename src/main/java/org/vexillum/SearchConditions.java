package org.vexillum;

/**
 * Object used to store all the search conditions relevant to a search for stock
 */
public class SearchConditions {
    private String search = null;
    private Integer type = null;
    private Integer region = null;
    //startLetters[0] - startLetters[1] is the range in the alphabet to search inclusive
    //e.g. startLetters[0] == "a" & startLetters[1] == "e" would search letters: a,b,c,d,e
    private String[] startLetters = new String[2];

    /**
     * Primary constructor
     */
    public SearchConditions() { }

    /**
     * Constructor used exclusively for testing
     */
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
