package com.medepia.pi.education.simple1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class SimpleJavaMapping extends AbstractTransformation {

	@Override
	public void transform(TransformationInput tInput, TransformationOutput tOutput)
			throws StreamTransformationException {
		InputStream is = tInput.getInputPayload().getInputStream();
		OutputStream os = tOutput.getOutputPayload().getOutputStream();

		// String senderSystem = (String)
		// tInput.getInputHeader().getSenderService();
		AbstractTrace trace = getTrace();

		try {
			// input parameter example
			// String systemParam =
			// tInput.getInputParameters().getString("system");

			process(is, os, trace, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (trace != null) {
				trace.addWarning(e.toString());
			}
			e.printStackTrace();
			new StreamTransformationException(e.toString());
		}

	}

	public static void main(String[] args) throws Exception {
		String path = "test/simple_mapping/case_1";
		SimpleJavaMapping fix = new SimpleJavaMapping();
		fix.process(new FileInputStream(new File(path, "input.xml")),
				new FileOutputStream(new File(path, "output.xml")), null, true);
	}

	// Cloud Integration Java Mapping example
	private void process(InputStream is, OutputStream os, AbstractTrace trace, boolean testenv) throws Exception {

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