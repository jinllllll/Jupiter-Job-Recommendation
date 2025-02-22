        var user_id = '1111';
		var user_fullname = 'John';
		var lng = -122.08;
		var lat = 37.38;


// entry 1 这里是入口，从这里开始
    function init(){
        	
    	
    	//1.0点击登录就显示登录部分
    	document.querySelector('#login-form-btn').addEventListener('click', onSessionInvalid);
    	
    	//1.0点击注册就显示注册部分
    	document.querySelector('#register-form-btn').addEventListener('click', showRegisterForm);
    	
    	// 2.0点击login botton的listener
    	document.querySelector('#login-btn').addEventListener('click', login);
        	
    	// 进行分组
		validateSession();
	}
	
	//1.1 分组 --- 为了分组隐藏/显示
	function validateSession(){
		onSessionInvalid();
	}
	
	// 1.2 这个是login 组
	// show login form
	function onSessionInvalid(){
	    var loginForm = document.querySelector('#login-form');
	    var registerForm = document.querySelector('#register-form');
	    var itemNav = document.querySelector('#item-nav');
	    var itemList = document.querySelector('#item-list');
	    var avatar = document.querySelector('#avatar');
	    var welcomeMsg = document.querySelector('#welcome-msg');
	    var logoutBtn = document.querySelector('#logout-link');
	    
	    hideElement(itemNav);
	    hideElement(itemList);
	    hideElement(avatar);
	    hideElement(logoutBtn);
	    hideElement(welcomeMsg);
	    hideElement(registerForm);

	    clearLoginError();
	    showElement(loginForm);
	}
	
	// 2.0 分组显示结束后，这里是handle登录的事情
	function login() {
	    var username = document.querySelector('#username').value;
	    var password = document.querySelector('#password').value;
	    password = md5(username + md5(password));
	    // 用了md5加密，在index.html里引用了

	    // The request parameters
	    var url = './login';
	    var req = JSON.stringify({
	      user_id : username,
	      password : password,
	    });

	    ajax('POST', url, req,
	      // successful callback
	      function(res) {
	        var result = JSON.parse(res);

	        // successfully logged in
	        if (result.status === 'OK') {
	        	   console.log(result);
	        }
	      },
	      // error
	      function() {
	        showLoginError();
	      }
	      );
	  }

	
	// 1.2 - 2 这个是register组 show register form
	function showRegisterForm() {
	        var loginForm = document.querySelector('#login-form');
	        var registerForm = document.querySelector('#register-form');
	        var itemNav = document.querySelector('#item-nav');
	        var itemList = document.querySelector('#item-list');
	        var avatar = document.querySelector('#avatar');
	        var welcomeMsg = document.querySelector('#welcome-msg');
	        var logoutBtn = document.querySelector('#logout-link');

	        hideElement(itemNav);
	        hideElement(itemList);
	        hideElement(avatar);
	        hideElement(logoutBtn);
	        hideElement(welcomeMsg);
	        hideElement(loginForm);
	    
	        clearRegisterResult();
	        showElement(registerForm);
	  }
	    
      init();
