package pl.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.model.Mp3Info;

public class Mp3ListContentHandler extends DefaultHandler {
	private List<Mp3Info> infos = null;
	private Mp3Info mp3Info=null;
	private String tagName = null;
	
	//构造函数
	public Mp3ListContentHandler(List<Mp3Info> infos) {
		super();
		this.infos = infos;
	}
	
	public List<Mp3Info> getInfos() {
		return infos;
	}
	public void setInfos(List<Mp3Info> infos) {	
		this.infos = infos;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String temp = new String(ch,start,length);
		if(tagName.equals("id")){
			mp3Info.setId(temp);
		}
		else if(tagName.equals("mp3.name")){
			mp3Info.setMp3Name(temp);
		}
		else if (tagName.equals("mp3.size")){
			mp3Info.setMp3Size(temp);
		}
		else if (tagName.equals("lrc.name")){
			mp3Info.setLrcName(temp);
		}
		else if (tagName.equals("lrc.size")){
			mp3Info.setLrcSize(temp);
		}
	}
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}
	
	//结束解析，将对象加入到List数据infos中
	@Override
	public void endDocument() throws SAXException {
	}

	//开始解析
	//localName不带前缀，如android：name中不带android；qName为带前缀android;
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
//		System.out.println("startElement");
//		System.out.println("uri---->"+uri);
//		System.out.println("localName---->"+localName);
//		System.out.println("qName---->"+qName);
//		System.out.println("attributes----->"+attributes);
		this.tagName=localName;
		if(tagName.equals("resource")){
			mp3Info = new Mp3Info();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
//		System.out.println("endElement");
//		System.out.println("uri---->"+uri);
//		System.out.println("localName---->"+localName);
//		System.out.println("qName---->"+qName);
		if(qName.equals("resource")){
			infos.add(mp3Info);
		}
		tagName="";
	}
}
