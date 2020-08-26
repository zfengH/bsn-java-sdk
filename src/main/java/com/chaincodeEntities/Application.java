package com.chaincodeEntities;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Application {
    //UID string `json:"applicationUid"`
    //	PID string `json:"pid"`
    //	ApplyFor string `json:"applyFor"`
    //	Status string `json:"status"`

    @JSONField(name = "applicationUid")
    String applicationUid;

    //profile id
    @JSONField(name = "pid")
    String pid;

    @JSONField(name = "applyFor")
    String applyFor;
    @JSONField(name = "status")
    String status;
}
