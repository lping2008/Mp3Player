package pl.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.model.UserInfo;

public class UserXmlContentHandler extends DefaultHandler{
	private String tagName;
	private UserInfo userInfo;
	
	public UserXmlContentHandler(UserInfo userInfo) {
		super();
		this.userInfo = userInfo;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
//		System.out.println("startElement");
//		System.out.println("uri---->"+uri);
//		System.out.println("localName---->"+localName);
//		System.out.println("qName---->"+qName);
//		System.out.println("attributes----->"+attributes);
		this.tagName=localName;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
//		System.out.println("endElement");
//		System.out.println("uri---->"+uri);
//		System.out.println("localName---->"+localName);
//		System.out.println("qName---->"+qName);
		tagName="";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String temp = new String(ch,start,length);
		if(tagName.equals("userName")){
			userInfo.setUserName(temp);
		}
		if(tagName.equals("userPW")){
			userInfo.setUserPW(temp);
		}
	}
}
