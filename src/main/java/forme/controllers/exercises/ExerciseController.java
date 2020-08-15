package forme.controllers.exercises;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import forme.models.Workout;

@Path("")
public class ExerciseController {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getAllUsersExercisesHTML() {
		return "<html> " + "<title>" + "All exercises" + "</title>" + "<body><h1>" + "All exercises" + "</body></h1>"
				+ "</html> ";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Workout getAllUserExercises() {
		return new Workout();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	public String getUserExerciseHTML(@PathParam("id") Integer id) {
		return "<html> " + "<title>" + "Exercise: " + id + "</title>" + "<body><h1>" + "Exerciser: " + id + "</body></h1>"
				+ "</html> ";
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Workout getUserExercise() {
		return new Workout();
	}
	
	@GET
	@Path("create")
	@Produces(MediaType.TEXT_HTML)
	public String createUserExerciseHTML() {
		return "<html> " + "<title>" + "Create" + "</title>" + "<body><h1>" + "Create" + "</body></h1>"
				+ "</html> ";
	}

	@POST
	@Path("create")
	public void createUserExercise() {

	}
}
