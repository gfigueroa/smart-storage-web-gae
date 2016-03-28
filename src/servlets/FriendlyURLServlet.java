/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet class is used to hide the JSP extensions of every page.
 * For example, if the page listAdmin is requested, it will be
 * redirected to listAdmin.jsp
 * 
 */

@SuppressWarnings("serial")
public class FriendlyURLServlet extends HttpServlet {
	
    private static final Logger log = 
        Logger.getLogger(FriendlyURLServlet.class.getName());
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	
    	try {
			req.getRequestDispatcher(
					"/admin" + req.getPathInfo() + ".jsp").forward(req, resp);
		} 
    	catch (ServletException e) {
			e.printStackTrace();
		}
	}
}
