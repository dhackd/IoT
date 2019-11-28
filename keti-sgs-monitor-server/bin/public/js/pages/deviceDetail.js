const path = window.location.pathname.split('/');
const address = path[3];
        
function getPeerLists() {
	$.ajax({
		type : "GET",
		url : apiV1 + "/servers",
		success : function(res) {
			_renderMonitor(res.data)
		}
	})
}

function _renderMonitor(peerTable) {
	const monitoringServer = _findMonitoringServer(peerTable);
	
	const dashboard = 'http://'+ monitoringServer +'/d/device/device-metrics?orgId=1&var-address='
	+ address + '&refresh=10s&kiosk';
	
    $('#dashboard').html('<iframe src="'+ dashboard +'"></iframe>')
}

function _findMonitoringServer(peerTable) {
	let result = '';
	
    for(i = 0; i< peerTable.length; i++) {
      const monitoringServer = peerTable[i].domain;
  	  const peerHostName = monitoringServer.split(":")[0]
  	  
      if ( window.location.hostname === peerHostName) {
    	  result = monitoringServer;
    	  break;
  	  }
  	}
    
    return result;
}