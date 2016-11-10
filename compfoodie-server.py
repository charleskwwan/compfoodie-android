# compfoodie-server - handles server side logic for the CompFoodie firebase
# by: Nga Pham and Charles Wan
# on: 10/6/16

import os
import pyrebase # https://github.com/thisbejim/Pyrebase, firebase wrapper
import flask # http://flask.pocoo.org/, microframewor
import sched, time, datetime
import sys

# firebase initial config
config = { # for user-based authentication
	"apiKey": "AIzaSyAwIkTTkT6co5t2udD9iL88l45UOWnWay0",
	"authDomain": "api-project-598529388832.firebaseapp.com",
	"databaseURL": "https://api-project-598529388832.firebaseio.com/",
	"storageBucket": "api-project-598529388832.appspot.com"
}
firebase = pyrebase.initialize_app(config)
db = firebase.database()

# flask initial config
app = flask.Flask(__name__)

# tests if a json group is valid
def groupIsValid(grp):
	keys = ["creator", "location", "message", "orderTime", 
            "partyCap", "partySize"]
	for k in keys:
		if not k in grp:
			return False
	return True

# API calls
@app.route("/api/addgroup", methods=["POST"])
def addgroup():
	nw = flask.request.get_json(silent=False)
	if groupIsValid(nw):
		grp_id = db.child("groups").push(nw)
		resp = { u"id": str(grp_id["name"]) }
		return flask.jsonify(resp), 201
	else:
		return flask.Response(status=400)


# Firebase notification
s = sched.scheduler(time.time, time.sleep)

def listenForFirebase():
	print "listenForFirebase"
	sys.stdout.flush()

	ref = db.child("groups").get()
	groups = ref.val()

	for groupID, group in groups.iteritems():
		now = time.time()
		hr = group["hour"]
		mn = group["minute"]
		nw = datetime.datetime.now()
		tm = nw.replace(hour=hr, minute=mn)

		diff = (nw - tm).total_seconds()
		if diff <= 3600: 
			sendNotificationToUser()
		elif diff <= 7200: 
			removeFromFirebase(groupID, group["guests"])
		else: 
			print "nothing"
			sys.stdout.flush()

@app.route("https://fcm.googleapis.com/fcm/send", methods=["POST"])
def sendNotificationToUser():
	print "sendNotificationToUser"
	sys.stdout.flush()

def removeFromFirebase(groupID, guests):
	db.child("groups").child(groupID).remove()
	for userID in guests:
		ref = db.child("users").child(userID)
		user = ref.get().val()
		user["groups"].remove(groupID)
		ref.set(user)
	print "remove", groupID	
	sys.stdout.flush()

if __name__ == "__main__":
	port = int(os.environ.get("PORT", 5000)) # for listening
	app.run(host="0.0.0.0", port=port, debug=True) # change to false for actual

	print "HELLO"
	sys.stdout.flush()
	listenForFirebase()
	s.enter(60, 1, listenForFirebase, (s,))
	s.run()

