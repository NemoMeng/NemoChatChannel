/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/23 11:10
 */
package com.nemo.channel.client;

import com.alibaba.fastjson.JSONObject;
import com.nemo.channel.bean.AuthBean;
import com.nemo.channel.bean.MsgBean;
import com.nemo.channel.bean.RequestBean;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 客户端主界面
 * @author Nemo
 *
 */
public class ClientUI extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1010620833724339752L;
    private JButton btSend; //发送消息按钮
    private JTextField tfSend; //消息输入框
    private JTextField tfMethod;
    protected JTextArea taShow; //所有消息展示框
    private ClientThread clientThread; //监听服务器端返回
    private static String title;

    private static Client client;

    public static void main(String[] args) throws IOException, InterruptedException {
        client = Client.getClient();

        title = "Nemo"+ new Random().nextInt();

        ClientUI clientUI = new ClientUI();
        client.setUI(clientUI);

        //启动的时候先登录
        AuthBean auth = new AuthBean();
        auth.setName(title);
        auth.setPassword("123456");
        client.writeMsg("login",auth);
    }
    public ClientUI() {
        super(title);
        btSend = new JButton("发送信息");
        tfSend = new JTextField(15);
        tfMethod = new JTextField(10);
        taShow = new JTextArea();

        tfMethod.setText("chat/global");

        btSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    RequestBean requestBean = new RequestBean();

                    String method = tfMethod.getText();
                    if(method == null){
                        return;
                    }
                    if(method.equals("chat/global")){
                        MsgBean params = new MsgBean();
                        params.setMsg(tfSend.getText());
                        requestBean.setMethod(method);
                        requestBean.setParams(params);
                        client.writeStringMessage(JSONObject.toJSONString(requestBean));
                    }

                } catch (CharacterCodingException e1) {
                    e1.printStackTrace();
                }
                tfSend.setText("");
            }
        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int a = JOptionPane.showConfirmDialog(null, "确定关闭吗？", "温馨提示",JOptionPane.YES_NO_OPTION);
                if (a == 1) {
                    try {
                        client.shutdown();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0); // 关闭
                }
            }
        });
        JPanel top = new JPanel(new FlowLayout());
        top.add(tfMethod);
        top.add(tfSend);
        top.add(btSend);
        //top.add(btStart);
        this.add(top, BorderLayout.SOUTH);
        final JScrollPane sp = new JScrollPane();
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setViewportView(this.taShow);
        this.taShow.setEditable(false);
        this.add(sp, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 300);
        this.setLocation(0, 200);
        this.setVisible(true);
    }

    public synchronized void addShow(String msg){
        taShow.insert(msg+"\r\n",0);
    }

}
