package com.cureme.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

	public List<XmlModel> list = null;

	// string builder acts as a buffer
	StringBuilder builder;

	XmlModel xmlValues = null;

	@Override
	public void startDocument() throws SAXException {
		list = new ArrayList<XmlModel>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		// Create StringBuilder object to store xml node value                 
		builder=new StringBuilder(); 
		
		if(localName.equals("Item")){                                           
			xmlValues = new XmlModel();
			   
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (localName.equals("Item")) {
			list.add(xmlValues);
		} else if (localName.equalsIgnoreCase("title")) {
			xmlValues.setTitle(builder.toString());
		} else if (localName.equalsIgnoreCase("element")) {
			xmlValues.setElement(builder.toString());
		} else if (localName.equalsIgnoreCase("Description")) {
			xmlValues.setDescription(builder.toString());
		} else if (localName.equalsIgnoreCase("Content")){
			xmlValues.setContent(builder.toString());
		} else if (localName.equalsIgnoreCase("ImagePath")){
			xmlValues.setImagePath(builder.toString());
		}
	}
	
	@Override            
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		/****** Read the characters and append them to the buffer ******/
		String tempString = new String(ch, start, length);
		builder.append(tempString);
	} 
	

}
