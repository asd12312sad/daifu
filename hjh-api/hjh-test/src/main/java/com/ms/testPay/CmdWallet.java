package com.ms.testPay;

import org.springframework.stereotype.Service;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * 创建SSH连接
 */
public class CmdWallet {
    public static void main(String[] args) {
        CmdWallet cmdWallet = new CmdWallet();
    }

    private static Connection connection;

    private static Session session;
    private static String DEFAULTCHARTSET = "UTF-8";

    //创建SSH连接
    public CmdWallet() {
        //创建SSH连接到服务器
        boolean isAuthenticated = false;
        Connection conn = null;
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            conn = new Connection("47.242.74.15");
            conn.connect(); // 连接主机

            isAuthenticated = conn.authenticateWithPassword("root", "CFm200!@#"); // 认证
            if (isAuthenticated) {
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        connection = conn;
        try {
            session = connection.openSession();// 打开一个会话
            session.startShell();// 执行命令

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            executeSuccess("pwd");
            Thread.sleep(100L);
            executeSuccess("cd /opt/wallet-cli77/build/libs");
            Thread.sleep(100L);
            executeSuccess("pwd");
            Thread.sleep(1000L);
            executeSuccess("java -jar wallet-cli.jar");
            Thread.sleep(100L);
            executeSuccess("Login");
            Thread.sleep(100L);
            executeSuccess("Xiaobing1998");
            Thread.sleep(100L);
            PrintWriter out = new PrintWriter(session.getStdin());

            out.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        session.close();
        InputStream stderr = new StreamGobbler(session.getStderr());

        InputStream in = new StreamGobbler(session.getStdout());

        try {
            String result=   processStdErr(stderr,DEFAULTCHARTSET);
            System.out.println(result);

             result=   processStdErr(in,DEFAULTCHARTSET);
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String processStdErr(InputStream in, String charset)
            throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
        StringBuffer sb = new StringBuffer();

        if (in.available() != 0) {
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line).append(System.getProperty("line.separator"));
            }
        }

        return sb.toString();
    }

    //关闭连接
    public void closeConnection() {
        session.close();
        connection.close();
    }


    /**
     * @param cmd shell脚本或者命令
     * @return String 命令执行成功后返回的结果值，如果命令执行失败，返回空字符串，不是null
     * @throws
     * @Title: executeSuccess
     * @Description: 远程执行shell脚本或者命令
     */

    public static String executeSuccess(String cmd) throws IOException {
        String result = "";
        OutputStream outputStream = session.getStdin();
            outputStream.write(cmd.getBytes());
        outputStream.flush();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(session.getStderr(), "utf-8"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        return result;
    }

    /**
     * @param in      输入流对象
     * @param charset 编码
     * @return String 以纯文本的格式返回
     * @throws
     * @Title: processStdout
     * @Description: 解析脚本执行的返回结果
     */

    public static String processStdout(InputStream in, String charset) {

        InputStream stdout = new StreamGobbler(in);

        StringBuffer buffer = new StringBuffer();

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));

            String line = null;

            while ((line = br.readLine()) != null) {

                buffer.append(line + "\n");

            }

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return buffer.toString();

    }


}
