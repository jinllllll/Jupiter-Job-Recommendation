package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Item {
	//1.private field (read only) ֻ����immutable��û��public setter��ֻ��һ�θ�ֵ���ᣬ ���ǵ��ù��캯����ʱ��
	private String itemId;
	private String name;
	private String address;
	private Set<String> keywords;
	private String imageUrl;
	private String url;
	
	//3.constructor �����صģ�method��public ��ͬ����Ҫ�Ĺ���˳��/���ݲ�һ��������Ҫ��builder pattern
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.address = builder.address;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.keywords = builder.keywords;
	}
	
	//2. public getters
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getKeywords() {
		return keywords;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("item_id", itemId);
		obj.put("name", name);
		obj.put("address", address);
		obj.put("keywords", new JSONArray(keywords));
		obj.put("image_url", imageUrl);
		obj.put("url", url);
		return obj;
	}
	
	
	//�ù��캯���Ļ���input��˳���ܱ䣬���Ҷ�Ҫ������construct����builder pattern�Ͳ���
	//��һЩvariable���ܸĵĻ�immutable����Ҫ��builder pattern
	//�����ԸĵĻ��������ÿ�constructor��Ȼ��public setter
	//inner class ���ܵ���item��build����
	//static�� ��̬�ˣ��������ڶ�����
	public static class ItemBuilder {
		private String itemId;
		private String name;
		private String address;
		private String imageUrl;
		private String url;
		private Set<String> keywords;

		public void setItemId(String itemId) {
			this.itemId = itemId;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setKeywords(Set<String> keywords) {
			this.keywords = keywords;
		}
		
		//��󷵻�item����
		public Item build() {
			return new Item(this);
		}
	}
}


// ItemBuilder = new ItemBuilder();
// builder.setItemId("1");
//������
// Item item = builder.build();
