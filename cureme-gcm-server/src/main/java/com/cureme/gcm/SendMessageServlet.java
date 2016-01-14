package com.cureme.gcm;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

//import javax.servlet.ServletContext;

@SuppressWarnings("serial")
public class SendMessageServlet extends BaseServlet {

	  private static final String HEADER_QUEUE_COUNT = "X-AppEngine-TaskRetryCount";
	  private static final String HEADER_QUEUE_NAME = "X-AppEngine-QueueName";
	  private static final int MAX_RETRY = 3;

	  static final String PARAMETER_DEVICE = "device";
	  static final String PARAMETER_MULTICAST = "multicastKey";
	  static final String PARAMETER_MESSAGE = "message";
	  

	  private Sender sender;
	  
	  //private String tipContent ;

	  @Override
	  public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    sender = newSender(config);
	  }

	  /**
	   * Creates the {@link Sender} based on the servlet settings.
	   */
	  protected Sender newSender(ServletConfig config) {
	    String key = (String) config.getServletContext()
	        .getAttribute(ApiKeyInitializer.ATTRIBUTE_ACCESS_KEY);
	    return new Sender(key);
	  }

	  /**
	   * Indicates to App Engine that this task should be retried.
	   */
	  private void retryTask(HttpServletResponse resp) {
	    resp.setStatus(500);
	  }

	  /**
	   * Indicates to App Engine that this task is done.
	   */
	  private void taskDone(HttpServletResponse resp) {
	    resp.setStatus(200);
	  }

	  /**
	   * Processes the request to add a new message.
	   */
	  @Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	      throws IOException {
	    if (req.getHeader(HEADER_QUEUE_NAME) == null) {
	      throw new IOException("Missing header " + HEADER_QUEUE_NAME);
	    }
	    String retryCountHeader = req.getHeader(HEADER_QUEUE_COUNT);
	    logger.fine("retry count: " + retryCountHeader);
	    if (retryCountHeader != null) {
	      int retryCount = Integer.parseInt(retryCountHeader);
	      if (retryCount > MAX_RETRY) {
	          logger.severe("Too many retries, dropping task");
	          taskDone(resp);
	          return;
	      }
	    }
	    //tipContent = req.getParameter(PARAMETER_MESSAGE);
	    
	    /*if (tipContent.length() == 0){
	    	return ;
	    }*/
	    String regId = req.getParameter(PARAMETER_DEVICE);
	    if (regId != null) {
	      sendSingleMessage(regId, resp);
	      return;
	    }
	    String multicastKey = req.getParameter(PARAMETER_MULTICAST);
	    if (multicastKey != null) {
	      sendMulticastMessage(multicastKey, resp);
	      return;
	    }
	    logger.severe("Invalid request!");
	    taskDone(resp);
	    return;
	  }

	private Message createMessage() throws IOException {

		//ServletContext context = getServletContext();
		//InputStream stream = context.getResourceAsStream("/WEB-INF/data/tip.html");
		String mess = getDailyTip();
		Message message = new Message.Builder()
		.collapseKey("1")
		.delayWhileIdle(true)
		.timeToLive(600)
		.addData("CureMe", mess)
		.build();
		return message;
	}

	public static String fromStream(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder out = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
		}
		return out.toString();
	}

	public String getDailyTip() {
		ServletContext context = getServletContext();
		InputStream stream = context
				.getResourceAsStream("/WEB-INF/data/tipofday.xml");
		
		String tip = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(stream);
			// Create XPathFactory object
			XPathFactory xpathFactory = XPathFactory.newInstance();
			// Create XPath object
			XPath xpath = xpathFactory.newXPath();
			tip = getTipContentBydate(doc, xpath);

		} catch (ParserConfigurationException e) {
			logger.severe("Parse Excption " + e.getMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.severe("SAX Exception " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.severe( "IO Exception" + e.getMessage());
			
		}
		
		return tip;
	}

	private String getTipContentBydate(Document doc, XPath xpath) {
		String content = null;
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
			String dateFormatted = sdf.format(date);

			String strXpath = String.format("/Tips/tipOfDay[@date='%s']",
					dateFormatted);
			// create XPathExpression object
			XPathExpression expr = xpath.compile(strXpath);
			// evaluate expression result on XML document
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			content = innerXml(nodes.item(0));
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return content;
	}

	private String innerXml(Node node) {
		String s = "";
		NodeList childs = node.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			s += serializeNode(childs.item(i));
		}
		return s;
	}

	private String serializeNode(Node node) {
        String s = "";
        if( node.getNodeName().equals("#text")) {
        	String nodeContent = node.getTextContent().trim();
        	nodeContent = nodeContent.replace("\t", "");
        	nodeContent = nodeContent.replace("\n", "");
        	return nodeContent;
        }
        s+= "\n <" + node.getNodeName()+" ";
        NamedNodeMap attributes = node.getAttributes();
        if( attributes!= null ){
            for( int i = 0;i<attributes.getLength();i++ ){
                s+=attributes.item(i).getNodeName()+"=\""+attributes.item(i).getNodeValue()+"\"";
            }
        }
        NodeList childs = node.getChildNodes();
        if( childs == null || childs.getLength() == 0 ){
            s+= "/>";
            return s;
        }
        s+=">";
        for( int i = 0;i<childs.getLength();i++ )
            s+=serializeNode(childs.item(i));
        s+= "</"+node.getNodeName()+">";
        return s;
    }

	  private void sendSingleMessage(String regId, HttpServletResponse resp) {
	    logger.info("Sending message to device " + regId);
	    Message message = null ;
	    Result result;
	    try {
		    message = createMessage();
	      result = sender.sendNoRetry(message, regId);
	    } catch (IOException e) {
	      logger.log(Level.SEVERE, "Exception posting " + message, e);
	      taskDone(resp);
	      return;
	    }
	    if (result == null) {
	      retryTask(resp);
	      return;
	    }
	    if (result.getMessageId() != null) {
	      logger.info("Succesfully sent message to device " + regId);
	      String canonicalRegId = result.getCanonicalRegistrationId();
	      if (canonicalRegId != null) {
	        // same device has more than on registration id: update it
	        logger.finest("canonicalRegId " + canonicalRegId);
	        Datastore.updateRegistration(regId, canonicalRegId);
	      }
	    } else {
	      String error = result.getErrorCodeName();
	      if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
	        // application has been removed from device - unregister it
	        Datastore.unregister(regId);
	      } else {
	        logger.severe("Error sending message to device " + regId
	            + ": " + error);
	      }
	    }
	  }

	  private void sendMulticastMessage(String multicastKey,
	      HttpServletResponse resp) {
	    // Recover registration ids from datastore
	    List<String> regIds = Datastore.getMulticast(multicastKey);
	    Message message = null;
	    MulticastResult multicastResult;
	    try {
	    	message = createMessage();
	      multicastResult = sender.sendNoRetry(message, regIds);
	    } catch (IOException e) {
	      logger.log(Level.SEVERE, "Exception posting " + message, e);
	      multicastDone(resp, multicastKey);
	      return;
	    }
	    boolean allDone = true;
	    // check if any registration id must be updated
	    if (multicastResult.getCanonicalIds() != 0) {
	      List<Result> results = multicastResult.getResults();
	      for (int i = 0; i < results.size(); i++) {
	        String canonicalRegId = results.get(i).getCanonicalRegistrationId();
	        if (canonicalRegId != null) {
	          String regId = regIds.get(i);
	          Datastore.updateRegistration(regId, canonicalRegId);
	        }
	      }
	    }
	    if (multicastResult.getFailure() != 0) {
	      List<Result> results = multicastResult.getResults();
	      List<String> retriableRegIds = new ArrayList<String>();
	      for (int i = 0; i < results.size(); i++) {
	        String error = results.get(i).getErrorCodeName();
	        if (error != null) {
	          String regId = regIds.get(i);
	          logger.warning("Got error (" + error + ") for regId " + regId);
	          if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
	            // application has been removed from device - unregister it
	            Datastore.unregister(regId);
	          }
	          if (error.equals(Constants.ERROR_UNAVAILABLE)) {
	            retriableRegIds.add(regId);
	          }
	        }
	      }
	      if (!retriableRegIds.isEmpty()) {
	        // update task
	        Datastore.updateMulticast(multicastKey, retriableRegIds);
	        allDone = false;
	        retryTask(resp);
	      }
	    }
	    if (allDone) {
	      multicastDone(resp, multicastKey);
	    } else {
	      retryTask(resp);
	    }
	  }

	  private void multicastDone(HttpServletResponse resp, String encodedKey) {
	    Datastore.deleteMulticast(encodedKey);
	    taskDone(resp);
	  }

}
