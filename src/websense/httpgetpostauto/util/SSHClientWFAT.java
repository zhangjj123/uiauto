package websense.httpgetpostauto.util;import java.io.InputStream;import java.io.PrintStream;import com.jcraft.jsch.Channel;import com.jcraft.jsch.ChannelExec;import com.jcraft.jsch.JSch;import com.jcraft.jsch.Session;import com.jcraft.jsch.UIKeyboardInteractive;import com.jcraft.jsch.UserInfo;import websense.httpgetpostauto.util.Log4j;import org.apache.log4j.Logger;/** * Description: Class for SSH Client. * * @author Wang Jiren */public class SSHClientWFAT {    private String sshServerIP;    private int port;    private String username;    private String password;    private InputStream in;    private PrintStream out;    private String prompt;    private Logger sshClientLogger;    private Channel channel;    private Session session;    /**     * Description: Construct function to initialize object.     *     * @param ip       :SSH server IP address     * @param port     :SSH server port     * @param username :user name     * @param password :password     */    public SSHClientWFAT(String ip, int port, String username, String password) {        try {            //invoke log4j to record logging            sshClientLogger = Log4j.logger(SSHClientWFAT.class.getName());            this.sshServerIP = ip;            this.port = port;            this.username = username;            this.password = password;        } catch (Exception e) {            e.printStackTrace();        }    }    /**     * Description: connect and login to SSH server.     */    public void connectAndLogin() {        try {            JSch jsch = new JSch();            session = jsch.getSession(username, sshServerIP, port);            sshClientLogger.debug("Try to connect:" + sshServerIP + " ; port:" + port + " ; with username:" + username);            session.setPassword(password);            sshClientLogger.debug("Login with password: " + password);            UserInfo ui = new MyUserInfo() {                public void showMessage(String message) {                }                public boolean promptYesNo(String message) {                    return true;                }            };            session.setUserInfo(ui);            session.connect(30000);            sshClientLogger.debug("SSH session to " + sshServerIP + " is established:");            channel = session.openChannel("shell");            channel.connect(1000);            //channel.connect(3000);            sshClientLogger.debug("SSH channel to " + sshServerIP + " is established.");            sshClientLogger.info("Connect and login to SSH Server: " + sshServerIP);            in = channel.getInputStream();            out = new PrintStream(channel.getOutputStream());        } catch (Exception e) {            sshClientLogger.error("Fail to connect and login: " + sshServerIP);            e.printStackTrace();        }    }    /**     * Description: Login SSH server, then execute one command. Don't forget to invoke disconnect() method after execution.     *     * @param command command to run     * @return response from SSH server.     */    public String connectAndLoginAndExcuteOneCommand(String command) {        try {            JSch jsch = new JSch();            session = jsch.getSession(username, sshServerIP, port);            sshClientLogger.debug("Try to connect:" + sshServerIP + " ; port:" + port + " ; with username:" + username);            session.setPassword(password);            sshClientLogger.debug("Login with password " + password);            UserInfo ui = new MyUserInfo() {                public void showMessage(String message) {                }                public boolean promptYesNo(String message) {                    return true;                }            };            session.setUserInfo(ui);            session.connect(30000);            sshClientLogger.debug("SSH session to " + sshServerIP + " is established.");            channel = session.openChannel("exec");            ((ChannelExec) channel).setCommand(command);            channel.connect(1000);            sshClientLogger.debug("SSH channel to " + sshServerIP + " is established.");            sshClientLogger.debug("The command " + command + " will be sent to server.");            sshClientLogger.info("Login to " + sshServerIP + " and send command:" + command);        } catch (Exception e) {            sshClientLogger.error("Fail to login and send command to: " + sshServerIP);            e.printStackTrace();        }        try {            in = channel.getInputStream();            //return server's response (String).            StringBuffer sb = new StringBuffer();            //read response of ssh server.            int nextChar;            while (!(channel.isClosed())) {                while ((nextChar = in.read()) != -1) {                    sb.append((char) nextChar);                }            }            sshClientLogger.info("The command exit-status: " + channel.getExitStatus());            sshClientLogger.debug("The reponse of SSH server is ** " + sb.toString() + " **");            return sb.toString();        } catch (Exception e) {            sshClientLogger.error("Fail to read response from SSH server: " + sshServerIP);            e.printStackTrace();        }        return null;    }    /**     * Description: keep reading response of SSH server, until matching the pattern.     *     * @param pattern : Keyword to be matched from SSH server response. When defining paterns, please note:     *                <p/>     *                1. No need to include ending blank space, for example "password: ";     *                <p/>     *                2. Be careful to Non-English character, for example: "����������������" != "dministrator". Integer of char should be used to match.     * @return :response (string) from SSH server, until matching the pattern.     */    public String readUntil(String pattern) {        return this.readUntil(pattern, 5);    }    public String readUntil(String pattern, int timeout) {    	return this.readUntil2(pattern, timeout, 5120);    }    public String readUntil2(String pattern) {        return this.readUntil2(pattern, 5,5120);    }            public String readUntil2(String pattern, int timeout,int butterSize) {        try {            char lastChar = pattern.charAt(pattern.length() - 1);            StringBuffer sb = new StringBuffer();            char ch = (char) in.read();            int count = 0;            // The time to start loop.            long startLoopTime = System.currentTimeMillis();            //TODO: Timer and multi-threads will be used to calculate interval more precisely.            while (true) {                // Calculate the loop interval.                long endLoopTime = System.currentTimeMillis();                if (endLoopTime - startLoopTime > timeout * 1000) {                    sshClientLogger.error("Keep reading response from SSH server: " + sshServerIP + " for more than " + timeout + " seconds. Timeout.");                    sshClientLogger.error("current response is:"+sb.toString());                                       throw (new Exception("Fail to match the pattern * " + pattern + " * after waiting for  " + timeout + " seconds to get response from SSH server: " + sshServerIP));                }                // If returned String > 5120 bytes, throw exception. Otherwise keep returning String.                if (sb.length() < butterSize)               // if (sb.length() < 5120*3)                {                    if ((int) ch != -1) {                        sb.append(ch);                        //System.out.println("######" + sb + "##########");                    }                } else {                    sshClientLogger.error("Response String from SSH server: " + sshServerIP + " is more than "+butterSize+" bytes.");                    sshClientLogger.error("current response is:"+sb.toString());                    throw new Exception("Response String is more than "+butterSize+" bytes.");                }                // Match the pattern                if (ch == lastChar && sb.toString().endsWith(pattern)) {                    count++;                }                if (count == 2) {                    sshClientLogger.info("The prompt ** " + pattern + " ** is matched.");                    sshClientLogger.debug("The returned String from SSH server is ** " + sb.toString() + " **");                    return sb.toString();                }                ch = (char) in.read();            }        } catch (Exception e) {            sshClientLogger.info("Fail to match the pattern: " + pattern);            e.printStackTrace();        }        return null;    }    /**     * Description: send a string to SSH server. This function can be used with "readUntil" method, for example:     * <p/>     * <p/>     * <p/>     * readUntil("login:")     * <p/>     * write("jiren")     * <p/>     * readUntil("password:")     * <p/>     * write("123456")     *     * @param requeString : String to be sent     */    public void write(String requeString) {        try {            out.println(requeString);            sshClientLogger.info("The command: * " + requeString + " * is sent.");            out.flush();        } catch (Exception e) {            sshClientLogger.error("Fail to send command * " + requeString + " *");            e.printStackTrace();        }    }    /**     * Description: send commands to SSH server, then return the result (String) of last command. Don't use this method with readUntil.     *     * @param commands      :commands sent to SSH server. They will be executed one by one.     * @param commandPrompt :After login, the command prompt. For example: root#, user> and so on.     * @param timeout :timeout in second, if not set, default value is 5s.     * @return The execution result (String) of last command     * @throws Exception which command is failed to be executed     */    public String sendCommand(String[] commands, String commandPrompt, int timeout) throws Exception {        this.prompt = commandPrompt;        int i = 0;        // execute commands (except last command) one by one and don't return execution result.        for (; i < commands.length - 1; i++) {            try {                write(commands[i]);                readUntil(prompt, timeout);            } catch (Exception e) {                throw (new Exception("Fail to execute command: " + commands[i]));            }        }        // execute the last command and return the result (String).        try {            write(commands[i]);            return readUntil(prompt, timeout);        } catch (Exception e) {            throw (new Exception("Fail to execute command: " + commands[i]));        }    }    public String sendCommand(String[] commands, String commandPrompt) throws Exception {        return this.sendCommand(commands, commandPrompt, 5);    }    public String sendCommand2(String[] commands, String commandPrompt) throws Exception {        return this.sendCommand2(commands, commandPrompt, 5,5120);    }        public String sendCommand2(String[] commands, String commandPrompt,int timeout) throws Exception {        return this.sendCommand2(commands, commandPrompt, timeout,5120);    }        public String sendCommand2(String[] commands, String commandPrompt, int timeout,int butterSize) throws Exception {        this.prompt = commandPrompt;        int i = 0;        // execute commands (except last command) one by one and don't return execution result.        for (; i < commands.length - 1; i++) {            try {                write(commands[i]);                readUntil2(prompt, timeout,butterSize);            } catch (Exception e) {                throw (new Exception("Fail to execute command: " + commands[i]));            }        }        // execute the last command and return the result (String).        try {            write(commands[i]);            return readUntil2(prompt, timeout,butterSize);        } catch (Exception e) {            throw (new Exception("Fail to execute command: " + commands[i]));        }    }    /**     * Close connection to SSH server.     */    public void disconnect() {        try {            if (channel.isConnected()) {                channel.disconnect();                sshClientLogger.info("Channel to: " + sshServerIP + " is closed now.");            }            if (session.isConnected()) {                session.disconnect();                sshClientLogger.info("Session to: " + sshServerIP + " is closed now.");            }        } catch (Exception e) {            sshClientLogger.error("Fail to close channel and session to: " + sshServerIP);            e.printStackTrace();        }    }    /**     * Description: Required by JSCH to configure how to login SSH server. Here, we use password to login and require to hide banner.     */    public static abstract class MyUserInfo implements UserInfo,            UIKeyboardInteractive {        public String getPassword() {            return null;        }        public boolean promptYesNo(String str) {            return false;        }        public String getPassphrase() {            return null;        }        public boolean promptPassphrase(String message) {            return false;        }        public boolean promptPassword(String message) {            return false;        }        public void showMessage(String message) {        }        public String[] promptKeyboardInteractive(String destination,                                                  String name, String instruction, String[] prompt, boolean[] echo) {            return null;        }    }}