package forme.controllers.auth;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mindrot.jbcrypt.BCrypt;

import forme.config.DBConnection;
import forme.models.User;

@Path("auth")
public class AuthController {
	
	static int count = 0;
	SecureRandom random = new SecureRandom();
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String loginUser(@FormParam("name") String name, @FormParam("password") String password ) {
		Connection connection;
		try {
			connection = DriverManager.getConnection(DBConnection.getDB_URL(), DBConnection.getUser(), DBConnection.getPW());
			Statement stmt = connection.createStatement();
			
			String getPassword = "SELECT password FROM users WHERE username = " + "'" + name + "'";
			ResultSet rs = stmt.executeQuery(getPassword);
			
			rs.next();
			String hashed = rs.getString("password");
			boolean matches = BCrypt.checkpw(password, hashed);

			
			if (matches) {
				//return session?
				return "match";
			} else {
				//error
				return "logged in";
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}	
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
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String registerUser(@FormParam("name") String name, 
			@FormParam("email") String email, 
			@FormParam("password") String password) {
		
		String hash = BCrypt.hashpw(password, BCrypt.gensalt());
		Connection connection;
		try {
			connection = DriverManager.getConnection(DBConnection.getDB_URL(), DBConnection.getUser(), DBConnection.getPW());
			Statement stmt = connection.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS users ( " 
					   + "id varchar(20)," 
					   + "username varchar(20),"
					   + "email varchar(40),"
					   + "password varchar(100),"
					   + "PRIMARY KEY (id) );");
			
			
			User user = new User("test4", name, email, hash);
			
			String insert = "INSERT INTO users (id, username, email, password) VALUES ( "
					+ String.format(" '%s', '%s', '%s', '%s' );", user.getId(), user.getName(), user.getEmail(), user.getPassword());
			System.out.println(insert);
			
			stmt.executeUpdate(insert);
			
			return "registered";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}	
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
