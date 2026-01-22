package main.StopStem;

import java.io.*;
import java.util.HashSet;

import main.Porter.Porter;

public class StopStem {
	private Porter porter;
	private HashSet<String> stopWords;

	public boolean isStopWord(String str) {
		return stopWords.contains(str);
	}

	public StopStem(String str) {
		super();
		String currentDir = System.getProperty("user.dir");
		if (currentDir.endsWith(".base")) {
			str = "../stopwords.txt";
		} else {
			str = "./src/main/java/helper/stopwords.txt";
		}

		porter = new Porter();
		stopWords = new HashSet<String>();
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(str);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			System.out.println("##################################################");
			System.out.println("##################################################");
			System.out.println("stopwords.txt not found!,add it to the directories");
			System.out.println("##################################################");
			System.out.println("##################################################");
		}
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				stopWords.add(line);
			}
		} catch (IOException e) {
			System.out.println("IO Exception");
		}

	}

	/**
	 *
	 * @param str
	 * @return a stemmed word, empty string if get nothing after stemmed(stopped)
	 */
	public String stem(String str) {
		return porter.stripAffixes(str);
	}

}
