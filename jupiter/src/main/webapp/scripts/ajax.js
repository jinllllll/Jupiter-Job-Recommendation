/**
 * API #1 Load the nearby items API end point: [GET]
 * /search?user_id=1111&lat=37.38&lon=-122.08
 */
function loadNearbyItems() {
	console.log('loadNearbyItems');
	activeBtn('nearby-btn');

	// The request parameters
	var url = './search';
	var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
	var data = null;

	// display loading message
	showLoadingMessage('Loading nearby items...');

	// make AJAX call
	ajax('GET', url + '?' + params, data,
	// successful callback
	function(res) {
		var items = JSON.parse(res);
		if (!items || items.length === 0) {
			showWarningMessage('No nearby item.');
		} else {
			listItems(items);
		}
	},
	// failed callback
	function() {
		showErrorMessage('Cannot load nearby items.');
	});
}
