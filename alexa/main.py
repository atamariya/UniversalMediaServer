#
#
# main() will be run when you invoke this action
#
# @param Cloud Functions actions accept a single parameter, which must be a JSON object.
#
# @return The output of this action, which must be a JSON object.
#
#
import sys
import requests, json 
from urllib import parse

message = {};
authenticated = False;
db = {
	"atamariya@gmail.com" : "https://f8d17f43.ngrok.io"
}

def main(dict):
	print(dict)
	# api-endpoint 
	URL = "http://maps.googleapis.com/maps/api/geocode/json"
	  
	# location given here 
	location = "delhi technological university"
	  
	# defining a params dict for the parameters to be sent to the API 
	PARAMS = {'address':location} 
	  
	# sending get request and saving the response as response object 
	#r = requests.get(url = URL, params = PARAMS) 
	  
	# extracting data in json format 
	#data = r.json() 
	  
	# printing the output 
	#print(data) 

    #return { 'message': 'Hello world' }
    
	# Authorize
	if (dict.get("context") != None and dict["context"]["System"]["user"].get("accessToken") != None):
		global authenticated
		accessToken = dict["context"]["System"]["user"]["accessToken"];
		amznProfileUrl = "https://api.amazon.com/user/profile?access_token=" + accessToken;
		r = requests.get(amznProfileUrl);
		user = r.json();
		print(user);

		if (user and user.get("email") and db[user["email"]]):
			authenticated = True;
			email = user["email"];
		else:
			authenticated = False;
		print("Authenticated: ", authenticated);

		if (authenticated):
			userUrl = db[email] + "/alexa";
			print(userUrl);
			
			obj = message
			try:
				r = requests.post(userUrl, data=json.dumps(dict), timeout=30);
				
				print(r.text);
				obj = r.json();
				# Point resource to user url
				print("trying")
				url = obj["response"]["directives"][0]["audioItem"]["stream"]["url"]
				print(url)
				tmp = parse.urlsplit(url)
				url = db[email] + tmp.path
				obj["response"]["directives"][0]["audioItem"]["stream"]["url"] = url
				print(url)
			except requests.exceptions.ConnectTimeout:
				say("Please ensure UMS is running.", "Your setup is incomplete.");
				return message;
			except:
			    pass
			return obj
		else:
			say("Please register at www.universalmediaserver.com.", "Your setup is incomplete.");
	else:
		say("Welcome to the Universal Media Server. Please log in.", "You need to log in.");
	
	return message;
	
def say(message, repromptMessage):
    response = {
        "version": "1.0",
        "response": {
            "shouldEndSession": False,
            "outputSpeech": {
                "type": "SSML",
                "ssml": "<speak> " + message + " </speak>"
            },
            "reprompt": {
                "outputSpeech": {
                    "type": "SSML",
                    "ssml": "<speak> " + repromptMessage + " </speak>"
                }
            }
        }
    };
    succeed(response);

def succeed(msg):
	global message;
	message = msg;
	print("Authenticated: " , authenticated);
	if (not authenticated):
		message["response"]["card"] = {
			"type": "LinkAccount"
		}
	print(message);

with open('req.txt') as data_file:    
    data = json.load(data_file)
#data = {}
print (main(data))