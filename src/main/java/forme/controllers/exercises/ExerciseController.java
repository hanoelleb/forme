package forme.controllers.exercises;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import forme.models.Workout;
//import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Path("")
public class ExerciseController {
	
	static int counter = 0;
	
	private static String secretStr = System.getenv("SECRET");
	byte[] secret = Base64.getDecoder().decode(secretStr);
	
	private boolean authenticate(String jws) {
		try {
			Jwts.parserBuilder()
				      .setSigningKey(Keys.hmacShaKeyFor(secret))
				      .build()
				      .parseClaimsJws(jws);
			return true;

		} catch (JwtException e) {
			return false;
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getAllUsersExercisesHTML() {
		return "<html> " + "<title>" + "All exercises" + "</title>" + "<body><h1>" + "All exercises" + "</body></h1>"
				+ "</html> ";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Workout getAllUserExercises() {
		return null;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserExercise(@HeaderParam("token") String jws, @PathParam("id") String id) {	
		Connection connection;
		
		boolean isValid = authenticate(jws);
		if (!isValid)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		try {
			connection =  DriverManager.getConnection(System.getenv("DB_URL"), System.getenv("USER"), System.getenv("PW"));
			Statement stmt = connection.createStatement();
			
			String log = "SELECT * FROM workouts w "
					+ "WHERE w.id IN "
					+ "("
					+ "SELECT workout_id FROM user_logs WHERE user_id = '" + id + "'" 
					+ ")";
			
			String result = "{ \"logs\": [ ";
			ResultSet rs = stmt.executeQuery(log);
			
			while (rs.next()) {
				
				String exercise = "{ \"date\": \"" + rs.getDate("log_date") + "\"" 
					+ ", \"length\": " + rs.getFloat("length") 
					+ ", \"description\": " + "\"" + rs.getString("description") + "\"";
				
				if (rs.isLast())
					exercise += "} ";
				else
					exercise += "}, ";
				
				result += exercise;
			}
			result += " ] }";
			
			return Response
					.ok()
					.entity(result)
					.type(MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path("{id}/create")
	@Produces(MediaType.TEXT_HTML)
	public String createUserExerciseHTML() {
		return "<html> " + "<title>" + "Create" + "</title>" + "<body><h1>" + "Create" + "</body></h1>"
				+ "</html> ";
	}

	@POST
	@Path("{id}/create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUserExercise(@HeaderParam("token") String jws, 
			@PathParam("id") String id,
			@FormParam("length") float length, @FormParam("description") String description) {
		
		boolean isValid = authenticate(jws);
		if (!isValid)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		Date date = new Date();
		Connection connection;
		try {
			connection =  DriverManager.getConnection(System.getenv("DB_URL"), System.getenv("USER"), System.getenv("PW"));
			Statement stmt = connection.createStatement();
			
			String workouts = "CREATE TABLE IF NOT EXISTS workouts (" 
								+ "id varchar(40),"
								+ "length numeric,"
								+ "description varchar(200),"
								+ "log_date DATE,"
								+ "PRIMARY KEY(id) );";
			
			stmt.execute(workouts);
			
			String userWorkouts = "CREATE TABLE IF NOT EXISTS user_logs ( "
									+ "user_id varchar(20),"
									+ "workout_id varchar(40),"
									+ "FOREIGN KEY (user_id) REFERENCES users,"
									+ "FOREIGN KEY (workout_id) REFERENCES workouts)";
			stmt.execute(userWorkouts);

			Workout workout = new Workout("wo0845632"+(++counter), length, date, description);
			
			String query = "INSERT INTO workouts (id, length, log_date, description) VALUES ( "
					+ String.format(" '%s', '%f', '%s', '%s')", workout.getId(), workout.getLength(), workout.getDate(), workout.getDescription());
			stmt.executeUpdate(query);
			
			String query2 = "INSERT INTO user_logs (user_id, workout_id) VALUES ( "
								+ String.format(" '%s','%s')" , id, workout.getId());
			stmt.executeUpdate(query2);
			
			return Response.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "POST")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
