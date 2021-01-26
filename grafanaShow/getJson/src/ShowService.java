import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ShowService {
    private static JSONObject jsonObject;
    static HashMap<String, Float> delayRefMap = null;
    static HashMap<String, Float> hashMap = new HashMap<>();
    static HashMap<String, Float> temp = new HashMap<>();
    static Set<String> keySet;
    static Connection con;
    static String driver = "com.mysql.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3306/grafana";
    static String user = "root";
    static String password = "2334867ks";
    static String createtotaldata = "create table if not exists totaldata("
            + "mindelay double not null,"
            + "maxdelay double not null,"
            + "meandelay double not null,"
            + "maxloss double not null,"
            + "meanloss double not null,"
            + "date_time_date timestamp not null"
            + ")charset=utf8;";
    static String deletetotaldata = "TRUNCATE TABLE totaldata";

    static String createtraffic = "create table if not exists traffic("
            + "demand varchar(20) not null,"
            + "flow double not null,"
            + "srcIP varchar(20) not null,"
            + "dstIP varchar(20) not null,"
            + "date_time_date timestamp not null"
            + ")charset=utf8;";
    static String deletetraffic = "TRUNCATE TABLE traffic";

    static String createflowcharting = "create table if not exists flowcharting("
            + "demand varchar(20) not null,"
            + "delay double not null,"
            + "loss double not null,"
            + "date_time_date timestamp not null"
            + ")charset=utf8;";
    static String deleteflowcharting = "TRUNCATE TABLE flowcharting";

    static String createlinkrate = "create table if not exists linkrate("
            + "demand varchar(20) not null,"
            + "rate double not null,"
            + "date_time_date timestamp not null"
            + ")charset=utf8;";
    static String deletelinkrate = "TRUNCATE TABLE linkrate";

    public static void main(String[] args) {
      /*  if(args.length != 2) {
            System.out.println("the parameter number is not correct");
            return;
        }
        String arg1 = args[0];
        String arg2 = args[1];
        if(!arg1.equals("-config")) {
            System.out.println("The correct statment:java -jar xxx  -config xx.json");
            return;
        }*/
        String arg2 = "config2.json";
        InputStream resourceAsStream = ShowService.class.getClassLoader().getResourceAsStream(arg2);
        jsonObject = readJsonFile(resourceAsStream);
        if (null == jsonObject) {
            System.out.println("read config file error");
        }


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                delayRefMap = getDelayRef();
                System.out.println("更新基准值***********************");
            }
        }, 500, 200000);
        //每隔5s从redis中请求一次数据
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                System.out.println("更新时间:" + formatter.format(date));
                Timestamp sqlTime = new Timestamp(date.getTime());
                uploadTotalres(getShowConfig(), delayRefMap, sqlTime);
                uploadTopores(ReqAPI.getTraffic(), ReqAPI.geteachdelay(), ReqAPI.geteachloss(), null/*ReqAPI.getlinkrate()*/, sqlTime);
            }
        }, 2000, 5000);

    }

    /**
     * 处理返回的数据用于演示.
     *
     * @param res
     * @param refMap
     * @param sqlTime
     */
    private static void uploadTotalres(List<List<String>> res, HashMap<String, Float> refMap, Timestamp sqlTime) {
        float delaySum = 0;
        float min = 100;
        float max = 0;
        float delayCnt = 0;
        float maxLoss = 0;
        float lossSum = 0;
        float lossCnt = 0;
        for (String data : res.get(0)) {
            float[] st = ReqAPI.handleDelayData(data, refMap);
            min = Math.min(min, st[0]);
            max = Math.max(max, st[1]);
            delaySum += st[2];
            delayCnt += st[3];
        }
        for (String data : res.get(1)) {
            float[] lt = ReqAPI.handleLossData(data);
            lossSum += lt[1];
            lossCnt += lt[2];
            maxLoss = Math.max(lt[0], maxLoss);
        }
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            Statement stmt = con.createStatement();
            stmt.executeUpdate(createtotaldata);
            //stmt.executeUpdate(deletetotaldata);
            stmt.close();
            PreparedStatement statement;
            String sqltotaldata = "insert into totaldata (mindelay,maxdelay,meandelay,maxloss,meanloss,date_time_date) values(?,?,?,?,?,?)";
            statement = con.prepareStatement(sqltotaldata);
            statement.setDouble(1, min);
            statement.setDouble(2, max);
            statement.setDouble(3, (delaySum / delayCnt));
            statement.setDouble(4, maxLoss);
            statement.setDouble(5, (lossSum / lossCnt));
            statement.setTimestamp(6, sqlTime);
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            System.out.println("统计结果数据上传成功！！");
        }
/*        System.out.print("最小时延: " + min + "ms     ");
        System.out.print("最大时延: " + max+ "ms     ");
        System.out.print("平均时延: " + (delaySum / delayCnt) + "ms     ");
        System.out.print("最大丢包率：" + maxLoss+ "     ");
        System.out.print("平均丢包率：" + (lossSum / lossCnt) + "\n");*/
    }

    /**
     * 处理返回的数据用于演示.
     *
     * @param traffic
     * @param eachdelay
     * @param eachloss
     * @param sqlTime
     */
    private static void uploadTopores(String traffic, String eachdelay, String eachloss, String linkrate, Timestamp sqlTime) {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            Statement stmt = con.createStatement();
            stmt.executeUpdate(createtraffic);
            stmt.executeUpdate(createflowcharting);
            stmt.executeUpdate(createlinkrate);
            stmt.executeUpdate(deletetraffic);
            //stmt.executeUpdate(deleteeachdelay);
            //stmt.executeUpdate(deleteeachloss);
            stmt.close();

            PreparedStatement statement;
            String sqltraffic = "insert into traffic (demand,flow,srcIP,dstIP,date_time_date) values(?,?,?,?,?)";
            String sqlflowcharting = "insert into flowcharting (demand,delay,loss,date_time_date) values(?,?,?,?)";
            String sqllinkrate = "insert into linkrate (demand,rate,date_time_date) values(?,?,?)";

            statement = con.prepareStatement(sqltraffic);
            hashMap = ReqAPI.handleData(traffic);
            keySet = hashMap.keySet();
            for (String str : keySet) {
                String[] split = str.split("-");
                statement.setString(1, str);
                statement.setDouble(2, hashMap.get(str));
                statement.setString(3,split[0]);
                statement.setString(4,split[1]);
                statement.setTimestamp(5, sqlTime);
                statement.executeUpdate();
            }

            statement = con.prepareStatement(sqlflowcharting);
            hashMap = ReqAPI.handleData(eachdelay);
            temp = ReqAPI.handleData(eachloss);
            keySet = hashMap.keySet();
            for (String str : keySet) {
                statement.setString(1, str);
                statement.setDouble(2, hashMap.get(str));
                statement.setDouble(3, temp.get(str));
                statement.setTimestamp(4, sqlTime);
                statement.executeUpdate();
            }

/*            statement = con.prepareStatement(sqllinkrate);
            hashMap = ReqAPI.handleData(linkrate);
            keySet = hashMap.keySet();
            for (String str : keySet) {
                statement.setString(1, str);
                statement.setDouble(2, hashMap.get(str));
                statement.setTimestamp(3, sqlTime);
                statement.executeUpdate();
            }*/

        } catch (ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            System.out.println("拓扑数据上传成功！！");
        }
    }


    /**
     * 获取时延基准值
     *
     * @return
     */
    private static HashMap<String, Float> getDelayRef() {
        JSONArray refSrc = jsonObject.getJSONArray("refSrc");
        JSONArray refDst = jsonObject.getJSONArray("refDst");
        HashMap<String, Float> dataMap = new HashMap<>();
        for (int i = 0; i < refSrc.size(); i++) {
            int src = refSrc.getIntValue(i);
            for (int j = 0; j < refDst.size(); j++) {
                int dst = refDst.getIntValue(j);
                float[] data = ReqAPI.handleRefDelayData(ReqAPI.getNowDelay(src, dst));
                String regionIdSrc = ReqAPI.getRegionId(src);
                String regionIdDst = ReqAPI.getRegionId(dst);
                String key = regionIdSrc + "-" + regionIdDst;
                dataMap.put(key, data[2] / data[3]);
            }
        }
        System.out.println("基准值：" + dataMap.toString());
        return dataMap;
    }

    private static List<List<String>> getShowConfig() {
        JSONArray showSrc = jsonObject.getJSONArray("showSrc");
        JSONArray showDst = jsonObject.getJSONArray("showDst");
        if (showSrc.size() != showDst.size()) {
            System.out.println("show config data error");
        }
        int n = showSrc.size();
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            JSONArray srcJSONArray = showSrc.getJSONArray(i);
            JSONArray dstJSONArray = showDst.getJSONArray(i);
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < dstJSONArray.size(); j++) {
                list.add(dstJSONArray.getIntValue(j));
            }
            for (int k = 0; k < srcJSONArray.size(); k++) {
                map.put(srcJSONArray.getIntValue(k), list);
            }
        }
        List<List<String>> res = new ArrayList<>();
        List<String> resDelay = new ArrayList<>();
        List<String> resLoss = new ArrayList<>();
        //遍历构造的所有host对，通过getNowRes()与getNowLoss()获取数据
        for (int src : map.keySet()) {
            for (int dst : map.get(src)) {
                resDelay.add(ReqAPI.getNowDelay(src, dst));
                resLoss.add(ReqAPI.getNowLoss(src, dst));
            }
        }
        res.add(resDelay);
        res.add(resLoss);
        return res;
    }

    /**
     * 用于读取JSON文件
     *
     * @param
     * @return
     */
    public static JSONObject readJsonFile(InputStream is) {
        BufferedReader reader = null;
        StringBuilder readJson = new StringBuilder();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                readJson.append(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject jsonObject = null;
        // 获取json
        try {
            jsonObject = JSONObject.parseObject(readJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
