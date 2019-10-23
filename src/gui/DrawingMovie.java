package gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import data.Movie;
import data.Tag;
import io.FileIO;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class DrawingMovie {

	private Movie movie;
	private PImage coverArt;

	public DrawingMovie(Movie m) {
		this.movie = m;
		coverArt = null;
	}

	public void draw(PApplet drawer, float x, float y, float width, float height) {
		if (movie != null) {
			if (coverArt != null) {
				drawer.image(coverArt, x, y,width,height);
			}
		}
		drawer.stroke(0);
		drawer.noFill();
		drawer.rect(x, y, width, height);
		ArrayList<Tag> tags = movie.getTags();
		if(tags.size() > 0) {
			drawer.textAlign(PConstants.CENTER);
			String tag = tags.get(0).getTag();
			drawer.text("Tagged: " + tag, x + width/2, y + height + 30);
		}
			
	}


	public Movie getMovie() {
		return movie;
	}
	
	public void downloadArt(PApplet drawer) {

		Thread downloader = new Thread(new Runnable() {


			@Override
			public void run() {

				String pageURLString = "http://www.imdb.com/title/tt0" + movie.getImdbId() +"/";
				Scanner scan = null;

				try {
					URL pageURL = new URL(pageURLString);
					InputStream stream = pageURL.openStream();
					scan = new Scanner(stream);

					String fileData = "";
					while(scan.hasNext()) {
						String line = scan.nextLine();
						fileData += line + FileIO.LINE_SEPARATOR;
					}
					int index = fileData.indexOf("https://ia.media-imdb.com/images/M/");
					
					String imageURL = fileData.substring(index, fileData.indexOf(".jpg", index)) + ".jpg";
					
					coverArt = drawer.loadImage(imageURL);
				} catch (IOException e){
					pageURLString = "http://www.imdb.com/title/tt00" + movie.getImdbId() +"/";
					try {
						URL pageURL = new URL(pageURLString);
						InputStream stream = pageURL.openStream();
						scan = new Scanner(stream);

						String fileData = "";
						while(scan.hasNext()) {
							String line = scan.nextLine();
							fileData += line + FileIO.LINE_SEPARATOR;
						}
						int index = fileData.indexOf("https://ia.media-imdb.com/images/M/");
						
						String imageURL = fileData.substring(index, fileData.indexOf(".jpg", index)) + ".jpg";
						
						coverArt = drawer.loadImage(imageURL);
					}
					catch(IOException e1){
						
					}
				} 
				finally {
					if(scan != null)
						scan.close();
				}
				
			}

		});

		downloader.start();

	}


}
