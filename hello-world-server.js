var http = require('http'),
	express = require('express'),
	app = express();

var server = http.createServer(app).listen(process.env.PORT || 5000);
var io = require('socket.io').listen(server);

app.get('/', function (req, res) {
    res.send('hi');
    console.log('\n Hi');
});

//몽고디비
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
		mySqlConnection.query("create table if not exists user_auth (user_id VARCHAR(50) not null primary key, user_pw VARCHAR(20), user_mail VARCHAR(50) not null primary key, user_name VARCHAR(20), user_birth INT, user_socket VARCHAR(25));", function(err, result) {
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
	
	
	// 회원가입 
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
				'code':303
			});
			return;
		}
		
		var input = {
				user_id : id,
				user_pw : pw,
				user_name : name,
				user_mail : mail,
				user_birth : birth,
				user_socket : null
		};
		
		mySqlConnection.query('insert into user_auth set ?', input, function(err, result) {
			if (err) {
				// 아이디 중복 에러
				socket.emit('signUp', {
					'code':304
				});
				console.log('회원가입 아이디 중복 : ' + err);
			} else {
				// 성공
				socket.emit('signUp', {
					'code':200,
					'id':id
				});
				console.log('회원가입 성공');
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
		
		mySqlConnection.query("select user_id from user_auth where user_id = '" + id + "' and user_pw = '" + pw + "';", function(err, result) {
			if (err) {
				socket.emit('login', {
					'code':301
				});
				console.error('로그인  DB 에러 = ' + err);
			} else {
				if (!result[0] || !result[0].user_id) {
					// 일치하는 아이디가 없다면
					console.log('일치하는 아이디가 없음');
					socket.emit('login', {
						'code':302
					});
				} else {
					console.info('socket.id = ' + socket.id);
					
					// 일치하는 아이디가 있다면
					mySqlConnection.query('update user_auth set user_socket = "' + socket.id + '" where user_id = "' + id + '";', function(err, result) {
						if (err) {
							console.log('로그인 socket 정보 등록 에러 : ' + err);
							socket.emit('login', {
								'code':305
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
