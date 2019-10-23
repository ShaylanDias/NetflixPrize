package data;

public class Rating implements Comparable<Rating>{
	
	private double rating;
	private int movieId;
	private int userId;
	
	public Rating(int userId, int movie, double rating) {
		this.userId = userId;
		this.movieId = movie;
		this.rating = rating;
	}
	
	public Rating(int userId, int movieId) {
		this.userId = userId;
		this.movieId = movieId;
	}
	
	public String toString() {
		return "User: " + userId + " MovieId: " + movieId + ", Rating: " + rating;
	}
	
	public int getMovieId() {
		return movieId;
	}
		
	public int getUserId() {
		return userId;
	}
	
	public double getRating() {
		return rating;
	}

	@Override
	public int compareTo(Rating o) {
		int diff = userId - o.getUserId();
		if(diff == 0) {
			return movieId - o.getMovieId();
		}
		else
			return diff;
	}
	
}
