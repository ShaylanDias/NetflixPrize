package data;

import java.util.ArrayList;
import java.util.Collections;

public class User implements Comparable<User>{

	private int id;
	private ArrayList<Rating> ratings;
	private ArrayList<Tag> tags;
	private ArrayList<Movie> movies;
	//	private int averageYear;
	private ArrayList<Genre> topGenres;
	private boolean calcedTopGenres;
	private double stdDev, avgRating, stdDevFromAvg, diffFromAvg;
	private boolean calcedStdDev, calcedAvgRating, calcedStdDevFromAvg, calcedDiffFromAvg;

	public User(int id) {
		this.id = id;
		ratings = new ArrayList<Rating>();
		tags = new ArrayList<Tag>();
		movies = new ArrayList<Movie>();
		//		averageYear = averageYear();
		//		topGenres = topGenres();
		topGenres = new ArrayList<Genre>();
	}

	public Movie getTopMovie() {
		Rating r = ratings.get(0);
		for(int i = 1; i < ratings.size(); i++) {
			if(r.getRating() < ratings.get(i).getRating())
				r = ratings.get(i);
		}
		Movie movie = null;
		for(Movie m : movies) {
			if(r.getMovieId() == m.getId()) {
				movie = m;
				break;
			}
		}
		if(movie != null)
			return movie;
		else
			return movies.get(0);
	}

	public void addRating(Rating rating) {
		ratings.add(rating);
	}

	public void addMovie(Movie movie) {
		movies.add(movie);
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}

	public void setId(int id) {
		this.id = id;
	}

	//Error Code return value = 0, no movies of this genre
	public double diffFromAvg(Genre g) {
		if(!calcedDiffFromAvg) {
			ArrayList<Movie> movs = new ArrayList<Movie>();
			for(Movie m : movies) {
				for(Genre x : m.getGenres()) {
					if(x.equals(g)) {
						movs.add(m);
						break;
					}
				}
			}
			if(movs.size() < 1)
				diffFromAvg = 0;
			double sum = 0;
			for(Movie m : movs) {
				double rating = 0;
				for(Rating r : ratings) {
					if(r.getMovieId() == m.getId()) {
						rating = r.getRating();
						break;
					}
				}
				sum += rating - m.getAverageRating();
			}
			diffFromAvg = sum/movs.size();
			calcedDiffFromAvg = true;
		}
		return diffFromAvg;
	}


	//	public int getAverageYear() {
	//		return averageYear;
	//	}

	public int getAverageYear() {
		int sum = 0;
		int size = 0;
		for(Movie m : movies) {
			if(m.getYear() != -1) {
				sum += m.getYear();
				size++;
			}
			else
				size--;
		}
		if(size != 0)
			return sum/size;
		else
			return -1;
	}

	public String toString() {
		return "User: " + id + ", Ratings: " + ratings.toString();
	}

	public int getId() {
		return id;
	}

	public double getAverageRating() {
		if(!calcedAvgRating) {
			if(ratings.size() == 0)
				avgRating = 0;
			else {
				double sum = 0;
				for(Rating r : ratings)
					sum += r.getRating();
				avgRating = sum/ratings.size();
			}
			calcedAvgRating = true;
		}
		return avgRating;
	}

	public double genreAvgRating(Genre g) {
		if(ratings.size() == 0)
			return 0;
		else {
			double sum = 0;
			int count = 0;
			for(Rating r : ratings) {
				ArrayList<Genre> a = null;
				Movie mov = findMovie(r.getMovieId());
				if(mov != null)
					a = mov.getGenres();
				if(a != null) {
					for(Genre x : a) {
						if(x.equals(g)) {
							sum += r.getRating();
							count++;
							break;
						}
					}
				}
			}
			if(count != 0)
				return sum/count;
			else
				return 0;
		}
	}

	public double standardDeviation(double movieAvg) {
		if(!calcedStdDev) {
			boolean pos = false;
			if(ratings.size() <= 0)
				stdDev = 0;
			double sum = 0;
			double actSum = 0;
			for(Rating r : ratings) {
				double x = r.getRating()-movieAvg;
				sum += (Math.pow((x), 2));
				actSum += x;
			}
			double div = sum/ratings.size();
			if(actSum > 0)
				pos = true;
			if(pos)
				stdDev = Math.sqrt(div);
			else
				stdDev = -1 * Math.sqrt(div);

			calcedStdDev = true;
		}
		return stdDev;
	}

	public double standardDeviationFromAvg() {
		if(!calcedStdDevFromAvg) {
			if(ratings.size() <= 0)
				stdDevFromAvg = 0;
			else {
				double sum = 0;
				for(Rating r : ratings) {
					double d = 0;
					for(Movie m : movies) {
						if(m.getId() == r.getMovieId()) {
							d = m.getAverageRating();
							break;
						}
					}
					sum += r.getRating()-d;
				}
				double div = sum/ratings.size();
				stdDevFromAvg = div;
			}
			calcedStdDevFromAvg = true;
		}
		return stdDevFromAvg;
	}

	public ArrayList<Rating> getRatings(){
		return ratings;
	}

	/**
	 * 
	 * @param movieId Movie to find
	 * @throws IllegalArgumentException if it cannot find the movieID
	 */
	public Movie getUserMovie(int movieId) {
		for(Movie m : movies) {
			if(m.getId() == movieId)
				return m;
		}
		throw new IllegalArgumentException();
	}

	public boolean allOnesAndFives() {
		for(Rating r : ratings) {
			if(r.getRating() != 5 && r.getRating() != 1) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<Genre> getTopGenres() {
		if(!calcedTopGenres) {
			ArrayList<Rating> topRated = new ArrayList<Rating>();
			topRated.add(ratings.get(0));
			for(int i = 1; i < ratings.size(); i++) {
				Rating r = ratings.get(i);
				if(r.getRating() > topRated.get(0).getRating()) {
					topRated.add(r);
				}
			}

			for(Rating r : topRated) {
				int ind = Collections.binarySearch(movies, new Movie(r.getMovieId()));
				Movie m = movies.get(ind);
				for(Genre g : m.getGenres()) {
					boolean valid = true;
					for(Genre g2 : topGenres) {
						if(g.equals(g2)) {
							valid = false;
							break;
						}
					}
					if(valid)
						topGenres.add(g);
				}
			}
			calcedTopGenres = true;
		}

		return topGenres;
	}

	public ArrayList<Genre> getWorstGenres() {
		Rating lowest;
		lowest = (ratings.get(0));
		for(int i = 1; i < ratings.size(); i++) {
			Rating r = ratings.get(i);
			if(r.getRating() < lowest.getRating()) {
				lowest = r;
			}
		}

		return findMovie(lowest.getMovieId()).getGenres();
	}

	@Override
	public int compareTo(User o) {
		return id - o.getId();
	}

	private Movie findMovie(int id) {
		int ind = Collections.binarySearch(movies, new Movie(id));
		if(ind >= 0)
			return movies.get(ind);
		return null;
	}

	//	public ArrayList<Genre> getTopGenres(){
	//		return topGenres;
	//	}

}
