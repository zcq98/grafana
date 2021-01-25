import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class ReqAPI {
    static String REQ_IP = "localhost";

    /**
     * 获取当前时间前5s到当前时间中指定src和dst的时延数据
     * @param src
     * @param dst
     * @return String
     */
    public static String getNowDelay(int src, int dst){
        Date date = new Date();
        long time = date.getTime();
        String strTime = String.valueOf(time);
        String substring = strTime.substring(0, strTime.length() - 3);
        return getDelay(Long.parseLong(substring), 10, src, dst);
    }


    /**
     * 获取当前时间前5s到当前时间中指定src和dst的丢包率数据
     * @param src
     * @param dst
     * @return
     */
    public static String getNowLoss(int src, int dst) {
        Date date = new Date();
        long time = date.getTime();
        String strTime = String.valueOf(time);
        String substring = strTime.substring(0, strTime.length() - 3);
        return getLoss(Long.parseLong(substring), 10, src, dst);
    }


    /**
     * 获取从time - interval 开始到 time的指定src到dst的时延数据.
     * dst为-1则获取src到网络中所有dst的数据
     * @param time
     * @param interval
     * @param src
     * @param dst
     * @return
     */
    public static String getDelay(long time, long interval, int src, int dst) {
        try
        {
            String strUrl = "http://" + REQ_IP + ":10086/delay?from=" + String.valueOf(time - interval) + "&to=" +
                    String.valueOf(time) + "&src=" + src;
            if(dst != -1) {
                strUrl += ("&dst=" + dst);
            }
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if(urlConnection instanceof HttpURLConnection)
            {
                connection = (HttpURLConnection) urlConnection;
            }
            else
            {
                System.out.println("请输入 URL 地址");
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String urlString = "";
            String current;
            while((current = in.readLine()) != null)
            {
                urlString += current;
            }
//            System.out.println(urlString);
            return urlString;
        }catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取从time - interval 开始到 time的指定src到dst的丢包率数据.
     * dst为-1则获取src到网络中所有dst的数据
     * @param time
     * @param interval
     * @param src
     * @param dst
     * @return
     */
    public static String getLoss(long time, long interval, int src, int dst) {
        try
        {
            String strUrl = "http://" + REQ_IP + ":10086/loss?from=" + String.valueOf(time - interval) + "&to=" +
                    String.valueOf(time) + "&src=" + src;
            if(dst != -1) {
                strUrl += ("&dst=" + dst);
            }
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if(urlConnection instanceof HttpURLConnection)
            {
                connection = (HttpURLConnection) urlConnection;
            }
            else
            {
                System.out.println("请输入 URL 地址");
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String urlString = "";
            String current;
            while((current = in.readLine()) != null)
            {
                urlString += current;
            }
//            System.out.println(urlString);
            return urlString;
        }catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取拓扑所有节点对的流量
     *
     * @return
     */
    public static String getTraffic() {
        try {
            String strUrl = "http://" + REQ_IP + ":10086/traffic";
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            } else {
                System.out.println("请输入 URL 地址");
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String urlString = "";
            String current;
            while ((current = in.readLine()) != null) {
                urlString += current;
            }
//            System.out.println(urlString);
            return urlString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取拓扑所有节点对的时延
     *
     * @return
     */
    public static String geteachdelay() {
        try {
            String strUrl = "http://" + REQ_IP + ":10086/telemetry/delay";
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            } else {
                System.out.println("请输入 URL 地址");
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String urlString = "";
            String current;
            while ((current = in.readLine()) != null) {
                urlString += current;
            }
//            System.out.println(urlString);
            return urlString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取拓扑所有节点对的丢包率
     *
     * @return
     */
    public static String geteachloss() {
        try {
            String strUrl = "http://" + REQ_IP + ":10086/telemetry/loss";
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            } else {
                System.out.println("请输入 URL 地址");
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String urlString = "";
            String current;
            while ((current = in.readLine()) != null) {
                urlString += current;
            }
//            System.out.println(urlString);
            return urlString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取拓扑所有节点对的丢包率
     *
     * @return
     */
    public static String getlinkrate() {
        try {
            String strUrl = "http://" + REQ_IP + ":10086/linkrate";
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            } else {
                System.out.println("请输入 URL 地址");
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String urlString = "";
            String current;
            while ((current = in.readLine()) != null) {
                urlString += current;
            }
//            System.out.println(urlString);
            return urlString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 处理基准时延数据.
     * @param data
     * @return
     */
    public static float[] handleRefDelayData(String data) {
        JSONObject parse = JSON.parseObject(data);
        JSONArray jsonArray = parse.getJSONArray("data");
        int size = jsonArray.size();
        float average = 0f;
        float min =  Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float cnt = 0;
        if(size != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                float minDelay = jsonObject.getFloat("MinDelay");
                float maxDelay = jsonObject.getFloat("MaxDelay");
                min = Math.min(min, minDelay);
                max = Math.max(max, maxDelay);
                float meanDelay = jsonObject.getFloat("MeanDelay");
                average += meanDelay;
                cnt++;
            }
        }
        if(cnt == 0) {
            cnt = 1;
        }
        return new float[]{min, max, average, cnt};
    }

    /**
     * 处理获取到的时延数据.
     * @param data
     * @return
     */
    public static float[] handleDelayData(String data, HashMap<String, Float> refMap) {
        JSONObject parse = JSON.parseObject(data);
        JSONArray jsonArray = parse.getJSONArray("data");
        int size = jsonArray.size();
        float average = 0f;
        float min =  Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float cnt = 0;
        if(size != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                float minDelay = jsonObject.getFloat("MinDelay");
                float maxDelay = jsonObject.getFloat("MaxDelay");
                float meanDelay = jsonObject.getFloat("MeanDelay");
                String srcIP = jsonObject.getString("SrcIP");
                String dstIP = jsonObject.getString("DstIP");
                int srcId = Integer.parseInt(srcIP.split("10.0.0.")[1]) - 1;
                int dstId = Integer.parseInt(dstIP.split("10.0.0.")[1]) - 1;
                String regionIdSrc = getRegionId(srcId);
                String regionIdDst = getRegionId(dstId);
                String key = regionIdSrc + "-" + regionIdDst;
                Float refd = refMap.get(key);
                if(null == refd) {
                    System.out.println("ref delay error");
                    return new float[]{min, max, average, cnt};
                }
                min = Math.min(min, minDelay - refd);
                max = Math.max(max, maxDelay - refd);
                average += meanDelay - refd;
                cnt++;
            }
        }
        if(cnt == 0) {
            cnt = 1;
        }
        return new float[]{min, max, average, cnt};
    }

    /**
     * 处理获取到的丢包率数据.
     * @param data
     * @return
     */
    public static float[] handleLossData(String data) {
        JSONObject parse = JSON.parseObject(data);
        JSONArray jsonArray = parse.getJSONArray("data");
        int size = jsonArray.size();
        float maxLoss = 0f;
        float lossSum = 0;
        float cnt = 0;
        if(size != 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                cnt++;
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                String srcIP = jsonObject.getString("SrcIP");
//                String dstIP = jsonObject.getString("DstIP");
//                int periodPackets = jsonObject.getIntValue("PeriodPackets");
                Float periodLoss = jsonObject.getFloat("PeriodLoss");
                periodLoss = periodLoss < 0 ? 0 : periodLoss;
                periodLoss = periodLoss > 1 ? 0.95f : periodLoss;
                lossSum += periodLoss;
                maxLoss = Math.max(maxLoss,periodLoss);
            }
        }
        return new float[]{maxLoss, lossSum, cnt};
    }

    /**
     * 处理获取到的每条链路的流量,时延,丢包率.
     *
     * @param data
     * @return
     */
    public static HashMap handleData(String data) {
        HashMap<String,Float> hashMap = new HashMap<>();
        JSONObject parse = JSON.parseObject(data);
        Set<String> key = parse.keySet();
        for (String str : key) {
            hashMap.put(str,parse.getFloat(str));
        }
        return hashMap;
    }

    /**
     * 通过switchId返回该switch在哪一台服务器上
     * @param hostId
     * @return
     */
    public static String getRegionId(int hostId) {
        if(0 <= hostId && 11 >= hostId) {
            return "s0";
        } else if(12 <= hostId && 33 >= hostId) {
            return "s1";
        } else if(34 <= hostId && 55 >= hostId) {
            return "s2";
        } else if(56 <= hostId && 77 >= hostId) {
            return "s3";
        } else if(78 <= hostId && 99 >= hostId) {
            return "s4";
        } else {
            return "s5";
        }
    }

}
