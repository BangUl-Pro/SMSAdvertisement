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
//var mySqlConnection = mySql.createConnection({
//	port : mySqlPort,
//	user : mySqlUserName,
//	password : mySqlPw,
//	database : mySqlUrl
//});

mySqlConnection.connect(function(err) {
	if(err) {
		console.log('에러 = ' + err);
	}
});

io.sockets.on('connection', function(socket) {
	socket.on('connect', function(data) {
		console.log('연결');
	});
	
	socket.on('disconnect', function() {
		mySqlConnection.end();
		console.log('연결 해제');
	});
});
