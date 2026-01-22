package main.Crawler;

/* --
COMP4321 Lab2 Exercise
Student Name:
Student ID:
Section:
Email:
*/
import java.net.URLConnection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.util.ParserException;

import org.htmlparser.beans.LinkBean;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Crawler {
	private String url;

	public Crawler(String _url) {
		url = _url;
	}

	/**
	 * extract the children link in the url
	 * 
	 * @return Vector<String> of URL
	 * @throws ParserException cannor Parse the url
	 */
	public Vector<String> extractLinks() throws ParserException {
		// extract links in url and return them
		LinkBean lbean = new LinkBean();
		lbean.setURL(url);
		URL[] link = lbean.getLinks();
		Vector<String> content = new Vector<>();
		for (URL i : link) {
			content.add(i.toString());
		}
		return content;
	}

	/**
	 * extract the Last Modified Day in the url
	 * 
	 * @return a long, need use util.Date to transfer to date
	 */
	public long getWebLastModifiedDay() {
		long lastModifiedDate = -1;
		try {
			URL url_url = new URL(this.url);
			HttpURLConnection connection = (HttpURLConnection) url_url.openConnection();
			connection.setRequestMethod("HEAD");

			lastModifiedDate = connection.getLastModified(); // 0 mean unavailable!!!
			connection.disconnect();

		} catch (IOException e) {
		}
		return lastModifiedDate;
	}

	/**
	 * extract the Title of the url
	 * 
	 * @return the title of the page
	 */
	public String getTitle() {
		String st = new String();
		try {
			Document document = Jsoup.connect(url).get();
			st = document.title();
		} catch (IOException e) {
			st = "Title Not Available";
		}
		return st;
	}

	/**
	 * extract the word of the url
	 * jsoup is use to prevent unprecedented symbol in original function e.g. | or "
	 * 
	 * @return vector<String> of word
	 */
	public Vector<String> newExtractWords() throws ParserException {
		Vector<String> vec = new Vector<>();
		try {
			Document document = Jsoup.connect(url).get();
			String text = document.text();
			Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				vec.addElement(matcher.group());
			}

		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
		return vec;
	}

	/**
	 * self-explanatory
	 * 
	 * @return the url
	 */
	public String getURL() {
		return url;
	}

	public int getSize() {
		int size = -1;
		try {
			URL url = new URL(this.url);
			URLConnection connection = url.openConnection();
			size = connection.getContentLength();

		} catch (IOException ignored) {

		}
		return size;
	}

}
