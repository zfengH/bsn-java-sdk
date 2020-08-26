package com.chaincodeEntities;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Ticket {
    @JSONField(name = "type")
    String type;
    @JSONField(name = "ticketUid")
    String uid;
    @JSONField (name = "ticketName")
    String name;

    @JSONField(name = "description")
    String description;

    @JSONField(name = "status")
    String status;
}
