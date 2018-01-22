/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 15:38
 */

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.bean.RequestBean;
import com.nemo.channel.client.Client;
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

        RequestBean requestBean = new RequestBean();
        requestBean.setMethod("login");
        Map<String,Object> params = new HashMap<>();
        params.put("name","Nemo");
        params.put("password","123456");
        requestBean.setParams(params);

        Thread.sleep(1000);

        //连接完成以后，开始推送消息
        client.writeStringMessage(JSONObject.toJSONString(requestBean));

        //推送完成，等下，看下啥回复
        Thread.sleep(1000);
    }

}
