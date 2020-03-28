package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Item {
	//1.private field (read only) 只读的immutable，没有public setter，只有一次赋值机会， 就是调用构造函数的时候
	private String itemId;
	private String name;
	private String address;
	private Set<String> keywords;
	private String imageUrl;
	private String url;
	
	//3.constructor 是隐藏的，method是public 不同人想要的构造顺序/内容不一样，所以要用builder pattern
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
	
	
	//用构造函数的话，input的顺序不能变，而且都要给才能construct，用builder pattern就不用
	//有一些variable不能改的话immutable，就要用builder pattern
	//都可以改的话，可以用空constructor，然后public setter
	//inner class 才能调用item的build方法
	//static： 静态了，不依赖于对象了
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
		
		//最后返回item对象
		public Item build() {
			return new Item(this);
		}
	}
}


// ItemBuilder = new ItemBuilder();
// builder.setItemId("1");
//。。。
// Item item = builder.build();
