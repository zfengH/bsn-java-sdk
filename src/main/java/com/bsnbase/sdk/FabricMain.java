package com.bsnbase.sdk;

import com.bsnbase.sdk.client.fabric.service.NodeService;
import com.bsnbase.sdk.client.fabric.service.TransactionService;
import com.bsnbase.sdk.client.fabric.service.UserService;
import com.bsnbase.sdk.entity.config.Config;
import com.bsnbase.sdk.entity.req.fabric.ReqGetTransaction;
import com.bsnbase.sdk.entity.req.fabric.ReqKeyEscrow;
import com.bsnbase.sdk.entity.req.fabric.ReqKeyEscrowEnroll;
import com.bsnbase.sdk.entity.req.fabric.ReqUserRegister;
import com.bsnbase.sdk.util.exception.GlobalException;

import java.io.IOException;

public class FabricMain {
    public static void main(String[] args) {
        //first init config
        //创建config对象
        Config config = new Config();
        //设置bsn上对饮的AppCode
        config.setAppCode("app0001202006081111440843077");
        //设置参加bsn节点交互的userCode，每个userCode对应有自己的证书和私钥
        config.setUserCode("USER0001202006050930126612022");
        //与bsn节点进行通信的地址，目前有三个节点，分别在衢州、北京、和广州南基
        config.setApi("https://nanjinode.bsngate.com:17602");
        //设置与bsn节点交互的https证书
        config.setCert("certs/bsn_gateway_https.crt");
        //用户自己的私钥，用于对上传数据进行签名；bsn节点会对上传的数据用用户的公钥进行验签
        config.setPrk("certs/nanji/userAppCert/private_key.pem");
        //bsn网关节点的公钥，当用户接收到bsn节点返回的消息时，可以用这个公钥对bsn节点返回的消息进行验签。
        config.setPuk("certs/nanji/gatewayCert/gateway_public_cert_secp256r1.pem");
        //这里是设置本地上传公钥模式下的用户自己生成的公私钥对和密钥证书的存放地址
        config.setMspDir("D:/test");

        //正式对config进行初始化
        config.initConfig(config);


        //调用智能合约的 http请求对象
        ReqKeyEscrow reqkey = new ReqKeyEscrow();
        //需要传入到智能合约的参数
        String[] params = {"type:"};
        //设置请求中的智能合约参数字段
        reqkey.setArgs(params);
        //设置请求中的智能合约方法名称字段
        reqkey.setFuncName("createTicket");
        //设置调用智能合约的名称标识
        reqkey.setChainCode("cc_app0001202006081111440843077_00");
        //可以设置调用者名称，也可以不设置
//        reqkey.setUserName("test21");
        //调用的暂态数据，暂时用不上
        reqkey.setTransientData(null);
        try {
            //正式调用
            TransactionService.reqChainCode(reqkey);
        } catch(GlobalException  | IOException e) {
            e.printStackTrace();
        }


        //
    }

}
