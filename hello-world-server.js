var http = require('http'),
	express = require('express'),
	app = express();

var server = http.createServer(app).listen(process.env.PORT || 5000);
var io = require('socket.io').listen(server);

var bodyParser = require('body-parser');
app.use(bodyParser());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
	  extended: true
	})); 

app.get('/', function (req, res) {
    res.send('hi');
    console.log('\n Hi');
});

app.post('/signUp', function(req, res) {
	req.accepts('application/json');
	
	var id = req.body.id;
	var pw = req.body.pw;
	var name = req.body.name;
	var birth = req.body.birth;
	var mail = req.body.mail;
	
	console.log('회원가입');
	console.info('id = ' + id);
	console.info('pw = ' + pw);
	console.info('name = ' + name);
	console.info('mail = ' + mail);
	console.info('birth = ' + birth);
	
	if (!id || !pw || !name || !mail || !birth) {
		console.error('NullPointerException');
		res.send({
			'code':303
		});
		return;
	}
	
	
	mySqlConnection.query('select * from user_auth where user_id = "' + id + '";', function(err, result) {
		if (err) {
			// 아이디 중복검사 에러 
			console.error('회원가입 아이디 중복검사 에러 = ' + err);
			res.send({
				'code':304
			});
		} else if (result[0]) {
			// 아이디 중복 에러
			res.send({
				'code':305
			});
			console.error('회원가입 아이디 중복 : ' + err);
		} else {
			// 아이디 중복 없을 때
			mySqlConnection.query('select * from user_auth where user_mail = "' + mail + '";', function(err, emailResult) {
				if (err) {
					// 이메일 중복 검사 에러
					res.send({
						'code':306
					});
					console.error('회원가입 이메일 중복검사 에러 : ' + err);
				} else if (emailResult[0]) {
					// 이메일 중복 에러
					res.send({
						'code':307
					});
					console.error('회원가입 이메일 중복 : ' + err);
				} else {
					// 이메일 중복 없을 때 
					var input = {
							user_id : id,
							user_pw : pw,
							user_name : name,
							user_mail : mail,
							user_birth : birth,
							user_socket : null
					};
					
					
					mySqlConnection.query('insert into user_auth set ?', input, function(err, signUpResult) {
						if (err) {
							// db 입력 에러 
							res.send({
								'code':308
							});
							console.error('DB입력 에러 : ' + err);
						} else {
							// 성공
							res.send({
								'code':200,
								'id':id
							});
							console.log('회원가입 성공');
						}
					});
				}
			});
		}
	});
});


app.post('/findId', function(req, res){
	var mail = req.body.mail;
	var name = req.body.name;
	var birth = req.body.birth;
	
	console.log('아이디 찾기');
	console.info('mail = ' + mail);
	console.info('name = ' + name);
	console.info('birth = ' + birth);
	
	if (!mail || !name || !birth) {
		// 값 누락
		res.send({
			'code':309
		});
		console.error('아이디 찾기 값 누락');
	} else {
		mySqlConnection.query('select user_id from user_auth where user_mail = "' + mail + '" and user_name = "' + name + '" and user_birth = "' + birth + '";', function(err, result) {
			if (err) {
				// 아이디 찾기 에러
				res.send({
					'code':310
				});
				console.error('아이디 찾기 에러 = ' + err);
			} else if (!result[0]) {
				// 아무 값도 없을 때
				res.send({
					'code':311
				});
				console.error('일치하는 아이디 없음');
			} else {
				// 일치하는 아이디 발견
				res.send({
					'code':200,
					'id':result[0].user_id
				});
				console.log('일치하는 아이디 발견 = ' + result[0].user_id);
			}
		});
	}
});


// 비밀번호 조
app.post('/findPw', function(req, res) {
	var id = req.body.id;
	var mail = req.body.mail;
	var name = req.body.name;
	
	console.log('비밀번호 조회');
	console.info('id = ' + id);
	console.info('mail = ' + mail);
	console.info('name = ' + name);
	
	if (!id || !mail || !name) {
		// 값 누락
		console.error('비밀번호 조 값 누락');
		res.send({
			'code':312
		});
	} else {
		mySqlConnection.query('select * from user_auth where user_id = "' + id + '" and user_mail = "' + mail + '" and user_name = "' + name + '";', function (err, result) {
			if (err) {
				console.error('비밀번호 조회 에러 = ' + err);
				res.send({
					'code':313
				});
			} else if (result[0]) {
				// 일치하는 계정이 있으면
				res.send({
					'code':200,
					'id':id
				});
				console.log('비밀번호 변경 승인');
			} else {
				// 일치하는 계정이 없으면
				res.send({
					'code':314
				});
				console.error('일치하는 계정이 없음');
			}
		});
	}
});


// 비밀번호 변경
app.post('/updatePw', function(req, res) {
	var id = req.body.id;
	var pw = req.body.pw;
	
	console.log('비밀번호 변경');
	console.info('id = ' + id);
	console.info('pw = ' + pw);
	
	if (!id || !pw) {
		// 값 누락
		console.error('비밀번호 변경 값 누락');
		res.send({
			'code':315
		});
	} else {
		mySqlConnection.query('update user_auth set user_pw = "' + pw + '" where user_id = "' + id + '";', function(err ,result) {
			if (err) {
				console.error('비밀번호 변경 에러 = ' + err);
				res.send({
					'code':316
				})
			} else {
				console.log('비밀번호 변경');
				res.send({
					'code':200
				});
			}
		});
		
	}
});


// 메세지 보내기
app.post('/sendMsg', function(req, res) {
	var id = req.body.id;
	var num = req.body.num;
	
	console.log('메세지 보내기');
	console.info('id = ' + id);
	console.info('num = ' + num);
	
	if (!id || !num) {
		console.error('메세지 보내기 값 누락');
		res.send({
			'code':328
		});
	} else {
		mySqlConnection.query('select user_socket from user_auth where user_id = "' + id + '";', function(err, result){
			if (err) {
				console.error('소켓 값 추출 에러 = ' + err);
				res.send({
					'code':329
				})
			} else {
				var socketId = result[0].user_socket;
				console.info('user_socketId = ' + socketId);
				
				io.sockets.sockets(socketId).emit('sendMsg', {
					'code':200,
					'num':num
				});
			}
		});
	}
})


// 로그인
app.post('/login', function(req, res) {
	var id = req.body.id;
	var pw = req.body.pw;
	console.log('로그인 요청');
	console.info('id = ' + id);
	console.info('pw = ' + pw);
	
	mySqlConnection.query("select user_id from user_auth where user_id = '" + id + "' and user_pw = '" + pw + "';", function(err, result) {
		if (err) {
			res.send({
				'code':317
			});
			console.error('로그인  DB 에러 = ' + err);
		} else {
			if (!result[0] || !result[0].user_id) {
				// 일치하는 아이디가 없다면
				console.log('일치하는 아이디가 없음');
				res.send({
					'code':318
				});
			} else {
				// 일치하는 아이디가 있다면
				console.log('로그인 성공');
				res.send({
					'code':200,
					'id':id
				});
			} 
		}
	});
});


// 몽고디비
var mongoose = require('mongoose');
mongoose.connect('mongodb://username:12345678@ds041154.mongolab.com:41154/heroku_s264w1vj');
var ObjectId = mongoose.Schema.ObjectId;

var errSchema = mongoose.Schema({
	code : Number,
	msg : String,
	user_id : String
});

var errModel = mongoose.model('err', errSchema);



//mySQL
var mySqlUrl = 'mysql://vw3vn7zlemz835me:zupct54e81evwokd@jw0ch9vofhcajqg7.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/eooioxp79le8d0gp';
var mySqlHost = 'jw0ch9vofhcajqg7.cbetxkdyhwsb.us-east-1.rds.amazonaws.com';
var mySqlUserName = 'vw3vn7zlemz835me';
var mySqlPw = 'zupct54e81evwokd';
var mySqlPort = 3306;
var mySql = require('mysql');
var mySqlConnection = mySql.createConnection(process.env.JAWSDB_URL); 
var pool = mySql.createPool({
	connectionLimit : 100
});

mySqlConnection.connect(function(err) {
	if(err) {
		console.error('mySql 연결 에러 = ' + err);
	} else {
		console.log('mySql 연결 성공');
	}
});

io.sockets.on('connection', function(socket) {
	socket.on('hi', function(data) {
		console.log('gd');
	});
	
	socket.on('create', function() {
		mySqlConnection.query("create table if not exists user_auth (user_id VARCHAR(50) not null primary key, user_pw VARCHAR, user_mail VARCHAR(50) not null, user_name VARCHAR(20), user_birth INT, user_socket VARCHAR(25));", function(err, result) {
			if (err) {
				console.error('테이블 생성 에러 = ' + err);
			} else {
				console.log('테이블 생성');
			}
		});
	});
	
	socket.on('drop', function() {
		mySqlConnection.query("drop table user_auth", function(err, result) {
			if (err) {
				console.error('테이블 삭제 에러 = ' + err);
			} else {
				console.log('테이블 삭제');
			}
		});
	})
	
	socket.on('insert', function() {
		var inputData = {
				user_id : 'id',
				user_pw : '1234',
				user_mail : 'mail',
				user_name : 'lee',
				user_birth : 970224,
				user_socket : null
		};
		
		mySqlConnection.query('insert into user_auth set ?', inputData, function(err, rows) {
			if (err) {
				console.error('insert 에러 = ' + err);
			} else {
				console.log(rows);
			}
		});
	});
	
	
	socket.on('select', function() {
		mySqlConnection.query("select * from user_auth", function(err, rows) {
			if (err) {
				console.error('select 에러 = ' + err);
			} else {
				console.log(rows);
			}
		});
	});
	
	socket.on('connection', function() {
		console.log('연결');
	});
	
	socket.on('disconnect', function() {
		pool.releaseConnection(mySqlConnection);
		console.log('연결 해제');
	});
	
	socket.on('signUp', function(data) {
		var id = data.id;
		var pw = data.pw;
		var name = data.name;
		var birth = data.birth;
		var mail = data.mail;
		
		console.log('회원가입');
		console.info('id = ' + id);
		console.info('pw = ' + pw);
		console.info('name = ' + name);
		console.info('mail = ' + mail);
		console.info('birth = ' + birth);
		
		if (!id || !pw || !name || !mail || !birth) {
			console.error('NullPointerException');
			socket.emit('signUp', {
				'code':319
			});
			return;
		}
		
		
		mySqlConnection.query('select * from user_auth where user_id = "' + id + '";', function(err, result) {
			if (err) {
				// 아이디 중복검사 에러 
				console.error('회원가입 아이디 중복검사 에러 = ' + err);
				socket.emit('signUp', {
					'code' : 320
				});
			} else if (result[0]) {
				// 아이디 중복 에러
				socket.emit('signUp', {
					'code':321
				});
				console.error('회원가입 아이디 중복 : ' + err);
			} else {
				// 아이디 중복 없을 때
				mySqlConnection.query('select * from user_auth where user_mail = "' + mail + '";', function(err, emailResult) {
					if (err) {
						// 이메일 중복 검사 에러
						socket.emit('signUp', {
							'code':322
						});
						console.error('회원가입 이메일 중복검사 에러 : ' + err);
					} else if (emailResult[0]) {
						// 이메일 중복 에러
						socket.emit('signUp', {
							'code':323
						});
						console.error('회원가입 이메일 중복 : ' + err);
					} else {
						// 이메일 중복 없을 때 
						var input = {
								user_id : id,
								user_pw : pw,
								user_name : name,
								user_mail : mail,
								user_birth : birth,
								user_socket : null
						};
						
						
						mySqlConnection.query('insert into user_auth set ?', input, function(err, signUpResult) {
							if (err) {
								// db 입력 에러 
								socket.emit('signUp', {
									'code':324
								});
								console.error('DB입력 에러 : ' + err);
							} else {
								// 성공
								socket.emit('signUp', {
									'code':200,
									'id':id
								});
								console.log('회원가입 성공');
							}
						});
					}
				});
			}
		});
	});
	
	
	// 아이디 찾기
	socket.on('findId', function(data) {
		var mail = data.mail;
		var name = data.name;
		var birth = data.birth;
		
		console.log('아이디 찾기');
		
	});
	
	
	// 로그인 
	socket.on('login', function(data) {
		var id = data.id;
		var pw = data.pw;
		console.log('로그인 요청');
		console.info('id = ' + id);
		console.info('pw = ' + pw);
		
		if (!id || !pw) {
			socket.emit('login', {
				'code' : 328
			});
			console.error('값 누락');
		}
		
		mySqlConnection.query("select user_id from user_auth where user_id = '" + id + "' and user_pw = '" + pw + "';", function(err, result) {
			if (err) {
				socket.emit('login', {
					'code':325
				});
				console.error('로그인  DB 에러 = ' + err);
			} else {
				if (!result[0] || !result[0].user_id) {
					// 일치하는 아이디가 없다면
					console.log('일치하는 아이디가 없음');
					socket.emit('login', {
						'code':326
					});
				} else {
					console.info('socket.id = ' + socket.id);
					
					// 일치하는 아이디가 있다면
					mySqlConnection.query('update user_auth set user_socket = "' + socket.id + '" where user_id = "' + id + '";', function(err, result) {
						if (err) {
							console.log('로그인 socket 정보 등록 에러 : ' + err);
							socket.emit('login', {
								'code':327
							});
						} else {
							console.log('로그인 성공');
							socket.emit('login', {
								'code':200,
								'id':id
							});
						}
					});
				} 
			}
		});
	})
});
