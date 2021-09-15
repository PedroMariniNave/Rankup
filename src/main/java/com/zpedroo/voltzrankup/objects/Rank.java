package com.zpedroo.voltzrankup.objects;

import com.zpedroo.voltzrankup.enums.Requirement;
import java.math.BigInteger;
import java.util.*;

public class Rank {

    private String name;
    private String tag;
    private Map<Requirement, BigInteger> requirements;
    private List<String> rankupCommands;
    private Integer id;

    public Rank(String name, String tag, Map<Requirement, BigInteger> requirements, List<String> rankupCommands, Integer id) {
        this.name = name;
        this.tag = tag;
        this.requirements = requirements;
        this.rankupCommands = rankupCommands;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Map<Requirement, BigInteger> getRequirements() {
        return requirements;
    }

    public List<String> getRankUPCommands() {
        return rankupCommands;
    }

    public Integer getId() {
        return id;
    }
}