package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class MonkeyLearnClient {
	
	private static final String API_KEY = "0645c4f87c3e4bdd35c82e73c070fb4ddc514053";
	
	//java as main, to test, run as java application
//	public static void main( String[] args ) throws MonkeyLearnException {
//		
//		//test
//		String[] textList = {
//				"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit¡¯s full-body look.", };
//		
//		List<List<String>> keywords = extractKeywords(textList);
//		for (List<String> ws : keywords) {
//			for (String w: ws) {
//				System.out.println(w);
//			}
//			System.out.println();
//		}
//	}
	
	// send request
	public static List<List<String>> extractKeywords(String[] text) {
		if (text == null || text.length == 0) {
			return new ArrayList<>();
		}

		// Use the API key from your account
		MonkeyLearn ml = new MonkeyLearn(API_KEY);

		// Use the keyword extractor
		ExtraParam[] extraParams = { new ExtraParam("max_keywords", "3") };
		MonkeyLearnResponse response;
		try {
			response = ml.extractors.extract("ex_YCya9nrn", text, extraParams);
			JSONArray resultArray = response.arrayResult;
			return getKeywords(resultArray);
			
		} catch (MonkeyLearnException e) {// it¡¯s likely to have an exception
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	
	// get the json array as respond, convert to List<List<String>>
	private static List<List<String>> getKeywords(JSONArray mlResultArray) {
		List<List<String>> topKeywords = new ArrayList<>();
		// Iterate the result array and convert it to our format.
		for (int i = 0; i < mlResultArray.size(); ++i) {
			List<String> keywords = new ArrayList<>();
			JSONArray keywordsArray = (JSONArray) mlResultArray.get(i);
			for (int j = 0; j < keywordsArray.size(); ++j) {
				JSONObject keywordObject = (JSONObject) keywordsArray.get(j);
				// We just need the keyword, excluding other fields.
				String keyword = (String) keywordObject.get("keyword");
				keywords.add(keyword);

			}
			topKeywords.add(keywords);
		}
		return topKeywords;
	}
}
