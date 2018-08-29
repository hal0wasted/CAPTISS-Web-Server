import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;
import java.util.Date;

// Extend HttpServlet class
public class MastersTea extends HttpServlet {
   private String message;
   private HashMap<String, HashMap<String, String>> allLines;
   private ServletContext application;

   public void init() throws ServletException {
      // Do required initialization
      message = "";

      application = getServletContext();  // Get context for logging purposes

      // Retrieve records already held on the server
      allLines = (HashMap<String, HashMap<String, String>>)application.getAttribute("masterstearecords");
   }

   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
      // Set response content type
      response.setContentType("text/html");
      message = request.getParameter("qrcode");

      // Get corresponding record based on ID
      HashMap<String, String> record = allLines.get(message);
      // PrintWriter out = response.getWriter();
      String message = "<h1>Error: no registration record found for QR code provided.</h1>";
      if (record != null) {
         // Found record
         ConcurrentHashMap<String, Date> registrationTime = (ConcurrentHashMap<String, Date>)application.getAttribute("registrationtime");

         if (!registrationTime.containsKey(record.get("id"))) {
            registrationTime.putIfAbsent(record.get("id"), new Date());
//            application.log(registrationTime.toString());
            message = "<h1>Welcome, " + record.get("name") + "! You have been successfully registered.</h1>";
         } else { // Already registered
            message = "<h1>Welcome back, " + record.get("name") + ", you have already been registered previously.</h1>";
         }
      } else {
         // No record found
         // out.println("<h1>No registration record found!</h1>");
         // message = "<h1>No registration record found!</h1>";
      }

      // Redirect to qrcode request page with welcome message
      request.setAttribute("responsemessage", message);
      RequestDispatcher dispatcher = application.getRequestDispatcher("/masterstea.jsp");
      dispatcher.forward(request, response);
   }

   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }

   public void destroy() {
      // do nothing.
   }
}