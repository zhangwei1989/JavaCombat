<head>
<jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
	<title>My Home Page</title>
    <style>
      .bd-placeholder-img {
        font-size: 1.125rem;
        text-anchor: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
      }

      @media (min-width: 768px) {
        .bd-placeholder-img-lg {
          font-size: 3.5rem;
        }
      }
    </style>
</head>
<body>
	<div class="container">
		<form class="form-signin">
			<h1 class="h3 mb-3 font-weight-normal">注册</h1>
			<label for="inputUsername" class="sr-only">请输出用户名</label>
			 <input name="username"
				type="text" id="inputUsername" class="form-control"
				placeholder="请输入用户名" required autofocus>
            <label for="inputPassword" class="sr-only">Password</label>
				<input name="password"
				type="text" id="inputPassword" class="form-control"
				placeholder="请输入密码" required>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
			<p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
		</form>
	</div>
</body>