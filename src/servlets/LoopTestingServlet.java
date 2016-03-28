/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.TripleDES;

/**
 * This servlet class is used to serve tests from sensor data.
 * It is a loop test, which means that whatever the sensor data simulator
 * uploads, is sent back with a message.
 * 
 */

@SuppressWarnings("serial")
public class LoopTestingServlet extends HttpServlet {

	private static final String TESTING_PRIVATE_KEY = 
			"xL354/vEK1BMWFwNDobgQizZKmn0APcVKkV11TqL4IY=";
    private static final Logger log = 
        Logger.getLogger(LoopTestingServlet.class.getName());
    
    // JSP file locations
    private static final String thisServlet = "/loopTest";
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	
	    // Lets check the action and parameters returned
	    String status = req.getParameter("status");
    	String command = req.getParameter("command");
	    String message = req.getParameter("message");
	    
	    String response = "";
	    response += "status=" + status;
	    if (status.equals("success")) {
	    	response += ",command=" + command;
	    	response += ",message=" + message;
	    	resp.getWriter().println(response);
	    }
	    else if (status.equals("failure")){
	    	resp.getWriter().println(response);
	    }
	}

    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws IOException {
    	
        try {
	    	// User fields
//	        Email userEmail = new Email(req.getParameter("u_email"));
//	        String userPassword = req.getParameter("u_password");
//	        String deviceId = req.getParameter("device_id");
//	        String temperatureString = req.getParameter("temperature");
//	        Double temperature = Double.parseDouble(temperatureString);
//	        String humidityString = req.getParameter("humidity");
//	        Double humidity = Double.parseDouble(humidityString);

//	        String timestampString = req.getParameter("timestamp");
//	        //timestampString = URLDecoder.decode(timestampString, "UTF-8");
//	        Date timestamp = DateManager.getDateValueISO8601(timestampString);
//	        TimeZone timeZone = DateManager.getTimeZoneFromISO8601Date(timestampString);
	        
	        String content = req.getParameter("content");
	        content = URLDecoder.decode(content, "UTF-8");	
//	        content = TripleDES.decrypt(content, TESTING_PRIVATE_KEY);

//	        String message = "u_Email:" + userEmail.getEmail() + "," + 
//	        		"u_Password:" + userPassword + "," +
//	        		"device_id:" + deviceId + "," +
//	        		"temperature:" + temperature + "," +
//	        		"humidity:" + humidity + "," +
//	        		"timestamp:" + DateManager.printDateAsStringISO8601(timestamp, timeZone);
	        
	        String message = content;
	        message = URLEncoder.encode(message, "UTF-8");
	        resp.setStatus(HttpServletResponse.SC_OK);
	        
            resp.sendRedirect(thisServlet + "?status=success&message=" + message + "&command=loop_test");
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, ex.toString());
            ex.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            		"Internal server error.");
            return;
        }
    }
}
