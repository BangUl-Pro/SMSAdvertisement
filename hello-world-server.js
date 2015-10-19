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

mySqlConnection.query('create table user_auth (' +
		'user_id text, ' +
		'user_pw text, ' +
		'user_mail text, ' +
		'user_name text, ' +
		'user_birth int);');

io.sockets.on('connection', function(socket) {
	socket.on('hi', function(data) {
		console.log('gd');
	});
	
	socket.on('insert', function() {
		var data = {
				'user_id' : "id",
				'user_pw' : "1234",
				'user_mail' : 'mail',
				'user_name' : '이동규',
				'user_birth' : 970224
		};
		mySqlConnection.query("insert into user_auth set ?", data, function(err, rows) {
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
});
