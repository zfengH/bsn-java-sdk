package com.chaincodeEntities;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

@Data
public class Profile {
    //Type string `json:"type"`
    //	Id string `json:"id"`
    //	Name string `json:"name"`
    //	Sex string `json:"sex"`
    //	PoliticalStatus string `json:"politicalStatus"`


    public Profile(String type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    @JSONField(name = "type")
    String type;

    @JSONField(name = "id")
    String id;

    @JSONField(name = "name")
    String name;

    @JSONField(name = "sex")
    String sex;

    @JSONField(name = "politicalStatus")
    String politicalStatus;
}
