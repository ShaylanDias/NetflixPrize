package parsers;

import java.util.ArrayList;

import data.Genre;
import data.Movie;
import data.Rating;
import data.Tag;

public class MovieLensCSVTranslator {

	public Movie parseMovie(String line) {
		int id = 0;
		int startInd = 0;
		int ind = 0;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}
		id = Integer.parseInt(line.substring(startInd, ind));
		startInd = ind + 1;

		String name = null;
		char endChar = ',';
		if(line.charAt(startInd) == '"') {
			endChar = '"';
			startInd++;
		}
		for(int i = startInd; i < line.length(); i++) {
			if(line.charAt(i) == endChar && line.charAt(i+1) != '"') {
				ind = i;
				if(endChar == '"')
					ind++;
				break;
			}
		}
		name = line.substring(startInd, ind);
		startInd = ind + 1;
		int year = -1;
		for(int i = name.length()-1; i >= 0; i--) {
			if(name.charAt(i) == '(') {
				if(Character.isDigit(name.charAt(i+1))) {
					year = Integer.parseInt(name.substring(i+1, i+5));
					name = name.substring(0, i-1);
				}
				break;
			}
		}

		ArrayList<Genre> genres = new ArrayList<Genre>();
		for(int i = startInd; i < line.length(); i++) {
			if(line.charAt(i) == '|') {
				genres.add(parseGenre(line.substring(startInd, i)));
				startInd = i+1;
			}
			else if(i == line.length()-1)
				genres.add(parseGenre(line.substring(startInd, line.length())));
		}

		return new Movie(name, id, year, genres);
	}

	public Tag parseTag(String line) {
		int userId = -1, movieId = -1;
		String tag = null;

		int startInd = 0;
		int ind = 0;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}

		userId = Integer.parseInt(line.substring(startInd, ind));

		startInd = ind + 1;
		for(int i = startInd; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}
		movieId = Integer.parseInt(line.substring(startInd, ind));
		startInd = ind + 1;

		char endChar = ',';
		if(line.charAt(startInd) == '"') {
			endChar = '"';
			startInd++;
		}
		for(int i = startInd; i < line.length(); i++) {
			if(line.charAt(i) == endChar) {
				if(line.charAt(i+1) != endChar && line.charAt(i-1) != endChar) {
					ind = i;
					break;
				}
				else {
					line = line.substring(0, i) + line.substring(i+1, line.length());
				}
			}
		}
		tag = line.substring(startInd, ind);

		return new Tag(userId, movieId, tag);

	}

	public Rating parseRating(String line, ArrayList<Movie> movies) {
		int userId = -1, movieId = -1;
		double rating = -1;

		int startInd = 0;
		int ind = 0;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}

		userId = Integer.parseInt(line.substring(startInd, ind));

		startInd = ind + 1;
		for(int i = startInd; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}
		movieId = Integer.parseInt(line.substring(startInd, ind));
		startInd = ind + 1;

		startInd = ind + 1;
		for(int i = startInd; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}
		rating = Double.parseDouble(line.substring(startInd, ind));			

		Rating r = new Rating(userId, movieId, rating);

		for(int i = 0; i < movies.size(); i++) {
			if(movieId == movies.get(i).getId()) {
				movies.get(i).addRating(r);
				break;
			}
		}

		return r;
	}

	public void parseLinks(String line, ArrayList<Movie> movies) {
		int imdbId = -1, movieId = -1, tmdbId = -1;

		int startInd = 0;
		int ind = 0;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}

		movieId = Integer.parseInt(line.substring(startInd, ind));

		startInd = ind + 1;
		for(int i = startInd; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				ind = i;
				break;
			}
		}
		if(!line.substring(startInd, ind).equals("")) {
			imdbId = Integer.parseInt(line.substring(startInd, ind));
		} else {
			return;
		}

		startInd = ind + 1;

		startInd = ind + 1;
		if(startInd != line.length()) {
			for(int i = startInd; i < line.length(); i++) {
				if(i == line.length()-1) {
					ind = i;
					break;
				}
			}

			if(!line.substring(startInd, ind).equals("")) {
				tmdbId = Integer.parseInt(line.substring(startInd, ind));		
			} else {
				return;
			}
		}

		for(int i = 0; i < movies.size(); i++) {
			if(movieId == movies.get(i).getId()) {
				movies.get(i).setImdbId(imdbId);
				movies.get(i).setTmdbId(tmdbId);
			}
		}

	}


	private Genre parseGenre(String str) {
		if(str.equalsIgnoreCase("ACTION"))
			return Genre.ACTION;
		else if(str.equalsIgnoreCase("ADVENTURE"))
			return Genre.ADVENTURE;
		else if(str.equalsIgnoreCase("ANIMATION"))
			return Genre.ANIMATION;
		else if(str.equalsIgnoreCase("CHILDREN"))
			return Genre.CHILDRENS;
		else if(str.equalsIgnoreCase("COMEDY"))
			return Genre.COMEDY;
		else if(str.equalsIgnoreCase("CRIME"))
			return Genre.CRIME;
		else if(str.equalsIgnoreCase("DOCUMENTARY"))
			return Genre.DOCUMENTARY;
		else if(str.equalsIgnoreCase("DRAMA"))
			return Genre.DRAMA;
		else if(str.equalsIgnoreCase("FANTASY"))
			return Genre.FANTASY;
		else if(str.equalsIgnoreCase("FILM-NOIR"))
			return Genre.FILM_NOIR;
		else if(str.equalsIgnoreCase("HORROR"))
			return Genre.HORROR;
		else if(str.equalsIgnoreCase("IMAX"))
			return Genre.IMAX;
		else if(str.equalsIgnoreCase("MUSICAL"))
			return Genre.MUSICAL;
		else if(str.equalsIgnoreCase("MYSTERY"))
			return Genre.MYSTERY;
		else if(str.equalsIgnoreCase("ROMANCE"))
			return Genre.ROMANCE;
		else if(str.equalsIgnoreCase("SCI-FI"))
			return Genre.SCI_FI;
		else if(str.equalsIgnoreCase("THRILLER"))
			return Genre.THRILLER;
		else if(str.equalsIgnoreCase("WAR"))
			return Genre.WAR;
		else if(str.equalsIgnoreCase("WESTERN"))
			return Genre.WESTERN;
		else
			return Genre.NO_GENRE;

	}

}
