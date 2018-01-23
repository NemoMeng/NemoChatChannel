/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 15:38
 */

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.bean.RequestBean;
import com.nemo.channel.client.Client;
import com.nemo.channel.client.ClientThread;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nemo on 2018/1/19.
 */
public class ClientTest {

    @Test
    public void test() throws IOException, InterruptedException {
        Client client = Client.getClient();

        ClientThread thread = new ClientThread();
        thread.setClient(client);
        new Thread(thread).start();

        RequestBean requestBean = new RequestBean();
        requestBean.setMethod("msg");
        Map<String,Object> params = new HashMap<>();
        params.put("name","Nemo");
        params.put("password","123456");
        requestBean.setParams(params);


        //连接完成以后，开始推送消息
        client.writeStringMessage(JSONObject.toJSONString(requestBean));
        requestBean.setMethod("msg");
        client.writeStringMessage(JSONObject.toJSONString(requestBean));
        client.writeStringMessage(JSONObject.toJSONString(requestBean));

    }

}
