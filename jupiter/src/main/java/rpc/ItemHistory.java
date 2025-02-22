package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;

public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ItemHistory() {
        super();
    }

    //读取 fav
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// session validation
		HttpSession session = request.getSession();
		if (session==null) {
			response.setStatus(403);
			return;
		}
		
		//request里拿到userid
		String userId = request.getParameter("user_id");
		
		//建立连接
		MySQLConnection connection = new MySQLConnection();
		//找到favItems
		Set<Item> items = connection.getFavoriteItems(userId);
		//断开
		connection.close();
		
		//从结果里放进response -- 一个JSON Array
		JSONArray array = new JSONArray();
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			obj.put("favorite", true);
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
	}

	//存fav
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		if (session==null) {
			response.setStatus(403);
			return;
		}
		
		//1.create connection to MySQL
		MySQLConnection connection = new MySQLConnection();
		//2.read from request
		JSONObject input = RpcHelper.readJSONObject(request);
		String userId = input.getString("user_id");
		Item item = RpcHelper.parseFavoriteItem(input.getJSONObject("favorite"));
		
		//3. save to MySQL
		connection.setFavoriteItems(userId, item);
		connection.close();
		//4. 用rpchelper写个response
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		}

	//删fav
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		if (session==null) {
			response.setStatus(403);
			return;
		}
		
		//1.create connection to MySQL
		MySQLConnection connection = new MySQLConnection();
		//2.read from request
		JSONObject input = RpcHelper.readJSONObject(request);
		String userId = input.getString("user_id");
		Item item = RpcHelper.parseFavoriteItem(input.getJSONObject("favorite"));
		
		//3. save to MySQL
		connection.unsetFavoriteItems(userId, item.getItemId());
		connection.close();
		//4. 用rpchelper写个response
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	}

}
