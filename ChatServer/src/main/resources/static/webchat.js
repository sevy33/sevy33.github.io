var username;
var chatroom;
var msg;
var msgCount = 0;
var joinCount = 0;
var reqText;
var mySocket = new WebSocket('ws://' + location.host);

mySocket.onopen = function () {
	console.log("Connected");
};

function routeChat() {
	var form = document.getElementById("valueform");
	username = form.getElementsByTagName("input")[0].value;
	chatroom = form.getElementsByTagName("input")[1].value;

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		console.log(this);
	    if (this.status == 200) {
	      document.getElementById("main").innerHTML = this.response;
	      setupChatRoom();
	    }
	    joinChat();
	};
	xhttp.open("GET", "chat.html", true);
	xhttp.send();
}

function setupChatRoom() {
	var chatH1 = document.getElementById("chatH1");
	var text = document.createTextNode(" " + chatroom);
	chatH1.appendChild(text);
	chatH1.style.color = "#66FF00";

	document.getElementsByTagName("span")[0].style.fontSize = "x-large";

	var textA = document.getElementsByTagName("textarea")[0];
	textA.style.height = "50px";
	textA.style.width = "500px";
	textA.style.verticalAlign = "text-top";

	var button = document.getElementsByTagName("button")[0];
	button.style.width = "100px";
	button.style.fontSize = "22px";
	button.style.verticalAlign = "text-top";

	document.getElementById("respMes").style.backgroundColor = "white";
	document.getElementById("respMes").style.height = "500px";
	document.getElementById("respMes").style.width = "700px";
}

function joinChat() {
	mySocket.addEventListener("message", onMessage());
	var req = "join " + chatroom + " " + username; 
	mySocket.send(req);
}

function postChat() {
	msg = document.getElementsByTagName("textarea")[0].value;

	mySocket.addEventListener("message", onMessage());
	var newreq = "post " +chatroom + " " + username + " " + msg;
	mySocket.send(newreq);
}

function onMessage() {
	mySocket.onmessage = function(evt) {
			reqText = evt.data;
			splitreq = reqText.split(" ");

			console.log(splitreq);

			var respMes = document.getElementById("respMes");
			if(joinCount == 0) {
				var a = document.createElement("h2");
				var text = document.createTextNode("Joined: " + splitreq[1]);
				a.appendChild(text);
				respMes.appendChild(a);
			
				joinCount++;
			} else {
				if(splitreq[0] == "Joined:") return;
				if(msgCount == 0) {
					var c = document.createElement("ul");
					c.setAttribute("id", "ul1");
					respMes.appendChild(c);
				}

				var b = document.createElement("li");
				var text = document.createTextNode(splitreq[0] + ": "+ splitreq[1]);
				b.appendChild(text);
				document.getElementById("ul1").appendChild(b);

				document.getElementsByTagName("textarea")[0].value = "";

				msgCount++;
			}
		};
}









