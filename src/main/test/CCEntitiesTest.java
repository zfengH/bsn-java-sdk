import com.alibaba.fastjson.JSON;
import com.bsnbase.sdk.client.fabric.service.TransactionService;
import com.bsnbase.sdk.entity.config.Config;
import com.bsnbase.sdk.entity.req.fabric.ReqKeyEscrow;
import com.bsnbase.sdk.util.Log;
import com.bsnbase.sdk.util.exception.GlobalException;
import com.chaincodeEntities.Ticket;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CCEntitiesTest {


    public void initConfig() throws IOException {
        Config config = new Config();
        config.setAppCode("app0001202006081111440843077");
        config.setUserCode("USER0001202006050930126612022");
        config.setApi("https://nanjinode.bsngate.com:17602");
        config.setCert("certs/bsn_gateway_https.crt");
        config.setPrk("certs/nanji/userAppCert/private_key.pem");
        config.setPuk("certs/nanji/gatewayCert/gateway_public_cert_secp256r1.pem");
        config.setMspDir("D:/test");
        config.initConfig(config);
    }



    @Test
    public void testEntitiesToJson(){
        Ticket ticket = new Ticket();
        ticket.setType("test");
        ticket.setUid("0001");
        ticket.setName("testticket");
        ticket.setDescription("only for test");
        ticket.setStatus("ok");
        String ret = JSON.toJSONString(ticket);
        Log.d("testJson:"+ ret);
    }

    @Test
    public void reqCreateTicketChainCode() {
        try {
            initConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
        ReqKeyEscrow reqkey = new ReqKeyEscrow();
//        String[] args = {"test"};
        Ticket ticket = new Ticket();
        ticket.setType("test");
        ticket.setUid("0001");
        ticket.setName("testticket");
        ticket.setDescription("only for test");
        ticket.setStatus("ok");
        String[] args = {JSON.toJSONString(ticket)};
        reqkey.setArgs(args);
        reqkey.setFuncName("createTicket");
        reqkey.setChainCode("cc_app0001202006081111440843077_00");
//        reqkey.setUserName("test21");
        reqkey.setTransientData(null);
        try {
            TransactionService.reqChainCode(reqkey);
        } catch(GlobalException g) {
            g.printStackTrace();
            return ;
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
    }

    @Test
    public void reqGetTicketChainCode() {
        try {
            initConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
        ReqKeyEscrow reqkey = new ReqKeyEscrow();
//        String[] args = {"test"};
        Ticket ticket = new Ticket();
        ticket.setUid("0001");
        String[] args = {JSON.toJSONString(ticket)};
        reqkey.setArgs(args);
        reqkey.setFuncName("getTicketInfo");
        reqkey.setChainCode("cc_app0001202006081111440843077_00");
//        reqkey.setUserName("test21");
        reqkey.setTransientData(null);
        try {
            TransactionService.reqChainCode(reqkey);
        } catch(GlobalException g) {
            g.printStackTrace();
            return ;
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
    }

    @Test
    public void reqProfileChainCode() {
        try {
            initConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
        ReqKeyEscrow reqkey = new ReqKeyEscrow();
//        String[] args = {"test"};
        Ticket ticket = new Ticket();
        ticket.setType("test");
        ticket.setUid("0001");
        ticket.setName("testticket");
        ticket.setDescription("only for test");
        ticket.setStatus("ok");
        String[] args = {JSON.toJSONString(ticket)};
        reqkey.setArgs(args);
        reqkey.setFuncName("createTicket");
        reqkey.setChainCode("cc_app0001202006081111440843077_00");
//        reqkey.setUserName("test21");
        reqkey.setTransientData(null);
        try {
            TransactionService.reqChainCode(reqkey);
        } catch(GlobalException g) {
            g.printStackTrace();
            return ;
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
    }

    @Test
    public void reqApplicationChainCode() {
        try {
            initConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
        ReqKeyEscrow reqkey = new ReqKeyEscrow();
//        String[] args = {"test"};
        Ticket ticket = new Ticket();
        ticket.setType("test");
        ticket.setUid("0001");
        ticket.setName("testticket");
        ticket.setDescription("only for test");
        ticket.setStatus("ok");
        String[] args = {JSON.toJSONString(ticket)};
        reqkey.setArgs(args);
        reqkey.setFuncName("createTicket");
        reqkey.setChainCode("cc_app0001202006081111440843077_00");
//        reqkey.setUserName("test21");
        reqkey.setTransientData(null);
        try {
            TransactionService.reqChainCode(reqkey);
        } catch(GlobalException g) {
            g.printStackTrace();
            return ;
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
    }

}
