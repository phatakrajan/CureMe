package com.cureme.gcm;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SendAllMessagesServlet extends BaseServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			doPost(req, resp);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Processes the request to add a new message.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		List<String> devices = Datastore.getDevices();
		
		//List<String> devices = new ArrayList<String>();
		//devices.add("APA91bF4dpJK1l8jIkbh91HjL3GFGQzMg1WNl6mQ-9Vg65apMfmyWr74DfhvAGjxg1NMRAakM09hLcKlyxPVV0azxazlBE8MwCALNpo5FGxxb5-A8mglDjg4HwakVkFj_cXrNbBrxb8NV1jpYnR5-1rbIvtf8qw6vg");
		
		String status;
		if (devices.isEmpty()) {
			status = "Message ignored as there is no device registered!";
		} else {
			// String tipContent = req.getParameter("tipContent");
			//String tipContent = getDailyTip();
			//logger.severe("Content: " + tipContent);

			//if (tipContent.contains("<p>") == false) {
			//	status = "Message Length is 0. Please enter the HTML message";
			//} else {
				Queue queue = QueueFactory.getQueue("gcm");
				// NOTE: check below is for demonstration purposes; a real
				// application
				// could always send a multicast, even for just one recipient
				/*if (devices.size() == 1) {
					// send a single message using plain post
					String device = devices.get(0);
					queue.add(withUrl("/send").param(
							SendMessageServlet.PARAMETER_DEVICE, device).param(
							SendMessageServlet.PARAMETER_MESSAGE, ""));
					status = "Single message queued for registration id "
							+ device;
				} else {*/
					// send a multicast message using JSON
					// must split in chunks of 1000 devices (GCM limit)
					int total = devices.size();
					List<String> partialDevices = new ArrayList<String>(total);
					int counter = 0;
					int tasks = 0;
					for (String device : devices) {
						counter++;
						partialDevices.add(device);
						int partialSize = partialDevices.size();
						if (partialSize == Datastore.MULTICAST_SIZE
								|| counter == total) {
							String multicastKey = Datastore
									.createMulticast(partialDevices);
							logger.fine("Queuing " + partialSize
									+ " devices on multicast " + multicastKey);
							TaskOptions taskOptions = TaskOptions.Builder
									.withUrl("/send")
									.param(SendMessageServlet.PARAMETER_MULTICAST,
											multicastKey)
									.param(SendMessageServlet.PARAMETER_MESSAGE,
											"").method(Method.POST);
							queue.add(taskOptions);
							partialDevices.clear();
							tasks++;
						}
					}
					status = "Queued tasks to send " + tasks
							+ " multicast messages to " + total + " devices";
				//}
			//}
		}
		req.setAttribute(HomeServlet.ATTRIBUTE_STATUS, status.toString());
		//getServletContext().getRequestDispatcher("/home").forward(req, resp);
	}

}
