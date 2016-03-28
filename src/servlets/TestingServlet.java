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
 * This servlet class is used for testing purposes.
 * 
 */

@SuppressWarnings("serial")
public class TestingServlet extends HttpServlet {

	private static final String TESTING_PRIVATE_KEY = 
			"xL354/vEK1BMWFwNDobgQizZKmn0APcVKkV11TqL4IY=";
    private static final Logger log = 
        Logger.getLogger(TestingServlet.class.getName());
    
    // JSP file locations
    private static final String thisServlet = "/testing";
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	
	    // Lets check the action and parameters returned
	    String status = req.getParameter("status");
	    String message = req.getParameter("message");
	    
	    if (status.equals("success")) {
	    	resp.getWriter().println("success");
	    }
	    else if (status.equals("failure")){
	    	resp.getWriter().println("failure");
	    }
	    
	    resp.getWriter().println(message);
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
	        content = TripleDES.decrypt(content, TESTING_PRIVATE_KEY);

//	        String message = "u_Email:" + userEmail.getEmail() + "," + 
//	        		"u_Password:" + userPassword + "," +
//	        		"device_id:" + deviceId + "," +
//	        		"temperature:" + temperature + "," +
//	        		"humidity:" + humidity + "," +
//	        		"timestamp:" + DateManager.printDateAsStringISO8601(timestamp, timeZone);
	        String message = content;
	        message = URLEncoder.encode(message, "UTF-8");
	        resp.setStatus(HttpServletResponse.SC_OK);
	        
            resp.sendRedirect(thisServlet + "?status=success&message=" + message);
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