package forme.controllers.exercises;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import forme.config.DBConnection;
import forme.models.Workout;

@Path("")
public class ExerciseController {
	
	static int counter = 0;

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
	/*
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String getUserExerciseHTML(@PathParam("id") Integer id) {
		return "<html> " + "<title>" + "Exercise: " + id + "</title>" + "<body><h1>" + "Exerciser: " + id + "</body></h1>"
				+ "</html> ";
	}
*/
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserExercise(@PathParam("id") String id) {	
		
		System.out.println("flag0");
		
		Connection connection;
		try {
			connection = DriverManager.getConnection(DBConnection.getDB_URL(), DBConnection.getUser(), DBConnection.getPW());	
			Statement stmt = connection.createStatement();
			
			System.out.println("flag1");
			
			String log = "SELECT * FROM workouts w "
					+ "WHERE w.id IN "
					+ "("
					+ "SELECT workout_id FROM user_logs WHERE user_id = '" + id + "'" 
					+ ")";
			
			
			//String test = "SELECT * FROM user_logs WHERE user_id = '" + id + "')";
			
			
			String result = "{ logs: [ ";
			ResultSet rs = stmt.executeQuery(log);
			
			while (rs.next()) {
				
				//result += "user: " +  rs.getString("user_id");
				//result += " workout: " + rs.getString("workout_id");
				
				String exercise = "{ date: " + rs.getDate("log_date") 
					+ ", length: " + rs.getFloat("length") 
					+ ", description: " + rs.getString("description") 
					+ "}, ";
				
				result += exercise;
			}
			result += " ] }";
			
			return result;
		} catch (SQLException e) {
			return e.getMessage();
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
	public String createUserExercise(@PathParam("id") String id, @FormParam("length") float length, @FormParam("description") String description) {
		System.out.println(id + " " + length + " " + description);
		Date date = new Date();
		Connection connection;
		try {
			connection = DriverManager.getConnection(DBConnection.getDB_URL(), DBConnection.getUser(), DBConnection.getPW());	
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
			
			return "";
		} catch (SQLException e) {
			return e.getMessage();
		}
	}
}
