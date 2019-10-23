package testing;

import java.util.ArrayList;

import data.Movie;
import data.Rating;
import data.Tag;
import data.User;
import io.FileIO;
import parsers.MovieLensCSVTranslator;

public class NetflixPrize {

	
	public static void main(String[] args) {
		ArrayList<String> movieStrings = FileIO.read("data" + FileIO.FILE_SEPARATOR + "movies.csv");
		ArrayList<String> tagStrings = FileIO.read("data" + FileIO.FILE_SEPARATOR + "tags.csv");
		ArrayList<String> ratingStrings = FileIO.read("data" + FileIO.FILE_SEPARATOR + "ratings.csv");
		ArrayList<String> linkStrings = FileIO.read("data" + FileIO.FILE_SEPARATOR + "links.csv");
		ArrayList<User> users = new ArrayList<User>(671);
		users.add(new User(1));
		
		movieStrings.remove(0);
		tagStrings.remove(0);
		ratingStrings.remove(0);
		
		MovieLensCSVTranslator translator = new MovieLensCSVTranslator();
		
		ArrayList<Movie> movies = new ArrayList<Movie>();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		ArrayList<Rating> ratings = new ArrayList<Rating>();

		for(String s: movieStrings) {
			movies.add(translator.parseMovie(s));
		}
		
		for(String s: tagStrings) {
			tags.add(translator.parseTag(s));
		}
	
		for(String s: ratingStrings) {
			ratings.add(translator.parseRating(s, movies));
		}
		
		int userId = users.get(0).getId();
		for(Rating r: ratings) {
			int user = r.getUserId();
			if(user == userId) {
				users.get(user-1).addRating(r);
			}
			else {
				userId = r.getUserId();
				User x = new User(user);
				x.addRating(r);
				users.add(x);
			}
		}
		
		for(Tag t: tags) {
//			System.out.println(t);
			for(int i = 0; i < movies.size(); i++) {
				if(movies.get(i).getId() == t.getMovieId()) {
					movies.get(i).addTag(t);
					break;
				}
			}
			boolean userExists = false;
			for(int i = 0; i < users.size(); i++) {
				if(users.get(i).getId() == t.getUserId()) {
					users.get(i).addTag(t);
					userExists = true;
					break;
				}
			}
			if(!userExists) {
				User u = new User(t.getUserId());
				u.addTag(t);
				users.add(u);
			}
		}
		
		for(int i = 1; i < linkStrings.size(); i++) {
			String s = linkStrings.get(i);
			translator.parseLinks(s, movies);
		}
		
//		for(Movie m : movies) {
//			System.out.println(m);
//		}
		
				
		for(User u: users) {
			System.out.println(u);
		}
		
	}
		
}
