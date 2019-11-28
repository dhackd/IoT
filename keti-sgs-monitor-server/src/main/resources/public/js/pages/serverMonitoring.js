function search() {
	// call 조회
	_getCheckerList($('#server_list'), $('#node_list'))
}

function _getCheckerList(serverDomId, nodeDomId) {
	$.ajax({
		type : "GET",
		url : apiV1 + "/servers",
		success : function(res) {
			_renderList(res.data, serverDomId, nodeDomId)
		}
	})
}

function _renderList(list, serverDomId, nodeDomId) {
	let serverList = '';
	let nodeList = '';
	
	for (count in list) {
		const serverId = list[count].serverId;
		const nodeId = list[count].nodeId;
		const connectionDomain = list[count].domain
		const viewDomain = list[count].domain !== '' ? list[count].domain : '미등록' 

		if(serverDomId!==null) {
			serverList += '<div onClick=_goDetailPage("' + connectionDomain + '","vm") id="'
					+ connectionDomain + '" class="server">';
			
			serverList += '<div class="row"><div class="title">' + serverId
			+ '</div></div><div class="data"><div class="key">Domain</div><div class="value">'
			+ viewDomain + '</div></div></div>'
		}
		
		if(nodeDomId!==null) {
			nodeList += '<div onClick=_goDetailPage("' + connectionDomain + '","node") id="'
			+ connectionDomain + '" class="server">';
			nodeList += '<div class="row"><div class="title">' + nodeId
			+ '</div></div><div class="data"><div class="key">Domain</div><div class="value">'
			+ viewDomain + '</div></div></div>'
		}
	}

	$(serverDomId).html(serverList)
	$(nodeDomId).html(nodeList)
}

function _goDetailPage(connectionDomain, type) {
  window.location.href = '/monitoring/servers/' + type + '/' + connectionDomain
}
