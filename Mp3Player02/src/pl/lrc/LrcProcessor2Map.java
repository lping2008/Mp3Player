package pl.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcProcessor2Map {
	String encoding = "utf-8";
	
	 /**
     * ����LRC����ļ�
     * 
     * @param path
     *            lrc�ļ�·��
     * @return
     */
    public List<Map<Long, String>> parse(String path) {
        // �洢���и����Ϣ������
        List<Map<Long, String>> list = new ArrayList<Map<Long, String>>();
        try {
            // String encoding = "utf-8"; // �ַ����룬�������ļ����벻�������������
            File file = new File(path);
            if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String regex = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]"; // ������ʽ
                Pattern pattern = Pattern.compile(regex); // ���� Pattern ����
                String lineStr = null; // ÿ�ζ�ȡһ���ַ���
                while ((lineStr = bufferedReader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(lineStr);
                    while (matcher.find()) {
                        // ���ڴ洢��ǰʱ���������Ϣ������
                        Map<Long, String> map = new HashMap<Long, String>();
                        // System.out.println(m.group(0)); // ����[02:34.94]
                        // [02:34.94] ----��Ӧ---> [����:��.����]
                        String min = matcher.group(1); // ����
                        String sec = matcher.group(2); // ��
                        String mill = matcher.group(3); // ���룬ע��������ʵ��Ҫ����10
                        long time = getLongTime(min, sec, mill + "0");
                        // ��ȡ��ǰʱ��ĸ����Ϣ
                        String text = lineStr.substring(matcher.end());
                        map.put(time, text); // ��ӵ�������
                        list.add(map);
                    }
                }
                read.close();
                return list;
            } else {
                System.out.println("LrcProcessor2Map�Ҳ���ָ�����ļ�:" + path);
            }
        } catch (Exception e) {
            System.out.println("��ȡ�ļ�����!");
            e.printStackTrace();
        }
        return null;
    }
    public String getLrcString(String path){
    	 File file = new File(path);
    	StringBuffer lrcString = new StringBuffer();
    	String lineStr=null;
		try {
			InputStreamReader read = new InputStreamReader(
			        new FileInputStream(file), encoding);
			@SuppressWarnings("resource")
			BufferedReader bufferReader = new BufferedReader(read);
			while((lineStr = bufferReader.readLine())!=null){
				lrcString.append(lineStr);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return lrcString.toString();
    }

    /**
     * �����ַ�����ʽ�����ķ��ӡ����ӡ�����ת����һ���Ժ���Ϊ��λ��long����
     * 
     * @param min
     *            ����
     * @param sec
     *            ����
     * @param mill
     *            ����
     * @return
     */
    private long getLongTime(String min, String sec, String mill) {
        // ת������
        int m = Integer.parseInt(min);
        int s = Integer.parseInt(sec);
        int ms = Integer.parseInt(mill);

        if (s >= 60) {
            System.out.println("����: ������һ��ʱ�䲻��ȷ���� --> [" + min + ":" + sec + "."
                    + mill.substring(0, 2) + "]");
        }
        // ��ϳ�һ�������ͱ�ʾ���Ժ���Ϊ��λ��ʱ��
        long time = m * 60 * 1000 + s * 1000 + ms;
        return time;
    }

    /**
     * ��ӡ�����Ϣ
     */
    private void printLrc(List<Map<Long, String>> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("û���κθ����Ϣ��");
        } else {
            for (Map<Long, String> map : list) {
                for (Entry<Long, String> entry : map.entrySet()) {
                    System.out.println("ʱ��:" + entry.getKey() + "  \t���:"
                            + entry.getValue());
                }
            }
        }
    }
}
