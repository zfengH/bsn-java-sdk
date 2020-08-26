# 区块链服务网络（BSN）接入Java-SDK说明文档

## 1、配置对象设定
在发送http请求与bsn交互之前，必须先构造出一个Config对象，然后在config对象中填充下列字段，作为配置。

config对象的字段意义如下：

字段|类型|含义|必填字段
| :-----: | :------: |:----------------------------- |:---:|
AppCode|String|bsn中我们部署应用的AppCode，目前是“app0001202006081111440843077”|是
UserCode|String|参加bsn节点交互的userCode，每个userCode对应有自己的证书和私钥，目前我们的UserCode是“USER0001202006050930126612022”|是
Api|String|与bsn节点进行通信的地址，目前有三个节点，分别在衢州、北京、和广州南基<br>南基api地址为:https://nanjinode.bsngate.com:17602<br>北京api地址为：http://180.76.133.31:17502/<br>衢州api地址为：https://quzhounode.bsngate.com:17602/|是
Cert|String|bsn节点进行https通讯时所用的https证书，目前在resources文件夹下的certs/bsn_gateway_https.crt|https通讯则是
Prk|String|用户自己的私钥，用于对上传数据进行签名；bsn节点会对上传的数据用用户的公钥进行验签，目前存放在resources目录下对应三个节点的文件夹下的userAppCert/private_key.pem|是
Puk|String|bsn网关节点的公钥，当用户接收到bsn节点返回的消息时，可以用这个公钥对bsn节点返回的消息进行验签。目前存放在resources目录下对应三个节点的文件夹下的gatewayCert/gateway_public_cert_secp256r1.pem|是
MspDir|String|这里是设置本地上传公钥模式下的用户自己生成的公私钥对和密钥证书的存放地址|否。本地上传公钥模式则需要设置。
---
<b>示例代码：</b>

```java
//first init config
        //创建config对象
        Config config = new Config();
        //设置bsn上对饮的AppCode
        config.setAppCode("app0001202006081111440843077");
        //设置参加bsn节点交互的userCode，每个userCode对应有自己的证书和私钥
        config.setUserCode("USER0001202006050930126612022");
        //与bsn节点进行通信的地址，目前有三个节点，分别在衢州、北京、和广州南基
        config.setApi("https://quzhounode.bsngate.com:17602");
        //设置与bsn节点交互的https证书
        config.setCert("certs/bsn_gateway_https.crt");
        //用户自己的私钥，用于对上传数据进行签名；bsn节点会对上传的数据用用户的公钥进行验签
        config.setPrk("certs/quzhou/userAppCert/private_key.pem");
        //bsn网关节点的公钥，当用户接收到bsn节点返回的消息时，可以用这个公钥对bsn节点返回的消息进行验签。
        config.setPuk("certs/quzhou/gatewayCert/gateway_public_cert_secp256r1.pem");
        //这里是设置本地上传公钥模式下的用户自己生成的公私钥对和密钥证书的存放地址
        config.setMspDir("D:/test");

```

## 2、调用智能合约

调用智能合约主要分为两步：
1. 构造调用智能合约的http请求。在bsn-java-sdk中，是构造ReqKeyEscrow类的对象，然后在对象中填充必填字段。
2. 将构造好的对象，传入TransactionService类的reqChaincode方法中作为参数，进行正式调用。



构造的请求对象ReqKeyEscrow的字段参数含义如下：
```java
    String userName;
    String nonce;
    String chainCode;
    String funcName;
    String[]  args;
    Map<String,String> transientData;
```
字段|类型|含义|必填字段
| :---: | :---: | :----------------- | :--------:|
userName|String|bsn网络用户注册时的用户名|否
nonce|String|随机字符串，使用base64编码的24位随机byte数据，在reqChaincode方法内部已经嵌入了设置nonce的方法，所以可以不需要再在调用的时候设置nonce。|是
chainCode|String|bsn网络上调用智能合约的名称标识|是
funcName|String|要调用的智能合约的方法名称。一个智能合约可能有很多方法可供调用，所以要传入要调用的方法名称。具体每个智能合约方法名称在文档第三点中说明。|是
args|String[]|调用智能合约的时候，可能需要往智能合约中传递参数。args字符串数组是传递给智能合约对应方法参数的json形式。当调用智能合约的方法不需要传入参数时，可以为空。<br>具体每个合约的每个方法的传入参数请看第三点。|否
transientData|Map<String,String>|保存到bsn节点上的一个暂时性的键值对数据|否

---
<b>示例代码如下：</b>

```java
//调用智能合约的 http请求对象
        ReqKeyEscrow reqkey = new ReqKeyEscrow();
        //需要传入到智能合约的参数，json字符串
        //这里为了方便设置调用参数，根据现有的智能合约的参数， 创建了几个实体对象，通过fastjson让对象转变为json字符串然后再作为ReqKeyEscrow的args字段参数
        Ticket ticket = new Ticket();
        ticket.setType("test");
        ticket.setUid("0001");
        ticket.setName("testticket");
        ticket.setDescription("only for test");
        ticket.setStatus("ok");
        String[] args = {JSON.toJSONString(ticket)};
        //实际传递的args的json形式如下
        //String[] args = {"{\"baseKey\":\"test2020068\",\"baseValue\":\"this is string \"}"};
        //设置请求中的智能合约参数字段
        reqkey.setArgs(params);
        //设置请求中的智能合约方法名称字段
        reqkey.setFuncName("createTicket");
        //设置调用智能合约的名称标识
        reqkey.setChainCode("cc_app0001202006081111440843077_00");
        //可以设置调用者名称，也可以不设置
//        reqkey.setUserName("test21");
        //调用的暂态数据，暂时用不上
        //reqkey.setTransientData(null);
        try {
            //正式调用
            TransactionService.reqChainCode(reqkey);
        } catch(GlobalException  | IOException e) {
            e.printStackTrace();
        }


```
# 3、调用返回字段说明：

序号|字段名称|字段|类型|是否必填|备注
| :---: | :---: | :---: | :--------:| :---: | :---: |
1|信息头|header|ResHeader|是|
2|信息体|body|ResKeyEscrow|是|
3|签名值|mac|String|是
---
header字段
序号|字段名称|字段|类型|是否必填|备注
| :---: | :---: | :---: | :--------:| :---: | :---: | 
1|响应标识|code|int|是|0：校验成功；-1：校验失败
2|响应信息|msg|String|否|code==0时可为null
---
body字段：
序号|字段名称|字段|类型|是否必填|备注
| :---: | :---: | :---: | :--------:| :---: | :---: | 
1|块信息|blockInfo|BlockInfo|否|code不为0时为空|
2|智能合约响应结果|ccRes|CcRes|否|code不为0时为空|

blockInfo字段：
序号|字段名称|字段|类型|是否必填|备注
| :---: | :---: | :---: | :--------:| :---: | :---: | 
1|交易Id|txId|String|是|
2|块哈希|blockHash|String|否|同步接口返回块哈希|
3|状态值|status|Int|是|详见交易状态描述

ccRes字段：
序号|字段名称|字段|类型|是否必填|备注
| :---: | :---: | :---: | :--------:| :---: | :---: | 
1|智能合约响应状态|CCCode|Int|是|200：成功；500：失败|
2|智能合约响应结果|ccData|String|否|具体智能合约响应的结果|

返回示例：
```json
{
	"header": {
		"code": 0,
		"msg": "success"
	},
	"mac": "MEQCIAcXHapS44cG2ObeEOGKYXJI/kEpGZhEYFjacWudsuh7AiAwr2QBQFQhDWQylNeuIAKU9x+uCh3SVqvrNP+gLd61EQ==",
	"body": {
		"blockInfo": {
			"txId": "c75091475e9cdab5af1f8325d19069c9aab865862d9434a5f772f1159d569048",
			"blockHash": "",
			"status": 0
		},
		"ccRes": {
			"ccCode": 200,
			"ccData": "test2"
		}
	}
}
```

# 4、智能合约方法及参数说明

目前智能合约提供三个业务逻辑的处理，包括：
1. 对人才档案信息的上链保存;
2. 对人才申请政策记录的上链保存；
3. 对政策发放记录的上链保存。

为方便参数的传递，在bsn-java-sdk中的chaincodeEntities包中，封装了要传递的参数的java类，当需要将参数传递给智能合约时，只需要创建一个对象实例，然后通过JSON.toJSONString，将对象序列化为json字符串，然后传入第二点中提到的args参数即可。

## 4-1 人才数据智能合约方法名称及参数说明

方法名称|功能|参数类型|参数含义|返回值类型|返回值|备注
| :---: | :---: | :---: | :---: | :---: | :---| :---: |
createProfile|保存人才数据到区块链上|String[]|Profile类的json序列化字符串|String|成功返回OK，否则返回Error的信息
getProfile|查询人才数据|String[]|Profile类的id，json序列化字符串|String|成功则返回Profile的各个字段；否则返回Error的信息
updateProfile|更新人才数据|String[]|Profile类的json序列化字符串|String|成功则返回OK；否则返回Error的信息

---

bsn-java-sdk中的人才数据实体类：

```java
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
```

字段|类型|含义|必填
|:---:|:---:| :--- |:---|
type|String|人才类型|是
id|String|人才档案的唯一标识|是
name|String|人才的姓名资料|是
sex|String|性别|否
politicalStatus|String|政治面貌|否

---
## 4-2 人才申请政策智能合约参数字段说明：

方法名称|功能|参数类型|参数含义|返回值类型|返回值|备注
| :---: | :---: | :---: | :---: | :---: | :---| :---: |
apply|人才发起政策申请|String[]|Application类的json序列化字符串|String|如果成功返回OK，否则返回Error的具体信息|
getApplicationInfo|查询某次申请的详细信息|String[]|Application类的id,json序列化字符串|String|返回Application类的各个字段
getHistoryForApplication|追溯某次申请的状态更改历史|String[]|Application类的json序列化字符串|String|如果成功返回历史状态结构，否则返回Error的具体信息|

---
bsn-java-sdk中的人才申请实体类：
```java
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
```


字段|类型|含义|必填
|:---:|:---:| :--- |:---|
applicationUid|String|申请事务的唯一ID|是|
pid|String|人才数据的唯一ID|是|
applyFor|String|申请政策的名称|是|
Status|String|申请政策的状态|否

---
## 4-3 政府部门审批发放政策智能合约参数字段说明：

目前字段包括applicationUid、pid、name、status几项。若有需要可以再往智能合约中添加字段，并在Java-sdk中对应的实体类添加字段。

方法名称|功能|参数类型|参数含义|返回值类型|返回值|备注
| :---: | :---: | :---: | :---: | :---: | :---| :---: |
createTicket|创建政策数据到区块链上|String[]|Ticket类的json序列化字符串|String|成功返回OK，否则返回Error的信息
getTicketInfo|查询政策数据状态|String[]|Ticket类的id，json序列化字符串|String|成功则返回Ticket的各个字段；否则返回Error的信息
changeTicket|更新政策数据状态|String[]|Ticket类的json序列化字符串|String|成功则返回OK；否则返回Error的信息
invokeTicket|核销删除政策信息|String[]|Ticket类的json序列化字符串|String|成功则返回OK；否则返回Error的信息
getHistoryForTicketStatus|追溯政策发放过程的状态改变|String[]|Ticket类的id，json序列化字符串|String|成功则返回历史状态结构；否则返回Error的信息

---
bsn-java-sdk中的政策的Ticket实体类：
```java
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
```

字段|类型|含义|必填
|:---:|:---:| :--- |:---|
type|String|一次申请事务的唯一标识id
uid|String|人才对应的唯一标识id
name|String|申请政策的名称
description|String|对政策内容的详情描述|
status|String|政策的状态，比如审批中、通过、不通过等。
---
## 4-4 历史状态结构返回信息参数说明：

当查询某一ID对象的历史状态，对该对象进行历史追溯时，在返回信息的ccData字段中，会返回该对象的历史状态结构。该结构各参数如下：
字段|类型|含义|
|:---:|:---| :--- |:---|
txId|String|区块链上该事务的ID|
dataInfo|String|在这次事务发生后，该ID对应对象的各个字段的数据值|
txTime|String|事务发生的时间|
isDelete|String|该事务是否被删除过|
---
示例：
```json
{
"header":{
        "code":0,
        "msg":"success"
    },
"mac":"MEQCIF4iYNJRRdRR+NxOnfXKVV2UcKx8k3u/iIvX/daoAoAXAiBHdaNZOqKR2UTPOGhHpJTMs7FH3MXfAzAu0cwieVMftQ==",
"body":{
    "blockInfo":{
    "txId":"6e275137a31cb9f6f34c7ca94ff21d7177c2f3c87dc8c04e5dfde771ae90e57f",
    "blockHash":"",
    "status":0
    },
    "ccRes":{
        "ccCode":200,
        "ccData":"[{\"txId\":\"45c4d1a4ed369c392efaf5f28ea6c43e21e7b9c587587eb4aa7076979cac4064\",\"dataInfo\":\"{\\\"type\\\":\\\"test\\\",\\\"ticketUid\\\":\\\"0001\\\",\\\"ticketName\\\":\\\"testticket\\\",\\\"description\\\":\\\"only for test\\\",\\\"status\\\":\\\"ok\\\"}\",\"txTime\":\"2020-08-24 09:03:06\",\"isDelete\":false},{\"txId\":\"6fac4400101f6476bb35924d383ed9680384063ccbac7a5b72555fa2c38e9880\",\"dataInfo\":\"{\\\"type\\\":\\\"test\\\",\\\"ticketUid\\\":\\\"0001\\\",\\\"ticketName\\\":\\\"testticket\\\",\\\"description\\\":\\\"only for test\\\",\\\"status\\\":\\\"ok\\\"}\",\"txTime\":\"2020-08-24 09:18:10\",\"isDelete\":false},{\"txId\":\"ac5a1f6a3c1d0ca9d227dc05a2b98a28d2f17c1efa538c678f7771fd8d48c904\",\"dataInfo\":\"{\\\"type\\\":\\\"test\\\",\\\"ticketUid\\\":\\\"0001\\\",\\\"ticketName\\\":\\\"testticket\\\",\\\"description\\\":\\\"only for test\\\",\\\"status\\\":\\\"ok\\\"}\",\"txTime\":\"2020-08-24 09:31:51\",\"isDelete\":false}]"
        }
    }
}
```
