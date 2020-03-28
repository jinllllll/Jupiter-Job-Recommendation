package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class GitHubClient {
	
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	public List<Item> search(double lat, double lon, String keyword) {
		//prepare HTTP request parameter
		if (keyword==null) {
			keyword = DEFAULT_KEYWORD;
		} try {
			//corner case: encode the keyword, in case there's " "
			keyword=URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = String.format(URL_TEMPLATE, keyword, lat ,lon);
		
		//send HTTP request
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try {
			//this is what we got from sending get request to gitHub API
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			
			//if not successful just end it
			if (response.getStatusLine().getStatusCode() !=200) {
				return new ArrayList<>();
			}
			
			//if the response entity is null just end it
			HttpEntity entity = response.getEntity();
			if (entity==null) {
				return new ArrayList<>();
			}
			//return the data we got from github
			//use this inputStreamReader to read the content as stream -- letter by letter is too slow, read all at one time may result error
			//buffer reader, read per line, not slow, not too risky
			BufferedReader reader = new BufferedReader (new InputStreamReader(entity.getContent()));
			StringBuilder responseBody = new StringBuilder();
			String line = null;
			//not null -> append
			while ((line=reader.readLine())!=null) {
				responseBody.append(line);
			}
			JSONArray array = new JSONArray(responseBody.toString());
			return getItemList(array);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get HTTP response body
		return new ArrayList<>();
	}
	
	private List<Item> getItemList (JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		
		// get the description from description using the MonkeyLearn API(client)
		List<String> descriptionList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			// We need to extract keywords from description since GitHub API
			// doesn't return keywords.
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			if (description.equals("") || description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			} else {
				descriptionList.add(description);
			}	
		}

		// We need to get keywords from multiple text in one request since
		// MonkeyLearnAPI has limitation on request per minute.
		List<List<String>> keywords = MonkeyLearnClient
				.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));
		
		
		
		for (int i=0; i<array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			
			builder.setItemId(getStringFieldOrEmpty(object, "id"));
			builder.setName(getStringFieldOrEmpty(object, "title"));
			builder.setAddress(getStringFieldOrEmpty(object, "location"));
			builder.setUrl(getStringFieldOrEmpty(object, "url"));
			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
			
			Set<String> set = new HashSet<>(keywords.get(i));
			builder.setKeywords(set);
			
			Item item = builder.build();
			itemList.add(item);
			
		}
		return itemList;
	}
	
	private String getStringFieldOrEmpty (JSONObject obj, String field) {
		return obj.isNull(field)? "" : obj.getString(field);
	}

}
