package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.Item;
import entity.Item.ItemBuilder;



public class MySQLConnection {
	
	private Connection conn;

	//0 建立连接
	public MySQLConnection() {
		try {
			//官方推荐写法，new 了一个 driverManager
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			//然后第二步用这个driverManager来建立连接
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//-0 断开连接
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//存：  存储favorite
	public void setFavoriteItems(String userId, Item item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		//保证item一定存在
		saveItem(item);
		String sql = "INSERT INTO history (user_id, item_id) VALUES (?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, item.getItemId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//删： 删除 favorite
	public void unsetFavoriteItems(String userId, String itemId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
		// ? 占位 比%s好，可以去掉整段的逻辑，防止 userId = 1111 OR 1=1, 逻辑成立，删除所有
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, itemId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//存： 存储item
	public void saveItem(Item item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		//这里的ignore就可以排除重复存储 -- 用primary key查重 -- 这里是itemId
		String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getName());
			statement.setString(3, item.getAddress());
			statement.setString(4, item.getImageUrl());
			statement.setString(5, item.getUrl());
			statement.executeUpdate();
			
			//因为keywords是多个string，写在上面麻烦，就单独弄了个table
			sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
            statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			for (String keyword : item.getKeywords()) {
				statement.setString(2, keyword);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	//查： 用 userId 查询 favItem ID
	public Set<String> getFavoriteItemIds(String userId) {
		//没有连上， 返回空set
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}

		Set<String> favoriteItems = new HashSet<>();

		try {
			//request
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			//response
			ResultSet rs = statement.executeQuery();
			//这个next相当于一个iterator, 就是不断找下一个
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return favoriteItems;
	}
	
	//查： 用 userId 查询 favItem
	public Set<Item> getFavoriteItems(String userId) {
		//没有连上， 返回空set
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> favoriteItemIds = getFavoriteItemIds(userId);

		String sql = "SELECT * FROM items WHERE item_id = ?";
		try {
			//request
			PreparedStatement statement = conn.prepareStatement(sql);
			for (String itemId : favoriteItemIds) {
				statement.setString(1, itemId);
				//response
				ResultSet rs = statement.executeQuery();

				ItemBuilder builder = new ItemBuilder();
				//因为只有一个，可以if，不用while，一样的
				if (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setKeywords(getKeywords(itemId));
					favoriteItems.add(builder.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	//查： 用 itemId 查询 keywords
	public Set<String> getKeywords(String itemId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		Set<String> keywords = new HashSet<>();
		String sql = "SELECT keyword from keywords WHERE item_id = ? ";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String keyword = rs.getString("keyword");
				keywords.add(keyword);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return keywords;
	}
	
	public String getFullname(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return "";
		}
		String name = "";
		String sql = "SELECT first_name, last_name FROM users WHERE user_id = ? ";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return name;
	}

	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public boolean addUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			statement.setString(3, firstname);
			statement.setString(4, lastname);

			return statement.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
