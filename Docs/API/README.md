Ajapaik Hack4Fi API v1.0
=======

Every API call returns a JSON response.

# Data Types

	INTEGER - An integer value such as '2' or '50'
	NUMBER - A number value such as '2' or '5.3'
	STRING - A string value

# Error codes

Error responses are standardised - very API call include an error key at the root level:

	{ "error": 1000 }

List of possible error codes:

	[standard]
    0 - no error (default)
    1 - unknown error
    2 - invalid input parameter
    3 - missing input parameter
    4 - access denied
    5 - session is required
    6 - session is expired
    7 - session is invalid
    9 - application version is not supported

# Hyperlinks

Hyperlinks support 2 different notation styles. Either it's a simple string or a dictionary with a name and url:

	"source1": "http://example.com",
	"source2": {
		"url": "http://example.com",
		"name": "Example 1"
	}

# Authentication

All the API calls require a valid session, but accounts are created automatically so it's invisible to end-users.

Session parameters are standardised:

	[session]:
	STRING _u [R] = user ID (required)
	STRING _s [R] = session ID (required)
	STRING _v [O] = client version ID (optional)

## Login

Performs a login or creates an account if possible (type=auto). Returns a new session.

	/login
    
    Parameters:
        STRING type [R] - Login type. Use 'auto' to automatically create an account
        STRING username [R] - Username or a randomly generated unique identifier
        STRING password [R] - Hashed password or a randomly generated password
        INTEGER length [O=0] - Session length in seconds. 0 - automatic
        STRING os [O=android] - Platform
    
    Returns:
        {
        	"error": 0,
        	"id": 1234, /* _u value for the subsequent API calls */
        	"session": "12345678", /* _s value for the subsequent API calls */
        	"expires": 1000 /* Hint in seconds when the session should be automatically cleared by the client. */
        }
    
    Errors:
        [standard]

## Logout

Performs a logout. The session is not valid after this call.

	/logout
    
    Parameters:
        [session]
    
    Returns:
        { "error": 0 }
    
    Errors:
        [standard]

# Album calls

## Albums

Returns all the albums.

	/albums
	
	Parameters:
	    [session]
	
	Returns:
	    {
	        "error": 0,
	        "albums": [
	            {
	                "id": 1234, /* Album ID */
	                "title": "Abc" /* Album name */,
	                "subtitle": "hmm" /* Optional subtitle under the name */,
	                "image": "http://www.example.org/image.png", /* Album thumbnail image */
	                "tagged": 0, /* Number of tagged photos for the user */
	                "total": 0 /* Number of photos in the album */
	            }
	        ]
	    }
	
	Errors:
	    [standard]

## Album state

Returns the current state for an album

    /album/state
    
    Parameters:
        [session]
        INTEGER id [R] - Album ID
        INTEGER max [O=0] - Maximum number of tags per photo (client-side hint)
        STRING state [O] - The value of the state parameter from the previous API call
    
    Returns:
        {
        	"error": 0,
        	"state": "ABCDEF012345789", /* State variable managed by the back-end that can be used to track client-app state */
	        "title": "Abc" /* Album name */,
	        "subtitle": "hmm" /* Optional subtitle under the name */,
	        "image": "http://www.example.org/image.png", /* Album thumbnail image */,
	        "photos": [
	        	{
	        		"id": 1234, /* Photo ID */
	        		"image": "http://www.example.org/image.png", /* Photo image */
	        		"title": "Title", /* Photo title */
	        		"author": "Author Name", /* Photo author if available */
	        		"source": "http://example.com", /* Photo source. See: hyperlinks */
	        		"tags": [ /* Tag choices to display */
	        			"interior_or_exterior",
	        			"public_or_private",
	        			"urban_or_rural",
	        			"landscape_or_portrait",
	        			"ground_or_raised",
	        			"view_or_social",
	        			"staged_or_natural",
	        			"one_or_many",
	        			"whole_or_detail"
	        		]
	        	}
	        ],
	        "photos+": [
	        	/* Optional, photos to add or update */
	        ],
	        "photos-": [
	        	1234, 1235, 1236 /* Optional, photos to remove
	        ]
        }
    
    Errors:
        [standard]

## Album tagging

    /album/tag
    
    Parameters:
        [session]
        INTEGER id [R] - Album ID
        INTEGER photo [R] - Photo ID
        INTEGER max [O=0] - Maximum number of tags per photo (client-side hint)
        STRING tag [R] - Tag key (such as "interior_or_exterior")
        INTEGER value [R] - -1 - left, 0 - not applicable, 1 - right
        STRING state [O] - The value of the state parameter from the previous API call
    
    Returns:
        See /album/state
    
    Errors:
        See /album/state

# User calls

## Profile info

	/user/me
	
	Parameters:
		[session]
		STRING state [O] - The value of the state parameter from the previous API call
	
	Returns:
		{
			"error": 0,
			"state": "ABCDEF012345789", /* State variable managed by the back-end that can be used to track client-app state */
			"tagged": 123 /* Number of times tagged */
			"pics": 123, /* Number of tagged photos */
			"message": "Message", /* An optional message from the server to display */,
			"link": "http://", /* An optional link to open in the browser. See: hyperlinks */
			"favorites": [
				{
					"album_id": 1234, /* Album ID */
					"photo_id": 1245, /* Photo ID */
					"image": "http://www.example.org/image.png", /* Favorite thumbnail image */
					"title": "A photo", /* Favirute photo title */
					"date": "yyyy-MM-ddTHH:mm:ss.SSSZ" /* ISO-8601 date in UTC */
				}
			],
			"favorites+": [
				/* Optional, favorites to add or update */
			],
			"favorites-": [
				1234, 1235, 1236 /* Optional, favorites to remove */
			]
		}
	
	Errors:
        [standard]
    
## Add favorite

Adds a new favorite and updates client-side favorites

	/user/favorite/add
	
	Parameters:
		[session]
		INTEGER album [R] - Album ID
		INTEGER photo [R] - Photo ID
		STRING state [O] - The value of the state parameter from the previous API call
	
	Returns:
        See /user/me
    
    Errors:
        See /user/me

## Remove favorite

Removes a favorite and updates client-side favorites
 
	/user/favorite/remove
	
	Parameters:
		[session]
		INTEGER album [R] - Album ID
		INTEGER photo [R] - Photo ID
		STRING state [O] - The value of the state parameter from the previous API call
	
	Returns:
        See /user/me
    
    Errors:
        See /user/me

## Register for push notifications

Tells the back-end to send push notifications.

	/user/device/register
	
	Parameters:
		[session]
		STRING id [R] - Device ID
		STRING type [R] - Push notification service type (Google - 'gcm', Apple - 'apns')
		STRING token [R] - The token from the push notification service
		STRING filter [O=any] - Comma-separated list of notification types to send
	
	Returns:
        { "error": 0 }
    
    Errors:
        [standard]

## Unregister for push notifications

Tells the back-end not to send any additional push notifications.

	/user/device/unregister
	
	Parameters:
		[session]
		STRING id [R] - Device ID
		STRING type [R] - Push notification service type (Google - 'gcm', Apple - 'apns')
		STRING token [R] - The token from the push notification service
	
	Returns:
        { "error": 0 }
    
    Errors:
        [standard]
