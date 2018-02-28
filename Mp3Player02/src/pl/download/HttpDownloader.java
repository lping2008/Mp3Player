package pl.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pl.utils.FileUtils;

public class HttpDownloader {
	private URL url = null;

	/**
	 * ����URL�����ļ���ǰ��������ļ����е��������ı��������ķ���ֵ�����ļ����е����� 1.����һ��URL����
	 * 2.ͨ��URL���󣬴���һ��HttpURLConnection���� 3.�õ�InputStram 4.��InputStream���ж�ȡ����
	 * 
	 */
	public String download(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			// ����һ��URL����
			URL url = new URL(urlStr);
			// ����һ��Http����
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			// ʹ��IO����ȡ����
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream(), "GBK"));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * �ú����������� -1�����������ļ�����; 0�����������ļ��ɹ�; 1�������ļ��Ѿ�����
	 */
	public int downFile(String urlStr, String path, String mp3IdName) {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();
			if (FileUtils.isFileExist(mp3IdName, path)) {
				return 1;
			} else {
				inputStream = getInputStreamFromUrl(urlStr);
				File resultFile = fileUtils.write2SDFromInput(path, mp3IdName,
						inputStream);
				if (resultFile == null) {
					return -1;
				} else {
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ����URL�õ�������
	 * 
	 */
	public InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException {
		url = new URL(urlStr);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlConn.getInputStream();
		return inputStream;
	}
}
