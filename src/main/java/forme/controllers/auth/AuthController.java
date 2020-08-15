package forme.controllers.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import forme.config.DBConnection;

@Path("auth")
public class AuthController {
	@POST
	@Path("/login")
	public void loginUser() {
		
	}
	
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public String loginUserHTML() {
		return "<html> " + "<title>" + "Login" + "</title>" + "<body><h1>" + "Login" + "</body></h1>"
				+ "</html> ";
	}
	
	@POST
	@Path("/register")
	public void registerUser() {
		
	}
	
	@GET
	@Path("/register")
	@Produces(MediaType.TEXT_HTML)
	public String registerUserHTML() {
		return "<html> " + "<title>" + "Register" + "</title>" + "<body><h1>" + "Register" + "</body></h1>"
				+ "</html> ";
	}
	
	@GET
	@Path("/db")
	@Produces(MediaType.TEXT_HTML)
	  public String db() {
	    try (Connection connection = DriverManager.getConnection(DBConnection.getDB_URL(), DBConnection.getUser(), DBConnection.getPW())) {
	      Statement stmt = connection.createStatement();
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
	      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
	      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

	      ArrayList<String> output = new ArrayList<String>();
	      while (rs.next()) {
	        output.add("Read from DB: " + rs.getTimestamp("tick"));
	      }
	      return "<html> " + "<title>" + "Success" + "</title>" + "<body><h1>" + "Successful DB" + "</body></h1>"
			+ "</html> ";
	    } catch (Exception e) {
	      return "<html> " + "<title>" + "Fail" + "</title>" + "<body><h1>" + e.getMessage() + "</body></h1>"
					+ "</html> ";
	    }
	  }

}
