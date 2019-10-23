package testing;
import java.util.ArrayList;
import java.util.Collections;

import data.Genre;
import data.Movie;
import data.Rating;
import data.Tag;
import data.User;
import io.FileIO;
import parsers.MovieLensCSVTranslator;


public class NetFlixPredictor {


	// Add fields to represent your database.

	private ArrayList<User> users = new ArrayList<User>(671);
	private ArrayList<Movie> movies = new ArrayList<Movie>();
	private ArrayList<Tag> tags = new ArrayList<Tag>();
	private ArrayList<Rating> ratings = new ArrayList<Rating>();

	/**
	 * 
	 * Use the file names to read all data into some local structures. 
	 * 
	 * @param movieFilePath The full path to the movies database.
	 * @param ratingFilePath The full path to the ratings database.
	 * @param tagFilePath The full path to the tags database.
	 * @param linkFilePath The full path to the links database.
	 */
	public NetFlixPredictor(String movieFilePath, String ratingFilePath, String tagFilePath, String linkFilePath) {
		ArrayList<String> movieStrings = FileIO.read(movieFilePath);
		ArrayList<String> tagStrings = FileIO.read(tagFilePath);
		ArrayList<String> ratingStrings = FileIO.read(ratingFilePath);
		ArrayList<String> linkStrings = FileIO.read(linkFilePath);
		users = new ArrayList<User>();
		users.add(new User(1));

		movieStrings.remove(0);
		tagStrings.remove(0);
		ratingStrings.remove(0);

		MovieLensCSVTranslator translator = new MovieLensCSVTranslator();

		for(String s: movieStrings) {
			movies.add(translator.parseMovie(s));
		}
		Collections.sort(movies);
		for(String s: tagStrings) {
			tags.add(translator.parseTag(s));
		}

		for(String s: ratingStrings) {
			ratings.add(translator.parseRating(s, movies));
		}
		Collections.sort(ratings);

		int userId = users.get(0).getId();
		for(Rating r: ratings) {
			int user = r.getUserId();
			if(user == userId) {
				users.get(user-1).addRating(r);
				int ind = Collections.binarySearch(movies, new Movie(r.getMovieId()));
				if(ind >= 0) 
					users.get(user-1).addMovie(movies.get(ind));
			}
			else {
				userId = r.getUserId();
				User x = new User(user);
				x.addRating(r);
				int ind = Collections.binarySearch(movies, new Movie(r.getMovieId()));
				if(ind >= 0) {
					x.addMovie(movies.get(ind));
					users.add(x);
				}
			}
		}
		for(Tag t: tags) {
			int ind = Collections.binarySearch(movies, new Movie(t.getMovieId()));
			if(ind >= 0)
				movies.get(ind).addTag(t);
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
		Collections.sort(users);

		for(int i = 1; i < linkStrings.size(); i++) {
			String s = linkStrings.get(i);
			translator.parseLinks(s, movies);
		}
	}

	/**
	 * If userNumber has rated movieNumber, return the rating. Otherwise, return -1.
	 * 
	 * @param userNumber The ID of the user.
	 * @param movieNumber The ID of the movie.
	 * @return The rating that userNumber gave movieNumber, or -1 if the user does not exist in the database, the movie does not exist, or the movie has not been rated by this user.
	 */
	public double getRating(int userID, int movieID) {
		User x = null;
		int ind = Collections.binarySearch(users, new User(userID));
		if(ind >= 0)
			x = users.get(ind);
		else
			return -1;

		int ind2 = Collections.binarySearch(x.getRatings(), new Rating(userID, movieID));
		if(ind2 >=0)
			return x.getRatings().get(ind2).getRating();

		return -1;
	}

	/**
	 * If userNumber has rated movieNumber, return the rating. Otherwise, use other available data to guess what this user would rate the movie.
	 * 
	 * @param userNumber The ID of the user.
	 * @param movieNumber The ID of the movie.
	 * @return The rating that userNumber gave movieNumber, or the best guess if the movie has not been rated by this user.
	 * @pre A user with id userID and a movie with id movieID exist in the database.
	 */
	public double guessRating(int userID, int movieID) {
		Movie movie = null;
		User user = null;
		double avgRating = 0;
		for(Rating r : ratings) {
			avgRating += r.getRating();
		}
		avgRating /= ratings.size();
		for(Movie m : movies) {
			if(m.getId() == movieID) {
				movie = m;
				break;
			}
		}
		for(User m : users) {
			if(m.getId() == userID) {
				user = m;
				break;
			}
		}

		double movieAvg = movie.getAverageRating();
		double userAvg = user.getAverageRating();
		double userStdDeviationFromAvg  = user.standardDeviationFromAvg();

		double userGenreAvg = 0;
		int count = 0;
		for(Genre g : movie.getGenres()) {
			double x = user.genreAvgRating(g);
			if(x != 0) {
				userGenreAvg += x;
				count++;
			}
		}
		if(count != 0)
			userGenreAvg /= count;
		else
			userGenreAvg = userAvg;


		ArrayList<User> similar = findSimilarUsers(user);
		if(similar.size() > 20) {
			double similarAvg = 0;
			int divisor = 0;
			for(User u : similar) {
				ArrayList<Rating> rates = u.getRatings();
				int ind = Collections.binarySearch(rates, new Rating(u.getId(), movie.getId()));
				if(ind >= 0) {
					similarAvg += rates.get(ind).getRating();
					divisor++;
				}
			}
			if(divisor > 20) {
				//				0.7101228845726688
				similarAvg /= divisor;
				return similarAvg;
			}
		}

		double rating1 = userStdDeviationFromAvg + movieAvg;
		double rating2 = userGenreAvg;

		if(rating1 < 1)
			rating1 = 1;
		else if(rating1 > 5)
			rating1 = 5;

		double finalRating = 0.3 * rating1 + 0.7 * rating2;
		//		System.out.println(finalRating);


		return finalRating;
	}

	private ArrayList<User> findSimilarUsers(User user){
		ArrayList<User> results = new ArrayList<User>();
		for(User u: users) {
			//			if(!u.equals(user)) {
			//				ArrayList<Genre> genres2 = u.getTopGenres();
			//				int genCount = 0;
			//				for(Genre g : genres) {
			//					for(Genre g2 : genres2)
			//					if(g.equals(g2)) {
			//						genCount++;
			//						break;
			//					}
			//				}
			//				if(genCount >= 4)
			//					results.add(u);
			//			}
			if(Math.abs(u.getAverageRating() - user.getAverageRating()) < 0.1)
				results.add(u);
			//			else {
			//				if(!u.equals(user)) {
			//					ArrayList<Genre> genres = user.getTopGenres();
			//					ArrayList<Genre> genres2 = u.getTopGenres();
			//					int genCount = 0;
			//					for(Genre g : genres) {
			//						for(Genre g2 : genres2)
			//						if(g.equals(g2)) {
			//							genCount++;
			//							break;
			//						}
			//					}
			//					if(genCount >= 4)
			//						results.add(u);
			//				}
			//			}
		}

		//		genres = user.getTopGenres();
		//		for(User u: users) {
		//			if(!u.equals(user)) {
		//				ArrayList<Genre> genres2 = u.getWorstGenres();
		//				int genCount = 0;
		//				for(Genre g : genres) {
		//					for(Genre g2 : genres2)
		//					if(g.equals(g2)) {
		//						genCount++;
		//						break;
		//					}
		//				}
		//				if(genCount >= 3)
		//					results.add(u);
		//			}
		//		}

		return results;
	}

	private ArrayList<User> findSimilarGenreUsers(User user){
		ArrayList<User> results = new ArrayList<User>();
		for(User u: users) {
			if(!u.equals(user)) {
				ArrayList<Genre> genres = user.getTopGenres();
				ArrayList<Genre> genres2 = u.getTopGenres();
				int genCount = 0;
				for(Genre g : genres) {
					for(Genre g2 : genres2)
						if(g.equals(g2)) {
							genCount++;
							break;
						}
				}
				if(genCount >= 8)
					results.add(u);
			}
		}
		return results;
	}

	/**
	 * Recommend a movie that you think this user would enjoy (but they have not currently rated it). 
	 * 
	 * @param userNumber The ID of the user.
	 * @return The ID of a movie that data suggests this user would rate highly (but they haven't rated it currently).
	 * @pre A user with id userID exists in the database.
	 */
	public int recommendMovie(int userID) {
		int ind = Collections.binarySearch(users, new User(userID));
		if(ind >= 0) {
			User user = users.get(ind);
			if(ind >= 0) {
				ArrayList<User> similar = findSimilarGenreUsers(user);
				if(similar.size() != 0)
					return similar.get((int)(Math.random() * (similar.size()-1))).getTopMovie().getId();
			}

			ArrayList<Genre> genres = user.getTopGenres();
			Movie x = findMovieOverlap(3, genres);
			if(x!= null)
				return x.getId();		
			x = findMovieOverlap(2.5, genres);
			if(x!= null)
				return x.getId();
		}
		return movies.get((int)(Math.random()*(movies.size()-1))).getId();
	}

	public int recommendMovie(int userID, ArrayList<Movie> seen) {
		int recommended = recommendMovie(userID);
		int ind = Collections.binarySearch(movies, new Movie(recommended));
		Movie x = movies.get(ind);
		for(Movie m : seen) {
			if(m.getId() == x.getId()) {
				seen.add(x);
				return recommendMovie(userID, seen);
			}
		}
		return recommended;
	}


	private Movie findMovieOverlap(double avgRating, ArrayList<Genre> genres) {
		ArrayList<Movie> possMovies = new ArrayList<Movie>();
		int movieCount = 0;
		for(Movie m : movies) {
			if(m.getAverageRating() > avgRating) {
				ArrayList<Genre> movGenres = m.getGenres();
				int overlap = 0;
				for(Genre g : movGenres) {
					for(Genre g2 : genres) {
						if(g.equals(g2)) {
							overlap++;
							break;
						}
					}
				}
				if(overlap >= 3) {
					movieCount++;
					possMovies.add(m);
				}
			}
			if(movieCount > 10)
				break;
		}
		if(possMovies.size() > 0)
			return possMovies.get((int)(Math.random() * (possMovies.size()-1)));
		else
			return null;
	}

	public ArrayList<Movie> getMovies(){
		return movies;
	}

}
