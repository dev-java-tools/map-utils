# map-utils
This Java library contains the API to 

* Sort a map
	* Sort on custom keys if the map has a list of objects
* Get the list of all the properties in a Map
* Get the list of all the paths in a Map
* Update map by specifying the path of the element in the map and its value
	* Examples
		* friends[{name=Lenna Paprocki}].associatedAddresses[{state=TX}, {city=Irving}].street
			* In the above example, you can set the street field on the associatedAddresses object that has state as TX and city as Irving. Also when this associatedAddresses belongs to a friend whose name field s the value of Lenna Paprocki.
		* friends[{name=Lenna Paprocki}].associatedAddresses[{state=TX}].city"
			* In the above example, you can set the street field on the associatedAddresses object that has state as TX. Also when this associatedAddresses belongs to a friend whose name field s the value of Lenna Paprocki.
		* friends[2].associatedAddresses[5].city
			* In this you are setting the city of the 6th associatedAddresses of the 3rd friend.
		* friends[].associatedAddresses[5].city
			* In this you are setting the city of the 6th associatedAddresses of the first (default) friend in the list.
			
* Create Map from 
	* String
	* Java File object
	* Also from a custom model objects
