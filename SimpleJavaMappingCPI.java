package com.medepia.pi.education.simple1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SimpleJavaMappingCPI {

	public static void main(String[] args) throws Exception {
		String path = "test/simple_mapping/case_1";
		SimpleJavaMappingCPI fix = new SimpleJavaMappingCPI();
		fix.process(new FileInputStream(new File(path, "input.xml")),
				new FileOutputStream(new File(path, "output.xml")), true);
	}

	// Simple binding to Message String Cloud Integration Java Mapping example
	public String processString(String msg)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		ByteArrayInputStream bais = new ByteArrayInputStream(msg.getBytes(Charset.forName("UTF-8")));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		process(bais, baos, true);
		return new String(baos.toByteArray(), Charset.forName("UTF-8"));

	}

	// Cloud Integration Java Mapping example
	private void process(InputStream is, OutputStream os, boolean testenv)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		Document docIn = parse(is);

		// check if name element exists and get it
		NodeList nameNodeList = docIn.getElementsByTagName("name");
		if (nameNodeList.getLength() > 0) {
			Element nameEl = (Element) nameNodeList.item(0);

			// check if surname element exists and get it
			NodeList surnameNodeList = docIn.getElementsByTagName("surname");
			if (surnameNodeList.getLength() > 0) {
				Element surnameEl = (Element) surnameNodeList.item(0);

				// concat name and surname
				String nameSurnameString = nameEl.getTextContent() + " " + surnameEl.getTextContent();
				// set result to name element
				nameEl.setTextContent(nameSurnameString);
				// delete surname element
				surnameEl.getParentNode().removeChild(surnameEl);
			}
		}

		transform(docIn, os);

	}

	private static Document parse(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
			return builder.parse(is);

		} finally {
			builder = null;
			factory = null;
		}
	}

	private static void transform(Node node, OutputStream os) throws TransformerException {
		TransformerFactory factory;
		Transformer transformer;
		try {
			factory = TransformerFactory.newInstance();

			transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// standalone="no"
			// transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			transformer.transform(new DOMSource(node), new StreamResult(os));
		} finally {
			transformer = null;
			factory = null;
		}
	}

}