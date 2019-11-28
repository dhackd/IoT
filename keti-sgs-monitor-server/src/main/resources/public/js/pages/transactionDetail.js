const search = () => {
	const txid = window.location.pathname.split('/')[2];
	_getTransactionData(txid)
}

function _getTransactionData(txId) {
	$.ajax({
		 type : "GET",
		 url : apiV1 + '/transactions/' + txId,
		 success : function(res) {
			 _renderData(res.data)
		 }
	 })
}

const _beautyLine = function(payload) {
	return payload.replace(/\n/g, '<br>')
}

const _jsonBeautify = function(payload) {
	return JSON.stringify(JSON.parse(payload), null, 4).replace(/\n/g, '<br>').replace(/[ ]/g, "&nbsp;")
}

const _isJsonFormat = function(string) {
	try {
		JSON.parse(string);
		return true;
	}catch {
		return false;
	}
}

function _beautyDeployContract(payload) {
	const startRegex = /{\"version/g;
	const start = payload.search(startRegex)
	const endRegex = /\}\[/;
	const end = payload.search(endRegex) + 1;
	
	var abi = payload.substring(start, end)
	return _jsonBeautify(abi)
}

function _renderData(result) {
	const deviceId = result.deviceId !== null ? result.deviceId : 'Unknown';
	
	$('#deviceId').html(deviceId)
	$('#txid').html(result.txId)
	$('#from').html(result.from)
	$('#blockHash').html(result.blockHash)
	
//	// Contract인 경우
//	if(result.from !== result.to) {
//		if(result.to === "") {
//			// deploy
//			$('#payload').html(_beautyDeployContract(result.payload))
//			$('#to').html('Deploy Contract')
//			return
//		} else {
//			// exec
//			$('#payload').html(_jsonBeautify(result.payload))
//			$('#to').html(result.to)
//			return
//		}
//	}
	
	// normal transaction
	$('#to').html(result.to)
	if(_isJsonFormat(result.payload)) {
		$('#payload').html(_jsonBeautify(result.payload))
	} else {
		$('#payload').html(_beautyLine(result.payload))
	}
}