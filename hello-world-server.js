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
mongoose.connect('mongodb://username:12345678@ds041144.mongolab.com:41144/heroku_ncdrcmlf');
var ObjectId = mongoose.Schema.ObjectId;

var errSchema = mongoose.Schema({
	code : Number,
	msg : String,
	user_id : String
});

var errModel = mongoose.model('err', errSchema);



//mySQL
var mySqlUrl = 'mysql://q94j7nr8jh8yuyuu:ioz8fh0huec5hy0j@jw0ch9vofhcajqg7.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/djxwafbetlgttnhj';
var mySqlUserName = 'q94j7nr8jh8yuyuu';
var mySqlPw = 'ioz8fh0huec5hy0j';
var mySqlPort = 3306;
var mySql = require('mysql');
var mySqlConnection = mySql.createConnection(process.env.JAWSDB_URL || mySqlUrl);
mySqlConnection.connect();

io.sockets.on('connection', function(socket) {
	socket.on('connect', function(data) {
		console.log('연결');
	});
	
	socket.on('disconnect', function() {
		mySqlConnection.end();
		console.log('연결 해제');
	});
});
