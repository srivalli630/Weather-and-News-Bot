import org.jibble.pircbot.*;
import java.net.*;
import java.util.*;
import java.io.*;
import com.google.gson.*;
import java.lang.*;


public class MyBot extends PircBot {
    
	//Create my bot
    public MyBot() {
        this.setName("Beemo");
    }
    
    //variables to send to API calls
    public static String city;
    public static String searchWord;
 
    //List of keywords that trigger API Calls /Responses
    public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
    	
    	//returns the time
        if (message.contains("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": The time is now " + time);
        }
        
        //says goodbye
        if (message.contains("bye") && message.contains("Beemo")) {
            sendMessage(channel, sender + "Bye everyone!");
            disconnect();
        }

        //Returns the temperature, max temperature, min temperature, and general weather in city
        if (message.contains("eather")) {
        	String [] words = message.split(":");
        	String roughCity = words[1].trim();
        	city = roughCity.replaceAll(" ", "%20"); //%20 is the character in JSON for space
        	sendMessage(channel, sender + ": " + MyBot.getWeather());
        	
        }
        
        //Returns top 10 headlines containing user specified keyword
        if (message.contains("ews")) {
        	//Get the WORD after the colon
        	String [] words = message.split(":");
        	String roughWord = words[1].trim();
        	searchWord = roughWord.replaceAll(" ", "%20");
        	
        	//Calls the newsAPI function that makes an API call
        	String newsResults = MyBot.getNews();
        	String[] lines = newsResults.split("\n");
        	//if less than 10 titles
        	if (lines.length < 12) {
        		for (int i = 0; i < lines.length; i++)
            	{
            		if (i == 0) {
            			sendMessage(channel, sender + ": " + lines[i]);
            		}
            		else {
            			sendMessage(channel, lines[i]);
            		}
            	}
        	}
        	//if greater than 10 titles
        	else {
        		for (int i = 0; i < 12; i++)
            	{
            		if (i == 0) {
            			sendMessage(channel, sender + ": " + lines[i]);
            		}
            		else {
            			sendMessage(channel, lines[i]);
            		}
            	}
        	}
        	
        	
        }
        
        
    }
    
    //Opening message when the bot joins the channel
    public void onJoin(String channel, String sender, String login, String hostname) {
    	sendMessage(channel, "Hello I am Beemo!");
    	sendMessage(channel, "You can ask me about the Weather by typing \"Weather : [city name]\"");
    	sendMessage(channel, "You can search news articles by typing \"News: [keyword]\"");
    	
    }
    
    
  //************WEATHER API***********************
    
    //function that converts temperature from Kelvin to Fahrenheit
    public static double convertToF(double KelvinTemp) {
    	
    	double FahTemp = (KelvinTemp - 273.15) * (9.0/5.0) + 32.0;
    	return FahTemp;
    }
    
    
    public static String WeatherAPI(String cityName) throws Exception {
    	//Make link to API call
		String tokenString = "APPID=01d4a0ad5b4da94064c015e8f1bc04bf";
		String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=" 
		+ cityName.toLowerCase() + "&" + tokenString;
		
		// create URL object
		URL url = new URL(weatherURL);
		
		// create HTTPURLConnection object
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		//create a GET request
		conn.setRequestMethod("GET");
		
		// create a BufferReader to read connection
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		// convert BufferReader to String and store in 'result"
		String result = rd.readLine();

		// parse the JSON with GSON library
		Gson gson = new Gson();
		Response response = gson.fromJson(result, Response.class);
		
		//Convert from Kelvin to Fahrenheit
		double maxTempK = response.getMain().getTemp_max();
		double minTempK = response. getMain().getTemp_min();
		double nowTempK = response.getMain().getTemp();
		
		//Make temperatures integers
		int maxTempF = (int) convertToF(maxTempK);
		int minTempF = (int) convertToF(minTempK);
		int nowTempF = (int) convertToF(nowTempK);
		
		//Create output string
		result = "The weather in " + response.getName() + "," + response.getSys().getCountry() + " is going to be " +
				response.getWeather().get(0).getMain() + " with a high of " + 
				maxTempF + " F (in Fahrenheit) and a low of " + 
				minTempF + " F (in Fahrenheit). The temperature right now is " + nowTempF + " F.";
		
		//Return the result
		return result;
	}
	
    //Contains an object held in JSON file
	class WeatherMain {
		
		//Variables
		Double temp;
		Double temp_min;
		Double temp_max;
		int zip;
		
		//Get and Set functions
		public Double getTemp() {
			return temp;
		}
		public void setTemp(Double temp) {
			this.temp = temp;
		}
		
		public Double getTemp_max() {
			return temp_max;
		}
		public void setTemp_max(Double temp_max) {
			this.temp_max = temp_max;
		}

		public Double getTemp_min() {
			return temp_min;
		}
		public void setTemp_min(Double temp_min) {
			this.temp_min = temp_min;
		}
	}

	//Another object in JSON
	class Sys{
		
		//Variables
		String country;
		
		//Get and Set functions
		public String getCountry() {
			return country;
		}
		
		public void setCountry(String country) {
			this.country = country;
		}
	}
	
	//Main class that JSON puts variables into
	class Response {
		
		//Variables
		String name;
		WeatherMain main;
		String base;
		WeatherMain temp;
		List<Weather> weather;
		Sys sys;
		
		//get and set functions
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public String getBase() {
			return base;
		}

		public void setBase(String base) {
			this.base = base;
		}

		public WeatherMain getMain() {
			return main;
		}

		public void setMain(WeatherMain main) {
			this.main = main;
		}

		public WeatherMain getTemp() {
			return temp;
		}
		public void setTemp(WeatherMain temp) {
			this.temp = temp;
		}
		
		public List<Weather> getWeather() {
			return weather;
		}
		public void setWeather(List<Weather> weather) {
			this.weather = weather;
		}
		
		public Sys getSys() {
			return sys;
		}
		public void setSys(Sys sys) {
			this.sys = sys;
		}
	}
	
	//Class that holds the weather (sunny/clear/misty/rainy/etc)
	public class Weather {
		
		//Variables
		String main;

		//get and set functions
		public String getMain() {
			return main;
		}

		public void setMain(String main) {
			this.main = main;
		}
	}
	
	// function that returns the current weather
	public static String getWeather() {
		//If you can't find it:
		String answer = "Couldn't find the weather for this city :(";
		try {
			answer = MyBot.WeatherAPI(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
    
	//************ END WEATHER API***********************
	
	
	//************NEWS API***********************
	
	public static String NewsAPI(String word) throws Exception {
		String apiKey = "apiKey=e99f02edca374ef49390a003be475145";
		String newsURL = "http://newsapi.org/v2/everything?q=" 
		+ word + "&from=2020-07-05&sortBy=popularity&" + apiKey;
		
		// create the URL object
		URL url = new URL(newsURL);
		
		// create an HTTPURLConnection object
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		// use that object to create a GET request
		conn.setRequestMethod("GET");
		
		// create a BufferReader to read the connection inputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		// convert BufferReader to String and store in a result variable
		String result = rd.readLine();

		// parse the JSON
		Gson gson = new Gson();
		
		//NEED TO MAKE A CLASS THAT HOLDS NEWS INFO
		newsResponse response = gson.fromJson(result, newsResponse.class);
		
		//Response response = gson.fromJson(result, Response.class);
		
		//Strings to output to the user
		String readableWord = searchWord.replaceAll("%20", " ");
		String totalResults1 = "There are " + response.getTotalResults() + " results \n";
		String header = "Here are the titles of the top 10 articles with " + "\"" + readableWord + "\"\n";
		
		//Store the total results sentence in the result variable
		result = totalResults1;
		
		//loop through array and add titles to array
		
		//if there is no results, output the number of results
		if (response.getTotalResults() == 0) {
			return result;
		}
		//if there are less than 10 results, display all the results
		else if (response.getTotalResults() < 10) {
			
			//add header to result
			result += header;
			
			//add titles to the result
			for (int i = 0; i < response.getArticles().size(); i++) {
				String publishedAt = response.getArticles().get(i).getPublishedAt();
				String title1 = "\"" + response.getArticles().get(i).getTitle() + "\" written on " + response.getArticles().get(i).getDate(publishedAt) + "\n";
				result += title1;
			}
		}
		//if there are more than 10 results, display only 10 of them
		else {
			
			//add header to result
			result += header;
			
			//add titles to the string
			for (int i = 0; i < 10; i++) {
				String publishedAt = response.getArticles().get(i).getPublishedAt();
				String title1 = "\"" + response.getArticles().get(i).getTitle() + "\" written on " + response.getArticles().get(i).getDate(publishedAt) + "\n";
				result += title1;
			}
			
		}
		
		//return result
		return result;
	}
	
	//Main class where JSON is parsed into
	class newsResponse {
		int totalResults;
		List<Article> articles;
		
		public int getTotalResults() {
			return totalResults;
		}
		
		public void setTotalResults(int totalResults) {
			this.totalResults = totalResults;
		}
		
		public List<Article> getArticles(){
			return this.articles;
		}
		
		public void setArticles(List<Article> articles) {
			this.articles = articles;
		}
		
	}
	
	//a class which is the template for the articles in List<> articles
	class Article{
		
		//Variables
		String author;
		String title;
		String publishedAt;
		
		//get and set functions
		public String getAuthor() {
			return author;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getPublishedAt() {
			return publishedAt;
		}
		
		public void setAuthor(String author) {
			this.author = author;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public void setPublishedAt(String publishedAt) {
			this.publishedAt = publishedAt;
		}
		
		//function to get readable date
		public String getDate(String publishedAt) {
			//2020-07-05T01:33:52Z
			String words[] = publishedAt.split("T");
			String justDate[] = words[0].split("-");
			String month = justDate[1];
			String day = justDate[2];
			String year = justDate[0];
			String date = month + "/" + day + "/" + year;
			return date;
		}
	}
	
	
	//Let you know if there is an error
	public static String getNews() {
		String answer = "Couldn't find any news for this search term :(";
		try {
			answer = MyBot.NewsAPI(searchWord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	//************END NEWS API***********************
}