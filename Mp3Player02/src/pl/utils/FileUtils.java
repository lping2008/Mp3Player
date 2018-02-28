package pl.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pl.model.Mp3Info;
import pl.mp3player.AppConstant;
import pl.mp3player.Mp3ListActivity;
import android.os.Environment;

public class FileUtils {
	private String SDCardRoot;

	public FileUtils() {
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
	}

	// 在SD上创建文件
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(dir + fileName);
		file.createNewFile();
		return file;
	}

	// 在SD卡上创建目录
	public File createSDDir(String dir) {
		File dirFile = new File(dir);
		return dirFile;
	}

	// 判断SD上文件夹是否存在
	public static boolean isFileExist(String fileName, String path) {
		File file = new File(path + fileName);
		System.out.println("File isFileExist-->"+path+fileName+"--->"+file.exists());
		return file.exists();
	}

	public File write2SDFromInput(String path, String fileName,
			InputStream input) throws IOException {
		File file = null;
		OutputStream output = null;
		createSDDir(path);
		try {
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			int sum=0;
			while ((temp = input.read(buffer)) != -1) {
//				sum += temp;  //计算总下载量
				output.write(buffer, 0, temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return file;
	}

	public List<Mp3Info> getMp3Files(String path) {
		Mp3ListActivity mp3ListActivity = new Mp3ListActivity();
		String xml = mp3ListActivity.downloadXML(AppConstant.URL.BASE_URL
				+ "resources.xml");
		List<Mp3Info> mp3InfosOnServer = mp3ListActivity.parse(xml);
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		File file = new File(path);
		File[] files = file.listFiles();
		if (files != null) {
			for (Iterator iterator = mp3InfosOnServer.iterator(); iterator
					.hasNext();) {
				Mp3Info mp3InfoOnServer = (Mp3Info) iterator.next();
				Mp3Info mp3Info = new Mp3Info();
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().startsWith(mp3InfoOnServer.getId())&&
							files[i].getName().endsWith(".mp3")) {
						mp3Info.setId(mp3InfoOnServer.getId());
						mp3Info.setMp3Name(mp3InfoOnServer.getMp3Name());
						mp3Info.setMp3Size(mp3InfoOnServer.getMp3Size());
						mp3Info.setLrcName(mp3InfoOnServer.getLrcName());
						mp3Infos.add(mp3Info);
					}
				}
			}
			return mp3Infos;
		} else {
			return null;
		}
	}
	public List<Mp3Info> getMp3FilesFromPush(String path){
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		File file = new File(path);
		File []files = file.listFiles();
		if(files!=null){
			for (int i = 0; i < files.length; i++) {
				if(files[i].getName().endsWith("mp3")){
					Mp3Info mp3Info =new Mp3Info();
					mp3Info.setMp3Name(files[i].getName());
					mp3Info.setMp3Size(files[i].length()+"");
					mp3Info.setLrcName(files[i].getName().substring(0,files[i].getName().lastIndexOf("."))+".lrc");
					mp3Infos.add(mp3Info);
				}
			}
			return mp3Infos;
		}
		else{
			return null;
		}
	}
	/**
     * 删除单个文件
     *
     *            要删除的文件的文件名
     * 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
	
}
