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

var gcm = require('node-gcm');
var gcmServerKey = "AIzaSyCQ44BmvsY1SfEszh2JvVO5uAL4Z7M7Wso";
var sender = new gcm.Sender(gcmServerKey);


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
	var fromNum = req.body.fromNum;
	
	console.log('메세지 보내기');
	console.info('id = ' + id);
	console.info('num = ' + num);
	console.info('fromNum = ' + fromNum);
	
	if (!id || !num || !fromNum) {
		console.error('메세지 보내기 값 누락');
		res.send({
			'code':328
		});
	} else {
		mySqlConnection.beginTransaction(function(err) {
			var isOk = true;
			
			mySqlConnection.query('SELECT * FROM ' + GROUP_TABLE + ' JOIN ' + MEMBER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = "' + id + '"' +
					' WHERE ' + GROUP_TABLE + '.' + GROUP_ID + '=' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + ';', function(err, group_list) {
				if (err) {
					console.log('Select Group err = ' + err);
					res.send({
						'code' : 888
					});
					mySqlConnection.rollback();
				} else {
					var i;
					for (i = 0; i < group_list.length; i++) {
						var group = group_list[i];
						console.log('group.group_id = ' + group.group_id);
						console.log('group.group_coin = ' + group.group_coin);
						var queryData = {
							'group_id' : group.group_id,
							'day' : Date.now()
						};
						
						if (group.group_coin != -1) {
							getGroupCount(queryData, null, function(count) {
								console.log('return count = ' + count);
								if (count >= group.group_coin) {
									isOk = false;
									mySqlConnection.rollback();
									console.log('그룹 코인 한도 초과');
									res.send({
										'code' : 887
									});
									return;
								}
							});
						}
						
						if (i == group_list.length - 1) {
							mySqlConnection.query('select ' + USER_SOCKET + ' from ' + USER_TABLE + ' where user_id = "' + id + '";', function(err, result){
								if (err) {
									console.error('소켓 값 추출 에러 = ' + err);
									res.send({
										'code':885
									});
									mySqlConnection.rollback();
								} else {
									mySqlConnection.query('SELECT ' + USER_COIN + ', ' + USER_AUTHORITY_INFINITE_COIN + ' FROM ' + USER_TABLE + ' WHERE ' + USER_ID + ' = "' + id + '";', function(err, coinResult) {
										if (err) {
											console.log('SELECT COIN ' + err);
											res.send({
												'code' : 886
											});
										} else {
											if ((coinResult[0] && coinResult[0].user_coin > 0) || coinResult[0].user_authority_infinite_coin == 1) {
												
												// 5000코인 이하로 떨어진다면 최고 관리자에게 전달.
												if (coinResult[0].user_coin <= 5000) {
													var inputData = {
														charge_coin_id : id
													};
													
													mySqlConnection.query('INSERT INTO ' + CHARGE_COIN_TABLE + ' SET ?', inputData, function(err) {
														if (err) {
															console.log('코인 추가 요청 에러 = ' + err);
														} else {
															console.log('코인 추가 요청 성공.');
														}
													});
												}
												
												var msg = gcm.Message({
													collapseKey: "sms",
													delayWhileIdle: true,
													timeToLive: 3,
													data: {
														'title': num,
														'message' : fromNum,
														'id' : id
													}
												});
												
												var socketId = [];
												socketId.push(result[0].user_socket);
												console.info('user_socketId = ' + result[0].user_socket);
												
												if (isOk == false) {
													mySqlConnection.rollback();
													res.send({
														'code' : 884
													});
													return;
												}
												
												sender.send(msg, socketId, 4, function(err, result) {
													if (err) {
														console.error(err);
														mySqlConnection.rollback();
													} else {
														console.log('result = ' + result);
														
														if (isOk == false) {
															mySqlConnection.rollback();
															res.send({
																'code' : 883
															});
															return;
														}
														
														res.send({
															'code' : 200
														});
													}
												});
											} else {
												console.log('코인 없음');
												res.send({
													'code' : 875
												});
											}
										}
									});
								}
							});
						}
					}
				}
			});
		});
	}
});


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



//mySQL
//var mySqlUrl = 'mysql://vw3vn7zlemz835me:zupct54e81evwokd@jw0ch9vofhcajqg7.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/eooioxp79le8d0gp';
//var mySqlHost = 'jw0ch9vofhcajqg7.cbetxkdyhwsb.us-east-1.rds.amazonaws.com';
//var mySqlUserName = 'vw3vn7zlemz835me';
//var mySqlPw = 'zupct54e81evwokd';
//var mySqlPort = 3306;
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

var USER_TABLE = "user_auth";
var USER_ID = "user_id";//
var USER_PW = "user_pw";//
var USER_MAIL = "user_mail";
var USER_NAME = "user_name";//
var USER_BIRTH = "user_birth";//
var USER_COIN = "user_coin";
var USER_IS_CONNECTED = "user_is_connected";
var USER_AUTHORITY_CHANGE_MSG = "user_authority_change_msg";
var USER_AUTHORITY_SHOW_AD_DETAIL = "user_authority_show_ad_detail";
var USER_AUTHORITY_SEND_PHONE = "user_authority_send_phone";
var USER_AUTHORITY_INFINITE_COIN = "user_authority_infinite_coin";
var USER_PHONE = "user_phone";
var USER_SOCKET = "user_socket";//

var GROUP_TABLE = "groups";
var GROUP_ID = "group_id";
var GROUP_NAME = "group_name";
var GROUP_COIN = "group_coin";

var MEMBER_TABLE = "members";
var MEMBER_USER_ID = "member_user_id";
var MEMBER_GROUP_ID = "member_group_id";

var MASTER_TABLE = "masters";
var MASTER_ID = "master_id";
var MASTER_GROUP_ID = "master_group_id";

var COUNT_TABLE = "counts";
var COUNT_USER_ID = "count_user_id";
var COUNT_DATE = "count_date";

var MSG_TABLE = "msgs";
var MSG_ID = "msg_id";
var MSG_USER_ID = "msg_user_id";
var MSG_PORT = "msg_port";
var MSG_IS_EVERYBODY = "msg_is_everybody";
var MSG_CONTENT = "msg_content";

var DELETE_USER_TABLE = "delete_users";
var DELETE_USER_ID = "delete_user_id";

var DELETE_MEMBER_TABLE = "delete_members";
var DELETE_MEMBER_ID = "delete_member_id";
var DELETE_MEMBER_GROUP_ID = "delete_member_group_id";

var CHARGE_COIN_TABLE = "charge_coins";
var CHARGE_COIN_ID = "charge_coin_id";
var CHARGE_COIN_PRICE = "charge_coin_price";

Date.prototype.addHours= function(h) {
    this.setHours(this.getHours()+h);
    return this;
}

function getGroupCount(data, socket, callback) {
	var group_id = data.group_id;
	var date = new Date(data.day).addHours(9);
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	
	console.log('getGroupCount');
	console.log('getGroupCount group_id = ' + group_id);
	console.log('getGroupCount date = ' + date);
	console.log('getGroupCount year = ' + year);
	console.log('getGroupCount month = ' + month);
	console.log('getGroupCount day = ' + day);
	
	if (socket) {
		if (!group_id || !date) {
			console.log('getGroupCount 데이터 누락');
			socket.emit('getGroupCount', {
				'code' : 330
			});
		} else {
			mySqlConnection.query('SELECT COUNT(*) FROM ' + COUNT_TABLE + ' JOIN ' + MEMBER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = ' + COUNT_TABLE + '.' + COUNT_USER_ID +
					' AND ' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + ' = ' + group_id +
					' WHERE ' + COUNT_TABLE + '.' + COUNT_DATE + ' = "' + year + '-' + month + '-' + day + '";', function(err, count) {
				if (err) {
					console.log('getGroupCount SELECT MEMBER ' + err);
					socket.emit('getGroupCount', {
						'code' : 332
					});
				} else {
					console.log('getGroupCount 성공');
					socket.emit('getGroupCount', {
						'code' : 200,
						'count' : count[0]
					});
				}
			});
			
			mySqlConnection.query('SELECT * FROM ' + COUNT_TABLE + ' JOIN ' + MEMBER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = ' + COUNT_TABLE + '.' + COUNT_USER_ID +
					' AND ' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + ' = ' + group_id +
					' WHERE ' + COUNT_TABLE + '.' + COUNT_DATE + ' = "' + year + '-' + month + '-' + day + '";', function(err, count) {
				if (err) {
					console.log('getGroupCount SELECT MEMBER ' + err);
				} else {
					console.log('getGroupCount 성공');
					console.log('asdf = ' + JSON.stringify(count));
				}
			});
		}
	} else {
		if (!group_id || !date) {
			callback(0);
		} else {
			mySqlConnection.query('SELECT COUNT(*) AS GROUP_COUNT FROM ' + COUNT_TABLE + ' JOIN ' + MEMBER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = ' + COUNT_TABLE + '.' + COUNT_USER_ID +
					' AND ' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + ' = ' + group_id +
					' WHERE ' + COUNT_TABLE + '.' + COUNT_DATE + ' = "' + year + '-' + month + '-' + day + '";', function(err, count) {
				if (err) {
					console.log('getGroupCount SELECT MEMBER ' + err);
					callback(0);
				} else {
					console.log('getGroupCount 성공');
					var resultCount = count[0].GROUP_COUNT;
					callback(resultCount);
				}
			});
		}
	}
}

io.sockets.on('connection', function(socket) {
	
	socket.on('aaa', function() {
		mySqlConnection.query('ALTER TABLE ' + CHARGE_COIN_TABLE + ' ADD COLUMN ' + CHARGE_COIN_PRICE + ' INT;', function(err) {
			if (err) {
				console.log('add column err = ' + err);
			} else {
				console.log('add column 성공');
			}
		});
	});
	
	socket.on('select', function() {
//		mySqlConnection.query('DELETE FROM ' + COUNT_TABLE, function(err, result) {
//			if (err) {
//				console.log('select err = ' + err);
//			} else {
//				console.log('result = ' + JSON.stringify(result));
//			}
//		});
		
		mySqlConnection.query('SELECT * FROM ' + COUNT_TABLE + ' WHERE ' + COUNT_DATE + '>"2016-02-21";', function(err, result) {
			if (err) {
				console.log('select err = ' + err);
			} else {
				console.log('SELECT result = ' + JSON.stringify(result));
			}
		});
	});
	
	socket.on('create', function() {
		mySqlConnection.query('create table if not exists ' + USER_TABLE + ' (' +
				USER_ID + ' VARCHAR(50) not null primary key, ' +
				USER_PW + ' TEXT, ' +
				USER_MAIL + ' VARCHAR(50) not null, ' + 
				USER_NAME + ' VARCHAR(20), ' +
				USER_BIRTH + ' BIGINT, ' +
				USER_COIN + ' INT DEFAULT 0, ' +
				USER_IS_CONNECTED + ' INT DEFAULT 2, ' +
				USER_AUTHORITY_CHANGE_MSG + ' INT DEFAULT 2, ' +
				USER_AUTHORITY_SEND_PHONE + ' INT DEFAULT 2, ' +
				USER_AUTHORITY_SHOW_AD_DETAIL + ' INT DEFAULT 2, ' +
				USER_AUTHORITY_INFINITE_COIN + ' INT DEFAULT 2, ' +
				USER_PHONE + ' TEXT, ' +
				USER_SOCKET + ' TEXT);', function(err, result) {
			if (err) {
				console.error('유저 테이블 생성 에러 = ' + err);
			} else {
				console.log('유저 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + COUNT_TABLE + ' (' +
				COUNT_USER_ID + ' VARCHAR(50) not null, ' +
				COUNT_DATE + ' DATETIME);', function(err, result) {
			if (err) {
				console.error('카운트 테이블 생성 에러 = ' + err);
			} else {
				console.log('카운트 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + GROUP_TABLE + ' (' +
				GROUP_ID + ' INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ' +
				GROUP_COIN + ' INT NOT NULL, ' +
				GROUP_NAME + ' TEXT);', function(err, result) {
			if (err) {
				console.error('그룹 테이블 생성 에러 = ' + err);
			} else {
				console.log('그룹 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + MASTER_TABLE + ' (' +
				MASTER_GROUP_ID + ' INT not null, ' +
				MASTER_ID + ' VARCHAR(50) not null);', function(err, result) {
			if (err) {
				console.error('마스터 테이블 생성 에러 = ' + err);
			} else {
				console.log('마스터 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + MSG_TABLE + ' (' +
				MSG_ID + ' INT not null AUTO_INCREMENT PRIMARY KEY, ' +
				MSG_USER_ID + ' VARCHAR(50) NOT NULL, ' +
				MSG_PORT + ' TEXT NOT NULL, ' + 
				MSG_IS_EVERYBODY + ' INT NOT NULL, ' + 
				MSG_CONTENT + ' TEXT NOT NULL);', function(err, result) {
			if (err) {
				console.error('메세지 테이블 생성 에러 = ' + err);
			} else {
				console.log('메세지 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + DELETE_USER_TABLE + ' (' +
				DELETE_USER_ID + ' VARCHAR(50) NOT NULL);', function(err, result) {
			if (err) {
				console.error('유저삭제 테이블 생성 에러 = ' + err);
			} else {
				console.log('유저삭제 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + MEMBER_TABLE + ' (' +
				MEMBER_GROUP_ID + ' INT not null, ' +
				MEMBER_USER_ID + ' VARCHAR(50) NOT NULL);', function(err, result) {
			if (err) {
				console.error('그룹 멤버 테이블 생성 에러 = ' + err);
			} else {
				console.log('그룹 멤버 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + DELETE_MEMBER_TABLE + ' (' +
				DELETE_MEMBER_GROUP_ID + ' INT not null, ' +
				DELETE_MEMBER_ID + ' VARCHAR(50) NOT NULL);', function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 생성 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + CHARGE_COIN_TABLE + ' (' +
				CHARGE_COIN_ID + ' VARCHAR(50) NOT NULL);', function(err, result) {
			if (err) {
				console.error('코인 추가 요청 테이블 생성 에러 = ' + err);
			} else {
				console.log('코인 추가 요청 테이블 생성');
			}
		});
	});
	
	socket.on('drop', function() {
//		mySqlConnection.query("drop table " + USER_TABLE, function(err, result) {
//			if (err) {
//				console.error('유저 테이블 삭제 에러 = ' + err);
//			} else {
//				console.log('유저 테이블 삭제');
//			}
//		});
		
		mySqlConnection.query("drop table " + COUNT_TABLE, function(err, result) {
			if (err) {
				console.error('카운트 테이블 삭제 에러 = ' + err);
			} else {
				console.log('카운트 테이블 삭제');
			}
		});
		
		mySqlConnection.query("drop table " + GROUP_TABLE, function(err, result) {
			if (err) {
				console.error('그룹 테이블 삭제 에러 = ' + err);
			} else {
				console.log('그룹 테이블 삭제');
			}
		});
		
		mySqlConnection.query("drop table " + MASTER_TABLE, function(err, result) {
			if (err) {
				console.error('마스터 테이블 삭제 에러 = ' + err);
			} else {
				console.log('마스터 테이블 삭제');
			}
		});
		
		mySqlConnection.query("drop table " + MSG_TABLE, function(err, result) {
			if (err) {
				console.error('메세지 테이블 삭제 에러 = ' + err);
			} else {
				console.log('메세지 테이블 삭제');
			}
		});
		
		mySqlConnection.query("drop table " + DELETE_MEMBER_TABLE, function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 삭제');
			}
		});
		
		mySqlConnection.query("drop table " + MEMBER_TABLE, function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 삭제');
			}
		});
		
		mySqlConnection.query("drop table " + CHARGE_COIN_TABLE, function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 삭제');
			}
		});
		
		
	});

	socket.on('dropA', function(data) {
		mySqlConnection.query("drop table " + GROUP_TABLE, function(err, result) {
			if (err) {
				console.error('그룹 테이블 삭제 에러 = ' + err);
			} else {
				console.log('그룹 테이블 삭제');
			}
		});
		
		mySqlConnection.query("drop table " + MASTER_TABLE, function(err, result) {
			if (err) {
				console.error('마스터 테이블 삭제 에러 = ' + err);
			} else {
				console.log('마스터 테이블 삭제');
			}
		});

		mySqlConnection.query("drop table " + MEMBER_TABLE, function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 삭제');
			}
		});
	});

	socket.on('createA', function(data) {
		mySqlConnection.query('create table if not exists ' + MEMBER_TABLE + ' (' +
				MEMBER_GROUP_ID + ' INT not null, ' +
				MEMBER_USER_ID + ' VARCHAR(50) NOT NULL);', function(err, result) {
			if (err) {
				console.error('그룹 멤버 테이블 생성 에러 = ' + err);
			} else {
				console.log('그룹 멤버 테이블 생성');
			}
		});

		mySqlConnection.query('create table if not exists ' + GROUP_TABLE + ' (' +
				GROUP_ID + ' INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ' +
				GROUP_COIN + ' INT NOT NULL, ' +
				GROUP_NAME + ' TEXT);', function(err, result) {
			if (err) {
				console.error('그룹 테이블 생성 에러 = ' + err);
			} else {
				console.log('그룹 테이블 생성');
			}
		});
		
		mySqlConnection.query('create table if not exists ' + MASTER_TABLE + ' (' +
				MASTER_GROUP_ID + ' INT not null, ' +
				MASTER_ID + ' VARCHAR(50) not null);', function(err, result) {
			if (err) {
				console.error('마스터 테이블 생성 에러 = ' + err);
			} else {
				console.log('마스터 테이블 생성');
			}
		});
	});
	
	socket.on('delete', function() {
		mySqlConnection.query("DELETE FROM " + COUNT_TABLE, function(err, result) {
			if (err) {
				console.error('카운트 테이블 삭제 에러 = ' + err);
			} else {
				console.log('카운트 테이블 삭제');
			}
		});
		
		mySqlConnection.query("DELETE FROM " + GROUP_TABLE, function(err, result) {
			if (err) {
				console.error('그룹 테이블 삭제 에러 = ' + err);
			} else {
				console.log('그룹 테이블 삭제');
			}
		});
		
		mySqlConnection.query("DELETE FROM " + MASTER_TABLE, function(err, result) {
			if (err) {
				console.error('마스터 테이블 삭제 에러 = ' + err);
			} else {
				console.log('마스터 테이블 삭제');
			}
		});
		
		mySqlConnection.query("DELETE FROM " + MSG_TABLE, function(err, result) {
			if (err) {
				console.error('메세지 테이블 삭제 에러 = ' + err);
			} else {
				console.log('메세지 테이블 삭제');
			}
		});
		
		mySqlConnection.query("DELETE FROM " + DELETE_USER_TABLE, function(err, result) {
			if (err) {
				console.error('유저 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('유저 삭제 테이블 삭제');
			}
		});
		
		mySqlConnection.query("DELETE FROM " + CHARGE_COIN_TABLE, function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 삭제');
			}
		});
		
		mySqlConnection.query("DELETE FROM " + MEMBER_TABLE, function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 삭제');
			}
		});
		
		mySqlConnection.query("DELETE FROM " + DELETE_MEMBER_TABLE, function(err, result) {
			if (err) {
				console.error('멤버 삭제 테이블 삭제 에러 = ' + err);
			} else {
				console.log('멤버 삭제 테이블 삭제');
			}
		});
	});
	
	socket.on('insert', function() {
		mySqlConnection.query("drop table " + COUNT_TABLE, function(err, result) {
			if (err) {
				console.error('카운트 테이블 삭제 에러 = ' + err);
			} else {
				console.log('카운트 테이블 삭제');
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
		var phone = data.phone;
		
		console.log('회원가입');
		console.info('id = ' + id);
		console.info('pw = ' + pw);
		console.info('name = ' + name);
		console.info('phone = ' + phone);
		
		if (!id || !pw || !name || !phone) {
			console.error('NullPointerException');
			socket.emit('signUp', {
				'code':500
			});
			return;
		}
		
		
		mySqlConnection.query('select * from ' + USER_TABLE + ' where user_id = "' + id + '";', function(err, result) {
			if (err) {
				// 아이디 중복검사 에러 
				console.error('회원가입 아이디 중복검사 에러 = ' + err);
				socket.emit('signUp', {
					'code':501
				});
			} else if (result[0]) {
				// 아이디 중복 에러
				socket.emit('signUp', {
					'code':502
				});
				console.error('회원가입 아이디 중복 : ' + err);
			} else {
				// 아이디 중복 없을 때
				// 성공
				var input = {
						user_id : id,
						user_pw : pw,
						user_socket : null,
						user_name : name,
						user_phone : phone
				};
				
				
				mySqlConnection.query('insert into ' + USER_TABLE + ' set ?', input, function(err, signUpResult) {
					if (err) {
						// db 입력 에러 
						socket.emit('signUp', {
							'code':503
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
		var token = data.token;
		
		console.log('로그인 요청');
		console.info('id = ' + id);
		console.info('pw = ' + pw);
		console.info('token = ' + token);
		
		if (!id || !pw) {
			socket.emit('login', {
				'code' : 328
			});
			console.error('값 누락');
		}
		
		if (token) {
			mySqlConnection.query('SELECT * FROM ' + USER_TABLE + ' LEFT OUTER JOIN ' + MEMBER_TABLE + ' ON ' +
				 	MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = ' + USER_TABLE + '.' + USER_ID +  
				 	' LEFT OUTER JOIN ' + MASTER_TABLE + ' ON ' + USER_TABLE + '.' + USER_ID + '=' + MASTER_TABLE + '.' + MASTER_ID + 
				 	' LEFT OUTER JOIN ' + GROUP_TABLE + ' ON ' + GROUP_TABLE + '.' + GROUP_ID + '=' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + 
				 	' OR ' + GROUP_TABLE + '.' + GROUP_ID + '=' + MASTER_TABLE + '.' + MASTER_GROUP_ID + ' WHERE ' +
				 	USER_TABLE + '.' + USER_ID + ' = "' + id + '" AND ' +
				 	USER_TABLE + '.' + USER_PW + ' = "' + pw + '";', function(err, result) {
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
						// 일치하는 아이디가 있다면
						mySqlConnection.query('update ' + USER_TABLE + ' set user_socket = "' + token + '" where ' + USER_ID + ' = "' + id + '";', function(err, updateResult) {
							if (err) {
								console.log('로그인 socket 정보 등록 에러 : ' + err);
								socket.emit('login', {
									'code':327
								});
							} else {
								console.log('로그인 성공');
								socket.emit('login', {
									'code':200,
									'user':result
								});
							}
						});
					} 
				}
			});
		} else {
			mySqlConnection.query('SELECT * FROM ' + USER_TABLE + ' LEFT OUTER JOIN ' + MEMBER_TABLE + ' ON ' +
				 	MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = ' + USER_TABLE + '.' + USER_ID +  
				 	' LEFT OUTER JOIN ' + MASTER_TABLE + ' ON ' + USER_TABLE + '.' + USER_ID + '=' + MASTER_TABLE + '.' + MASTER_ID + 
				 	' LEFT OUTER JOIN ' + GROUP_TABLE + ' ON ' + GROUP_TABLE + '.' + GROUP_ID + '=' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + 
				 	' OR ' + GROUP_TABLE + '.' + GROUP_ID + '=' + MASTER_TABLE + '.' + MASTER_GROUP_ID + ' WHERE ' +
				 	USER_TABLE + '.' + USER_ID + ' = "' + id + '" AND ' +
				 	USER_TABLE + '.' + USER_PW + ' = "' + pw + '";', function(err, result) {
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
						// 일치하는 아이디가 있다면
						console.log('로그인 성공');
						socket.emit('login', {
							'code':200,
							'user':result
						});
					} 
				}
			});
		}
	});

	
	socket.on('masterLogin', function(data) {
		var id = data.id;
		var pw = data.pw;
		
		console.log('마스터 로그인 요청');
		console.info('id = ' + id);
		console.info('pw = ' + pw);
		
		if (!id || !pw) {
			socket.emit('masterLogin', {
				'code' : 328
			});
			console.error('값 누락');
		}
		
		mySqlConnection.query('SELECT * FROM ' + USER_TABLE +
			 	' LEFT OUTER JOIN ' + MASTER_TABLE + ' ON ' + USER_TABLE + '.' + USER_ID + '=' + MASTER_TABLE + '.' + MASTER_ID + 
			 	' LEFT OUTER JOIN ' + GROUP_TABLE + ' ON ' + GROUP_TABLE + '.' + GROUP_ID + '=' + MASTER_TABLE + '.' + MASTER_GROUP_ID + ' WHERE ' + 
			 	USER_TABLE + '.' + USER_ID + ' = "' + id + '" AND ' +
			 	USER_TABLE + '.' + USER_PW + ' = "' + pw + '";', function(err, result) {
			if (err) {
				socket.emit('masterLogin', {
					'code':325
				});
				console.error('로그인  DB 에러 = ' + err);
			} else {
				if (!result[0] || !result[0].user_id) {
					// 일치하는 아이디가 없다면
					console.log('일치하는 아이디가 없음');
					socket.emit('masterLogin', {
						'code':326
					});
				} else {
					// 일치하는 아이디가 있다면
					console.log('로그인 성공 = ' + JSON.stringify(result));
					socket.emit('masterLogin', {
						'code':200,
						'user':result
					});
				} 
			}
		});
	});

	
	socket.on('getGroupCount', function(data) {
		getGroupCount(data, socket);
	});
	
	
	socket.on('setChangeMsg', function(data) {
		var user_id = data.id;
		var isAble = data.isAble;
		
		console.log('setChangeMsg');
		console.log('setChangeMsg user_id = ' + user_id);
		console.log('setChangeMsg isAble = ' + isAble);
		
		if (!user_id || !isAble) {
			console.log('setChangeMsg 데이터 누락');
			socket.emit('setChangeMsg' , {
				'code' : 350
			});
		} else {
			mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ' + USER_AUTHORITY_CHANGE_MSG + ' = ' + isAble + ' WHERE ' + USER_ID + ' = "' + user_id + '";', function(err) {
				if (err) {
					console.log('setChangeMsg Update Err = ' + err);
					socket.emit('setChangeMsg', {
						'code' : 351
					});
				} else {
					console.log('setChangeMsg Success');
					socket.emit('setChangeMsg', {
						'code' : 200
					});
				}
			});
		}
	});
	
	
	socket.on('setSend', function(data) {
		var user_id = data.id;
		var isAble = data.isAble;
		
		console.log('setSend');
		console.log('setSend user_id = ' + user_id);
		console.log('setSend isAble = ' + isAble);
		
		if (!user_id || !isAble) {
			console.log('setSend 데이터 누락');
			socket.emit('setSend' , {
				'code' : 360
			});
		} else {
			mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ' + USER_AUTHORITY_SEND_PHONE + ' = ' + isAble + ' WHERE ' + USER_ID + ' = "' + user_id + '";', function(err) {
				if (err) {
					console.log('setSend Update Err = ' + err);
					socket.emit('setSend', {
						'code' : 361
					});
				} else {
					console.log('setSend Success');
					socket.emit('setSend', {
						'code' : 200
					});
				}
			});
		}
	});
	
	
	socket.on('setShowAdDetail', function(data) {
		var user_id = data.id;
		var isAble = data.isAble;
		
		console.log('setShowAdDetail');
		console.log('setShowAdDetail user_id = ' + user_id);
		console.log('setShowAdDetail isAble = ' + isAble);
		
		if (!user_id || !isAble) {
			console.log('setShowAdDetail 데이터 누락');
			socket.emit('setShowAdDetail' , {
				'code' : 370
			});
		} else {
			mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ' + USER_AUTHORITY_SHOW_AD_DETAIL + ' = ' + isAble + ' WHERE ' + USER_ID + ' = "' + user_id + '";', function(err) {
				if (err) {
					console.log('setShowAdDetail Update Err = ' + err);
					socket.emit('setShowAdDetail', {
						'code' : 371
					});
				} else {
					console.log('setShowAdDetail Success');
					socket.emit('setShowAdDetail', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('setInfiniteCoin', function(data) {
		var user_id = data.id;
		var isAble = data.isAble;
		
		console.log('setInfiniteCoin');
		console.log('setInfiniteCoin user_id = ' + user_id);
		console.log('setInfiniteCoin isAble = ' + isAble);
		
		if (!user_id || !isAble) {
			console.log('setInfiniteCoin 데이터 누락');
			socket.emit('setInfiniteCoin' , {
				'code' : 370
			});
		} else {
			mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ' + USER_AUTHORITY_INFINITE_COIN + ' = ' + isAble + ' WHERE ' + USER_ID + ' = "' + user_id + '";', function(err) {
				if (err) {
					console.log('setInfiniteCoin Update Err = ' + err);
					socket.emit('setInfiniteCoin', {
						'code' : 371
					});
				} else {
					console.log('setInfiniteCoin Success');
					socket.emit('setInfiniteCoin', {
						'code' : 200
					});
				}
			});
		}
	});
	
	
	socket.on('insertMsg', function(data) {
		var id = data.id;
		var port = data.msg_port;
		var content = data.msg_content;
		var isEverybody = data.msg_is_everybody;
		
		console.log('insertMsg');
		console.log('insertMsg id = ' + id);
		console.log('insertMsg port = ' + port);
		console.log('insertMsg content = ' + content);
		console.log('insertMsg isEverybody = ' + isEverybody);
		
		if (!id || !port || !content || !isEverybody) {
			console.log('insertMsg 데이터 누락');
			socket.emit('insertMsg', {
				'code' : 380
			});
		} else {
			var inputData = {
				'msg_port' : port,
				'msg_content' : content,
				'msg_user_id' : id,
				'msg_is_everybody' : isEverybody
			};
			mySqlConnection.query('INSERT INTO ' + MSG_TABLE + ' SET ?', inputData, function(err) {
				if (err) {
					console.log('insertMsg Err = ' + err);
					socket.emit('insertMsg', {
						'code' : 381
					});
				} else {
					console.log('insertMsg 성공');
					socket.emit('insertMsg', {
						'code': 200
					});
				}
			});
		}
	});
	
	
	socket.on('updateMsg', function(data) {
		var id = data.msg_id;
		var port = data.msg_port;
		var content = data.msg_content;
		var isEverybody = data.msg_is_everybody;
		
		console.log('updateMsg');
		console.log('updateMsg id = ' + id);
		console.log('updateMsg port = ' + port);
		console.log('updateMsg content = ' + content);
		console.log('updateMsg isEverybody = ' + isEverybody);
		
		if (!id || !port || !content || !isEverybody) {
			console.log('updateMsg 데이터 누락');
			socket.emit('updateMsg', {
				'code' : 390
			});
		} else {
			var inputData = {
				'msg_port' : port,
				'msg_content' : content,
				'msg_is_everybody' : isEverybody
			};
			
			var queryData = {
				'msg_id' : id
			}
			
			mySqlConnection.query('UPDATE ' + MSG_TABLE + ' SET ? WHERE ?', [inputData, queryData], function(err) {
				if (err) {
					console.log('updateMsg Err = ' + err);
					socket.emit('updateMsg', {
						'code' : 391
					});
				} else {
					console.log('updateMsg 성공');
					socket.emit('updateMsg', {
						'code': 200
					});
				}
			});
		}
	});
	
	
	socket.on('deleteMsg', function(data) {
		var id = data.msg_id;
		
		console.log('deleteMsg');
		console.log('deleteMsg id = ' + id);
		
		if (!id) {
			console.log('deleteMsg 데이터 누락');
			socket.emit('deleteMsg', {
				'code' : 400
			});
		} else {
			var queryData = {
				'msg_id' : id
			}
			
			mySqlConnection.query('DELETE FROM ' + MSG_TABLE + ' WHERE ?', queryData, function(err) {
				if (err) {
					console.log('deleteMsg Err = ' + err);
					socket.emit('deleteMsg', {
						'code' : 401
					});
				} else {
					console.log('deleteMsg 성공');
					socket.emit('deleteMsg', {
						'code': 200
					});
				}
			});
		}
	});
	
	
	socket.on('getMsg', function(data) {
		var id = data.id;
		
		console.log('getMsg');
		console.log('getMsg id = ' + id);
		
		if (!id) {
			console.log('getMsg 데이터 누락');
			socket.emit('getMsg', {
				'code' : 410
			});
		} else {
			var queryData = {
				'msg_user_id' : id
			}
			
			mySqlConnection.query('SELECT * FROM ' + MSG_TABLE + ' WHERE ?', queryData, function(err, msgs) {
				if (err) {
					console.log('getMsg Err = ' + err);
					socket.emit('getMsg', {
						'code' : 411
					});
				} else {
					console.log('getMsg 성공');
					socket.emit('getMsg', {
						'code': 200,
						'msg' : msgs
					});
				}
			});
		}
	});
	
	
	socket.on('getGroup', function(data) {
		var group_id = data.id;
		
		console.log('getGroup');
		console.log('getGroup group_id = ' + group_id);
		
		mySqlConnection.query('SELECT * FROM ' + GROUP_TABLE + ' LEFT JOIN ' + MASTER_TABLE + ' ON ' + MASTER_TABLE + '.' + MASTER_GROUP_ID + ' = ' + GROUP_ID +
				' LEFT JOIN ' + MEMBER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + ' = ' + GROUP_TABLE + '.' + GROUP_ID +
				' LEFT JOIN ' + USER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = ' + USER_TABLE + '.' + USER_ID +
				' WHERE ' + GROUP_TABLE + '.' + GROUP_ID + ' = ' + group_id +
				' ORDER BY ' + GROUP_TABLE + '.' + GROUP_ID + ' ASC' +
				', ' + MASTER_TABLE + '.' + MASTER_ID + ' ASC' +
				', ' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ' ASC' + ';', function(err, result) {
			if (err) {
				console.log('getGroup err = ' + err);
				socket.emit('getGroup', {
					'code' : 420
				});
			} else {
				console.log('getGroup 성공');
				console.log('result = ' + JSON.stringify(result));
				socket.emit('getGroup', {
					'code' : 200,
					'group' : result
				});
			}
		});
	});

	socket.on('deleteGroups', function(data) {
		mySqlConnection.query('DELETE FROM ' + GROUP_TABLE, function(err) {
				if (err) {
					console.log('deleteGroup err = ' + err);
					socket.emit('deleteGroup', {
						'code' : 661
					});
				} else {
					console.log('deleteGroup 성공');
					socket.emit('deleteGroup', {
						'code' : 200
					});
				}
			});
	});
	
	socket.on('insertMaster', function(data) {
		var group_id = data.group_id;
		var id = data.id;
		
		console.log('insertMaster');
		console.log('insertMaster group_id = ' + group_id);
		console.log('insertMaster id = ' + id);
		
		if (!group_id || !id) {
			console.log('insertMaster 데이터 누락');
			socket.emit('insertMaster', {
				'code' : 430
			});
		} else {
			mySqlConnection.query('SELECT ' + USER_ID + ' FROM ' + USER_TABLE + ' WHERE ' + USER_ID + ' = "' + id + '";', function(err, userResult) {
				if (err) {
					console.log('아이디 존재 유무 검사 에러 = ' + err);
					sockete.emit('insertMaster', {
						'code' : 431
					});
				} else {
					console.log('userResult = ' + JSON.stringify(userResult));
					if (!userResult || !userResult[0] || !userResult[0].user_id) {
						console.log('존재하지 않는 아이디');
						socket.emit('insertMaster', {
							'code' : 432
						});
					} else {
						mySqlConnection.query('SELECT * FROM ' + MASTER_TABLE + ' WHERE ' + MASTER_ID + ' = "' + id + '" AND ' + MASTER_GROUP_ID + ' = ' + group_id + ';', function(err, memberResult) {
							if (err) {
								console.log('그룹장 이미 있는거 검사 실패. = ' + err);
								socket.emit('insertMaster', {
									'code' : 434
								});
							} else {
								if (!memberResult[0]) {
									var inputData = {
										master_group_id : group_id,
										master_id : id
									};
									
									mySqlConnection.query('INSERT INTO ' + MASTER_TABLE + ' SET ?', inputData, function(err) {
										if (err) {
											console.log('insertMaster err = ' + err);
											socket.emit('insertMaster', {
												'code' : 433
											});
										} else {
											console.log('insertMaster 성공');
											socket.emit('insertMaster', {
												'code' : 200
											});
										}
									});
								} else {
									console.log('그룹장 이미 있음');
									socket.emit('insertMaster', {
										'code' : 435
									});
								}
							}
						});
					}
				}
			});
		}
	});
	
	socket.on('deleteMasters', function(data) {
		mySqlConnection.query('DELETE FROM ' + MASTER_TABLE, function(err) {
					if (err) {
						console.log('deleteMaster err = ' + err);
						socket.emit('deleteMaster', {
							'code' : 433
						});
					} else {
						console.log('deleteMaster 성공');
						socket.emit('deleteMaster', {
							'code' : 200
						});
					}
				});
	});

	socket.on('deleteMaster', function(data) {
		var group_id = data.group_id;
		var id = data.id;
		
		console.log('deleteMaster');
		console.log('deleteMaster group_id = ' + group_id);
		console.log('deleteMaster id = ' + id);
		
		if (!group_id || !id) {
			console.log('deleteMaster 데이터 누락');
			socket.emit('deleteMaster', {
				'code' : 430
			});
		} else {
			if (id === 'adplan') {
				console.log('deleteMaster adplan 삭제 시도');
				socket.emit('deleteMaster', {
					'code' : 434
				});
			} else {
				mySqlConnection.query('DELETE FROM ' + MASTER_TABLE + ' WHERE ' + MASTER_GROUP_ID + ' = ' + group_id + ' AND ' + MASTER_ID + ' = "' + id + '";', function(err) {
					if (err) {
						console.log('deleteMaster err = ' + err);
						socket.emit('deleteMaster', {
							'code' : 433
						});
					} else {
						console.log('deleteMaster 성공');
						socket.emit('deleteMaster', {
							'code' : 200
						});
					}
				});
			}
		}
	});
	
	socket.on('insertMember', function(data) {
		var group_id = data.group_id;
		var id = data.id;
		
		console.log('insertMember');
		console.log('insertMember group_id = ' + group_id);
		console.log('insertMember id = ' + id);
		
		if (!group_id || !id) {
			console.log('insertMember 데이터 누락');
			socket.emit('insertMember', {
				'code' : 440
			});
		} else {
			mySqlConnection.query('SELECT * FROM ' + USER_TABLE + ' WHERE ' + USER_ID + ' = "' + id + '";', function(err, userResult) {
				if (err) {
					console.log('아이디 존재 유무 검사 에러 = ' + err);
					sockete.emit('insertMember', {
						'code' : 441
					});
				} else {
					console.log('userResult = ' + JSON.stringify(userResult));
					if (!userResult || !userResult[0] || !userResult[0].user_id) {
						console.log('존재하지 않는 아이디');
						socket.emit('insertMember', {
							'code' : 442
						});
					} else {
						mySqlConnection.query('SELECT * FROM ' + MEMBER_TABLE + ' WHERE ' + MEMBER_USER_ID + ' = "' + id + '" AND ' + MEMBER_GROUP_ID + ' = ' + group_id + ';', function(err, memberResult) {
							if (err) {
								console.log('멤버 이미 있는거 검사 실패. = ' + err);
								socket.emit('insertMember', {
									'code' : 444
								});
							} else {
								if (!memberResult[0]) {
									// 처음 추가된 멤버라면.
									var inputData = {
										'member_user_id' : id,
										'member_group_id' : group_id
									};
									
									mySqlConnection.query('INSERT INTO ' + MEMBER_TABLE + ' SET ?', inputData, function(err) {
										if (err) {
											console.log('insertMember err = ' + err);
											socket.emit('insertMember', {
												'code' : 443
											});
										} else {
											console.log('insertMember 성공');
											socket.emit('insertMember', {
												'code' : 200,
												'user' : userResult[0]
											});
										}
									});
								} else {
									// 이미 추가됬던 멤버라면
									console.log('이미 추가된 아이디');
									socket.emit('insertMember', {
										'code' : 445
									});
								}
							}
						});
					}
				}
			});
		}
	});
	
	socket.on('updateUserId', function(data) {
		var id = data.user;
		var curId = data.id;
		
		console.log('updateUserId');
		console.log('updateUserId id = ' + id);
		console.log('updateUserId curId = ' + curId);
		
		if (!id || !curId) {
			console.log('updateUserId 데이터 누락');
			socket.emit('updateUserId', {
				'code' : 460
			});
		} else {
			var inputData = {
				'user_id' : curId
			};
			
			var updateData = {
				'user_id' : id
			};
			
			mySqlConnection('UPDATE ' + USER_TABLE + ' SET ? WHERE ?', [inputData, updateData], function(err) {
				if (err) {
					console.log('updateUserId err = ' + err);
					socket.emit('updateUserId', {
						'code' : 461
					});
				} else {
					console.log('updateUserId 성공');
					socket.emit('updateUserId', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('updateUserPw', function(data) {
		var id = data.id;
		var pw = data.pw;
		
		console.log('updateUserPw');
		console.log('updateUserPw id = ' + id);
		console.log('updateUserPw pw = ' + pw);
		
		if (!id || !pw) {
			console.log('updateUserPw 데이터 누락');
			socket.emit('updateUserPw', {
				'code' : 470
			});
		} else {
			var inputData = {
				'user_pw' : pw
			};
			
			var updateData = {
				'user_id' : id
			};
			
			mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ? WHERE ?', [inputData, updateData], function(err) {
				if (err) {
					console.log('updateUserPw err = ' + err);
					socket.emit('updateUserPw', {
						'code' : 471
					});
				} else {
					console.log('updateUserPw 성공');
					socket.emit('updateUserPw', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('deleteUserReq', function(data) {
		var id = data.id;
		
		console.log('deleteUserReq');
		console.log('deleteUserReq id = ' + id);
		
		if (!id) {
			console.log('deleteUserReq 데이터 누락');
			socket.emit('deleteUserReq', {
				'code' : 480
			});
		} else {
			var inputData = {
				'delete_user_id' : id
			};
			
			mySqlConnection.query('INSERT INTO ' + DELETE_USER_TABLE + ' SET ?', inputData, function(err) {
				if (err) {
					console.log('deleteUserReq', {
						'code' : 481
					});
				} else {
					console.log('deleteUserReq 성공');
					socket.emit('deleteUserReq', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('deleteUser', function(data) {
		var id = data.id;
		
		console.log('deleteUser');
		console.log('deleteUser id = ' + id);
		
		if (!id) {
			console.log('deleteUser 데이터 누락');
			socket.emit('deleteUser', {
				'code' : 450
			});
		} else {
			mySqlConnection.query('DELETE ' + USER_TABLE + ',' + MEMBER_TABLE + ',' + DELETE_USER_TABLE + ',' + MASTER_TABLE + ',' + DELETE_MEMBER_TABLE + ',' + CHARGE_COIN_TABLE + ',' + MSG_TABLE + ' FROM ' + USER_TABLE +
					' LEFT JOIN ' + MEMBER_TABLE + ' ON ' + 
					USER_TABLE + '.' + USER_ID + '=' + MEMBER_TABLE + '.' + MEMBER_USER_ID +
					' LEFT JOIN ' + DELETE_USER_TABLE + ' ON ' + 
					USER_TABLE + '.' + USER_ID + '=' + DELETE_USER_TABLE + '.' + DELETE_USER_ID +
					' LEFT JOIN ' + MASTER_TABLE + ' ON ' + 
					USER_TABLE + '.' + USER_ID + '=' + MASTER_TABLE + '.' + MASTER_ID +
					' LEFT JOIN ' + DELETE_MEMBER_TABLE + ' ON ' +
					USER_TABLE + '.' + USER_ID + '=' + DELETE_MEMBER_TABLE + '.' + DELETE_MEMBER_ID +
					' LEFT JOIN ' + CHARGE_COIN_TABLE + ' ON ' +
					USER_TABLE + '.' + USER_ID + '=' + CHARGE_COIN_TABLE + '.' + CHARGE_COIN_ID +
					' LEFT JOIN ' + MSG_TABLE + ' ON ' +
					USER_TABLE + '.' + USER_ID + '=' + MSG_TABLE + '.' + MSG_USER_ID +
					' WHERE ' + USER_TABLE + '.' + USER_ID + ' = "' + id +'";', function(err) {
				if (err) {
					console.log('deleteUser err = ' + err);
					socket.emit('deleteUser', {
						'code' : 451
					});
				} else {
					console.log('deleteUser 성공');
					socket.emit('deleteUser', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('cancelDeleteUser', function(data) {
		var id = data.id;
		
		console.log('cancelDeleteUser');
		console.log('cancelDeleteUser id = ' + id);
		
		if (!id) {
			console.log('cancelDeleteUser 데이터 누락');
			socket.emit('cancelDeleteUser', {
				'code' : 455
			});
		} else {
			mySqlConnection.query('DELETE FROM ' + DELETE_USER_TABLE + ' WHERE ' + DELETE_USER_ID + ' = "' + id + '";', function(err) {
				if (err) {
					console.log('cancelDeleteUser err = ' + err);
					socket.emit('cancelDeleteUser', {
						'code' : 456
					});
				} else {
					console.log('cancelDeleteUser 성공');
					socket.emit('cancelDeleteUser', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('getDeleteUserList', function(data) {
		console.log('getDeleteUserList');
		
		mySqlConnection.query('SELECT * FROM ' + DELETE_USER_TABLE, function(err, result) {
			if (err) {
				console.log('getDeleteUserList err = ' + err);
				socket.emit('getDeleteUserList', {
					'code':460
				});
			} else {
				console.log('getDeleteUserList 성공');
				socket.emit('getDeleteUserList', {
					'code' : 200,
					'user' : result
				});
			}
		});
	});

	socket.on('insertGroup', function(data) {
		var group_name = data.group_name;
		
		console.log('insertGroup');
		console.log('insertGroup group_name = ' + group_name);
		
		if (!group_name) {
			console.log('insertGroup 데이터누락');
			socket.emit('insertGroup', {
				'code':470
			});
		} else {
			mySqlConnection.query('SELECT * FROM ' + GROUP_TABLE + ' WHERE ' + GROUP_NAME + ' = "' + group_name + '";', function(err, groupResult) {
				if (err) {
					console.log('insertGroup err = ' + err);
					socket.emit('insertGroup', {
						'code' : 473
					});
				} else {
					console.log('groupResult = ' + JSON.stringify(groupResult));
					if (groupResult[0]) {
						// 이미 해당 이름 사용중.
						console.log('insertGroup 이미 그 이름 사용중.');
						socket.emit('insertGroup', {
							'code' : 474
						});
					} else {
						var inputData = {
								'group_name':group_name,
								'group_coin' : -1
							};
							
							mySqlConnection.query('INSERT INTO ' + GROUP_TABLE + ' SET ?', inputData, function(err) {
								if (err) {
									console.log('insertGroup err = ' + err);
									socket.emit('insertGroup', {
										'code' : 471
									});
								} else {
									mySqlConnection.query('SELECT * FROM ' + GROUP_TABLE + ' WHERE ' + GROUP_NAME + ' = "' + group_name + '";', function(err, result) {
										if (err) {
											console.log('insertGroup err = ' + err);
											socket.emit('insertGroup', {
												'code' : 472
											});
										} else {
											console.log('insertGroup 성공');
											socket.emit('insertGroup', {
												'code' : 200,
												'group' : result[0]
											});
										}
									});
								}
							});
					}
				}
			});
		}
	});
	
	socket.on('updateGroupName', function(data) {
		var group_id = data.group_id;
		var group_name = data.group_name;
		
		console.log('updateGroupName');
		console.log('updateGroupName group_id = ' + group_id);
		console.log('updateGroupName group_name = ' + group_name);
		
		if (!group_id || !group_name) {
			console.log('updateGroupName 데이터 누락');
			socket.emit('updateGroupName', {
				'code' : 480
			});
		} else {
			var updateData = {
				'group_name' : group_name
			};
			
			var whereData = {
				'group_id' : group_id
			};
			
			mySqlConnection.query('UPDATE ' + GROUP_TABLE + ' SET ? WHERE ?', [updateData, whereData], function(err) {
				if (err) {
					console.log('updateGroupName err = ' + err);
					socket.emit('updateGroupName', {
						'code' : 481
					});
				} else {
					console.log('updateGroupName 성공.');
					socket.emit('updateGroupName', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('deleteMemberReq', function(data) {
		var id = data.id;
		var group_id = data.group_id;
		
		console.log('deleteMemberReq');
		console.log('deleteMemberReq id = ' + id);
		console.log('deleteMemberReq group_id = ' + group_id);
		
		if (!id || !group_id) {
			console.log('deleteMemberReq 데이터 누락');
			socket.emit('deleteMemberReq', {
				'code' : 490
			});
		} else {
			var inputData = {
				'delete_member_id' : id,
				'delete_member_group_id' : group_id
			};
			
			mySqlConnection.query('INSERT INTO ' + DELETE_MEMBER_TABLE + ' SET ?', inputData, function(err) {
				if (err) {
					console.log('deleteMemberReq', {
						'code' : 491
					});
				} else {
					console.log('deleteMemberReq 성공');
					socket.emit('deleteMemberReq', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('deleteMember', function(data) {
		var id = data.id;
		var group_id = data.group_id;
		
		console.log('deleteMember');
		console.log('deleteMember id = ' + id);
		console.log('deleteMember group_id = ' + group_id);
		
		if (!id || !group_id) {
			console.log('deleteMember 데이터 누락');
			socket.emit('deleteMember', {
				'code' : 550
			});
		} else {
//			mySqlConnection.query('DELETE FROM ' + MEMBER_TABLE + ' ,' + DELETE_MEMBER_TABLE +' WHERE ' +
//					DELETE_MEMBER_TABLE + '.' + DELETE_MEMBER_ID + ' = "' + id + '" AND ' +
//					MEMBER_TABLE + '.' + MEMBER_GROUP_ID + '=' + group_id + ' AND ' +
//					DELETE_MEMBER_TABLE + '.' + DELETE_MEMBER_ID + '=' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ';', function(err) {
			
			mySqlConnection.query('DELETE ' + MEMBER_TABLE + ', ' + DELETE_MEMBER_TABLE + ' FROM ' + MEMBER_TABLE +
					' INNER JOIN ' + DELETE_MEMBER_TABLE +
					' WHERE ' + DELETE_MEMBER_TABLE + '.' + DELETE_MEMBER_ID + ' = "' + id + '" AND ' +
					MEMBER_TABLE + '.' + MEMBER_GROUP_ID + '=' + group_id + ' AND ' +
					DELETE_MEMBER_TABLE + '.' + DELETE_MEMBER_ID + '=' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ';', function(err) {
				if (err) {
					console.log('deleteMember err = ' + err);
					socket.emit('deleteMember', {
						'code' : 551
					});
				} else {
					console.log('deleteMember 성공');
					socket.emit('deleteMember', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('cancelDeleteMember', function(data) {
		var id = data.id;
		var group_id = data.group_id;
		
		console.log('cancelDeleteMember');
		console.log('cancelDeleteMember id = ' + id);
		console.log('cancelDeleteMember group_id = ' + group_id);
		
		if (!id || !group_id) {
			console.log('cancelDeleteMember 데이터 누락');
			socket.emit('cancelDeleteMember', {
				'code' : 560
			});
		} else {
			mySqlConnection.query('DELETE FROM ' + DELETE_MEMBER_TABLE + ' WHERE ' +
					DELETE_MEMBER_ID + ' = "' + id + '" AND ' +
					DELETE_MEMBER_GROUP_ID + '=' + group_id + ';', function(err) {
				if (err) {
					console.log('cancelDeleteMember err = ' + err);
					socket.emit('cancelDeleteMember', {
						'code' : 561
					});
				} else {
					console.log('cancelDeleteMember 성공');
					socket.emit('cancelDeleteMember', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('getDeleteMemberList', function(data) {
		console.log('getDeleteMemberList');
		
		mySqlConnection.query('SELECT * FROM ' + DELETE_MEMBER_TABLE, function(err, result) {
			if (err) {
				console.log('getDeleteMemberList err = ' + err);
				socket.emit('getDeleteMemberList', {
					'code':570
				});
			} else {
				console.log('getDeleteMemberList 성공');
				socket.emit('getDeleteMemberList', {
					'code' : 200,
					'user' : result
				});
			}
		});
	});
	
	socket.on('chargeCoinReq', function(data) {
		var id = data.id;
		var coin = data.coin;
		
		console.log('chargeCoinReq');
		console.log('chargeCoinReq id = ' + id);
		console.log('chargeCoinReq coin = ' + coin);
		
		if (!id || !coin) {
			console.log('chargeCoinReq 데이터 누락');
			socket.emit('chargeCoinReq', {
				'code' : 580
			});
		} else {
			var inputData = {
				'charge_coin_id' : id,
				'charge_coin_price' : coin
			};
			
			mySqlConnection.query('INSERT INTO ' + CHARGE_COIN_TABLE + ' SET ?', inputData, function(err) {
				if (err) {
					console.log('chargeCoinReq', {
						'code' : 581
					});
				} else {
					console.log('chargeCoinReq 성공');
					socket.emit('chargeCoinReq', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('chargeCoin', function(data) {
		var id = data.id;
		var coin = data.coin;
		
		console.log('chargeCoin');
		console.log('chargeCoin id = ' + id);
		console.log('chargeCoin coin = ' + coin);
		
		if (!id || !coin) {
			console.log('chargeCoin 데이터 누락');
			socket.emit('chargeCoin', {
				'code' : 590
			});
		} else {
			mySqlConnection.beginTransaction(function(err) {
				if (!err) {
					mySqlConnection.query('DELETE FROM ' + CHARGE_COIN_TABLE + ' WHERE ' +
							CHARGE_COIN_ID + '="' + id + '";', function(err) {
						if (err) {
							console.log('chargeCoin err = ' + err);
							socket.emit('chargeCoin', {
								'code' : 591
							});
							mySqlConnection.rollback();
						} else {
							mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ' + USER_COIN + ' = ' + coin + ' WHERE ' + USER_ID + ' = "' + id + '";', function(err) {
								if (err) {
									console.log('코인 추가 실패.');
									socket.emit('chargeCoin', {
										'code' : 592
									});
									mySqlConnection.rollback();
								} else {
									console.log('chargeCoin 성공');
									socket.emit('chargeCoin', {
										'code' : 200
									});
									mySqlConnection.commit();
								}
							});
						}
					});
				}
			});
		}
	});
	
	socket.on('cancelChargeCoin', function(data) {
		var id = data.id;
		
		console.log('cancelChargeCoin');
		console.log('cancelChargeCoin id = ' + id);
		
		if (!id) {
			console.log('cancelChargeCoin 데이터 누락');
			socket.emit('cancelChargeCoin', {
				'code' : 600
			});
		} else {
			mySqlConnection.query('DELETE FROM ' + CHARGE_COIN_TABLE + ' WHERE ' +
					CHARGE_COIN_ID + ' = "' + id + '";', function(err) {
				if (err) {
					console.log('cancelChargeCoin err = ' + err);
					socket.emit('cancelChargeCoin', {
						'code' : 601
					});
				} else {
					console.log('cancelChargeCoin 성공');
					socket.emit('cancelChargeCoin', {
						'code' : 200
					});
				}
			});
		}
	});
	
	socket.on('getChargeCoinList', function(data) {
		console.log('getChargeCoinList');
		
		mySqlConnection.query('SELECT * FROM ' + CHARGE_COIN_TABLE, function(err, result) {
			if (err) {
				console.log('getChargeCoinList err = ' + err);
				socket.emit('getChargeCoinList', {
					'code':611
				});
			} else {
				console.log('getChargeCoinList 성공');
				socket.emit('getChargeCoinList', {
					'code' : 200,
					'user' : result
				});
			}
		});
	});
	
	socket.on('getAllGroup', function(data) {
		console.log('getAllGroup');
		
		mySqlConnection.query('SELECT * FROM ' + GROUP_TABLE + ' LEFT JOIN ' + MASTER_TABLE + ' ON ' + MASTER_TABLE + '.' + MASTER_GROUP_ID + ' = ' + GROUP_ID +
				' LEFT JOIN ' + MEMBER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_GROUP_ID + ' = ' + GROUP_TABLE + '.' + GROUP_ID +
				' LEFT JOIN ' + USER_TABLE + ' ON ' + MEMBER_TABLE + '.' + MEMBER_USER_ID + ' = ' + USER_TABLE + '.' + USER_ID +
				' ORDER BY ' + GROUP_TABLE + '.' + GROUP_ID + ' ASC;', function(err, result) {
			if (err) {
				console.log('getAllGroup err = ' + err);
				socket.emit('getAllGroup', {
					'code' : 620
				});
			} else {
				console.log('getAllGroup 성공');
				console.log('result = ' + JSON.stringify(result));
				socket.emit('getAllGroup', {
					'code' : 200,
					'group' : result
				});
			}
		});
	});
	
	socket.on('getUserCount', function(data) {
		var id = data.id;
		var date = new Date(data.day).addHours(9);
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		
		console.log('getUserCount');
		console.log('getUserCount id = ' + id);
		console.log('getUserCount date = ' + date);
		
		if (!id || !date) {
			console.log('getUserCount 데이터 누락');
			socket.emit('getUserCount', {
				'code' : 630
			});
		} else {
			mySqlConnection.query('SELECT COUNT(*) FROM ' + COUNT_TABLE + ' WHERE ' +
					COUNT_USER_ID + ' = "' + id + '" AND ' +
					COUNT_DATE + ' = "' + year + '-' + month + '-' + day + '";', function(err, count) {
				if (err) {
					console.log('getUserCount err = ' + err);
					socket.emit('getUserCount', {
						'code' : 631
					});
				} else {
					console.log('getUserCount 성공');
					socket.emit('getUserCount', {
						'code' : 200,
						'count' : count[0]
					});
				}
			});
		}
	});
	
	socket.on('setMaxCoin', function(data) {
		var group_id = data.id;
		var coin = data.count;
		
		console.log('setMaxCoin');
		console.log('setMaxCoin group_id = ' + group_id);
		console.log('setMaxCoin coin = ' + coin);
		
		if (!group_id || !coin)	{
			console.log('setMaxCoin 데이터 누락');
			socket.emit('setMaxCoin', {
				'code' : 641
			});
		} else {
			mySqlConnection.query('UPDATE ' + GROUP_TABLE + ' SET ' + GROUP_COIN + ' = ' + coin + ' WHERE ' + GROUP_ID + ' = ' + group_id + ';', function(err) {
				if (err) {
					console.log('setMaxCoin err = ' + err);
					socket.emit('setMaxCoin', {
						'code' : 642
					});
				} else {
					console.log('setMaxCoin성공');
					socket.emit('setMaxCoin', {
						'code' : 200
					})
				}
			});
		}
	});
	
	
	socket.on('connection', function(data) {
		console.log('연결');
		
		var id = data.id;
		console.log('연결 id = ' + id);
		
		if (!id) {
			console.log('연결 데이터 누락');
		} else {
			var updateQuery = {
				'user_is_connected' : 1
			};
			
			mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ? WHERE ' + USER_ID + ' = "' + id + '";' ,updateQuery, function(err) {
				if (err) {
					console.log('update isConnected ' + err);
				} else {
					console.log('연결 성공');
				}
			});
		}
	});
	
	socket.on('disconnection', function(data) {
		console.log('연결해제');
		
		var id = data.id;
		console.log('연결해제 id = ' + id);
		
		if (!id) {
			console.log('연결해제 데이터 누락');
		} else {
			var updateQuery = {
				'user_is_connected' : 2
			};
			
			mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ? WHERE ' + USER_ID + ' = "' + id + '";', updateQuery, function(err) {
				if (err) {
					console.log('update isConnected ' + err);
				} else {
					console.log('연결해제 성공');
				}
			});
		}
	});

	socket.on('getGroupName', function(data) {
		var id = data.id;
		
		console.log('getGroupName');
		console.log('getGroupName id = ' + id);
		
		if (!id) {
			console.log('getGroupName 데이터 누락');
			socket.emit('getGroupName', {
				'code' : 650
			});
		} else {
			mySqlConnection.query('SELECT ' + GROUP_NAME + ' FROM ' + GROUP_TABLE + ' WHERE ' + GROUP_ID + ' = ' + id, function(err, groupName) {
				if (err) {
					console.log('getGroupName Err = ' + err);
					socket.emit('getGroupName', {
						'code' : 651
					});
				} else {
					socket.emit('getGroupName', {
						'code' : 200,
						'group'	: groupName[0]
					});
				}
			});
		}
	});

	socket.on('deleteGroup', function(data) {
		var id = data.id;
		
		console.log('deleteGroup');
		console.log('deleteGroup id = ' + id);
		
		if (!id) {
			console.log('deleteGroup 데이터누락.');
			socket.emit('deleteGroup', {
				'code' : 660
			});
		} else {
			mySqlConnection.query('DELETE FROM ' + GROUP_TABLE + ' WHERE ' + GROUP_ID + ' = ' + id, function(err) {
				if (err) {
					console.log('deleteGroup err = ' + err);
					socket.emit('deleteGroup', {
						'code' : 661
					});
				} else {
					console.log('deleteGroup 성공');
					socket.emit('deleteGroup', {
						'code' : 200
					});
				}
			});
		}
	});
	
	
	socket.on('sendMsg', function(data) {
		var id = data.id;
		
		console.log('sendMsg');
		console.log('sendMsg id = ' + id);
		
		mySqlConnection.query('SELECT ' + MEMBER_GROUP_ID + ' FROM ' + MEMBER_TABLE + ' WHERE ' + MEMBER_USER_ID + ' = "' + id + '";', function(err, member_groups) {
			if (err) {
				console.log('get groupId err = ' + err);
				socket.emit('sendMsg', {
					'code' : 670
				});
			} else {
				console.log('groups = ' + JSON.stringify(member_groups));
				
				if (member_groups.length > 0) {
					mySqlConnection.query('SELECT ' + USER_COIN + ', ' + USER_AUTHORITY_INFINITE_COIN + ' FROM ' + USER_TABLE + ' WHERE ' + USER_ID + ' = "' + id + '";', function(err, coinResult) {
						if (err) {
							console.log('SELECT COIN ' + err);
							socket.emit('sendMsg', {
								'code' : 671
							});
						} else {
							if (coinResult[0].user_authority_infinite_coin != 1) {
								mySqlConnection.beginTransaction(function(err) {
									mySqlConnection.query('UPDATE ' + USER_TABLE + ' SET ' + USER_COIN + ' = ' + USER_COIN + ' - 1 WHERE ' + USER_ID + ' = "' + id + '";', function(err) {
										if (err) {
											console.log('update Coin err = ' + err);
											socket.emit('sendMsg', {
												'code' : 672
											});
											mySqlConnection.rollback();
										} else {
											var date = new Date().addHours(9);
											var year = date.getFullYear();
											var month = date.getMonth() + 1;
											var day = date.getDate();
											
											console.log('year = ' + year);
											console.log('month = ' + month);
											console.log('day = ' + day);
											
											var today = year + '-' + month + '-' + day;
											console.log('today = ' + today);
											
											var inputData = {
												'count_user_id' : id,
												'count_date' : today
											};
											
											mySqlConnection.query('INSERT INTO ' + COUNT_TABLE + ' SET ?', inputData, function(err) {
												if (err) {
													console.log('Insert Count err = ' + err);
													socket.emit('sendMsg', {
														'code' : 673
													});
													mySqlConnection.rollback();
												} else {
													console.log('insert Count Success');
													socket.emit('sendMsg', {
														'code' : 200
													});
													
													mySqlConnection.commit(function(err) {
														if (err) {
															console.log('err = ' + err);
														}
													});
												}
											});
										}
									});

								});
							} else {
								var date = new Date().addHours(9);
								var year = date.getFullYear();
								var month = date.getMonth() + 1;
								var day = date.getDate();
								
								console.log('year = ' + year);
								console.log('month = ' + month);
								console.log('day = ' + day);
								
								var today = year + '-' + month + '-' + day;
								console.log('today = ' + today);
								
								var inputData = {
									'count_user_id' : id,
									'count_date' : today
								};
								
								mySqlConnection.query('INSERT INTO ' + COUNT_TABLE + ' SET ?', inputData, function(err) {
									if (err) {
										console.log('Insert Count err = ' + err);
										socket.emit('sendMsg', {
											'code' : 675
										});
									} else {
										console.log('insert Count Success');
										socket.emit('sendMsg', {
											'code' : 200
										});
									}
								});
							}
						}
					});
				} else {
					socket.emit('sendMsg', {
						'code' : 200
					});
				}
			}
		});

	});
	
});
