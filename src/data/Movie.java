package data;

import java.util.ArrayList;

public class Movie implements Comparable<Movie>{

	private String name;
	private int id;
	//If year is -1, there is no given year
	private int year;
	private ArrayList<Genre> genres;
	private ArrayList<Tag> tags;
	private ArrayList<Rating> ratings;
	private double averageRating, standardDeviation;
	private boolean calcedAvgRating = false, calcedStdDev = false;
	//	private double standardDeviation;
	private int imdbId, tmdbId;

	public Movie(String name, int id, int year, ArrayList<Genre> genres, int imdbId, int tmdbId) {
		this.name = name;
		this.id = id;
		this.year = year;
		this.genres = genres;
		tags = new ArrayList<Tag>();
		ratings = new ArrayList<Rating>();
		this.imdbId = imdbId;
		this.tmdbId = tmdbId;
		//		averageRating = averageRating();
		//		standardDeviation = standardDeviation();
	}

	public Movie(int id) {
		this.id = id;
	}
	
	public Movie(String name, int id, int year, ArrayList<Genre> genres) {
		this(name, id, year, genres, -1, -1);
	}

	public int getYear() {
		return year;
	}

	public String toString() {
		StringBuffer str =  new StringBuffer("Name: " + name + ", Year: " + year);
		str.append(" Genres: ");
		for(int i = 0; i < genres.size(); i++) {
			str.append(genres.get(i) + ", ");
		}
		str.append("Id: " + id + ", ");
		str.append("imdbId: " + imdbId + ", ");
		str.append("tmdbId: " + tmdbId);
		str.append(", Ratings: " + ratings.toString());
		str.append(", Tags: " + tags.toString());
		return str.toString();
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}

	public int getId() {
		return id;
	}

	public ArrayList<Genre> getGenres(){
		return genres;
	}

	public void addRating(Rating r) {
		ratings.add(r);
	}

	public ArrayList<Tag> getTags(){
		return tags;
	}
	
	public String getTitle() {
		return name;
	}
	
	/**
	 * Gets the average rating for this movie
	 * 
	 * @return Average rating, -1 if there are no ratings
	 */
	private double averageRating() {
		if(!calcedAvgRating) {
			if(ratings.size() == 0)
				averageRating = 0;
			else {
				int sum = 0;
				for(Rating r : ratings)
					sum += r.getRating();
				averageRating = sum/ratings.size();
			}
			calcedAvgRating = true;
		}
		return averageRating;
	}

	public double getStdDeviation() {
		return standardDeviation();
	}

	private double standardDeviation() {
		if(!calcedStdDev) {
			if(ratings.size() <= 0)
				standardDeviation = 0;
			else {
				double sum = 0;
				for(Rating r : ratings) {
					sum += (Math.pow((r.getRating()-getAverageRating()), 2));
				}
				double div = sum/ratings.size();
				standardDeviation = Math.sqrt(div);
			}
			calcedStdDev = true;
		}
		return standardDeviation;
	}

	public double getAverageRating() {
		return averageRating();
	}

	public double getMedianRating(){
		double middle = 0;
		if(ratings.size() > 1) {
			middle = ratings.size()/2;
			if (ratings.size()%2 == 1) {
				middle = (ratings.get(ratings.size()/2).getRating() + ratings.get(ratings.size()/2 - 1).getRating())/2;
			} 
		}
		else if(ratings.size() < 1)
			middle = 0;
		return middle;
	}

	public void setImdbId(int imdbId) {
		this.imdbId = imdbId;
	}

	public int getImdbId() {
		return imdbId;
	}
	
	public void setTmdbId(int tmdbId) {
		this.tmdbId = tmdbId;
	}

	@Override
	public int compareTo(Movie o) {
		return id - o.getId();
	}

}
