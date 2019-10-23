package data;

public class Tag {

	private int user;
	private String tag;
	private int movieId;
	
	public Tag(int userId, int movieId, String tag) {
		user = userId;
		this.tag = tag;
		this.movieId = movieId;
	}
	
	public String getTag() {
		return tag;
	}
	
	public String toString() {
		return "User: " + user + ", Tag: " + tag;
	}
	
	public int getMovieId() {
		return movieId;
	}

	public int getUserId() {
		return user;
	}
	
}
